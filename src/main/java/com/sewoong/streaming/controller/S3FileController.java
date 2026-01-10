package com.sewoong.streaming.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.MultipartUpload;
import com.amazonaws.services.s3.model.MultipartUploadListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/s3/file")
@RequiredArgsConstructor
public class S3FileController {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${file.path}")
    private String tempPath;

    private final AmazonS3Client amazonS3Client;

    public String fileUploadByS3(MultipartFile file) {
        try {
            // 파일명
            String fileName = file.getOriginalFilename();
            // 파일 확장자
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

            //
            String convertFileName= convertFileName(fileName) + "." + ext;
            String fileUrl= "https://" + bucket + ".s3." + region + ".amazonaws.com/" + convertFileName;

            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket,convertFileName,file.getInputStream(),metadata);

            return fileUrl;
        }  catch (Exception e){
            return "";
        }
    }

    public String videoUploadByS3(MultipartFile multipartFile){

        String uploadKey = "";
        String uploadId = "";
        try {

            uploadKey = "video/" + UUID.randomUUID();
            String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + uploadKey;

            long contentLength = multipartFile.getSize(); // 파일 전체 크기
            long partSize = 5 * 1024 * 1024; // 한 부분당 바이트(멀티파트 업로드 최소 크기는 5Mb)

            List<PartETag> partETags = new ArrayList<>(); // 각 부분별 ETags를 저장 할 리스트

            // 멀티파트 업로드 시작
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucket, uploadKey);
            InitiateMultipartUploadResult initResponse = amazonS3Client.initiateMultipartUpload(initRequest);

            uploadId = initResponse.getUploadId();

            // 시작 바이트
            long filePosition = 0;

            for (int i = 1; filePosition < contentLength; i++) {
                // 마지막 파트 사이즈를 체크하기위해 전체 파일사이즈 에서 시작 바이트를 뺸값과 파트 사이즈를 비교하여 작은값을 파트 사이즈로 설정
                partSize = Math.min(partSize, (contentLength - filePosition));

                // 업로드할 파트 생성
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucket)
                        .withKey(uploadKey)
                        .withUploadId(uploadId)
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withInputStream(multipartFile.getInputStream())
                        .withPartSize(partSize);

                // 생성한 파트를 업로드 하고 ETag 저장
                UploadPartResult uploadResult = amazonS3Client.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());

                //시작 바이트 갱신
                filePosition += partSize;
            }

            // 업로드 완료 처리
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucket, uploadKey,
                    initResponse.getUploadId(), partETags);
            amazonS3Client.completeMultipartUpload(compRequest);

            return fileUrl;
        } catch (Exception e) {
            if (!uploadKey.equals("") && !uploadId.equals("")) {
                // 멀티파트 요청 중단
                amazonS3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                        bucket, uploadKey, uploadId));
            }
            return "";
        }
    }

    private Upload pgUpload;
    private int progress = 0;

    public String videoUploadProgressByS3(MultipartFile multipartFile, SseEmitter emitter){
        String uploadKey = "";
        try {

            uploadKey = "video/" + UUID.randomUUID();
            String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + uploadKey;

            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            TransferManager tm = TransferManagerBuilder.standard()
                    .withS3Client(amazonS3Client)
                    .build();

            PutObjectRequest request = new PutObjectRequest(
                    bucket, uploadKey, multipartFile.getInputStream(), metadata);


            progress = 0;
            pgUpload = null;

            request.setGeneralProgressListener(new ProgressListener() {
                @Override
                public void progressChanged(ProgressEvent progressEvent) {
                    if(pgUpload == null) return;

                    int percent = (int)pgUpload.getProgress().getPercentTransferred();
                    if(progress != percent) {
                        try {
                            emitter.send(SseEmitter.event().name("progress").data(percent));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        progress = percent;
                    }
                }
            });

            pgUpload = tm.upload(request);

            pgUpload.waitForCompletion();

            return fileUrl;
        }catch (AmazonClientException amazonClientException) {
            System.out.println("Unable to upload file, upload aborted.");
            amazonClientException.printStackTrace();
            return "";
        }
        catch (Exception e) {
            return "";
        }
    }

    @GetMapping("/multiFileUploadAllStop")
    public ResponseEntity AllStop() {

        // 전체 멀티파트 요청 조회
        ListMultipartUploadsRequest allMultpartUploadsRequest =
                new ListMultipartUploadsRequest(bucket);
        MultipartUploadListing multipartUploadListing =
                amazonS3Client.listMultipartUploads(allMultpartUploadsRequest);

        for (MultipartUpload part : multipartUploadListing.getMultipartUploads()
             ) {
            // 멀티파트 요청 중단
            amazonS3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    bucket, part.getKey(), part.getUploadId()));
        }
        return ResponseEntity.ok("ok");
    }

    private String convertFileName(String originFileName){
        UUID uuid = UUID.randomUUID();
        return uuid + "_" + originFileName;
    }
}

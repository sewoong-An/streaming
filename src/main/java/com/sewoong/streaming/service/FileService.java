package com.sewoong.streaming.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class FileService {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${file.path}")
    private String tempPath;

    @Value("${file.ffprobe.path}")
    private String ffmpegPath;

    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    public String videoUpload(MultipartFile file) {
        try {

            UUID uuid = UUID.randomUUID();
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            String videoUrl = "/video/" + uuid + "." + extension;

            File uploadPath = new File(tempPath + videoUrl);
            file.transferTo(uploadPath);

            return videoUrl;
        }  catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String thumbnailUpload(MultipartFile file, String origin) {
        try {

            if(!origin.equals("")) {
                // amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, "thumbnail/" + originKey));
                File imgfile = new File(tempPath + origin);
                imgfile.delete();
            }

            UUID uuid = UUID.randomUUID();
            String thumbnailUrl = "/thumbnail/" + uuid + ".png";
            File uploadPath = new File(tempPath + thumbnailUrl);
            file.transferTo(uploadPath);

            // String uploadKey = "thumbnail/" + uuid;
            // String fileUrl= "https://" + bucket + ".s3." + region + ".amazonaws.com/" + uploadKey;

            // ObjectMetadata metadata= new ObjectMetadata();
            // metadata.setContentType(file.getContentType());
            // metadata.setContentLength(file.getSize());
            // amazonS3Client.putObject(bucket, uploadKey, file.getInputStream(), metadata);

            return thumbnailUrl;
        }  catch (Exception e){
            return "";
        }
    }

    public String initialThumbnail(String videoUrl) throws Exception {
        UUID uuid = UUID.randomUUID();

        String thumbnailUrl = "/thumbnail/" + uuid + ".png";

        // 명령어를 리스트 형태로 전달 (공백 포함 경로 대응)
        List<String> command = Arrays.asList(
            "ffmpeg", "-i", tempPath + videoUrl,
            "-ss", "00:00:01",
            "-vcodec", "png",
            "-vframes", "1",
            "-vf", "scale=320:180",
            tempPath + thumbnailUrl
        );

        ProcessBuilder pb = new ProcessBuilder(command);

        // 중요: 에러 스트림을 표준 출력 스트림으로 합침
        pb.redirectErrorStream(true); 

        Process p = pb.start();

        // 이제 하나의 InputStream으로 ffmpeg의 모든 출력(로그+에러)을 읽을 수 있음
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("FFMPEG LOG: " + line);
            }
        }

        // 프로세스 종료 대기 및 결과 확인
        int exitCode = p.waitFor();
        if (exitCode == 0) {
            System.out.println("썸네일 생성 성공");
        } else {
            System.out.println("ffmpeg 종료 코드: " + exitCode);
        }

        return thumbnailUrl;
    }

    public String createBasicChannelImage(String userName){
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = img.createGraphics();    // Graphics2D를 얻어와 그림을 그린다

        Color[] colorList = {Color.magenta, Color.blue, Color.cyan, Color.green, Color.yellow,
                Color.red, Color.orange, Color.pink};

        Integer colorIdx = (int)(Math.random() * 7);

        graphics.setColor(colorList[colorIdx]);                       // 색상을 지정한다(파란색)
        graphics.fillRect(0,0,200, 200);              // 사각형을 하나 그린다

        graphics.setFont(new Font("나눔고딕", Font.BOLD, 80));
        graphics.setColor(Color.white);

        FontMetrics metrics = graphics.getFontMetrics(new Font("나눔고딕", Font.BOLD, 80));

        String text = userName;
        int x = 0;
        int y = 0;

        if(userName.length() > 1){
            text = userName.substring(0, 2);
        }

        x = (200 - metrics.stringWidth(text)) / 2;
        y = ((200 - metrics.getHeight()) / 2) + metrics.getAscent();
        graphics.drawString(text, x, y); //설정한 위치에 따른 텍스트를 그림

        try{

            UUID uuid = UUID.randomUUID();
            String fileUrl = "/channel/" + uuid + ".png";
            File imgfile = new File(tempPath + fileUrl);        // 파일의 이름을 설정한다
            ImageIO.write(img, "png", imgfile);                     // write메소드를 이용해 파일을 만든다


            // aws s3에 저장
            // InputStream imgStream = new FileInputStream(imgfile);
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setContentType("image/png");
            // metadata.setContentLength(imgfile.length());
            // amazonS3Client.putObject(bucket, uploadKey, imgStream, metadata);

            // String fileUrl = amazonS3Client.getUrl(bucket, uploadKey).toString();

            // imgfile.delete();

            return fileUrl;
        }
        catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String channelImageUpload(MultipartFile file, String origin) {
        try {
            if(!origin.equals("")) {
                String originKey = origin.substring(origin.lastIndexOf("/") + 1);

                // amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, "channel/" + originKey));
                File imgfile = new File(tempPath + origin);
                imgfile.delete();
            }

            UUID uuid = UUID.randomUUID();
            String fileUrl = "/channel/" + uuid + ".png";
            File uploadPath = new File(tempPath + fileUrl);
            file.transferTo(uploadPath);

            // aws s3에 저장
            // String fileUrl= "https://" + bucket + ".s3." + region + ".amazonaws.com/" + uploadKey;

            // ObjectMetadata metadata= new ObjectMetadata();
            // metadata.setContentType(file.getContentType());
            // metadata.setContentLength(file.getSize());
            // amazonS3Client.putObject(bucket, uploadKey, file.getInputStream(), metadata);

            return fileUrl;
        }  catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public SseEmitter sseConnect(String sseId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L * 10); // 10분 타임아웃
        emitters.put(sseId, emitter);

        emitter.onCompletion(() -> emitters.remove(sseId));
        emitter.onTimeout(() -> emitters.remove(sseId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect") // 이벤트 이름
                    .data("connected!")); // 데이터 내용
        } catch (IOException e) {
            emitters.remove(sseId);
        }
        return emitter;
    }

    private void sendToClient(String sseId, Object data) {
        SseEmitter emitter = emitters.get(sseId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("progress").data(data));
            } catch (IOException e) {
                emitters.remove(sseId);
            }
        }
    }

    public String saveAsAv1(String inputPath, String sseId) {
        try {

            UUID uuid = UUID.randomUUID();
            String extension = inputPath.substring(inputPath.lastIndexOf(".") + 1);
            String outputPath = "/video/" + uuid + "." + extension;

            // 설치된 FFmpeg 경로 지정
            FFmpeg ffmpeg = new FFmpeg("C:/ffmpeg/bin/ffmpeg"); 
            FFprobe ffprobe = new FFprobe("C:/ffmpeg/bin/ffprobe");

            double durationNs = ffprobe.probe(tempPath + inputPath).format.duration * 1_000_000_000.0;

            FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(tempPath + inputPath)
                .overrideOutputFiles(true)
                .addOutput(tempPath + outputPath)
                    .setVideoCodec("libsvtav1") 
                    .setVideoResolution(1920, 1080)
                    .setVideoFrameRate(30, 1)
                    // 키프레임 간격 설정 (GOP): 탐색 속도 개선
                    // 30fps 기준 60으로 설정하면 약 2초마다 이동 지점 생성
                    .addExtraArgs("-g", "60") 
                    
                    // FastStart 설정: 웹 스트리밍 시 즉시 탐색 가능하게 함
                    .addExtraArgs("-movflags", "+faststart")

                    .addExtraArgs("-preset", "10") 
                    .addExtraArgs("-crf", "35")   
                    // SVT-AV1 전용 파라미터로 키프레임 거리 강제 (선택 사항)
                    .addExtraArgs("-svtav1-params", "keyint=60")
                .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
            executor.createJob(builder, progress -> {
                double percentage = Math.min(100.0, (progress.out_time_ns / durationNs) * 100);

                // SSE로 데이터 전송
                sendToClient(sseId, Map.of("percent", Math.round(percentage), "status", "ENCODING"));
                
            }).run();

            sendToClient(sseId, Map.of("percent", 100, "status", "COMPLETE"));

            //인코딩 하기전 동영상 삭제
            Path filePath = Paths.get(tempPath + inputPath);
            Files.delete(filePath);
            System.out.println("원본 파일 삭제 성공");

            return outputPath;
        } catch (Exception e) {
            throw new RuntimeException("비디오 인코딩 실패", e);
        }
    }
}

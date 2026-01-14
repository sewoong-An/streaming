package com.sewoong.streaming.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

@RestController
public class MainController {

    @Value("${file.path}")
    private String tempPath;

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello, world!";
    }

    @GetMapping("/healthCheck")
    public ResponseEntity healthCheck() {
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @GetMapping(path = "/video/{filename}")
    public ResponseEntity<ResourceRegion> getVideo(@PathVariable("filename")String filename, @RequestHeader HttpHeaders headers) throws IOException{

        // 파일 경로 (본인의 경로에 맞게 수정)
        Resource video = new FileSystemResource(tempPath + "/video/" + filename);
        
        long contentLength = video.contentLength();
        
        // 브라우저의 Range 요청 파싱
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(1024 * 1024, end - start + 1); // 1MB 단위로 쪼개기
            ResourceRegion region = new ResourceRegion(video, start, rangeLength);
            
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT) // 206 응답
                    .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.parseMediaType("video/mp4")))
                    .body(region);
        } else {
            // 초기 요청 시 메타데이터를 위해 처음 일부만 전송
            long rangeLength = Math.min(1024 * 1024, contentLength);
            ResourceRegion region = new ResourceRegion(video, 0, rangeLength);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body(region);
        }
    }

    @GetMapping("/thumbnail/{filename}")
    public ResponseEntity<byte[]> getThumbnailImage(
                        @PathVariable("filename")String filename) {
        
        //파일이 저장된 경로
        String imagePath = tempPath+"/thumbnail/"+filename;
        File file = new File(imagePath);
        
        //저장된 이미지파일의 이진데이터 형식을 구함
        byte[] result=null;//1. data
        ResponseEntity<byte[]> entity=null;
        
        try {
            result = FileCopyUtils.copyToByteArray(file);
            
            //2. header
            HttpHeaders header = new HttpHeaders();
            header.add("Content-type",Files.probeContentType(file.toPath())); //파일의 컨텐츠타입을 직접 구해서 header에 저장
                
            //3. 응답본문
            entity = new ResponseEntity<>(result,header,HttpStatus.OK);//데이터, 헤더, 상태값
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return entity;
    }

    @GetMapping("/channel/{filename}")
    public ResponseEntity<byte[]> getChannelImage(
                        @PathVariable("filename")String filename) {
        
        //파일이 저장된 경로
        String imagePath = tempPath+"/channel/"+filename;
        File file = new File(imagePath);
        
        //저장된 이미지파일의 이진데이터 형식을 구함
        byte[] result=null;//1. data
        ResponseEntity<byte[]> entity=null;
        
        try {
            result = FileCopyUtils.copyToByteArray(file);
            
            //2. header
            HttpHeaders header = new HttpHeaders();
            header.add("Content-type",Files.probeContentType(file.toPath())); //파일의 컨텐츠타입을 직접 구해서 header에 저장
                
            //3. 응답본문
            entity = new ResponseEntity<>(result,header,HttpStatus.OK);//데이터, 헤더, 상태값
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return entity;
    }

}

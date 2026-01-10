package com.sewoong.streaming.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
    public ResponseEntity<StreamingResponseBody> getVideo(@PathVariable("filename")String filename) {
        File file = new File(tempPath + "/video/" + filename);
        if (!file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody streamingResponseBody = outputStream -> FileCopyUtils.copy(new FileInputStream(file), outputStream);

        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "video/mp4");
        responseHeaders.add("Content-Length", Long.toString(file.length()));

        return ResponseEntity.ok().headers(responseHeaders).body(streamingResponseBody);
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

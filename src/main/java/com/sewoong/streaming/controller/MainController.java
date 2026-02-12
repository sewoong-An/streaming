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

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;

@Slf4j
@RestController
public class MainController {

    @Value("${file.path}")
    private String tempPath;

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello, world!";
    }

    @GetMapping("/healthCheck")
    public ResponseEntity<Void> healthCheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/video/{filename}")
    public ResponseEntity<ResourceRegion> getVideo(@PathVariable("filename") String filename,
            @RequestHeader HttpHeaders headers) throws IOException {

        // 파일 경로 설정
        Resource video = new FileSystemResource(tempPath + "/video/" + filename);
        long contentLength = video.contentLength();

        // 브라우저의 Range 요청 파싱
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            // 1MB 단위로 쪼개어 전송하여 네트워크 부하 감소 및 빠른 재생 유도
            long rangeLength = Math.min(1024 * 1024, end - start + 1);
            ResourceRegion region = new ResourceRegion(video, start, rangeLength);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT) // 206 Partial Content
                    .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.parseMediaType("video/mp4")))
                    .body(region);
        } else {
            // 초기 요청 시 메타데이터를 위해 처음 1MB만 전송
            long rangeLength = Math.min(1024 * 1024, contentLength);
            ResourceRegion region = new ResourceRegion(video, 0, rangeLength);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body(region);
        }
    }

    @GetMapping("/thumbnail/{filename}")
    public ResponseEntity<byte[]> getThumbnailImage(@PathVariable("filename") String filename) {

        String imagePath = tempPath + "/thumbnail/" + filename;
        File file = new File(imagePath);

        byte[] result = null;
        ResponseEntity<byte[]> entity = null;

        try {
            result = FileCopyUtils.copyToByteArray(file);

            HttpHeaders header = new HttpHeaders();
            // 파일의 실제 컨텐츠 타입을 조사하여 헤더에 추가
            header.add("Content-type", Files.probeContentType(file.toPath()));

            entity = new ResponseEntity<>(result, header, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Thumbnail fetch error: ", e);
            entity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return entity;
    }

    @GetMapping("/channel/{filename}")
    public ResponseEntity<byte[]> getChannelImage(@PathVariable("filename") String filename) {

        String imagePath = tempPath + "/channel/" + filename;
        File file = new File(imagePath);

        byte[] result = null;
        ResponseEntity<byte[]> entity = null;

        try {
            result = FileCopyUtils.copyToByteArray(file);

            HttpHeaders header = new HttpHeaders();
            header.add("Content-type", Files.probeContentType(file.toPath()));

            entity = new ResponseEntity<>(result, header, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Channel image fetch error: ", e);
            entity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return entity;
    }
}
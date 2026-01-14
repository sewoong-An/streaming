package com.sewoong.streaming.controller;

import com.sewoong.streaming.domain.Subscribe;

import com.sewoong.streaming.dto.VideoDto;
import com.sewoong.streaming.service.FileService;
import com.sewoong.streaming.service.SubscribeService;
import com.sewoong.streaming.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoController {
    @Autowired
    private final VideoService videoService;
    @Autowired
    private final FileService fileService;
    @Autowired
    private final SubscribeService subscribeService;

    @Value("${file.ffprobe.path}")
    private String ffprobePath;

    @Value("${file.path}")
    private String tempPath;

    @GetMapping(value = "/sseConnect/{sseId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseConnect(@PathVariable String sseId) {
        return fileService.sseConnect(sseId);
    }


    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam("file")MultipartFile file, @RequestParam("sseId") String sseId){
        JSONObject resJobj = new JSONObject();

        try {
            String originVideoPath = fileService.videoUpload(file);

            if (originVideoPath.equals("")) {
                resJobj.put("status", "ERROR");
                return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
            }

            String videoPath = fileService.saveAsAv1(originVideoPath, sseId);

            String thumbnailPath = fileService.initialThumbnail(videoPath);

            if (thumbnailPath.equals("")) {
                resJobj.put("status", "ERROR");
                return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
            }

            FFprobe ffprobe = new FFprobe(ffprobePath);  //리눅스에 설치되어 있는 ffmpeg 폴더
            FFmpegProbeResult probeResult = ffprobe.probe(tempPath + videoPath);
            FFmpegFormat format = probeResult.getFormat();
            Integer runningTime = (int) format.duration;

            Integer videoId = videoService.createVideo(videoPath, thumbnailPath, runningTime);
            resJobj.put("status", "SUCCESS");
            resJobj.put("videoId", videoId);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/uploadThumbnail")
    public ResponseEntity uploadThumbnail(@RequestParam("file")MultipartFile file, @RequestParam("origin")String origin){
        JSONObject resJobj = new JSONObject();

        try {
            String thumbnailUrl = fileService.thumbnailUpload(file, origin);
            if (thumbnailUrl.equals("")) {
                resJobj.put("status", "ERROR");
                return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
            }
            resJobj.put("status", "SUCCESS");
            resJobj.put("thumbnailUrl", thumbnailUrl);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getMyVideos")
    public ResponseEntity getMyVideos(){
        JSONObject resJobj = new JSONObject();
        try{
            List<VideoDto> videoList = videoService.getMyVideos();

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", videoList);

            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getVideos")
    public ResponseEntity getVideos(@RequestParam @Nullable String searchQuery){
        JSONObject resJobj = new JSONObject();
        try{
            List<VideoDto> videoList = videoService.getVideos(searchQuery);

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", videoList);

            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getChannelVideos/{handle}")
    public ResponseEntity getChannelVideos(@PathVariable String handle){
        JSONObject resJobj = new JSONObject();
        try{
            List<VideoDto> videoList = videoService.getChannelVideos(handle);

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", videoList);

            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getVideoInfo/{id}")
    public ResponseEntity getVideoInfo(@PathVariable String id){
        JSONObject resJobj = new JSONObject();
        try{
            VideoDto video = videoService.getVideoInfo(Integer.parseInt(id));

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", video);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch(Exception e) {
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updateVideo/{id}")
    public ResponseEntity updateVideo(@PathVariable String id, @RequestBody Map<String, Object> data){
        JSONObject resJobj = new JSONObject();
        try{
            videoService.updateVideo(Integer.parseInt(id), data);

            resJobj.put("status", "SUCCESS");
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch(Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/increaseViews/{id}")
    public ResponseEntity increaseViews(@PathVariable String id){
        JSONObject resJobj = new JSONObject();
        try{
            videoService.increaseViews(Integer.parseInt(id));

            resJobj.put("status", "SUCCESS");
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch(Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getSubscribeVideos")
    public ResponseEntity getSubscribeVideos(){
        JSONObject resJobj = new JSONObject();
        try{
            List<Subscribe> subscribes = subscribeService.getSubscribeList();

            List<VideoDto> videoList = videoService.getSubscribeVideos(subscribes);

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", videoList);

            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteVideos")
    public ResponseEntity deleteVideos(@RequestBody Map<String, Object> data){
        JSONObject resJobj = new JSONObject();
        try {
            List<Integer> ids = ((List<String>) data.get("videoIds")).stream()
                    .map(id -> Integer.parseInt(id))
                    .collect(Collectors.toList());

            videoService.deleteVideos(ids);

            resJobj.put("status", "SUCCESS");

            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }
}

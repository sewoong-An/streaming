package com.sewoong.streaming.controller;

import com.sewoong.streaming.domain.Subscribe;
import com.sewoong.streaming.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscribe")
public class SubscribeController {
    public final SubscribeService subscribeService;

    @PostMapping("/createSubscribe/{subChannelCode}")
    public ResponseEntity createSubscribe(@PathVariable String subChannelCode){
        JSONObject resJobj = new JSONObject();
        try {
            subscribeService.createSubscribe(Integer.parseInt(subChannelCode));
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping ("/deleteSubscribe/{subChannelCode}")
    public ResponseEntity deleteSubscribe(@PathVariable String subChannelCode){
        JSONObject resJobj = new JSONObject();
        try {
            subscribeService.deleteSubscribe(Integer.parseInt(subChannelCode));
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getSubscribeCount/{subChannelCode}")
    public ResponseEntity getSubscribeCount(@PathVariable String subChannelCode){
        JSONObject resJobj = new JSONObject();
        try {
            Integer count = subscribeService.getSubscribeCount(Integer.parseInt(subChannelCode));
            resJobj.put("status", "SUCCESS");
            resJobj.put("count", count);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getSubscribeList")
    public ResponseEntity getSubscribeList(){
        JSONObject resJobj = new JSONObject();
        try {
            List<Subscribe> subList = subscribeService.getSubscribeList();
            JSONArray subJarr = new JSONArray();

            for(Subscribe sub : subList){
                JSONObject subJobj = new JSONObject();

                subJobj.put("channelCode", sub.getSubscribePK().getSubChannelCode());
                subJobj.put("channelHandle", sub.getChannel().getHandle());
                subJobj.put("channelName", sub.getChannel().getName());
                subJobj.put("channelImage", sub.getChannel().getImageUrl());

                subJarr.add(subJobj);
            }

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", subJarr);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkSubscribe/{subChannelCode}")
    public ResponseEntity checkSubscribe(@PathVariable String subChannelCode){
        JSONObject resJobj = new JSONObject();
        try {
            Boolean isSubscribe = subscribeService.checkSubscribe(Integer.parseInt(subChannelCode));
            resJobj.put("status", "SUCCESS");
            resJobj.put("isSubscribe", isSubscribe);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }
}

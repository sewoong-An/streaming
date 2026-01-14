package com.sewoong.streaming.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/sse")
public class SSEController {

    private static Map<String, SseEmitter> emitters = new HashMap<>();

    @GetMapping("/connect")
    public SseEmitter connect() throws IOException, InterruptedException {
        SseEmitter emitter = new SseEmitter(-1L);
        String sseKey = UUID.randomUUID().toString();
        emitters.put(sseKey, emitter);
        emitter.send(SseEmitter.event().name("key").data(sseKey));

        return emitter;
    }

    @GetMapping("/sseTest")
    public SseEmitter sseTest(@RequestParam String key) throws IOException, InterruptedException {
        SseEmitter emitter = getEmitter(key);

        for(int i = 1; i <= 100; i++){
            emitter.send(SseEmitter.event().name("progress").data(i));
            Thread.sleep(100);
        }

        deleteEmitter(key);

        return emitter;
    }

    public static SseEmitter getEmitter(String key){
        return emitters.get(key);
    }

    public static void deleteEmitter(String key){
        SseEmitter emitter = emitters.get(key);
        emitter.complete();
        emitters.remove(key);
    }

}

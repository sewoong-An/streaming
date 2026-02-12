package com.sewoong.streaming.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/sse")
public class SSEController {

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/connect")
    public SseEmitter connect() throws IOException {
        // -1L은 타임아웃을 무제한으로 설정합니다.
        SseEmitter emitter = new SseEmitter(-1L);
        String sseKey = UUID.randomUUID().toString();

        emitters.put(sseKey, emitter);

        // 연결 종료 혹은 타임아웃 시 맵에서 제거하도록 설정 (메모리 누수 방지)
        emitter.onCompletion(() -> emitters.remove(sseKey));
        emitter.onTimeout(() -> emitters.remove(sseKey));
        emitter.onError((e) -> emitters.remove(sseKey));

        // 최초 연결 시 클라이언트에게 고유 키 전달
        emitter.send(SseEmitter.event().name("key").data(sseKey));

        return emitter;
    }

    @GetMapping("/sseTest")
    public void sseTest(@RequestParam("key") String key) {
        SseEmitter emitter = getEmitter(key);

        if (emitter == null) {
            log.warn("No emitter found for key: {}", key);
            return;
        }

        new Thread(() -> {
            try {
                for (int i = 1; i <= 100; i++) {
                    emitter.send(SseEmitter.event().name("progress").data(i));
                    Thread.sleep(100);
                }
                deleteEmitter(key);
            } catch (Exception e) {
                log.error("SSE transfer error for key: {}", key, e);
                deleteEmitter(key);
            }
        }).start();
    }

    public static SseEmitter getEmitter(String key) {
        return emitters.get(key);
    }

    public static void deleteEmitter(String key) {
        SseEmitter emitter = emitters.get(key);
        if (emitter != null) {
            emitter.complete();
            emitters.remove(key);
        }
    }
}
package com.sewoong.streaming.controller;

import com.sewoong.streaming.dto.HistoryDto;
import com.sewoong.streaming.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/saveHistory/{id}")
    public ResponseEntity<JSONObject> saveHistory(@PathVariable("id") String id) {
        JSONObject resJobj = new JSONObject();
        try {
            historyService.saveHistory(Integer.parseInt(id));

            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getHistories")
    public ResponseEntity<JSONObject> getHistories() {
        JSONObject resJobj = new JSONObject();
        try {
            List<HistoryDto> histories = historyService.getHistories();

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", histories);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteHistory/{id}")
    public ResponseEntity<JSONObject> deleteHistory(@PathVariable("id") String id) {
        JSONObject resJobj = new JSONObject();
        try {
            historyService.deleteHistory(Integer.parseInt(id));

            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }
}
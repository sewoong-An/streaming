package com.sewoong.streaming.controller;

import com.sewoong.streaming.dto.PlaylistDto;
import com.sewoong.streaming.dto.VideoDto;
import com.sewoong.streaming.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/addPlaylist")
    public ResponseEntity<JSONObject> addPlaylist(@RequestBody Map<String, Object> data) {
        JSONObject resJobj = new JSONObject();
        try {
            playlistService.addPlaylist(data);
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getPlaylist")
    public ResponseEntity<JSONObject> getPlaylist() {
        JSONObject resJobj = new JSONObject();
        try {
            List<PlaylistDto> data = playlistService.getPlaylist();
            resJobj.put("status", "SUCCESS");
            resJobj.put("data", data);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getPlaylistByHandle/{handle}")
    public ResponseEntity<JSONObject> getPlaylistByHandle(@PathVariable("handle") String handle) {
        JSONObject resJobj = new JSONObject();
        try {
            List<PlaylistDto> data = playlistService.getPlaylistByHandle(handle);
            resJobj.put("status", "SUCCESS");
            resJobj.put("data", data);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getPlaylistInfo/{id}")
    public ResponseEntity<JSONObject> getPlaylistInfo(@PathVariable("id") String id) {
        JSONObject resJobj = new JSONObject();
        try {
            PlaylistDto data = playlistService.getPlaylistInfo(Integer.parseInt(id));
            resJobj.put("status", "SUCCESS");
            resJobj.put("data", data);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updatePlaylist/{id}")
    public ResponseEntity<JSONObject> updatePlaylist(@PathVariable("id") String id,
            @RequestBody Map<String, Object> data) {
        JSONObject resJobj = new JSONObject();
        try {
            playlistService.updatePlaylist(Integer.parseInt(id), data);
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addPlaylistVideo")
    public ResponseEntity<JSONObject> addPlaylistVideo(@RequestBody Map<String, Object> data) {
        JSONObject resJobj = new JSONObject();
        try {
            playlistService.addPlaylistVideo(data);
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getPlaylistVideos/{id}")
    public ResponseEntity<JSONObject> getPlaylistVideos(@PathVariable("id") String id) {
        JSONObject resJobj = new JSONObject();
        try {
            List<VideoDto> data = playlistService.getPlaylistVideos(Integer.parseInt(id));
            resJobj.put("status", "SUCCESS");
            resJobj.put("data", data);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{playlistId}/delete/{videoId}")
    public ResponseEntity<JSONObject> deletePlaylistVideo(
            @PathVariable("playlistId") String playlistId,
            @PathVariable("videoId") String videoId) {
        JSONObject resJobj = new JSONObject();
        try {
            playlistService.deletePlaylistVideo(Integer.parseInt(playlistId), Integer.parseInt(videoId));
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deletePlaylists")
    public ResponseEntity<JSONObject> deletePlaylists(@RequestBody Map<String, Object> data) {
        JSONObject resJobj = new JSONObject();
        try {
            @SuppressWarnings("unchecked")
            List<String> rawIds = (List<String>) data.get("playlistIds");
            List<Integer> ids = rawIds.stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            playlistService.deletePlaylists(ids);
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }
}
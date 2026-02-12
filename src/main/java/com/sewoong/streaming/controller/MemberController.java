package com.sewoong.streaming.controller;

import com.sewoong.streaming.dto.ChannelDto;
import com.sewoong.streaming.dto.MemberLoginRequestDto;
import com.sewoong.streaming.security.TokenInfo;
import com.sewoong.streaming.service.FileService;
import com.sewoong.streaming.service.MemberService;
import com.sewoong.streaming.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    @Value("${public.domain}")
    private String domain;

    private final MemberService memberService;
    private final SubscribeService subscribeService;
    private final FileService fileService;

    @PostMapping("/login")
    public ResponseEntity<JSONObject> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto,
            HttpServletResponse response) {
        JSONObject resJobj = new JSONObject();
        try {
            String memberId = memberLoginRequestDto.getMemberId();
            String password = memberLoginRequestDto.getPassword();
            TokenInfo tokenInfo = memberService.getToken(memberId, password);

            createCookie("refreshToken", tokenInfo.getRefreshToken(), response);
            resJobj.put("status", "SUCCESS");
            resJobj.put("tokenInfo", tokenInfo);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JSONObject> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resJobj = new JSONObject();
        try {
            String refreshToken = getCookie("refreshToken", request);
            TokenInfo tokenInfo = memberService.refreshToken(refreshToken);

            createCookie("refreshToken", tokenInfo.getRefreshToken(), response);
            resJobj.put("status", "SUCCESS"); // 기존 코드의 ERROR 오타 수정
            resJobj.put("tokenInfo", tokenInfo);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody Map<String, Object> data, UriComponentsBuilder uriBuilder) {
        JSONObject resJobj = new JSONObject();
        try {
            String imageUrl = fileService.createBasicChannelImage((String) data.get("name"));
            memberService.join(data, imageUrl);
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("error_massage", e.getMessage());
            return new ResponseEntity<>(resJobj.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updateChannelInfo")
    public ResponseEntity<String> updateChannelInfo(@RequestBody Map<String, Object> data) {
        JSONObject resJobj = new JSONObject();
        try {
            ChannelDto info = memberService.updateMember(data);
            resJobj.put("status", "SUCCESS");
            resJobj.put("data", info);
            return new ResponseEntity<>(resJobj.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("error_massage", e.getMessage());
            return new ResponseEntity<>(resJobj.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/uploadChannelImage")
    public ResponseEntity<JSONObject> uploadChannelImage(@RequestParam("file") MultipartFile file,
            @RequestParam("origin") String origin) {
        JSONObject resJobj = new JSONObject();
        try {
            String ImageUrl = fileService.channelImageUpload(file, origin);
            if (ImageUrl.equals("")) {
                resJobj.put("status", "ERROR");
                return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
            }
            resJobj.put("channelImageUrl", ImageUrl);
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<JSONObject> getUserInfo() {
        JSONObject resJobj = new JSONObject();
        try {
            ChannelDto info = memberService.getMyChannelInfo();
            resJobj.put("status", "SUCCESS");
            resJobj.put("data", info);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getChannelInfoByCode/{code}")
    public ResponseEntity<JSONObject> getChannelInfoByCode(@PathVariable("code") String code) {
        JSONObject resJobj = new JSONObject();
        try {
            ChannelDto info = memberService.getChannelInfoByCode(Integer.parseInt(code));
            info.setChannelSubscribeCount(subscribeService.getSubscribeCount(info.getChannelCode()));
            info.setIsSubscribe(subscribeService.checkSubscribe(info.getChannelCode()));

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", info);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getChannelInfoByHandle/{handle}")
    public ResponseEntity<JSONObject> getChannelInfoByHandle(@PathVariable("handle") String handle) {
        JSONObject resJobj = new JSONObject();
        try {
            ChannelDto info = memberService.getChannelInfoByHandle(handle);
            info.setChannelSubscribeCount(subscribeService.getSubscribeCount(info.getChannelCode()));
            info.setIsSubscribe(subscribeService.checkSubscribe(info.getChannelCode()));

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", info);
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkName")
    public ResponseEntity<JSONObject> checkName(@RequestParam("name") String name) {
        JSONObject resJobj = new JSONObject();
        try {
            if (memberService.checkName(name)) {
                resJobj.put("status", "ERROR");
                resJobj.put("message", "사용중인 채널명입니다.");
            } else {
                resJobj.put("status", "SUCCESS");
            }
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkHandle")
    public ResponseEntity<JSONObject> checkHandle(@RequestParam("handle") String handle) {
        JSONObject resJobj = new JSONObject();
        try {
            if (memberService.checkHandle(handle)) {
                resJobj.put("status", "ERROR");
                resJobj.put("message", "사용중인 핸들입니다.");
            } else {
                resJobj.put("status", "SUCCESS");
            }
            return new ResponseEntity<>(resJobj, HttpStatus.OK);
        } catch (Exception e) {
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity<>(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void createCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String getCookie(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(cookieName)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
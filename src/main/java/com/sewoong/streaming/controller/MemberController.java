package com.sewoong.streaming.controller;

import com.sewoong.streaming.domain.Member;
import com.sewoong.streaming.domain.Video;
import com.sewoong.streaming.dto.ChannelDto;
import com.sewoong.streaming.dto.MemberLoginRequestDto;
import com.sewoong.streaming.security.TokenInfo;
import com.sewoong.streaming.service.MemberService;
import com.sewoong.streaming.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    @Value("${public.domain}")
    private String domain;
    @Autowired
    private final MemberService memberService;

    @Autowired
    private final SubscribeService subscribeService;

    @Autowired
    private final FileController fileController;
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody MemberLoginRequestDto memberLoginRequestDto, HttpServletResponse response) {
        JSONObject resJobj = new JSONObject();
        try {
            String memberId = memberLoginRequestDto.getMemberId();
            String password = memberLoginRequestDto.getPassword();
            TokenInfo tokenInfo = memberService.getToken(memberId, password);

            createCookie("refreshToken", tokenInfo.getRefreshToken(), response);
            resJobj.put("status", "SUCCESS");
            resJobj.put("tokenInfo", tokenInfo);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){

            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resJobj = new JSONObject();
        try {
            String refreshToken = getCookie("refreshToken", request);
            TokenInfo tokenInfo = memberService.refreshToken(refreshToken);

            createCookie("refreshToken", tokenInfo.getRefreshToken(), response);
            resJobj.put("status", "ERROR");
            resJobj.put("tokenInfo", tokenInfo);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/join")
    public ResponseEntity join(@RequestBody Map<String, Object> data, UriComponentsBuilder uriBuilder) {

        JSONObject resJobj = new JSONObject();
        try {
            String imageUrl = fileController.createBasicChannelImage((String) data.get("name"));

            Integer memberCode = memberService.join(data, imageUrl);
            resJobj.put("status", "SUCCESS");
            return new ResponseEntity(resJobj, HttpStatus.OK);
        } catch (Exception e) {

            resJobj.put("status", "ERROR");
            resJobj.put("error_massage", e.getMessage());
            return new ResponseEntity(resJobj.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updateChannelInfo")
    public ResponseEntity updateChannelInfo(@RequestBody Map<String, Object> data) {

        JSONObject resJobj = new JSONObject();
        try {
            ChannelDto info = memberService.updateMember(data);

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", info);
            return new ResponseEntity(resJobj.toJSONString(), HttpStatus.OK);
        } catch (Exception e) {

            resJobj.put("status", "ERROR");
            resJobj.put("error_massage", e.getMessage());
            return new ResponseEntity(resJobj.toJSONString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/uploadChannelImage")
    public ResponseEntity uploadChannelImage(@RequestParam("file") MultipartFile file, @RequestParam("origin")String origin){
        JSONObject resJobj = new JSONObject();

        try {
            String ImageUrl = fileController.channelImageUpload(file, origin);
            if (ImageUrl.equals("")) {
                resJobj.put("status", "ERROR");
                return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
            }
            resJobj.put("channelImageUrl", ImageUrl);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch (Exception e){
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity getUserInfo(){
        JSONObject resJobj = new JSONObject();
        try{
            ChannelDto info = memberService.getUserInfo();
            resJobj.put("status", "SUCCESS");
            resJobj.put("data", info);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch(Exception e) {
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getChannelInfoByCode/{code}")
    public ResponseEntity getChannelInfoByCode(@PathVariable String code){
        JSONObject resJobj = new JSONObject();
        try{
            ChannelDto info = memberService.getChannelInfoByCode(Integer.parseInt(code));
            info.setChannelSubscribeCount(subscribeService.getSubscribeCount(info.getChannelCode()));
            info.setIsSubscribe(subscribeService.checkSubscribe(info.getChannelCode()));

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", info);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch(Exception e) {
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getChannelInfoByHandle/{handle}")
    public ResponseEntity getChannelInfoByHandle(@PathVariable String handle){
        JSONObject resJobj = new JSONObject();
        try{
            ChannelDto info = memberService.getMemberInfoByHandle(handle);
            info.setChannelSubscribeCount(subscribeService.getSubscribeCount(info.getChannelCode()));
            info.setIsSubscribe(subscribeService.checkSubscribe(info.getChannelCode()));

            resJobj.put("status", "SUCCESS");
            resJobj.put("data", info);
            return new ResponseEntity(resJobj, HttpStatus.OK);
        }
        catch(Exception e) {
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkName")
    public ResponseEntity checkName(@RequestParam String name){
        JSONObject resJobj = new JSONObject();
        try{
            if(memberService.checkName(name)){
                resJobj.put("status", "ERROR");
                resJobj.put("message", "사용중인 채널명입니다.");
                return new ResponseEntity(resJobj, HttpStatus.OK);
            }
            else{
                resJobj.put("status", "SUCCESS");
                return new ResponseEntity(resJobj, HttpStatus.OK);
            }
        }
        catch(Exception e) {
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkHandle")
    public ResponseEntity checkHandle(@RequestParam String handle){
        JSONObject resJobj = new JSONObject();
        try{
            if(memberService.checkHandle(handle)){
                resJobj.put("status", "ERROR");
                resJobj.put("message", "사용중인 핸들입니다.");
                return new ResponseEntity(resJobj, HttpStatus.OK);
            }
            else{
                resJobj.put("status", "SUCCESS");
                return new ResponseEntity(resJobj, HttpStatus.OK);
            }
        }
        catch(Exception e) {
            resJobj = new JSONObject();
            resJobj.put("status", "ERROR");
            resJobj.put("message", e.getMessage());
            return new ResponseEntity(resJobj, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void createCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);

//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // add cookie to response
        response.addCookie(cookie);
    }

    public String getCookie(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
        if(cookies != null){
            for (Cookie c : cookies) {
                String name = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기
                if (name.equals(cookieName)) {
                    return value;
                }
            }
        }
        return null;
    }
}

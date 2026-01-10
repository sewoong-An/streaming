package com.sewoong.streaming.service;

import com.sewoong.streaming.domain.Member;
import com.sewoong.streaming.domain.Subscribe;
import com.sewoong.streaming.domain.Video;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.dto.VideoDto;
import com.sewoong.streaming.repository.MemberRepository;
import com.sewoong.streaming.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class VideoService {

    private  final MemberRepository memberRepository;
    private  final VideoRepository videoRepository;

    public Integer createVideo(String videoUrl, String thumbnailUrl, Integer runningTime){
        Integer memberCode = getMyCode();

        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);

        Member memberInfo = memberRepository.findById(memberCode).get();

        Video videoInfo = Video.builder()
                .member(memberInfo)
                .state("private")
                .createdDt(formatter.format(today))
                .views(0)
                .runningtime(runningTime)
                .thumbnailUrl(thumbnailUrl)
                .videoUrl(videoUrl)
                .build();

        return videoRepository.save(videoInfo).getVideoId();
    }

    public List<VideoDto> getMyVideos(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        List<Video> videos = videoRepository.findAllByMember_memberCodeAndStateNotOrderByCreatedDtDesc(memberCode, "deleted");

        return videos.stream().map(v -> new VideoDto(v)).collect(Collectors.toList());
    }

    public List<VideoDto> getVideos(String searchQuery){

        List<Video> videos;

        if(searchQuery == null) {
            videos = videoRepository.findAllByStateOrderByCreatedDtDesc("public");
        }
        else{
            videos = videoRepository.findAllByStateAndTitleContainingIgnoreCaseOrderByCreatedDtDesc("public", searchQuery);
        }

        return videos.stream().map(v -> new VideoDto(v)).collect(Collectors.toList());
    }

    public List<VideoDto> getChannelVideos(String handle){
        List<Video> videos = videoRepository.findAllByMember_HandleAndStateOrderByCreatedDtDesc(handle, "public");
        return videos.stream().map(v -> new VideoDto(v)).collect(Collectors.toList());
    }

    public VideoDto getVideoInfo(Integer id) throws Exception {

        Optional<Video> info = videoRepository.findById(id);
        if(!info.isPresent()){
            throw new Exception("존재하지 않는 동영상 입니다.");
        }

        return new VideoDto(info.get());
    }

    public void updateVideo(Integer id, Map<String, Object> data) throws Exception{

        Video info = videoRepository.findById(id).get();

        info.setTitle((String) data.get("title"));
        info.setDescription((String) data.get("description"));
        info.setState((String) data.get("state"));

        String thumbnailUrl = (String) data.get("thumbnailUrl");
        if(thumbnailUrl != null && thumbnailUrl != "" && !thumbnailUrl.equals(info.getThumbnailUrl())){
            info.setThumbnailUrl(thumbnailUrl);
        }

        videoRepository.save(info);

    }

    public void increaseViews(Integer id){
        Video info = videoRepository.findById(id).get();
        info.setViews(info.getViews() + 1);
        videoRepository.save(info);
    }

    public List<VideoDto> getSubscribeVideos(List<Subscribe> subscribes){

        List<Integer> subChannelIds = subscribes.stream().map(s -> s.getSubscribePK().getSubChannelCode()).collect(Collectors.toList());

        List<Video> videos = videoRepository.findAllByMember_memberCodeInAndStateOrderByCreatedDtDesc(subChannelIds, "public");
        return videos.stream().map(v -> new VideoDto(v)).collect(Collectors.toList());
    }

    public void deleteVideos(List<Integer> ids) {
        List<Video> videos = videoRepository.findAllById(ids);
        videos.forEach(v -> v.setState("deleted"));
        videoRepository.saveAll(videos);
    }

    private Integer getMyCode(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        return userDetails.getMemberCode();
    }
}

package com.sewoong.streaming.service.impl;

import com.sewoong.streaming.domain.Member;
import com.sewoong.streaming.domain.Subscribe;
import com.sewoong.streaming.domain.Video;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.dto.VideoDto;
import com.sewoong.streaming.repository.MemberRepository;
import com.sewoong.streaming.repository.VideoRepository;
import com.sewoong.streaming.service.VideoService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService{

    private  final MemberRepository memberRepository;
    private  final VideoRepository videoRepository;

    /**
	 * 동영상 정보 저장
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
    public Integer insertVideo(String videoUrl, String thumbnailUrl, Integer runningTime){
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

    /**
	 * 동영상 리스트 조회(현재사용자)
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
    public List<VideoDto> getMyVideos(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        List<Video> videos = videoRepository.findAllByMember_memberCodeAndStateNotOrderByCreatedDtDesc(memberCode, "deleted");

        return videos.stream().map(v -> new VideoDto(v)).collect(Collectors.toList());
    }

    /**
	 * 동영상 리스트 조회(제목검색)
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
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

    /**
	 * 동영상 리스트 조회(handle로 조회)
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
    public List<VideoDto> getChannelVideos(String handle){
        List<Video> videos = videoRepository.findAllByMember_HandleAndStateOrderByCreatedDtDesc(handle, "public");
        return videos.stream().map(v -> new VideoDto(v)).collect(Collectors.toList());
    }

    /**
	 * 동영상 정보 조회
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
    public VideoDto getVideoInfo(Integer id) throws Exception {

        Optional<Video> info = videoRepository.findById(id);
        if(!info.isPresent()){
            throw new Exception("존재하지 않는 동영상 입니다.");
        }

        return new VideoDto(info.get());
    }

    /**
	 * 동영상 정보 수정
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
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

    /**
	 * 조회수 증가
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
    public void increaseViews(Integer id){
        Video info = videoRepository.findById(id).get();
        info.setViews(info.getViews() + 1);
        videoRepository.save(info);
    }

    /**
	 * 구독채널 동영상 조회
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
    public List<VideoDto> getSubscribeVideos(List<Subscribe> subscribes){

        List<Integer> subChannelIds = subscribes.stream().map(s -> s.getSubscribePK().getSubChannelCode()).collect(Collectors.toList());

        List<Video> videos = videoRepository.findAllByMember_memberCodeInAndStateOrderByCreatedDtDesc(subChannelIds, "public");
        return videos.stream().map(v -> new VideoDto(v)).collect(Collectors.toList());
    }

    /**
	 * 동영상 삭제
	 * @author ASW
	 * @date 2023.09.06.
	 */
    @Override
    public void deleteVideos(List<Integer> ids) {
        List<Video> videos = videoRepository.findAllById(ids);
        videos.forEach(v -> v.setState("deleted"));
        videoRepository.saveAll(videos);
    }

    /**
	 * 사용자 코드 조회
	 * @author ASW
	 * @date 2023.09.06.
	 */
    private Integer getMyCode(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        return userDetails.getMemberCode();
    }
}

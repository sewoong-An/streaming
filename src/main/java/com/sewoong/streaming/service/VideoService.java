package com.sewoong.streaming.service;

import java.util.List;
import java.util.Map;

import com.sewoong.streaming.domain.Subscribe;
import com.sewoong.streaming.dto.VideoDto;

public interface VideoService {

    /**
	 * 동영상 정보 저장
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public Integer insertVideo(String videoUrl, String thumbnailUrl, Integer runningTime);

    /**
	 * 동영상 리스트 조회(현재사용자)
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public List<VideoDto> getMyVideos();

    /**
	 * 동영상 리스트 조회(제목검색)
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public List<VideoDto> getVideos(String searchQuery);

    /**
	 * 동영상 리스트 조회(handle로 조회)
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public List<VideoDto> getChannelVideos(String handle);

    /**
	 * 동영상 정보 조회
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public VideoDto getVideoInfo(Integer id) throws Exception;

    /**
	 * 동영상 정보 수정
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public void updateVideo(Integer id, Map<String, Object> data) throws Exception;

    /**
	 * 조회수 증가
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public void increaseViews(Integer id);

    /**
	 * 구독채널 동영상 조회
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public List<VideoDto> getSubscribeVideos(List<Subscribe> subscribes);

    /**
	 * 동영상 삭제
	 * @author ASW
	 * @date 2023.09.06.
	 */
    public void deleteVideos(List<Integer> ids);
}

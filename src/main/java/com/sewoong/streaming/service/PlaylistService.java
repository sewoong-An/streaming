package com.sewoong.streaming.service;

import java.util.List;
import java.util.Map;

import com.sewoong.streaming.dto.PlaylistDto;
import com.sewoong.streaming.dto.VideoDto;

public interface PlaylistService {

    /**
	 * 재생목록 생성
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public void addPlaylist(Map<String, Object> data);

    /**
	 * 재생목록 리스트 조회(현재사용자)
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public List<PlaylistDto> getPlaylist();

    /**
	 * 재생목록 리스트 조회(handle로 조회)
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public List<PlaylistDto> getPlaylistByHandle(String handle);

    /**
	 * 재생목록 정보 조회
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public PlaylistDto getPlaylistInfo(Integer id) throws Exception;

    /**
	 * 재생목록 정보 수정
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public void updatePlaylist(Integer id, Map<String, Object> data) throws Exception;

    /**
	 * 재생목록 삭제
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public void deletePlaylists(List<Integer> ids);

    /**
	 * 재생목록에 동영상 추가
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public void addPlaylistVideo(Map<String, Object> data) throws Exception;

    /**
	 * 재생목록 동영상 조회
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public List<VideoDto> getPlaylistVideos(Integer id);

    /**
	 * 재생목록 동영상 삭제
	 * @author ASW
	 * @date 2023.09.12.
	 */
    public void deletePlaylistVideo(Integer playlistId, Integer videoId) throws Exception;
}

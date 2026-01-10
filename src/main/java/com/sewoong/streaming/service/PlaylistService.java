package com.sewoong.streaming.service;

import com.sewoong.streaming.domain.Playlist;
import com.sewoong.streaming.domain.PlaylistVideo;
import com.sewoong.streaming.domain.PlaylistVideoPK;
import com.sewoong.streaming.dto.PlaylistDto;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.dto.VideoDto;
import com.sewoong.streaming.repository.PlaylistRepository;
import com.sewoong.streaming.repository.PlaylistVideoRepository;
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
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistVideoRepository playlistVideoRepository;

    public void addPlaylist(Map<String, Object> data){

        Integer MemberCode = getMyCode();

        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);

        Playlist playlist = Playlist.builder()
                .memberCode(MemberCode)
                .title((String) data.get("title"))
                .state((String) data.get("state"))
                .createdDt(formatter.format(today))
                .updateDt(formatter.format(today))
                .build();

        playlistRepository.save(playlist);
    }

    @Transactional
    public List<PlaylistDto> getPlaylist(){
        List<Playlist> playlists = playlistRepository.findAllByMemberCodeAndStateNotOrderByCreatedDtDesc(getMyCode(), "deleted");
        return playlists.stream().map(p -> new PlaylistDto(p)).collect(Collectors.toList());
    }

    @Transactional
    public List<PlaylistDto> getPlaylistByHandle(String handle){
        List<Playlist> playlists = playlistRepository.findAllByMemberHandleAndStateOrderByUpdateDtDesc(handle, "public");
        return playlists.stream().map(p -> new PlaylistDto(p)).collect(Collectors.toList());
    }

    @Transactional
    public PlaylistDto getPlaylistInfo(Integer id) throws Exception{
        Optional<Playlist> info = playlistRepository.findById(id);

        if(!info.isPresent()){
            throw new Exception("존재하지 않는 재생목록 입니다.");
        }

        return new PlaylistDto(info.get());
    }

    @Transactional
    public void updatePlaylist(Integer id, Map<String, Object> data) throws Exception{

        Optional<Playlist> playlist = playlistRepository.findById(id);

        if(!playlist.isPresent()){
            throw new Exception("존재하지 않는 재생목록 입니다.");
        }

        playlist.get().setTitle((String) data.get("title"));
        playlist.get().setState((String) data.get("state"));

        playlistRepository.save(playlist.get());
    }

    @Transactional
    public void addPlaylistVideo(Map<String, Object> data) throws Exception{

        Optional<Playlist> playlist = playlistRepository.findById(Integer.parseInt((String) data.get("playlistId")));

        if(!playlist.isPresent()){
            throw new Exception("존재하지 않는 재생목록 입니다.");
        }

        PlaylistVideoPK pk = PlaylistVideoPK.builder()
                .playlistId(Integer.parseInt((String) data.get("playlistId")))
                .videoId(Integer.parseInt((String) data.get("videoId")))
                .build();

        if(playlistVideoRepository.findById(pk).isPresent()){
            return;
        }

        PlaylistVideo info = PlaylistVideo.builder()
                .playlistVideoPK(pk)
                .build();

        playlistVideoRepository.save(info);

        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);

        playlist.get().setUpdateDt(formatter.format(today));

        playlistRepository.save(playlist.get());
    }

    @Transactional
    public List<VideoDto> getPlaylistVideos(Integer id){
        List<PlaylistVideo> playlistVideos = playlistVideoRepository.findAllByPlaylistVideoPKPlaylistIdAndVideo_state(id, "public");
        return playlistVideos.stream().map(p -> new VideoDto(p.getVideo())).collect(Collectors.toList());
    }

    @Transactional
    public void deletePlaylistVideo(Integer playlistId, Integer videoId) throws Exception{

        Optional<Playlist> playlist = playlistRepository.findById(playlistId);

        if(!playlist.isPresent()){
            throw new Exception("존재하지 않는 재생목록 입니다.");
        }

        playlistVideoRepository.deleteById(PlaylistVideoPK.builder().playlistId(playlistId).videoId(videoId).build());
    }

    public void deletePlaylists(List<Integer> ids) {
        List<Playlist> playlists = playlistRepository.findAllById(ids);
        playlists.forEach(p -> p.setState("deleted"));
        playlistRepository.saveAll(playlists);
    }

    private Integer getMyCode(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        return userDetails.getMemberCode();
    }
}

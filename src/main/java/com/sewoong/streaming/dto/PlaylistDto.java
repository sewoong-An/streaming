package com.sewoong.streaming.dto;

import com.sewoong.streaming.domain.Playlist;
import lombok.Data;


@Data
public class PlaylistDto {

    private Integer playlistId;
    private ChannelDto channel;
    private String playlistTitle;
    private String playlistState;
    private String playlistCreatedDt;
    private String playlistUpdateDt;
    private Integer playlistVideoCount;
    private String playlistImageUrl;


    public PlaylistDto(Playlist playlist) {
        this.playlistId = playlist.getPlaylistId();
        this.channel = new ChannelDto(playlist.getMember());
        this.playlistTitle = playlist.getTitle();
        this.playlistState = playlist.getState();
        this.playlistCreatedDt = playlist.getCreatedDt();
        this.playlistUpdateDt = playlist.getUpdateDt();
        this.playlistVideoCount = playlist.getPlaylistVideos().size();
        this.playlistImageUrl = "";
        if(playlist.getPlaylistVideos() != null && playlist.getPlaylistVideos().size() > 0 ){
            this.playlistImageUrl =  playlist.getPlaylistVideos().get(0).getVideo().getThumbnailUrl();
        }

    }
}

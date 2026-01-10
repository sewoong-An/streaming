package com.sewoong.streaming.dto;

import com.sewoong.streaming.domain.Video;
import lombok.Data;
import lombok.Getter;

@Data
public class VideoDto {
    private Integer videoId;
    private ChannelDto channel;
    private String title;
    private String description;
    private String state;
    private Integer views;
    private Integer runningTime;
    private String createdDt;
    private String thumbnailUrl;
    private String videoUrl;

    public VideoDto(Video video){
        this.videoId = video.getVideoId();
        this.channel = new ChannelDto(video.getMember());
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.state = video.getState();
        this.views = video.getViews();
        this.runningTime = video.getRunningtime();
        this.createdDt = video.getCreatedDt();
        this.thumbnailUrl = video.getThumbnailUrl();
        this.videoUrl = video.getVideoUrl();
    }
}

package com.sewoong.streaming.dto;

import com.sewoong.streaming.domain.History;
import lombok.Data;

@Data
public class HistoryDto {
    private Integer videoId;
    private ChannelDto channel;
    private String title;
    private String description;
    private Integer views;

    private Integer runningTime;
    private String thumbnailUrl;
    private String createdDt;
    private String watchDt;

    public HistoryDto(History history){
        this.videoId = history.getVideo().getVideoId();
        this.channel = new ChannelDto(history.getVideo().getMember());
        this.title = history.getVideo().getTitle();
        this.description = history.getVideo().getDescription();
        this.views = history.getVideo().getViews();
        this.runningTime = history.getVideo().getRunningtime();
        this.thumbnailUrl = history.getVideo().getThumbnailUrl();
        this.createdDt = history.getVideo().getCreatedDt();
        this.watchDt = history.getWatchDt();
    }
}

package com.sewoong.streaming.dto;

import com.sewoong.streaming.domain.Member;
import lombok.Data;

@Data
public class ChannelDto {
    private Integer channelCode;
    private String channelHandle;
    private String channelName;
    private String channelImage;
    private Integer channelSubscribeCount;
    private Boolean isSubscribe;

    public ChannelDto(Member member){
        this.channelCode = member.getMemberCode();
        this.channelHandle = member.getHandle();
        this.channelName = member.getName();
        this.channelImage = member.getImageUrl();
    }
}

package com.sewoong.streaming.domain;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Builder
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoryPK implements Serializable {

    @Column(name = "member_code")
    private Integer memberCode;

    @Column(name = "video_id")
    private Integer videoId;
}


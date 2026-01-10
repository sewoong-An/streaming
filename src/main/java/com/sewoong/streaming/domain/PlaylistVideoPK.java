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
public class PlaylistVideoPK implements Serializable {

    @Column(name = "playlist_id")
    private Integer playlistId;

    @Column(name = "video_id")
    private Integer videoId;
}

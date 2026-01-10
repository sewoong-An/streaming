package com.sewoong.streaming.domain;

import lombok.*;

import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playlist")
public class Playlist {
    @Id
    @Column(name = "playlist_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer playlistId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="playlist_id", insertable = false, updatable = false)
    private List<PlaylistVideo> playlistVideos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_code", insertable = false, updatable = false)
    private Member member;

    @Column(name = "member_code",nullable = false)
    private Integer memberCode;

    @Column
    private String title;

    @Column
    private String state;

    @Column(name = "created_dt",nullable = false)
    private String createdDt;

    @Column(name = "update_dt",nullable = false)
    private String updateDt;

}

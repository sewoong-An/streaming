package com.sewoong.streaming.domain;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video")
public class Video {

    @Id
    @Column(name = "video_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer videoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="member_code")
    private Member member;

    @Column
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    private String state;

    @Column(name = "created_dt",nullable = false)
    private String createdDt;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    private Integer runningtime;

    @Column(name="thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name="video_url", nullable = false)
    private String videoUrl;


}

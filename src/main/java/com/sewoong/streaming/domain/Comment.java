package com.sewoong.streaming.domain;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentId;

    @Column(name = "video_id", nullable = false)
    private Integer videoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="member_code")
    private Member member;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_dt", nullable = false)
    private String createdDt;
}

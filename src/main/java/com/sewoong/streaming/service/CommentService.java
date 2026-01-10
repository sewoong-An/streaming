package com.sewoong.streaming.service;

import com.sewoong.streaming.domain.Comment;
import com.sewoong.streaming.domain.Member;
import com.sewoong.streaming.dto.CommentDto;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.repository.CommentRepository;
import com.sewoong.streaming.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public void addComment(Integer videoId, String content){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        Member member = memberRepository.findById(memberCode).get();

        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);

        Comment comment = Comment.builder()
                .videoId(videoId)
                .member(member)
                .content(content)
                .createdDt(formatter.format(today))
                .build();

        commentRepository.save(comment);
    }

    public List<CommentDto> getComments(Integer videoId){
        List<Comment> comments = commentRepository.findAllByVideoIdOrderByCreatedDtAsc(videoId);
        return comments.stream().map(c -> new CommentDto(c)).collect(Collectors.toList());
    }

    public void deleteComment(Integer commentId){
        commentRepository.deleteById(commentId);
    }
}

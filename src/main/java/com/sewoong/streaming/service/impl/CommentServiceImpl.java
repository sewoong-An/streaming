package com.sewoong.streaming.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sewoong.streaming.domain.Comment;
import com.sewoong.streaming.domain.Member;
import com.sewoong.streaming.dto.CommentDto;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.repository.CommentRepository;
import com.sewoong.streaming.repository.MemberRepository;
import com.sewoong.streaming.service.CommentService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    /**
	 * 댓글 작성
	 * @author ASW
	 * @date 2023.09.11.
	 */
    @Override
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

    /**
	 * 댓글 조회
	 * @author ASW
	 * @date 2023.09.11.
	 */
    @Override
    public List<CommentDto> getComments(Integer videoId){
        List<Comment> comments = commentRepository.findAllByVideoIdOrderByCreatedDtAsc(videoId);
        return comments.stream().map(c -> new CommentDto(c)).collect(Collectors.toList());
    }

    /**
	 * 댓글 삭제
	 * @author ASW
	 * @date 2023.09.11.
	 */
    @Override
    public void deleteComment(Integer commentId){
        commentRepository.deleteById(commentId);
    }
}

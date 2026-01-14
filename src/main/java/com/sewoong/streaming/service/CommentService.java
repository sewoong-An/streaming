package com.sewoong.streaming.service;

import java.util.List;
import com.sewoong.streaming.dto.CommentDto;

public interface CommentService {

    /**
	 * 댓글 작성
	 * @author ASW
	 * @date 2023.09.11.
	 */
    public void addComment(Integer videoId, String content);

    /**
	 * 댓글 조회
	 * @author ASW
	 * @date 2023.09.11.
	 */
    public List<CommentDto> getComments(Integer videoId);

    /**
	 * 댓글 삭제
	 * @author ASW
	 * @date 2023.09.11.
	 */
    public void deleteComment(Integer commentId);
}

package com.sewoong.streaming.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface FileService {
    
    /**
	 * 동영상 업로드
	 * @author ASW
	 * @date 2023.09.09.
	 */
    public String videoUpload(MultipartFile file);

    /**
	 * 썸네일 업로드
	 * @author ASW
	 * @date 2023.09.09.
	 */
    public String thumbnailUpload(MultipartFile file, String origin);

    /**
	 * 기본 썸네일 생성
	 * @author ASW
	 * @date 2023.09.09.
	 */
    public String initialThumbnail(String videoUrl) throws Exception;

    /**
	 * 기본 채널 이미지 생성
	 * @author ASW
	 * @date 2023.09.09.
	 */
    public String createBasicChannelImage(String userName);

    /**
	 * 기본 채널 이미지 업로드
	 * @author ASW
	 * @date 2023.09.09.
	 */
    public String channelImageUpload(MultipartFile file, String origin);

    /**
	 * sse 연결
	 * @author ASW
	 * @date 2023.09.11.
	 */
    public SseEmitter sseConnect(String sseId);

    /**
	 * 동영상 AV1 인코딩
	 * @author ASW
	 * @date 2023.09.11.
	 */
    public String saveAsAv1(String inputPath, String sseId);

}

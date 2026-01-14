package com.sewoong.streaming.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface S3FileService {

    /**
	 * Amazon S3 파일 업로드
	 * @author ASW
	 * @date 2023.10.05.
	 */
    public String fileUploadByS3(MultipartFile file);

    /**
	 * Amazon S3 동영상 업로드
	 * @author ASW
	 * @date 2023.10.05.
	 */
    public String videoUploadByS3(MultipartFile multipartFile);

    /**
	 * Amazon S3 동영상 업로드 + 프로그레스바
	 * @author ASW
	 * @date 2023.10.05.
	 */
    public String videoUploadProgressByS3(MultipartFile multipartFile, SseEmitter emitter);
}

package com.sewoong.streaming.service;

import java.util.List;

import com.sewoong.streaming.dto.HistoryDto;

public interface HistoryService {
    
    /**
	 * 시청 기록 저장
	 * @author ASW
	 * @date 2023.09.09.
	 */
    public void saveHistory(Integer videoId);

    /**
	 * 시청 기록 조회
	 * @author ASW
	 * @date 2023.09.09.
	 */
    public List<HistoryDto> getHistories();

    /**
	 * 시청 기록 삭제
	 * @author ASW
	 * @date 2023.09.09.
	 */
    public void deleteHistory(Integer videoId);
}

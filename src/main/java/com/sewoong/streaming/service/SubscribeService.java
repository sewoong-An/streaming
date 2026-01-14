package com.sewoong.streaming.service;

import java.util.List;

import com.sewoong.streaming.domain.Subscribe;

public interface SubscribeService {

    /**
	 * 구독
	 * @author ASW
	 * @date 2023.09.21.
	 */
    public void createSubscribe (Integer subChannelCode);

    /**
	 * 구독 해제
	 * @author ASW
	 * @date 2023.09.21.
	 */
    public void deleteSubscribe(Integer subChannelCode);

    /**
	 * 구독 채널 목록 조회
	 * @author ASW
	 * @date 2023.09.21.
	 */
    public List<Subscribe> getSubscribeList();

    /**
	 * 구독자 수 조회
	 * @author ASW
	 * @date 2023.09.21.
	 */
    public Integer getSubscribeCount(Integer subChannelCode);

    /**
	 * 구독 여부 체크
	 * @author ASW
	 * @date 2023.09.21.
	 */
    public Boolean checkSubscribe(Integer subChannelCode);
}

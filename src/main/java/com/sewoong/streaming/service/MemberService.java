package com.sewoong.streaming.service;

import java.util.Map;

import com.sewoong.streaming.dto.ChannelDto;
import com.sewoong.streaming.security.TokenInfo;

public interface MemberService {
    
    /**
	 * JWT 토큰 생성
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public TokenInfo getToken(String memberId, String password) throws Exception;

    /**
	 * refresh토큰 체크하여 JWT토큰 생성
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public TokenInfo refreshToken(String refreshToken) throws Exception;

    /**
	 * 회원가입
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public Integer join(Map<String, Object> data, String imageUrl) throws Exception;

    /**
	 * 사용자 정보 조회(현재사용자)
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public ChannelDto getMyChannelInfo() throws Exception;

    /**
	 * 채널 정보 조회(사용자 코드로 조회)
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public ChannelDto getChannelInfoByCode(Integer code) throws Exception;

    /**
	 * 사용자 정보 조회(Handle로 조회)
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public ChannelDto getChannelInfoByHandle(String handle) throws Exception;

    /**
	 * 사용자 이름 중복체크
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public Boolean checkName(String name);

    /**
	 * 사용자 handle 중복체크
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public Boolean checkHandle(String handle);

    /**
	 * 사용자 정보 수정
	 * @author ASW
	 * @date 2023.09.01.
	 */
    public ChannelDto updateMember(Map<String, Object> data);
}

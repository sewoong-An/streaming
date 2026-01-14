package com.sewoong.streaming.service.impl;

import com.sewoong.streaming.domain.Member;
import com.sewoong.streaming.dto.ChannelDto;
import com.sewoong.streaming.security.TokenInfo;
import com.sewoong.streaming.service.MemberService;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.repository.MemberRepository;
import com.sewoong.streaming.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
	 * JWT 토큰 생성
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    @Transactional
    public TokenInfo getToken(String memberId, String password) throws Exception{

        String salt = memberRepository.getSalt(memberId);

        if(salt == null || salt == "") {
            throw new Exception("해당하는 유저를 찾을 수 없습니다.");
        }

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, getSecurePassword(password, salt));

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 4. refreshToken DB에 저장
        Member member = memberRepository.findByMemberId(memberId).get();
        member.setRefreshToken(tokenInfo.getRefreshToken());
        memberRepository.save(member);

        tokenInfo.setMemberCode(member.getMemberCode());
        tokenInfo.setMemberName(member.getName());
        tokenInfo.setMemberImage(member.getImageUrl());

        return tokenInfo;
    }

    /**
	 * refresh토큰 체크하여 JWT토큰 생성
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    @Transactional
    public TokenInfo refreshToken(String refreshToken) throws Exception{

        Member member = memberRepository.findById(jwtTokenProvider.getMemberCodeByRefreshToken(refreshToken)).get();

        if(!refreshToken.equals(member.getRefreshToken())){
            throw new Exception("refreshToken 이 유효하지 않습니다.");
        }

        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new Exception("refreshToken 이 유효하지 않습니다.");
        }

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getMemberId(), member.getPassword());

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 4. refreshToken DB에 저장
        member.setRefreshToken(tokenInfo.getRefreshToken());
        memberRepository.save(member);
        return tokenInfo;
    }

    /**
	 * 회원가입
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    @Transactional
    public Integer join(Map<String, Object> data, String imageUrl) throws Exception{

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);

        // SALT 생성
        String salt = new String(Base64.getEncoder().encode(bytes));
        List<String> roles = new ArrayList<>();
        roles.add("USER");

        Member member = Member.builder()
                .memberId((String)data.get("memberId"))
                .password(getSecurePassword((String)data.get("password"), salt))
                .name((String)data.get("name"))
                .email((String)data.get("email"))
                .imageUrl(imageUrl)
                .handle("CH-" + UUID.randomUUID())
                .salt(salt)
                .roles(roles)
                .build();

        memberRepository.save(member);

        return member.getMemberCode();
    }

    /**
	 * 사용자 정보 조회(본인)
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    public ChannelDto getMyChannelInfo() throws Exception{

        Optional<Member> info = memberRepository.findById(getMyCode());

        if(!info.isPresent()){
            throw new Exception("존재하지 않는 채널 입니다.");
        }

        return new ChannelDto(info.get());
    }

    /**
	 * 채널 정보 조회(사용자 코드로 조회)
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    public ChannelDto getChannelInfoByCode(Integer code) throws Exception{

        Optional<Member> info = memberRepository.findById(code);

        if(!info.isPresent()){
            throw new Exception("존재하지 않는 채널 입니다.");
        }

        return new ChannelDto(info.get());
    }

    /**
	 * 사용자 정보 조회(Handle로 조회)
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    public ChannelDto getChannelInfoByHandle(String handle) throws Exception{
        Optional<Member> info = memberRepository.findByHandle(handle);

        if(!info.isPresent()){
            throw new Exception("존재하지 않는 채널 입니다.");
        }

        return new ChannelDto(info.get());
    }

    /**
	 * 사용자 이름 중복체크
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    public Boolean checkName(String name){
        Integer memberCode = getMyCode();

        Optional<Member> info = memberRepository.findByName(name);

        if(info.isPresent() && info.get().getMemberCode() != memberCode){
            return true;
        }
        else{
            return false;
        }
    }

    /**
	 * 사용자 handle 중복체크
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    public Boolean checkHandle(String handle){
        Integer memberCode = getMyCode();

        Optional<Member> info = memberRepository.findByHandle(handle);

        if(info.isPresent() && info.get().getMemberCode() != memberCode){
            return true;
        }
        else{
            return false;
        }
    }

    /**
	 * 사용자 정보 수정
	 * @author ASW
	 * @date 2023.09.01.
	 */
    @Override
    public ChannelDto updateMember(Map<String, Object> data){

        Integer memberCode = getMyCode();
        Member info = memberRepository.findById(memberCode).get();

        info.setName((String) data.get("name"));
        info.setHandle((String) data.get("handle"));
        info.setImageUrl((String) data.get("imageUrl"));

        return new ChannelDto(memberRepository.save(info));
    }


    /**
	 * 사용자 코드 조회
	 * @author ASW
	 * @date 2023.09.01.
	 */
    private Integer getMyCode(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        return userDetails.getMemberCode();
    }

    /**
	 * 비밀번호 SHA-256 암호화
	 * @author ASW
	 * @date 2023.09.01.
	 */
    private String getSecurePassword(String password, String salt) throws NoSuchAlgorithmException {

        String passwordAndSalt = password + salt;

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // 암호화
        md.update(passwordAndSalt.getBytes());
        return String.format("%064x", new BigInteger(1, md.digest()));
    }

    /**
	 * Base64 인코딩
	 * @author ASW
	 * @date 2023.09.01.
	 */
    private String EncodeStringToBase64(String input){

        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    /**
	 * Base64 디코딩
	 * @author ASW
	 * @date 2023.09.01.
	 */
    private String DecodeBase64ToString(String input){

        byte[] decodedBytes = Base64.getDecoder().decode(input);
        String decodedString = new String(decodedBytes);

        return decodedString;
    }
}

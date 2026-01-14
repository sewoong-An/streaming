package com.sewoong.streaming.service.impl;

import com.sewoong.streaming.domain.Subscribe;
import com.sewoong.streaming.domain.SubscribePK;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.repository.SubscribeRepository;
import com.sewoong.streaming.service.SubscribeService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService{
    
    private final SubscribeRepository subscribeRepository;

    /**
	 * 구독
	 * @author ASW
	 * @date 2023.09.21.
	 */
    @Override
    public void createSubscribe (Integer subChannelCode){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);

        Subscribe sub = Subscribe.builder()
                .subscribePK(SubscribePK.builder().memberCode(memberCode).subChannelCode(subChannelCode).build())
                .subDt(formatter.format(today))
                .build();

        subscribeRepository.save(sub);
    }

    /**
	 * 구독 해제
	 * @author ASW
	 * @date 2023.09.21.
	 */
    @Override
    public void deleteSubscribe(Integer subChannelCode){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        Optional<Subscribe> sub = subscribeRepository.findById(SubscribePK.builder().memberCode(memberCode).subChannelCode(subChannelCode).build());

        if(sub.isPresent()){
            subscribeRepository.delete(sub.get());
        }
    }

    /**
	 * 구독 채널 목록 조회
	 * @author ASW
	 * @date 2023.09.21.
	 */
    @Override
    public List<Subscribe> getSubscribeList(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        return subscribeRepository.findAllBySubscribePKMemberCode(memberCode);
    }

    /**
	 * 구독자 수 조회
	 * @author ASW
	 * @date 2023.09.21.
	 */
    @Override
    public Integer getSubscribeCount(Integer subChannelCode){
        return subscribeRepository.countBySubscribePKSubChannelCode(subChannelCode);
    }

    /**
	 * 구독 여부 체크
	 * @author ASW
	 * @date 2023.09.21.
	 */
    @Override
    public Boolean checkSubscribe(Integer subChannelCode){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        Optional<Subscribe> sub = subscribeRepository.findById(SubscribePK.builder().memberCode(memberCode).subChannelCode(subChannelCode).build());

        if(sub.isPresent()) {
            return true;
        }
        else{
            return false;
        }
    }

}

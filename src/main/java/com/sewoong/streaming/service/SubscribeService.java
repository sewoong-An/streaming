package com.sewoong.streaming.service;

import com.sewoong.streaming.domain.Subscribe;
import com.sewoong.streaming.domain.SubscribePK;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.repository.SubscribeRepository;
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
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;

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

    public void deleteSubscribe(Integer subChannelCode){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        Optional<Subscribe> sub = subscribeRepository.findById(SubscribePK.builder().memberCode(memberCode).subChannelCode(subChannelCode).build());

        if(sub.isPresent()){
            subscribeRepository.delete(sub.get());
        }
    }

    public List<Subscribe> getSubscribeList(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        return subscribeRepository.findAllBySubscribePKMemberCode(memberCode);
    }

    public Integer getSubscribeCount(Integer subChannelCode){
        return subscribeRepository.countBySubscribePKSubChannelCode(subChannelCode);
    }

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

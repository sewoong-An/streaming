package com.sewoong.streaming.service;

import com.sewoong.streaming.domain.History;
import com.sewoong.streaming.domain.HistoryPK;
import com.sewoong.streaming.dto.HistoryDto;
import com.sewoong.streaming.dto.UserCustom;
import com.sewoong.streaming.repository.HistoryRepository;
import com.sewoong.streaming.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    public void saveHistory(Integer videoId){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);

        History history = History.builder()
                .historyPK(HistoryPK.builder()
                        .memberCode(memberCode)
                        .videoId(videoId)
                        .build())
                .watchDt(formatter.format(today))
                .build();

        historyRepository.save(history);
    }

    public List<HistoryDto> getHistories(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        List<History> histories = historyRepository.findAllByHistoryPKMemberCodeAndVideo_stateOrderByWatchDtDesc(memberCode, "public");

        return histories.stream().map(h -> new HistoryDto(h)).collect(Collectors.toList());
    }

    public void deleteHistory(Integer videoId){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCustom userDetails = (UserCustom)principal;
        Integer memberCode = userDetails.getMemberCode();

        historyRepository.deleteById(HistoryPK.builder().memberCode(memberCode).videoId(videoId).build());
    }
}

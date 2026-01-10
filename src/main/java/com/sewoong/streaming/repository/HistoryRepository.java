package com.sewoong.streaming.repository;

import com.sewoong.streaming.domain.History;
import com.sewoong.streaming.domain.HistoryPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, HistoryPK> {

    List<History> findAllByHistoryPKMemberCodeAndVideo_stateOrderByWatchDtDesc(Integer memberCode, String state);
}

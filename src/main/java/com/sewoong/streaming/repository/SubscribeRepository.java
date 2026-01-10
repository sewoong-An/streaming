package com.sewoong.streaming.repository;

import com.sewoong.streaming.domain.Subscribe;
import com.sewoong.streaming.domain.SubscribePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, SubscribePK> {
    List<Subscribe> findAllBySubscribePKMemberCode(Integer memberCode);

    Integer countBySubscribePKSubChannelCode(Integer subChannelCode);
}

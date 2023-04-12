package com.bell.ringMyBell.boundedContext.likeInfo.repository;

import com.bell.ringMyBell.boundedContext.likeInfo.entity.LikeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeInfoRepository extends JpaRepository<LikeInfo, Long> {
    List<LikeInfo> findByFromInstaMemberId(Long fromInstaMemberId);
    List<LikeInfo> findByFromInstaMemberIdAndToInstaMemberId(Long fromInstaMemberId, Long toInstaMemeberId);
}

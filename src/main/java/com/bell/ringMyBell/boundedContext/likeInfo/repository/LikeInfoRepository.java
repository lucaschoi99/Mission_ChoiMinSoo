package com.bell.ringMyBell.boundedContext.likeInfo.repository;

import com.bell.ringMyBell.boundedContext.likeInfo.entity.LikeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeInfoRepository extends JpaRepository<LikeInfo, Long> {
    List<LikeInfo> findByFromInstaMemberId(Long fromInstaMemberId);
    Optional<LikeInfo> findByFromInstaMemberIdAndToInstaMemberId(Long fromInstaMemberId, Long toInstaMemeberId);
}

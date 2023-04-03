package com.bell.ringMyBell.boundedContext.likeInfo.service;

import com.bell.ringMyBell.base.response.ResponseData;
import com.bell.ringMyBell.boundedContext.instaMember.entity.InstaMember;
import com.bell.ringMyBell.boundedContext.instaMember.service.InstaMemberService;
import com.bell.ringMyBell.boundedContext.likeInfo.entity.LikeInfo;
import com.bell.ringMyBell.boundedContext.likeInfo.repository.LikeInfoRepository;
import com.bell.ringMyBell.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeInfoService {
    private final LikeInfoRepository likeInfoRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public ResponseData<LikeInfo> like(Member member, String username, int attractiveTypeCode) {
        if ( member.hasConnectedInstaMember() == false ) {
            return ResponseData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return ResponseData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();
        LikeInfo likeInfo = LikeInfo
                .builder()
                .fromInstaMember(member.getInstaMember())
                .fromInstaMemberUsername(member.getInstaMember().getUsername())
                .toInstaMember(toInstaMember)
                .toInstaMemberUsername(toInstaMember.getUsername())
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeInfoRepository.save(likeInfo);
        return ResponseData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeInfo);
    }

    public List<LikeInfo> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeInfoRepository.findByFromInstaMemberId(fromInstaMemberId);
    }
}
package com.bell.ringMyBell.boundedContext.likeInfo.service;

import com.bell.ringMyBell.base.request.RequestData;
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
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeInfoService {
    private final LikeInfoRepository likeInfoRepository;
    private final InstaMemberService instaMemberService;

    public Optional<LikeInfo> findById(Long id) {
        return likeInfoRepository.findById(id);
    }

    @Transactional
    public ResponseData<LikeInfo> like(Member member, String username, int attractiveTypeCode) {
        if ( member.hasConnectedInstaMember() == false ) {
            return ResponseData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return ResponseData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        ResponseData<LikeInfo> of = businessCaseException(member, username, attractiveTypeCode, toInstaMember);
        if (of != null) return of;

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

    private ResponseData<LikeInfo> businessCaseException(Member member, String username, int attractiveTypeCode, InstaMember toInstaMember) {
        Optional<LikeInfo> pair = findByFromInstaMemberIdAndToInstaMemberId(member.getInstaMember().getId(), toInstaMember.getId());
        int sendLikesSize = findByFromInstaMemberId(member.getInstaMember().getId()).size();

        // 중복 호감 표시 제한
        if (pair.isPresent()) {
            LikeInfo firstPair = pair.get();
            int originalTypeCode = firstPair.getAttractiveTypeCode();
            String originalTypeName = firstPair.getAttractiveTypeDisplayName();

            if (originalTypeCode == attractiveTypeCode) {
                return ResponseData.of("F-2", "같은 회원을 중복으로 등록할 수 없습니다.");
            }
            // 매력 코드가 상이할 경우 수정 후 허용
            firstPair.changeTypeCode(attractiveTypeCode);
            return ResponseData.of("S-2", username + " 에 대한 호감사유를 " + originalTypeName + " 에서 "
                    + firstPair.getAttractiveTypeDisplayName() + " (으)로 변경합니다.");
        }

        // 호감 표시 개수 제한
        if (sendLikesSize >= 10) {
            return ResponseData.of("F-2", "호감 표시 상대는 10명을 초과할 수 없습니다.");
        }
        return null;
    }

    public List<LikeInfo> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeInfoRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    public Optional<LikeInfo> findByFromInstaMemberIdAndToInstaMemberId(Long fromInstaMemberId, Long toInstaMemberId) {
        return likeInfoRepository.findByFromInstaMemberIdAndToInstaMemberId(fromInstaMemberId, toInstaMemberId);
    }

    public boolean matches(LikeInfo likeInfo, Member user) {
        if (user.getInstaMember() != null) {
            if (likeInfo.getFromInstaMember().getId().equals(user.getInstaMember().getId())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void delete(LikeInfo likeInfo) {
        likeInfoRepository.delete(likeInfo);
    }

    @Transactional
    public ResponseData<LikeInfo> canCancel(Member actor, LikeInfo likeablePerson) {
        if (likeablePerson == null) return ResponseData.of("F-1", "이미 취소되었습니다.");

        // 수행자의 인스타계정 번호
        long actorInstaMemberId = actor.getInstaMember().getId();
        // 삭제 대상의 작성자(호감표시한 사람)의 인스타계정 번호
        long fromInstaMemberId = likeablePerson.getFromInstaMember().getId();

        if (actorInstaMemberId != fromInstaMemberId) {
            return ResponseData.of("F-2", "취소할 권한이 없습니다.");
        }

        if (!likeablePerson.isModifyUnlocked()) {
            return ResponseData.of("F-3", "아직 취소할 수 없습니다. %s에는 가능합니다.".formatted(likeablePerson.getModifyUnlockDateRemainStrHuman()));
        }

        return ResponseData.of("S-1", "취소가 가능합니다.");
    }

    @Transactional
    public ResponseData<LikeInfo> canModify(Member actor, LikeInfo likeablePerson) {
        if (!actor.hasConnectedInstaMember()) {
            return ResponseData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
        }

        InstaMember fromInstaMember = actor.getInstaMember();

        if (!Objects.equals(likeablePerson.getFromInstaMember().getId(), fromInstaMember.getId())) {
            return ResponseData.of("F-2", "해당 호감표시에 대해서 사유변경을 수행할 권한이 없습니다.");
        }

        if (!likeablePerson.isModifyUnlocked()) return ResponseData.of("F-3", "아직 호감사유변경을 할 수 없습니다. %s에는 가능합니다.".formatted(likeablePerson.getModifyUnlockDateRemainStrHuman()));

        return ResponseData.of("S-1", "호감사유변경이 가능합니다.");
    }

}

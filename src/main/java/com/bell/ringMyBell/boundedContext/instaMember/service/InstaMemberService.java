package com.bell.ringMyBell.boundedContext.instaMember.service;

import com.bell.ringMyBell.base.response.ResponseData;
import com.bell.ringMyBell.boundedContext.instaMember.entity.InstaMember;
import com.bell.ringMyBell.boundedContext.instaMember.repository.InstaMemberRepository;
import com.bell.ringMyBell.boundedContext.member.entity.Member;
import com.bell.ringMyBell.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstaMemberService {
    private final InstaMemberRepository instaMemberRepository;
    private final MemberService memberService;

    public Optional<InstaMember> findByUsername(String username) {
        return instaMemberRepository.findByUsername(username);
    }

    @Transactional
    public ResponseData<InstaMember> connect(Member member, String username, String gender) {
        Optional<InstaMember> opInstaMember = findByUsername(username);

        if (opInstaMember.isPresent() && !opInstaMember.get().getGender().equals("U")) {
            return ResponseData.of("F-1", "해당 인스타그램 아이디는 이미 다른 사용자와 연결되었습니다.");
        }
        ResponseData<InstaMember> instaMemberResponseData = findByUsernameOrCreate(username, gender);
        memberService.updateInstaMember(member, instaMemberResponseData.getData());

        return instaMemberResponseData;
    }

    private ResponseData<InstaMember> create(String username, String gender) {
        InstaMember instaMember = InstaMember
                .builder()
                .username(username)
                .gender(gender)
                .build();

        instaMemberRepository.save(instaMember);

        return ResponseData.of("S-1", "인스타계정이 등록되었습니다.", instaMember);
    }

    @Transactional
    public ResponseData<InstaMember> findByUsernameOrCreate(String username) {
        Optional<InstaMember> opInstaMember = findByUsername(username);

        if (opInstaMember.isPresent()) return ResponseData.of("S-2", "인스타계정이 등록되었습니다.", opInstaMember.get());

        // 성별을 알 수 없음, Unknown
        return create(username, "U");
    }

    @Transactional
    public ResponseData<InstaMember> findByUsernameOrCreate(String username, String gender) {
        Optional<InstaMember> opInstaMember = findByUsername(username);

        if (opInstaMember.isPresent()) {
            InstaMember instaMember = opInstaMember.get();
            instaMember.setGender(gender);
            instaMemberRepository.save(instaMember);

            return ResponseData.of("S-2", "인스타계정이 등록되었습니다.", instaMember);
        }
        return create(username, gender);
    }
}

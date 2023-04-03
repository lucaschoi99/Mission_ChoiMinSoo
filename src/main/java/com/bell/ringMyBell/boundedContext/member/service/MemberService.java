package com.bell.ringMyBell.boundedContext.member.service;

import com.bell.ringMyBell.base.response.ResponseData;
import com.bell.ringMyBell.boundedContext.instaMember.entity.InstaMember;
import com.bell.ringMyBell.boundedContext.member.entity.Member;
import com.bell.ringMyBell.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    // 일반 회원가입(소셜 로그인을 통한 회원가입 X)
    public ResponseData<Member> join(String username, String password) {
        return join("GENERAL", username, password);
    }

    private ResponseData<Member> join(String providerTypeCode, String username, String password) {
        if (findByUsername(username).isPresent()) {
            return ResponseData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }
        // 소셜 로그인을 통한 회원가입 -> 비밀번호 X
        if (StringUtils.hasText(password)) password = passwordEncoder.encode(password);
        Member member = Member
                .builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .password(password)
                .build();
        memberRepository.save(member);
        return ResponseData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    @Transactional
    public void updateInstaMember(Member member, InstaMember instaMember) {
        member.setInstaMember(instaMember);
        memberRepository.save(member);
    }

    // 소셜 로그인(카카오, 구글, 네이버) 로그인
    @Transactional
    public ResponseData<Member> whenSocialLogin(String providerTypeCode, String username) {
        Optional<Member> opMember = findByUsername(username); // ex) username: KAKAO__1312319038130912, NAVER__1230812300

        if (opMember.isPresent()) return ResponseData.of("S-2", "로그인 되었습니다.", opMember.get());
        // 소셜 로그인를 통한 가입 -> 비밀번호 X
        return join(providerTypeCode, username, ""); // 최초 로그인 시 실행
    }
}

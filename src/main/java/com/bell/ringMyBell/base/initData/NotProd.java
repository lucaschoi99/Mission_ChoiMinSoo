package com.bell.ringMyBell.base.initData;

import com.bell.ringMyBell.boundedContext.instaMember.service.InstaMemberService;
import com.bell.ringMyBell.boundedContext.likeInfo.service.LikeInfoService;
import com.bell.ringMyBell.boundedContext.member.entity.Member;
import com.bell.ringMyBell.boundedContext.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            InstaMemberService instaMemberService,
            LikeInfoService likeInfoService
    ) {
        return args -> {
            Member memberAdmin = memberService.join("admin", "1234").getData();
            Member memberUser1 = memberService.join("user1", "1234").getData();
            Member memberUser2 = memberService.join("user2", "1234").getData();
            Member memberUser3 = memberService.join("user3", "1234").getData();
            Member memberUser4 = memberService.join("user4", "1234").getData();

            Member memberUser5ByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__2733210196").getData();
            Member memberUser6ByGoogle = memberService.whenSocialLogin("GOOGLE", "GOOGLE__112170912385186233067").getData();

            instaMemberService.connect(memberUser2, "insta_user2", "M");
            instaMemberService.connect(memberUser3, "insta_user3", "W");
            instaMemberService.connect(memberUser4, "insta_user4", "M");

            likeInfoService.like(memberUser3, "insta_user4", 1);
            likeInfoService.like(memberUser3, "insta_user100", 2);
        };
    }
}

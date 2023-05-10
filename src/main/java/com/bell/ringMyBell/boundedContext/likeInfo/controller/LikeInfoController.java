package com.bell.ringMyBell.boundedContext.likeInfo.controller;

import com.bell.ringMyBell.base.request.RequestData;
import com.bell.ringMyBell.base.response.ResponseData;
import com.bell.ringMyBell.boundedContext.instaMember.entity.InstaMember;
import com.bell.ringMyBell.boundedContext.instaMember.service.InstaMemberService;
import com.bell.ringMyBell.boundedContext.likeInfo.entity.LikeInfo;
import com.bell.ringMyBell.boundedContext.likeInfo.service.LikeInfoService;
import com.bell.ringMyBell.boundedContext.member.entity.Member;
import com.bell.ringMyBell.boundedContext.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/likeInfo")
@RequiredArgsConstructor
@Slf4j
public class LikeInfoController {
    private final RequestData requestData;
    private final LikeInfoService likeInfoService;
    private final InstaMemberService instaMemberService;
    private final MemberService memberService;

    @GetMapping("/add")
    public String showAdd() {
        return "usr/likeInfo/add";
    }

    @AllArgsConstructor
    @Getter
    public static class AddForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @PostMapping("/add")
    public String add(@Valid AddForm addForm) {
        ResponseData<LikeInfo> createResponseData = likeInfoService.like(requestData.getMember(), addForm.getUsername(), addForm.getAttractiveTypeCode());
        if (createResponseData.isFail()) {
            return requestData.historyBack(createResponseData);
        }
        return requestData.redirectWithMsg("/likeInfo/list", createResponseData);
    }

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = requestData.getMember().getInstaMember();
        // 인스타 인증 여부 체크
        if (instaMember != null) {
            List<LikeInfo> likeInfos = likeInfoService.findByFromInstaMemberId(instaMember.getId());
            model.addAttribute("likeInfo", likeInfos);
        }
        return "usr/likeInfo/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Principal principal) {
        Optional<LikeInfo> likeInfo = likeInfoService.findById(id);
        Optional<Member> loginUser = memberService.findByUsername(principal.getName());

        if (likeInfo.isPresent() && loginUser.isPresent()) {
            if (likeInfoService.matches(likeInfo.get(), loginUser.get())) {
                likeInfoService.delete(likeInfo.get());
                return requestData.redirectWithMsg("/likeInfo/list", "정상적으로 삭제되었습니다.");
            }
            return requestData.redirectWithMsg("/likeInfo/list", "삭제 권한이 존재하지 않습니다.");
        }
        return requestData.redirectWithMsg("/likeInfo/list", "존재하지 않는 정보입니다.");
    }



}

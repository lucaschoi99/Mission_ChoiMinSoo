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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showModify(@PathVariable Long id, Model model) {
        LikeInfo likeablePerson = likeInfoService.findById(id).orElseThrow();

        ResponseData<LikeInfo> canModifyRsData = likeInfoService.canModify(requestData.getMember(), likeablePerson);

        if (canModifyRsData.isFail()) return requestData.historyBack(canModifyRsData);

        model.addAttribute("likeablePerson", likeablePerson);
        return "usr/likeInfo/modify";
    }


    @GetMapping("/list")
    public String showList(Model model, String gender, @RequestParam(defaultValue = "0") int attractiveTypeCode, @RequestParam(defaultValue = "1") int sortCode) {
        InstaMember instaMember = requestData.getMember().getInstaMember();
        // 인스타 인증 여부 체크
        if (instaMember != null) {
            Stream<LikeInfo> likeablePeopleStream = (Stream<LikeInfo>) instaMember;

            if (gender != null) {
                 likeablePeopleStream = likeablePeopleStream.filter();
            }

            if (attractiveTypeCode != 0) {
                 likeablePeopleStream = likeablePeopleStream.filter();
            }

            switch (sortCode) {
                case 1:
                     likeablePeopleStream = likeablePeopleStream.sorted();
                    break;
                case 2:
                    likeablePeopleStream = likeablePeopleStream.sorted();
                    break;
                case 3:
                     likeablePeopleStream = likeablePeopleStream.sorted();
                    break;
                case 4:
                     likeablePeopleStream = likeablePeopleStream.sorted();
                    break;
                case 5:
                     likeablePeopleStream = likeablePeopleStream.sorted();
                    break;
                case 6:
                     likeablePeopleStream = likeablePeopleStream.sorted();
                    break;

            }

            List<LikeInfo> likeInfos = likeablePeopleStream.collect(Collectors.toList());
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

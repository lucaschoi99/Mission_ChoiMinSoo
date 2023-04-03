package com.bell.ringMyBell.boundedContext.likeInfo.controller;

import com.bell.ringMyBell.base.request.RequestData;
import com.bell.ringMyBell.base.response.ResponseData;
import com.bell.ringMyBell.boundedContext.instaMember.entity.InstaMember;
import com.bell.ringMyBell.boundedContext.likeInfo.entity.LikeInfo;
import com.bell.ringMyBell.boundedContext.likeInfo.service.LikeInfoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/likeInfo")
@RequiredArgsConstructor
public class LikeInfoController {
    private final RequestData requestData;
    private final LikeInfoService likeInfoService;

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
}

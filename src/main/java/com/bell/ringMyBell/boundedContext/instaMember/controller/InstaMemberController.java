package com.bell.ringMyBell.boundedContext.instaMember.controller;

import com.bell.ringMyBell.base.request.RequestData;
import com.bell.ringMyBell.base.response.ResponseData;
import com.bell.ringMyBell.boundedContext.instaMember.entity.InstaMember;
import com.bell.ringMyBell.boundedContext.instaMember.service.InstaMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/instaMember")
@RequiredArgsConstructor
public class InstaMemberController {
    private final RequestData requestData;
    private final InstaMemberService instaMemberService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/connect")
    public String showConnect() {
        return "usr/instaMember/connect";
    }

    @AllArgsConstructor
    @Getter
    public static class ConnectForm {
        @NotBlank
        @Size(min = 4, max = 30)
        private final String username;
        @NotBlank
        @Size(min = 1, max = 1)
        private final String gender;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/connect")
    public String connect(@Valid ConnectForm connectForm) {
        ResponseData<InstaMember> responseData = instaMemberService.connect(requestData.getMember(), connectForm.getUsername(), connectForm.getGender());

        if ( responseData.isFail() ) {
            return requestData.historyBack(responseData);
        }

        return requestData.redirectWithMsg("/likeInfo/add", "인스타그램 계정이 연결되었습니다.");
    }
}

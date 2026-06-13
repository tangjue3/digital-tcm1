package com.ruoyi.web.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.chat.TcmChatSendBody;
import com.ruoyi.system.service.ITcmMedicalChatService;

@RestController
@RequestMapping("/app/chat")
public class AppMedicalChatController extends BaseController
{
    @Autowired
    private ITcmMedicalChatService medicalChatService;

    @GetMapping("/conversation")
    public AjaxResult conversation()
    {
        return AjaxResult.success(medicalChatService.getAppConversation(getUserId()));
    }

    @PostMapping("/message")
    public AjaxResult send(@RequestBody TcmChatSendBody body)
    {
        SysUser patientUser = getLoginUser().getUser();
        return AjaxResult.success(medicalChatService.sendAppMessage(getUserId(), patientUser, body.getContent()));
    }
}

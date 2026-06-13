package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/system/medicalChat")
public class TcmMedicalChatController extends BaseController
{
    @Autowired
    private ITcmMedicalChatService medicalChatService;

    @GetMapping("/conversations")
    public AjaxResult conversations()
    {
        return AjaxResult.success(medicalChatService.listAdminConversations());
    }

    @GetMapping("/conversation/{patientUserId}")
    public AjaxResult conversation(@PathVariable("patientUserId") Long patientUserId)
    {
        SysUser doctorUser = getLoginUser().getUser();
        return AjaxResult.success(medicalChatService.getAdminConversation(patientUserId, doctorUser));
    }

    @PostMapping("/message")
    public AjaxResult send(@RequestBody TcmChatSendBody body)
    {
        SysUser doctorUser = getLoginUser().getUser();
        return AjaxResult.success(medicalChatService.sendAdminMessage(body.getPatientUserId(), doctorUser, body.getContent()));
    }
}

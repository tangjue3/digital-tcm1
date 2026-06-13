package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.chat.TcmChatConversation;
import com.ruoyi.system.domain.chat.TcmChatConversationSummary;
import com.ruoyi.system.domain.chat.TcmChatMessage;

public interface ITcmMedicalChatService
{
    public TcmChatConversation getAppConversation(Long patientUserId);

    public TcmChatMessage sendAppMessage(Long patientUserId, SysUser patientUser, String content);

    public List<TcmChatConversationSummary> listAdminConversations();

    public TcmChatConversation getAdminConversation(Long patientUserId, SysUser doctorUser);

    public TcmChatMessage sendAdminMessage(Long patientUserId, SysUser doctorUser, String content);
}

package com.ruoyi.system.domain.chat;

public class TcmChatSendBody
{
    private Long patientUserId;

    private String content;

    public Long getPatientUserId()
    {
        return patientUserId;
    }

    public void setPatientUserId(Long patientUserId)
    {
        this.patientUserId = patientUserId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}

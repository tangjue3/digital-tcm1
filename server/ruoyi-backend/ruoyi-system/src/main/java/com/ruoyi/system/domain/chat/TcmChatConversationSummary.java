package com.ruoyi.system.domain.chat;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TcmChatConversationSummary implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long patientUserId;

    private String patientName;

    private String patientPhone;

    private String doctorName;

    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMessageTime;

    private Integer messageCount;

    public Long getPatientUserId()
    {
        return patientUserId;
    }

    public void setPatientUserId(Long patientUserId)
    {
        this.patientUserId = patientUserId;
    }

    public String getPatientName()
    {
        return patientName;
    }

    public void setPatientName(String patientName)
    {
        this.patientName = patientName;
    }

    public String getPatientPhone()
    {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone)
    {
        this.patientPhone = patientPhone;
    }

    public String getDoctorName()
    {
        return doctorName;
    }

    public void setDoctorName(String doctorName)
    {
        this.doctorName = doctorName;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageTime()
    {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime)
    {
        this.lastMessageTime = lastMessageTime;
    }

    public Integer getMessageCount()
    {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount)
    {
        this.messageCount = messageCount;
    }
}

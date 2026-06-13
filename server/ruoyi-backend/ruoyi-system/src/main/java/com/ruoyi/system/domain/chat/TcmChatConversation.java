package com.ruoyi.system.domain.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TcmChatConversation implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long patientUserId;

    private String patientName;

    private String patientPhone;

    private Long doctorUserId;

    private String doctorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    private List<TcmChatMessage> messages = new ArrayList<>();

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

    public Long getDoctorUserId()
    {
        return doctorUserId;
    }

    public void setDoctorUserId(Long doctorUserId)
    {
        this.doctorUserId = doctorUserId;
    }

    public String getDoctorName()
    {
        return doctorName;
    }

    public void setDoctorName(String doctorName)
    {
        this.doctorName = doctorName;
    }

    public Date getUpdatedTime()
    {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime)
    {
        this.updatedTime = updatedTime;
    }

    public List<TcmChatMessage> getMessages()
    {
        return messages;
    }

    public void setMessages(List<TcmChatMessage> messages)
    {
        this.messages = messages == null ? new ArrayList<>() : messages;
    }
}

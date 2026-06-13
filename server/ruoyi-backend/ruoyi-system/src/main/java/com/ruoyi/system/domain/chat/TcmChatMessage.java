package com.ruoyi.system.domain.chat;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TcmChatMessage implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String messageId;

    private String senderRole;

    private Long senderUserId;

    private String senderName;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public String getSenderRole()
    {
        return senderRole;
    }

    public void setSenderRole(String senderRole)
    {
        this.senderRole = senderRole;
    }

    public Long getSenderUserId()
    {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId)
    {
        this.senderUserId = senderUserId;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }
}

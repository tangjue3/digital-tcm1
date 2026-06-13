package com.ruoyi.web.domain.ai;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TcmChatRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String message;

    private List<TcmChatHistory> history;

    private Map<String, Object> patientInfo;

    private Integer topK;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<TcmChatHistory> getHistory()
    {
        return history;
    }

    public void setHistory(List<TcmChatHistory> history)
    {
        this.history = history;
    }

    public Map<String, Object> getPatientInfo()
    {
        return patientInfo;
    }

    public void setPatientInfo(Map<String, Object> patientInfo)
    {
        this.patientInfo = patientInfo;
    }

    public Integer getTopK()
    {
        return topK;
    }

    public void setTopK(Integer topK)
    {
        this.topK = topK;
    }
}

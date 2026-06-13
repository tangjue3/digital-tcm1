package com.ruoyi.system.domain.ai;

import java.util.List;

public class TcmAiChatRequest
{
    private String sessionId;

    private String message;

    private List<TcmAiChatMessage> history;

    private String scene;

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<TcmAiChatMessage> getHistory()
    {
        return history;
    }

    public void setHistory(List<TcmAiChatMessage> history)
    {
        this.history = history;
    }

    public String getScene()
    {
        return scene;
    }

    public void setScene(String scene)
    {
        this.scene = scene;
    }
}

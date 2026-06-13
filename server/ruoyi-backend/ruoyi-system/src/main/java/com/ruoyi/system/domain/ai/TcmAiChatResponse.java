package com.ruoyi.system.domain.ai;

import java.util.List;

public class TcmAiChatResponse
{
    private String sessionId;

    private String answer;

    private List<String> suggestions;

    private String model;

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public List<String> getSuggestions()
    {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions)
    {
        this.suggestions = suggestions;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }
}

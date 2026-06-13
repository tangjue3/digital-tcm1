package com.ruoyi.web.domain.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TcmChatResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String answer;

    private List<TcmKnowledgeChunk> references = new ArrayList<>();

    private List<TcmKnowledgeChunk> matchedChunks = new ArrayList<>();

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public List<TcmKnowledgeChunk> getReferences()
    {
        return references;
    }

    public void setReferences(List<TcmKnowledgeChunk> references)
    {
        this.references = references;
    }

    public List<TcmKnowledgeChunk> getMatchedChunks()
    {
        return matchedChunks;
    }

    public void setMatchedChunks(List<TcmKnowledgeChunk> matchedChunks)
    {
        this.matchedChunks = matchedChunks;
    }
}

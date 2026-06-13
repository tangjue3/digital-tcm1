package com.ruoyi.system.service;

import com.ruoyi.system.domain.ai.TcmAiChatRequest;
import com.ruoyi.system.domain.ai.TcmAiChatResponse;

public interface ITcmAiChatService
{
    TcmAiChatResponse chat(Long userId, TcmAiChatRequest request);
}

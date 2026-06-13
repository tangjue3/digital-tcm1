package com.ruoyi.web.controller.system;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.web.domain.ai.TcmChatRequest;
import com.ruoyi.web.service.TcmAiChatService;
import com.ruoyi.web.service.TcmRagService;

/**
 * ISLab AI compatibility endpoints.
 */
@RestController
@RequestMapping("/system/ai")
public class TcmAiController
{
    @Autowired
    private TcmAiChatService tcmAiChatService;

    @Autowired
    private TcmRagService tcmRagService;

    /**
     * Mock endpoint for fzsy.vue. The old frontend called an external faceAiPlus service directly.
     */
    @GetMapping("/faceAiPlus")
    public AjaxResult faceAiPlus(String imageUrl, String type)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);
        data.put("type", type);
        data.put("features", Collections.emptyList());
        return AjaxResult.success(data);
    }

    @PostMapping("/tcm-chat")
    public AjaxResult tcmChat(@RequestBody TcmChatRequest request)
    {
        try
        {
            return AjaxResult.success(tcmAiChatService.chat(request));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/tcm-kb/status")
    public AjaxResult tcmKbStatus()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("files", tcmRagService.getIndexedFiles());
        data.put("fileCount", tcmRagService.getIndexedFiles().size());
        data.put("chunkCount", tcmRagService.getChunkCount());
        return AjaxResult.success(data);
    }

    @PostMapping("/tcm-kb/reload")
    public AjaxResult reloadTcmKb()
    {
        tcmRagService.reloadIndex();
        return tcmKbStatus();
    }
}

package com.ruoyi.web.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.ai.TcmAiChatRequest;
import com.ruoyi.system.service.ITcmAiChatService;

@RestController
@RequestMapping("/tcm/ai")
public class TcmAiChatController extends BaseController
{
    @Autowired
    private ITcmAiChatService tcmAiChatService;

    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody TcmAiChatRequest request)
    {
        try
        {
            return AjaxResult.success("success", tcmAiChatService.chat(getUserId(), request));
        }
        catch (ServiceException e)
        {
            Integer code = e.getCode();
            return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
        }
        catch (Exception e)
        {
            logger.warn("Tcm AI chat failed", e);
            return AjaxResult.error("AI 服务暂时不可用，请稍后重试");
        }
    }
}

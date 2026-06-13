package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TcmPulseRecord;
import com.ruoyi.system.service.ITcmPulseRecordService;

/**
 * Pulse record management APIs.
 */
@RestController
@RequestMapping("/system/pulse")
public class TcmPulseRecordController extends BaseController
{
    @Autowired
    private ITcmPulseRecordService pulseRecordService;

    @GetMapping("/list")
    public TableDataInfo list(TcmPulseRecord record,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime)
    {
        if (StringUtils.isNotEmpty(beginTime))
        {
            record.getParams().put("beginTime", beginTime);
        }
        if (StringUtils.isNotEmpty(endTime))
        {
            record.getParams().put("endTime", endTime);
        }
        startPage();
        List<TcmPulseRecord> list = pulseRecordService.selectPulseRecordList(record);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(pulseRecordService.selectPulseRecordById(id));
    }

    @GetMapping("/latest/{userId}")
    public AjaxResult latest(@PathVariable("userId") Long userId)
    {
        return AjaxResult.success(pulseRecordService.selectLatestPulseRecordByUserId(userId));
    }
}

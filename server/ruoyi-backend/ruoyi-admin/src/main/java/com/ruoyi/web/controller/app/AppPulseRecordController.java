package com.ruoyi.web.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.TcmPulseRecord;
import com.ruoyi.system.service.ITcmPulseRecordService;

/**
 * App pulse record APIs.
 */
@RestController
@RequestMapping("/app/pulse")
public class AppPulseRecordController extends BaseController
{
    @Autowired
    private ITcmPulseRecordService pulseRecordService;

    @PostMapping("/upload")
    public AjaxResult upload(@RequestBody TcmPulseRecord record)
    {
        if (record.getPulseRate() == null)
        {
            return AjaxResult.error("pulseRate is required");
        }
        if (record.getPulseRate() < 30 || record.getPulseRate() > 220)
        {
            return AjaxResult.error("pulseRate must be between 30 and 220");
        }
        record.setUserId(getUserId());
        return toAjax(pulseRecordService.insertPulseRecord(record));
    }

    @GetMapping("/latest")
    public AjaxResult latest()
    {
        return AjaxResult.success(pulseRecordService.selectLatestPulseRecordByUserId(getUserId()));
    }

    @GetMapping("/list")
    public TableDataInfo list(TcmPulseRecord record)
    {
        record.setUserId(getUserId());
        startPage();
        return getDataTable(pulseRecordService.selectPulseRecordList(record));
    }
}

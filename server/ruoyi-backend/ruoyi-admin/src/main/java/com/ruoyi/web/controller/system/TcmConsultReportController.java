package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TcmConsultReport;
import com.ruoyi.system.service.ITcmConsultReportService;

/**
 * 管理端问诊报告接口
 */
@RestController
@RequestMapping("/system/consult/report")
public class TcmConsultReportController extends BaseController
{
    @Autowired
    private ITcmConsultReportService consultReportService;

    @PreAuthorize("@ss.hasPermi('system:consultReport:list')")
    @GetMapping("/dashboard")
    public AjaxResult dashboard()
    {
        Map<String, Object> data = consultReportService.getConsultReportDashboard();
        return AjaxResult.success(data);
    }

    @PreAuthorize("@ss.hasPermi('system:consultReport:list')")
    @GetMapping("/urgent")
    public AjaxResult urgent(@RequestParam(value = "limit", required = false) Integer limit)
    {
        return AjaxResult.success(consultReportService.selectUrgentConsultReportList(limit));
    }

    @PreAuthorize("@ss.hasPermi('system:consultReport:list')")
    @GetMapping("/list")
    public TableDataInfo list(TcmConsultReport report,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime)
    {
        if (StringUtils.isNotEmpty(startTime))
        {
            report.getParams().put("startTime", startTime);
        }
        if (StringUtils.isNotEmpty(endTime))
        {
            report.getParams().put("endTime", endTime);
        }
        startPage();
        List<TcmConsultReport> list = consultReportService.selectConsultReportList(report);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:consultReport:query')")
    @GetMapping("/{reportId}")
    public AjaxResult getInfo(@PathVariable("reportId") Long reportId)
    {
        return AjaxResult.success(consultReportService.selectConsultReportDetail(reportId));
    }

    @PreAuthorize("@ss.hasPermi('system:consultReport:edit')")
    @PutMapping("/{reportId}/status")
    public AjaxResult updateStatus(@PathVariable("reportId") Long reportId, @RequestBody TcmConsultReport report)
    {
        report.setReportId(reportId);
        report.setUpdateBy(getUsername());
        report.setUpdateTime(DateUtils.getNowDate());
        return toAjax(consultReportService.updateConsultReportStatus(report));
    }
}

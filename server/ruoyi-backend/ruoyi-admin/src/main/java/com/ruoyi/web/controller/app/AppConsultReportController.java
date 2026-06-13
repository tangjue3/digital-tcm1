package com.ruoyi.web.controller.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TcmConsultReport;
import com.ruoyi.system.domain.vo.TcmConsultReportSubmitBody;
import com.ruoyi.system.service.ITcmConsultReportService;

/**
 * App 问诊报告接口
 */
@RestController
@RequestMapping("/app/consult/report")
public class AppConsultReportController extends BaseController
{
    @Autowired
    private ITcmConsultReportService consultReportService;

    @PostMapping
    public AjaxResult submit(@RequestBody TcmConsultReportSubmitBody body)
    {
        if (StringUtils.isEmpty(body.getPatientName()) || StringUtils.isEmpty(body.getGender())
                || body.getAge() == null || StringUtils.isEmpty(body.getVisitType()))
        {
            return AjaxResult.error("patientName、gender、age、visitType 为必填项");
        }
        TcmConsultReport report = consultReportService.submitAppConsultReport(body, getUserId(), getUsername());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("reportId", report.getReportId());
        data.put("reportNo", report.getReportNo());
        data.put("status", report.getStatus());
        return AjaxResult.success("提交成功", data);
    }

    @GetMapping("/my")
    public TableDataInfo myList(TcmConsultReport report,
            @RequestParam(value = "status", required = false) String status)
    {
        if (StringUtils.isNotEmpty(status))
        {
            report.setStatus(status);
        }
        startPage();
        List<TcmConsultReport> list = consultReportService.selectMyConsultReportList(report, getUserId());
        return getDataTable(list);
    }

    @GetMapping("/{reportId}")
    public AjaxResult detail(@PathVariable("reportId") Long reportId)
    {
        TcmConsultReport report = consultReportService.selectAppConsultReportDetail(reportId, getUserId());
        if (report == null)
        {
            return AjaxResult.error("报告不存在或无权查看");
        }
        return AjaxResult.success(report);
    }
}

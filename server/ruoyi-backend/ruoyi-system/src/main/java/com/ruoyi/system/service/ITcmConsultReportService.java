package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.TcmConsultReport;
import com.ruoyi.system.domain.vo.TcmConsultReportSubmitBody;

/**
 * 问诊报告服务接口
 */
public interface ITcmConsultReportService
{
    public TcmConsultReport submitAppConsultReport(TcmConsultReportSubmitBody body, Long userId, String operatorName);

    public List<TcmConsultReport> selectMyConsultReportList(TcmConsultReport report, Long userId);

    public TcmConsultReport selectAppConsultReportDetail(Long reportId, Long userId);

    public Map<String, Object> getConsultReportDashboard();

    public List<TcmConsultReport> selectUrgentConsultReportList(Integer limit);

    public List<TcmConsultReport> selectConsultReportList(TcmConsultReport report);

    public TcmConsultReport selectConsultReportDetail(Long reportId);

    public int updateConsultReportStatus(TcmConsultReport report);
}

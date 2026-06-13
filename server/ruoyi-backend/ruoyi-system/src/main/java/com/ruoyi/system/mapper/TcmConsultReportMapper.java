package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.TcmConsultReport;

/**
 * 问诊报告主表 Mapper
 */
public interface TcmConsultReportMapper
{
    public TcmConsultReport selectTcmConsultReportById(Long reportId);

    public List<TcmConsultReport> selectTcmConsultReportList(TcmConsultReport report);

    public int insertTcmConsultReport(TcmConsultReport report);

    public int updateTcmConsultReport(TcmConsultReport report);

    public Long countReportsByStatus(@Param("status") String status);

    public Long countTodayReports();

    public Long countHighRiskUnfinishedReports();

    public Long countCriticalUnfinishedReports();

    public List<TcmConsultReport> selectUrgentConsultReportList(@Param("limit") Integer limit);

    public List<Map<String, Object>> selectRecentReportTrend(@Param("startTime") String startTime);

    public List<Map<String, Object>> selectRecentDiseaseDistribution(@Param("startTime") String startTime,
            @Param("limit") Integer limit);
}

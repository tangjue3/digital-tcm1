package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmConsultReportDisease;

/**
 * 问诊报告病种 Mapper
 */
public interface TcmConsultReportDiseaseMapper
{
    public List<TcmConsultReportDisease> selectTcmConsultReportDiseaseListByReportId(Long reportId);

    public List<TcmConsultReportDisease> selectTcmConsultReportDiseaseListByReportIds(List<Long> reportIds);

    public int batchInsertTcmConsultReportDisease(List<TcmConsultReportDisease> list);

    public int deleteTcmConsultReportDiseaseByReportId(Long reportId);
}

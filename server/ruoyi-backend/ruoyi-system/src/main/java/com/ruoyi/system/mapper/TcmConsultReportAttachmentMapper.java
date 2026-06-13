package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmConsultReportAttachment;

/**
 * 问诊报告附件 Mapper
 */
public interface TcmConsultReportAttachmentMapper
{
    public List<TcmConsultReportAttachment> selectTcmConsultReportAttachmentListByReportId(Long reportId);

    public List<TcmConsultReportAttachment> selectTcmConsultReportAttachmentListByReportIds(List<Long> reportIds);

    public int batchInsertTcmConsultReportAttachment(List<TcmConsultReportAttachment> list);

    public int deleteTcmConsultReportAttachmentByReportId(Long reportId);
}

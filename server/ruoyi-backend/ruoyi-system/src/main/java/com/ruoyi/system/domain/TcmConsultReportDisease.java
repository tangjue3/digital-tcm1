package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 问诊报告病种对象 tcm_consult_report_disease
 */
public class TcmConsultReportDisease extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long reportId;

    private String diseaseCode;

    private String diseaseName;

    private String diseasePath;

    private String riskLevel;

    private String riskReason;

    private Integer sortOrder;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getReportId()
    {
        return reportId;
    }

    public void setReportId(Long reportId)
    {
        this.reportId = reportId;
    }

    public String getDiseaseCode()
    {
        return diseaseCode;
    }

    public void setDiseaseCode(String diseaseCode)
    {
        this.diseaseCode = diseaseCode;
    }

    public String getDiseaseName()
    {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName)
    {
        this.diseaseName = diseaseName;
    }

    public String getDiseasePath()
    {
        return diseasePath;
    }

    public void setDiseasePath(String diseasePath)
    {
        this.diseasePath = diseasePath;
    }

    public String getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public String getRiskReason()
    {
        return riskReason;
    }

    public void setRiskReason(String riskReason)
    {
        this.riskReason = riskReason;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }
}

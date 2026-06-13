package com.ruoyi.system.domain;

import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 问诊报告主表对象 tcm_consult_report
 */
public class TcmConsultReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long reportId;

    @Excel(name = "报告编号")
    private String reportNo;

    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "患者姓名")
    private String patientName;

    @Excel(name = "性别")
    private String gender;

    @Excel(name = "年龄")
    private Integer age;

    @Excel(name = "就诊类型")
    private String visitType;

    @Excel(name = "基础病")
    private String basicDisease;

    private String allergyHistoryType;

    private String allergyHistoryDetail;

    private String pastHistoryType;

    private String pastHistoryDetail;

    private String currentMedicationType;

    private String currentMedicationDetail;

    @Excel(name = "主要不适")
    private String chiefComplaint;

    private String duration;

    private Integer severityLevel;

    private String suddenWorse;

    private String lifeImpact;

    @Excel(name = "最高风险等级")
    private String highestRiskLevel;

    @Excel(name = "状态")
    private String status;

    private Long doctorId;

    private String doctorName;

    private String doctorAdvice;

    private String doctorSummary;

    private String suggestedDepartment;

    private Boolean needOfflineVisit;

    private Boolean needSupplement;

    private String delFlag;

    /**
     * 列表摘要字段，不落库。
     */
    private String diseaseSummary;

    /**
     * 详情病种列表，不落库。
     */
    private List<TcmConsultReportDisease> diseaseList;

    /**
     * 详情附件列表，不落库。
     */
    private List<TcmConsultReportAttachment> attachmentList;

    public Long getReportId()
    {
        return reportId;
    }

    public void setReportId(Long reportId)
    {
        this.reportId = reportId;
    }

    public String getReportNo()
    {
        return reportNo;
    }

    public void setReportNo(String reportNo)
    {
        this.reportNo = reportNo;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getPatientName()
    {
        return patientName;
    }

    public void setPatientName(String patientName)
    {
        this.patientName = patientName;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public String getVisitType()
    {
        return visitType;
    }

    public void setVisitType(String visitType)
    {
        this.visitType = visitType;
    }

    public String getBasicDisease()
    {
        return basicDisease;
    }

    public void setBasicDisease(String basicDisease)
    {
        this.basicDisease = basicDisease;
    }

    public String getAllergyHistoryType()
    {
        return allergyHistoryType;
    }

    public void setAllergyHistoryType(String allergyHistoryType)
    {
        this.allergyHistoryType = allergyHistoryType;
    }

    public String getAllergyHistoryDetail()
    {
        return allergyHistoryDetail;
    }

    public void setAllergyHistoryDetail(String allergyHistoryDetail)
    {
        this.allergyHistoryDetail = allergyHistoryDetail;
    }

    public String getPastHistoryType()
    {
        return pastHistoryType;
    }

    public void setPastHistoryType(String pastHistoryType)
    {
        this.pastHistoryType = pastHistoryType;
    }

    public String getPastHistoryDetail()
    {
        return pastHistoryDetail;
    }

    public void setPastHistoryDetail(String pastHistoryDetail)
    {
        this.pastHistoryDetail = pastHistoryDetail;
    }

    public String getCurrentMedicationType()
    {
        return currentMedicationType;
    }

    public void setCurrentMedicationType(String currentMedicationType)
    {
        this.currentMedicationType = currentMedicationType;
    }

    public String getCurrentMedicationDetail()
    {
        return currentMedicationDetail;
    }

    public void setCurrentMedicationDetail(String currentMedicationDetail)
    {
        this.currentMedicationDetail = currentMedicationDetail;
    }

    public String getChiefComplaint()
    {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint)
    {
        this.chiefComplaint = chiefComplaint;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public Integer getSeverityLevel()
    {
        return severityLevel;
    }

    public void setSeverityLevel(Integer severityLevel)
    {
        this.severityLevel = severityLevel;
    }

    public String getSuddenWorse()
    {
        return suddenWorse;
    }

    public void setSuddenWorse(String suddenWorse)
    {
        this.suddenWorse = suddenWorse;
    }

    public String getLifeImpact()
    {
        return lifeImpact;
    }

    public void setLifeImpact(String lifeImpact)
    {
        this.lifeImpact = lifeImpact;
    }

    public String getHighestRiskLevel()
    {
        return highestRiskLevel;
    }

    public void setHighestRiskLevel(String highestRiskLevel)
    {
        this.highestRiskLevel = highestRiskLevel;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getDoctorId()
    {
        return doctorId;
    }

    public void setDoctorId(Long doctorId)
    {
        this.doctorId = doctorId;
    }

    public String getDoctorName()
    {
        return doctorName;
    }

    public void setDoctorName(String doctorName)
    {
        this.doctorName = doctorName;
    }

    public String getDoctorAdvice()
    {
        return doctorAdvice;
    }

    public void setDoctorAdvice(String doctorAdvice)
    {
        this.doctorAdvice = doctorAdvice;
    }

    public String getDoctorSummary()
    {
        return doctorSummary;
    }

    public void setDoctorSummary(String doctorSummary)
    {
        this.doctorSummary = doctorSummary;
    }

    public String getSuggestedDepartment()
    {
        return suggestedDepartment;
    }

    public void setSuggestedDepartment(String suggestedDepartment)
    {
        this.suggestedDepartment = suggestedDepartment;
    }

    public Boolean getNeedOfflineVisit()
    {
        return needOfflineVisit;
    }

    public void setNeedOfflineVisit(Boolean needOfflineVisit)
    {
        this.needOfflineVisit = needOfflineVisit;
    }

    public Boolean getNeedSupplement()
    {
        return needSupplement;
    }

    public void setNeedSupplement(Boolean needSupplement)
    {
        this.needSupplement = needSupplement;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDiseaseSummary()
    {
        return diseaseSummary;
    }

    public void setDiseaseSummary(String diseaseSummary)
    {
        this.diseaseSummary = diseaseSummary;
    }

    public List<TcmConsultReportDisease> getDiseaseList()
    {
        return diseaseList;
    }

    public void setDiseaseList(List<TcmConsultReportDisease> diseaseList)
    {
        this.diseaseList = diseaseList;
    }

    public List<TcmConsultReportAttachment> getAttachmentList()
    {
        return attachmentList;
    }

    public void setAttachmentList(List<TcmConsultReportAttachment> attachmentList)
    {
        this.attachmentList = attachmentList;
    }
}

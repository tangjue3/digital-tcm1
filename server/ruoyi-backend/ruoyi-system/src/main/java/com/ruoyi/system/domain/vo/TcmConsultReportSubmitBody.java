package com.ruoyi.system.domain.vo;

import java.util.List;

/**
 * App 端提交问诊报告请求体
 */
public class TcmConsultReportSubmitBody
{
    private String patientName;

    private String gender;

    private Integer age;

    private String visitType;

    private String basicDisease;

    private String allergyHistoryType;

    private String allergyHistoryDetail;

    private String pastHistoryType;

    private String pastHistoryDetail;

    private String currentMedicationType;

    private String currentMedicationDetail;

    private String chiefComplaint;

    private String duration;

    private Integer severityLevel;

    private String suddenWorse;

    private List<String> lifeImpact;

    private List<SelectedDisease> selectedDiseases;

    private List<SelectedAttachment> attachments;

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

    public List<String> getLifeImpact()
    {
        return lifeImpact;
    }

    public void setLifeImpact(List<String> lifeImpact)
    {
        this.lifeImpact = lifeImpact;
    }

    public List<SelectedDisease> getSelectedDiseases()
    {
        return selectedDiseases;
    }

    public void setSelectedDiseases(List<SelectedDisease> selectedDiseases)
    {
        this.selectedDiseases = selectedDiseases;
    }

    public List<SelectedAttachment> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(List<SelectedAttachment> attachments)
    {
        this.attachments = attachments;
    }

    public static class SelectedDisease
    {
        private String code;

        private String name;

        private String path;

        private String riskLevel;

        private String riskReason;

        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getPath()
        {
            return path;
        }

        public void setPath(String path)
        {
            this.path = path;
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
    }

    public static class SelectedAttachment
    {
        private String fileType;

        private String fileName;

        private String fileUrl;

        private Long fileSize;

        public String getFileType()
        {
            return fileType;
        }

        public void setFileType(String fileType)
        {
            this.fileType = fileType;
        }

        public String getFileName()
        {
            return fileName;
        }

        public void setFileName(String fileName)
        {
            this.fileName = fileName;
        }

        public String getFileUrl()
        {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl)
        {
            this.fileUrl = fileUrl;
        }

        public Long getFileSize()
        {
            return fileSize;
        }

        public void setFileSize(Long fileSize)
        {
            this.fileSize = fileSize;
        }
    }
}

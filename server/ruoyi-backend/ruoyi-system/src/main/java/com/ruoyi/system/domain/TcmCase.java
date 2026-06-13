package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 实验案例库对象 tcm_case
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public class TcmCase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 案例名称 */
    @Excel(name = "案例名称")
    private String caseName;

    /** 案例编号 */
    @Excel(name = "案例编号")
    private String caseNo;

    /** 案例描述 */
    @Excel(name = "案例描述")
    private String caseRemark;

    /** 案例图片 */
    @Excel(name = "案例图片")
    private String picture;

    /** 诊断结果 */
    @Excel(name = "诊断结果")
    private String results;

    /** 治疗方案 */
    @Excel(name = "治疗方案")
    private String treatmentMethods;

    /** 备用字段1 */
    @Excel(name = "备用字段1")
    private String extend1;

    /** 备用字段2 */
    @Excel(name = "备用字段2")
    private String extend2;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCaseName(String caseName) 
    {
        this.caseName = caseName;
    }

    public String getCaseName() 
    {
        return caseName;
    }
    public void setCaseNo(String caseNo) 
    {
        this.caseNo = caseNo;
    }

    public String getCaseNo() 
    {
        return caseNo;
    }
    public void setCaseRemark(String caseRemark) 
    {
        this.caseRemark = caseRemark;
    }

    public String getCaseRemark() 
    {
        return caseRemark;
    }
    public void setPicture(String picture) 
    {
        this.picture = picture;
    }

    public String getPicture() 
    {
        return picture;
    }
    public void setResults(String results) 
    {
        this.results = results;
    }

    public String getResults() 
    {
        return results;
    }
    public void setTreatmentMethods(String treatmentMethods) 
    {
        this.treatmentMethods = treatmentMethods;
    }

    public String getTreatmentMethods() 
    {
        return treatmentMethods;
    }
    public void setExtend1(String extend1) 
    {
        this.extend1 = extend1;
    }

    public String getExtend1() 
    {
        return extend1;
    }
    public void setExtend2(String extend2) 
    {
        this.extend2 = extend2;
    }

    public String getExtend2() 
    {
        return extend2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("caseName", getCaseName())
            .append("caseNo", getCaseNo())
            .append("caseRemark", getCaseRemark())
            .append("picture", getPicture())
            .append("results", getResults())
            .append("treatmentMethods", getTreatmentMethods())
            .append("remark", getRemark())
            .append("extend1", getExtend1())
            .append("extend2", getExtend2())
            .toString();
    }
}

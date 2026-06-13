package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 实验案例记录对象 tcm_case_records
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public class TcmCaseRecords extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 实验人员 */
    @Excel(name = "实验人员")
    private String userId;

    /** 实验案例编号 */
    @Excel(name = "实验案例编号")
    private String caseNo;

    /** 实验唯一编号 */
    @Excel(name = "实验唯一编号")
    private String questionNo;

    /** 分数 */
    @Excel(name = "分数")
    private String score;

    /** 实验时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实验时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    /** 诊断结论 */
    @Excel(name = "诊断结论")
    private String conclusion;

    /** 治疗手段 */
    @Excel(name = "治疗手段")
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
    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }
    public void setCaseNo(String caseNo) 
    {
        this.caseNo = caseNo;
    }

    public String getCaseNo() 
    {
        return caseNo;
    }
    public void setQuestionNo(String questionNo) 
    {
        this.questionNo = questionNo;
    }

    public String getQuestionNo() 
    {
        return questionNo;
    }
    public void setScore(String score) 
    {
        this.score = score;
    }

    public String getScore() 
    {
        return score;
    }
    public void setTime(Date time) 
    {
        this.time = time;
    }

    public Date getTime() 
    {
        return time;
    }
    public void setConclusion(String conclusion) 
    {
        this.conclusion = conclusion;
    }

    public String getConclusion() 
    {
        return conclusion;
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
            .append("userId", getUserId())
            .append("caseNo", getCaseNo())
            .append("questionNo", getQuestionNo())
            .append("score", getScore())
            .append("time", getTime())
            .append("conclusion", getConclusion())
            .append("treatmentMethods", getTreatmentMethods())
            .append("remark", getRemark())
            .append("extend1", getExtend1())
            .append("extend2", getExtend2())
            .toString();
    }
}

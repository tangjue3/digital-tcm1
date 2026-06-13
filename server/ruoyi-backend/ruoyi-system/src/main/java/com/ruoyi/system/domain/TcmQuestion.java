package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 案例题库对象 tcm_question
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public class TcmQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 所属案例 */
    @Excel(name = "所属案例")
    private String caseNo;

    /** 所属模块 */
    @Excel(name = "所属模块")
    private String module;

    /** 题目名称 */
    @Excel(name = "题目名称")
    private String questionsName;

    /** 选项A */
    @Excel(name = "选项A")
    private String optionA;

    /** 选项B */
    @Excel(name = "选项B")
    private String optionB;

    /** 选项C */
    @Excel(name = "选项C")
    private String optionC;

    /** 选项D */
    @Excel(name = "选项D")
    private String optionD;

    /** 答案 */
    @Excel(name = "答案")
    private String questionsAnswer;

    /** 图片、视频、语言文件url */
    @Excel(name = "图片、视频、语言文件url")
    private String imageUrl;

    /** 链接类型 */
    @Excel(name = "链接类型")
    private String urlType;

    /** 扩展字段1 */
    @Excel(name = "扩展字段1")
    private String extend1;

    /** 扩展字段2 */
    @Excel(name = "扩展字段2")
    private String extend2;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCaseNo(String caseNo) 
    {
        this.caseNo = caseNo;
    }

    public String getCaseNo() 
    {
        return caseNo;
    }
    public void setModule(String module) 
    {
        this.module = module;
    }

    public String getModule() 
    {
        return module;
    }
    public void setQuestionsName(String questionsName) 
    {
        this.questionsName = questionsName;
    }

    public String getQuestionsName() 
    {
        return questionsName;
    }
    public void setOptionA(String optionA) 
    {
        this.optionA = optionA;
    }

    public String getOptionA() 
    {
        return optionA;
    }
    public void setOptionB(String optionB) 
    {
        this.optionB = optionB;
    }

    public String getOptionB() 
    {
        return optionB;
    }
    public void setOptionC(String optionC) 
    {
        this.optionC = optionC;
    }

    public String getOptionC() 
    {
        return optionC;
    }
    public void setOptionD(String optionD) 
    {
        this.optionD = optionD;
    }

    public String getOptionD() 
    {
        return optionD;
    }
    public void setQuestionsAnswer(String questionsAnswer) 
    {
        this.questionsAnswer = questionsAnswer;
    }

    public String getQuestionsAnswer() 
    {
        return questionsAnswer;
    }
    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }
    public void setUrlType(String urlType) 
    {
        this.urlType = urlType;
    }

    public String getUrlType() 
    {
        return urlType;
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
            .append("caseNo", getCaseNo())
            .append("module", getModule())
            .append("questionsName", getQuestionsName())
            .append("optionA", getOptionA())
            .append("optionB", getOptionB())
            .append("optionC", getOptionC())
            .append("optionD", getOptionD())
            .append("questionsAnswer", getQuestionsAnswer())
            .append("imageUrl", getImageUrl())
            .append("remark", getRemark())
            .append("urlType", getUrlType())
            .append("extend1", getExtend1())
            .append("extend2", getExtend2())
            .toString();
    }
}

package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 常见疾病对象 commondiseases
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public class Commondiseases extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键编号 */
    private Long id;

    /** 疾病名称 */
    @Excel(name = "疾病名称")
    private String name;

    /** 疾病介绍和体征表现 */
    @Excel(name = "疾病介绍和体征表现")
    private String introduction;

    /** 中医治疗方法 */
    @Excel(name = "中医治疗方法")
    private String method;

    /** 预防措施和注意事项 */
    @Excel(name = "预防措施和注意事项")
    private String preventive;

    /** 常见发病季节 */
    @Excel(name = "常见发病季节")
    private String onsetseasons;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setIntroduction(String introduction) 
    {
        this.introduction = introduction;
    }

    public String getIntroduction() 
    {
        return introduction;
    }
    public void setMethod(String method) 
    {
        this.method = method;
    }

    public String getMethod() 
    {
        return method;
    }
    public void setPreventive(String preventive) 
    {
        this.preventive = preventive;
    }

    public String getPreventive() 
    {
        return preventive;
    }
    public void setOnsetseasons(String onsetseasons) 
    {
        this.onsetseasons = onsetseasons;
    }

    public String getOnsetseasons() 
    {
        return onsetseasons;
    }

    /** ISLab 前端历史字段名是 OnsetSeasons，保持 JSON 大小写兼容。 */
    @JsonProperty("OnsetSeasons")
    public String getOnsetSeasons()
    {
        return onsetseasons;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("introduction", getIntroduction())
            .append("method", getMethod())
            .append("preventive", getPreventive())
            .append("onsetseasons", getOnsetseasons())
            .toString();
    }
}

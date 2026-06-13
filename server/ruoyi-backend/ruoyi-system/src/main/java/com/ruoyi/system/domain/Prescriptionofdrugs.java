package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 药方对象 prescriptionofdrugs
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public class Prescriptionofdrugs extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键编号 */
    private Long id;

    /** 药物名称 */
    @Excel(name = "药物名称")
    private String name;

    /** 药物图片地址 */
    @Excel(name = "药物图片地址")
    private String imgurl;

    /** 药物组成 */
    @Excel(name = "药物组成")
    private String composition;

    /** 功能 */
    @Excel(name = "功能")
    private String func;

    /** 主治 */
    @Excel(name = "主治")
    private String indicationoftreatment;

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
    public void setImgurl(String imgurl) 
    {
        this.imgurl = imgurl;
    }

    public String getImgurl() 
    {
        return imgurl;
    }
    public void setComposition(String composition) 
    {
        this.composition = composition;
    }

    public String getComposition() 
    {
        return composition;
    }
    public void setFunc(String func) 
    {
        this.func = func;
    }

    public String getFunc() 
    {
        return func;
    }

    /** ISLab 前端历史字段名是 Func，保留 func 的同时额外输出 Func。 */
    @JsonProperty("Func")
    public String getFuncUpper()
    {
        return func;
    }
    public void setIndicationoftreatment(String indicationoftreatment) 
    {
        this.indicationoftreatment = indicationoftreatment;
    }

    public String getIndicationoftreatment() 
    {
        return indicationoftreatment;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("imgurl", getImgurl())
            .append("composition", getComposition())
            .append("func", getFunc())
            .append("indicationoftreatment", getIndicationoftreatment())
            .toString();
    }
}

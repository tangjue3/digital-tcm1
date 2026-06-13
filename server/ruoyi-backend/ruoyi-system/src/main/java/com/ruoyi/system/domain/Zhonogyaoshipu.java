package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 中药食谱对象 zhonogyaoshipu
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public class Zhonogyaoshipu extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 药膳名 */
    @Excel(name = "药膳名")
    private String name;

    /** 图片链接 */
    @Excel(name = "图片链接")
    private String imageurl;

    /** 药材组成 */
    @Excel(name = "药材组成")
    private String yaocai;

    /** 食材 */
    @Excel(name = "食材")
    private String shicai;

    /** 做法 */
    @Excel(name = "做法")
    private String zuofa;

    /** 功效 */
    @Excel(name = "功效")
    private String gongxiao;

    /** 食用方法 */
    @Excel(name = "食用方法")
    private String shiyongfangfa;

    /** 禁忌 */
    @Excel(name = "禁忌")
    private String jinji;

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
    public void setImageurl(String imageurl) 
    {
        this.imageurl = imageurl;
    }

    public String getImageurl() 
    {
        return imageurl;
    }
    public void setYaocai(String yaocai) 
    {
        this.yaocai = yaocai;
    }

    public String getYaocai() 
    {
        return yaocai;
    }
    public void setShicai(String shicai) 
    {
        this.shicai = shicai;
    }

    public String getShicai() 
    {
        return shicai;
    }
    public void setZuofa(String zuofa) 
    {
        this.zuofa = zuofa;
    }

    public String getZuofa() 
    {
        return zuofa;
    }
    public void setGongxiao(String gongxiao) 
    {
        this.gongxiao = gongxiao;
    }

    public String getGongxiao() 
    {
        return gongxiao;
    }
    public void setShiyongfangfa(String shiyongfangfa) 
    {
        this.shiyongfangfa = shiyongfangfa;
    }

    public String getShiyongfangfa() 
    {
        return shiyongfangfa;
    }
    public void setJinji(String jinji) 
    {
        this.jinji = jinji;
    }

    public String getJinji() 
    {
        return jinji;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("imageurl", getImageurl())
            .append("yaocai", getYaocai())
            .append("shicai", getShicai())
            .append("zuofa", getZuofa())
            .append("gongxiao", getGongxiao())
            .append("shiyongfangfa", getShiyongfangfa())
            .append("jinji", getJinji())
            .toString();
    }
}

package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 中药材对象 chinesemedicine
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public class Chinesemedicine extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 中药名 */
    @Excel(name = "中药名")
    private String name;

    /** 照片地址 */
    @Excel(name = "照片地址")
    private String imageurl;

    /** 别名 */
    @Excel(name = "别名")
    private String otherName;

    /** 来源 */
    @Excel(name = "来源")
    private String source;

    /** 形态 */
    @Excel(name = "形态")
    private String xingtai;

    /** 产地 */
    @Excel(name = "产地")
    private String chandi;

    /** 形状 */
    @Excel(name = "形状")
    private String xingzhuang;

    /** 性味归经 */
    @Excel(name = "性味归经")
    private String xingweiguijing;

    /** 功效 */
    @Excel(name = "功效")
    private String gongxiao;

    /** 临床作用 */
    @Excel(name = "临床作用")
    private String linchuangzuoyong;

    /** 药理研究 */
    @Excel(name = "药理研究")
    private String yaoliyanjiu;

    /** 化学组成 */
    @Excel(name = "化学组成")
    private String huaxuezucheng;

    /** 使用禁忌 */
    @Excel(name = "使用禁忌")
    private String jinji;

    /** 药物配方 */
    @Excel(name = "药物配方")
    private String yaowupeifang;

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
    public void setOtherName(String otherName) 
    {
        this.otherName = otherName;
    }

    public String getOtherName() 
    {
        return otherName;
    }
    public void setSource(String source) 
    {
        this.source = source;
    }

    public String getSource() 
    {
        return source;
    }
    public void setXingtai(String xingtai) 
    {
        this.xingtai = xingtai;
    }

    public String getXingtai() 
    {
        return xingtai;
    }
    public void setChandi(String chandi) 
    {
        this.chandi = chandi;
    }

    public String getChandi() 
    {
        return chandi;
    }
    public void setXingzhuang(String xingzhuang) 
    {
        this.xingzhuang = xingzhuang;
    }

    public String getXingzhuang() 
    {
        return xingzhuang;
    }
    public void setXingweiguijing(String xingweiguijing) 
    {
        this.xingweiguijing = xingweiguijing;
    }

    public String getXingweiguijing() 
    {
        return xingweiguijing;
    }
    public void setGongxiao(String gongxiao) 
    {
        this.gongxiao = gongxiao;
    }

    public String getGongxiao() 
    {
        return gongxiao;
    }
    public void setLinchuangzuoyong(String linchuangzuoyong) 
    {
        this.linchuangzuoyong = linchuangzuoyong;
    }

    public String getLinchuangzuoyong() 
    {
        return linchuangzuoyong;
    }
    public void setYaoliyanjiu(String yaoliyanjiu) 
    {
        this.yaoliyanjiu = yaoliyanjiu;
    }

    public String getYaoliyanjiu() 
    {
        return yaoliyanjiu;
    }
    public void setHuaxuezucheng(String huaxuezucheng) 
    {
        this.huaxuezucheng = huaxuezucheng;
    }

    public String getHuaxuezucheng() 
    {
        return huaxuezucheng;
    }
    public void setJinji(String jinji) 
    {
        this.jinji = jinji;
    }

    public String getJinji() 
    {
        return jinji;
    }
    public void setYaowupeifang(String yaowupeifang) 
    {
        this.yaowupeifang = yaowupeifang;
    }

    public String getYaowupeifang() 
    {
        return yaowupeifang;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("imageurl", getImageurl())
            .append("otherName", getOtherName())
            .append("source", getSource())
            .append("xingtai", getXingtai())
            .append("chandi", getChandi())
            .append("xingzhuang", getXingzhuang())
            .append("xingweiguijing", getXingweiguijing())
            .append("gongxiao", getGongxiao())
            .append("linchuangzuoyong", getLinchuangzuoyong())
            .append("yaoliyanjiu", getYaoliyanjiu())
            .append("huaxuezucheng", getHuaxuezucheng())
            .append("jinji", getJinji())
            .append("yaowupeifang", getYaowupeifang())
            .toString();
    }
}

package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 人体穴位对象 tcm_acupoints
 * 
 * @author ruoyi
 * @date 2025-03-28
 */
public class TcmAcupoints extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /**  穴位名称 */
    @Excel(name = " 穴位名称")
    private String acupointName;

    /** 所属经络名称 */
    @Excel(name = "所属经络名称")
    private String meridianName;

    /** 穴位位置描述 */
    @Excel(name = "穴位位置描述")
    private String locationDescription;

    /** 穴位功能描述 */
    @Excel(name = "穴位功能描述")
    private String functionDescription;

    /** 常见用途（如治疗病症等） */
    @Excel(name = "常见用途", readConverterExp = "如=治疗病症等")
    private String commonUsage;

    /** 穴位相关图片的 URL */
    @Excel(name = "穴位相关图片的 URL")
    private String pictureUrl;

    /** 针刺深度（单位：寸等，可根据实际调整） */
    @Excel(name = "针刺深度", readConverterExp = "单=位：寸等，可根据实际调整")
    private BigDecimal acupunctureDepth;

    /** 艾灸时间（分钟） */
    @Excel(name = "艾灸时间", readConverterExp = "分=钟")
    private Long moxibustionTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAcupointName(String acupointName) 
    {
        this.acupointName = acupointName;
    }

    public String getAcupointName() 
    {
        return acupointName;
    }
    public void setMeridianName(String meridianName) 
    {
        this.meridianName = meridianName;
    }

    public String getMeridianName() 
    {
        return meridianName;
    }
    public void setLocationDescription(String locationDescription) 
    {
        this.locationDescription = locationDescription;
    }

    public String getLocationDescription() 
    {
        return locationDescription;
    }
    public void setFunctionDescription(String functionDescription) 
    {
        this.functionDescription = functionDescription;
    }

    public String getFunctionDescription() 
    {
        return functionDescription;
    }
    public void setCommonUsage(String commonUsage) 
    {
        this.commonUsage = commonUsage;
    }

    public String getCommonUsage() 
    {
        return commonUsage;
    }
    public void setPictureUrl(String pictureUrl) 
    {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrl() 
    {
        return pictureUrl;
    }
    public void setAcupunctureDepth(BigDecimal acupunctureDepth) 
    {
        this.acupunctureDepth = acupunctureDepth;
    }

    public BigDecimal getAcupunctureDepth() 
    {
        return acupunctureDepth;
    }
    public void setMoxibustionTime(Long moxibustionTime) 
    {
        this.moxibustionTime = moxibustionTime;
    }

    public Long getMoxibustionTime() 
    {
        return moxibustionTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("acupointName", getAcupointName())
            .append("meridianName", getMeridianName())
            .append("locationDescription", getLocationDescription())
            .append("functionDescription", getFunctionDescription())
            .append("commonUsage", getCommonUsage())
            .append("pictureUrl", getPictureUrl())
            .append("acupunctureDepth", getAcupunctureDepth())
            .append("moxibustionTime", getMoxibustionTime())
            .toString();
    }
}

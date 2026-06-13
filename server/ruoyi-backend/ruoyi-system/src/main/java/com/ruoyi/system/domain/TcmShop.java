package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 中医房对象 tcm_shop
 * 
 * @author ruoyi
 * @date 2024-06-03
 */
public class TcmShop extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 医药馆或者医院名称 */
    @Excel(name = "医药馆或者医院名称")
    private String name;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String address;

    /** 坐标 */
    @Excel(name = "坐标")
    private String coordinate;

    /** 介绍 */
    @Excel(name = "介绍")
    private String introduce;

    /** 药店照片 */
    @Excel(name = "药店照片")
    private String coverImage;

    /** 所在市 */
    @Excel(name = "所在市")
    private String city;

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
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setCoordinate(String coordinate) 
    {
        this.coordinate = coordinate;
    }

    public String getCoordinate() 
    {
        return coordinate;
    }
    public void setIntroduce(String introduce) 
    {
        this.introduce = introduce;
    }

    public String getIntroduce() 
    {
        return introduce;
    }
    public void setCoverImage(String coverImage) 
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage() 
    {
        return coverImage;
    }
    public void setCity(String city) 
    {
        this.city = city;
    }

    public String getCity() 
    {
        return city;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("address", getAddress())
            .append("coordinate", getCoordinate())
            .append("introduce", getIntroduce())
            .append("coverImage", getCoverImage())
            .append("city", getCity())
            .toString();
    }
}

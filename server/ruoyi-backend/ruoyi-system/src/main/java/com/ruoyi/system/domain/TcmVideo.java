package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 中医药视频对象 tcm_video
 * 
 * @author ruoyi
 * @date 2024-06-03
 */
public class TcmVideo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long id;

    /** 视频名称 */
    @Excel(name = "视频名称")
    private String name;

    /** 视频介绍 */
    @Excel(name = "视频介绍")
    private String introduce;

    /** 视频所存路径 */
    @Excel(name = "视频所存路径")
    private String path;

    /** 视频类别 */
    @Excel(name = "视频类别")
    private String category;

    /** 播放次数 */
    @Excel(name = "播放次数")
    private Long count;

    /** 封面图路径 */
    @Excel(name = "封面图路径")
    private String coverImage;

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
    public void setIntroduce(String introduce) 
    {
        this.introduce = introduce;
    }

    public String getIntroduce() 
    {
        return introduce;
    }
    public void setPath(String path) 
    {
        this.path = path;
    }

    public String getPath() 
    {
        return path;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setCount(Long count) 
    {
        this.count = count;
    }

    public Long getCount() 
    {
        return count;
    }
    public void setCoverImage(String coverImage) 
    {
        this.coverImage = coverImage;
    }

    public String getCoverImage() 
    {
        return coverImage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("introduce", getIntroduce())
            .append("path", getPath())
            .append("category", getCategory())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("count", getCount())
            .append("coverImage", getCoverImage())
            .toString();
    }
}

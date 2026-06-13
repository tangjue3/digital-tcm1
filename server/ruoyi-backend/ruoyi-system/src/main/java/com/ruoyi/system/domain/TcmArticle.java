package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 中医好文对象 tcm_article
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public class TcmArticle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 文章标题 */
    @Excel(name = "文章标题")
    private String articleTitle;

    /** 文章类型 */
    @Excel(name = "文章类型")
    private String articleType;

    /** 文章图片地址 */
    @Excel(name = "文章图片地址")
    private String imgurl;

    /** 公告内容 */
    @Excel(name = "公告内容")
    private String articleContent;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setArticleTitle(String articleTitle) 
    {
        this.articleTitle = articleTitle;
    }

    public String getArticleTitle() 
    {
        return articleTitle;
    }
    public void setArticleType(String articleType) 
    {
        this.articleType = articleType;
    }

    public String getArticleType() 
    {
        return articleType;
    }
    public void setImgurl(String imgurl) 
    {
        this.imgurl = imgurl;
    }

    public String getImgurl() 
    {
        return imgurl;
    }
    public void setArticleContent(String articleContent) 
    {
        this.articleContent = articleContent;
    }

    public String getArticleContent() 
    {
        return articleContent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("articleTitle", getArticleTitle())
            .append("articleType", getArticleType())
            .append("imgurl", getImgurl())
            .append("articleContent", getArticleContent())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

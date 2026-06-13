package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * App user collected profile.
 */
public class TcmAppUserProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long profileId;

    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "用户账号")
    private String userName;

    @Excel(name = "用户昵称")
    private String nickName;

    @Excel(name = "手机号")
    private String phonenumber;

    @Excel(name = "性别")
    private String sex;

    @Excel(name = "年龄")
    private Integer age;

    @Excel(name = "声音文件")
    private String voiceFile;

    @Excel(name = "舌苔图片")
    private String tongueImage;

    @Excel(name = "面部图片")
    private String faceImage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date collectTime;

    public Long getProfileId()
    {
        return profileId;
    }

    public void setProfileId(Long profileId)
    {
        this.profileId = profileId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public String getVoiceFile()
    {
        return voiceFile;
    }

    public void setVoiceFile(String voiceFile)
    {
        this.voiceFile = voiceFile;
    }

    public String getTongueImage()
    {
        return tongueImage;
    }

    public void setTongueImage(String tongueImage)
    {
        this.tongueImage = tongueImage;
    }

    public String getFaceImage()
    {
        return faceImage;
    }

    public void setFaceImage(String faceImage)
    {
        this.faceImage = faceImage;
    }

    public Date getCollectTime()
    {
        return collectTime;
    }

    public void setCollectTime(Date collectTime)
    {
        this.collectTime = collectTime;
    }
}

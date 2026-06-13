package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Pulse record collected from app or wearable device.
 */
public class TcmPulseRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "User ID")
    private Long userId;

    @Excel(name = "User Account")
    private String userName;

    @Excel(name = "Nickname")
    private String nickName;

    @Excel(name = "Phone")
    private String phonenumber;

    @Excel(name = "Device ID")
    private String deviceId;

    @Excel(name = "Pulse Rate")
    private Integer pulseRate;

    @Excel(name = "Signal Quality")
    private Integer signalQuality;

    private String rawData;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "Sample Time", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sampledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "Receive Time", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date receivedAt;

    @Excel(name = "Source")
    private String source;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public Integer getPulseRate()
    {
        return pulseRate;
    }

    public void setPulseRate(Integer pulseRate)
    {
        this.pulseRate = pulseRate;
    }

    public Integer getSignalQuality()
    {
        return signalQuality;
    }

    public void setSignalQuality(Integer signalQuality)
    {
        this.signalQuality = signalQuality;
    }

    public String getRawData()
    {
        return rawData;
    }

    public void setRawData(String rawData)
    {
        this.rawData = rawData;
    }

    public Date getSampledAt()
    {
        return sampledAt;
    }

    public void setSampledAt(Date sampledAt)
    {
        this.sampledAt = sampledAt;
    }

    public Date getReceivedAt()
    {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt)
    {
        this.receivedAt = receivedAt;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }
}

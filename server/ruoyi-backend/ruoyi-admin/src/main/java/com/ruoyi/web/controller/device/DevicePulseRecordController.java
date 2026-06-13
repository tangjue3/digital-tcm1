package com.ruoyi.web.controller.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TcmPulseRecord;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.ITcmPulseRecordService;

/**
 * Device-facing pulse upload API.
 */
@RestController
@RequestMapping("/device/pulse")
public class DevicePulseRecordController extends BaseController
{
    @Autowired
    private ITcmPulseRecordService pulseRecordService;

    @Autowired
    private ISysUserService userService;

    @PostMapping("/upload")
    public AjaxResult upload(@RequestBody TcmPulseRecord record)
    {
        if (record.getPulseRate() == null)
        {
            return AjaxResult.error("pulseRate is required");
        }
        if (record.getPulseRate() < 30 || record.getPulseRate() > 220)
        {
            return AjaxResult.error("pulseRate must be between 30 and 220");
        }
        if (record.getSignalQuality() != null && (record.getSignalQuality() < 0 || record.getSignalQuality() > 100))
        {
            return AjaxResult.error("signalQuality must be between 0 and 100");
        }
        if (StringUtils.isEmpty(record.getDeviceId()))
        {
            return AjaxResult.error("deviceId is required");
        }

        SysUser user = resolveUser(record);
        if (user == null)
        {
            return AjaxResult.error("userId or phonenumber does not match an existing user");
        }

        record.setUserId(user.getUserId());
        if (StringUtils.isEmpty(record.getSource()))
        {
            record.setSource("device");
        }
        return toAjax(pulseRecordService.insertPulseRecord(record));
    }

    private SysUser resolveUser(TcmPulseRecord record)
    {
        if (record.getUserId() != null)
        {
            return userService.selectUserById(record.getUserId());
        }
        if (StringUtils.isNotEmpty(record.getPhonenumber()))
        {
            return userService.selectUserByPhonenumber(record.getPhonenumber());
        }
        return null;
    }
}

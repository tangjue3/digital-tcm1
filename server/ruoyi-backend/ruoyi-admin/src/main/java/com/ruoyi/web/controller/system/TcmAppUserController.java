package com.ruoyi.web.controller.system;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TcmAppUserProfile;
import com.ruoyi.system.service.ITcmAppUserProfileService;
import com.ruoyi.system.service.ISysUserService;

/**
 * App user management for collected user profile and media.
 */
@RestController
@RequestMapping("/system/appUser")
public class TcmAppUserController extends BaseController
{
    @Autowired
    private ITcmAppUserProfileService appUserProfileService;

    @Autowired
    private ISysUserService sysUserService;

    @GetMapping("/list")
    public TableDataInfo list(TcmAppUserProfile profile,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime)
    {
        if (StringUtils.isNotEmpty(beginTime))
        {
            profile.getParams().put("beginTime", beginTime);
        }
        if (StringUtils.isNotEmpty(endTime))
        {
            profile.getParams().put("endTime", endTime);
        }
        startPage();
        List<TcmAppUserProfile> list;
        try
        {
            list = appUserProfileService.selectAppUserProfileList(profile);
        }
        catch (RuntimeException e)
        {
            logger.warn("App user profile list query failed, falling back to sys_user list", e);
            list = selectFallbackUsers(profile);
        }
        return getDataTable(list);
    }

    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        try
        {
            TcmAppUserProfile profile = appUserProfileService.selectAppUserProfileByUserId(userId);
            if (profile != null)
            {
                return AjaxResult.success(profile);
            }
        }
        catch (RuntimeException e)
        {
            logger.warn("App user profile detail query failed, falling back to sys_user detail. userId={}", userId, e);
        }

        SysUser user = sysUserService.selectUserById(userId);
        return AjaxResult.success(toProfile(user));
    }

    @DeleteMapping("/{userId}")
    public AjaxResult remove(@PathVariable("userId") Long userId)
    {
        return toAjax(appUserProfileService.deleteAppUserProfileByUserId(userId));
    }

    private List<TcmAppUserProfile> selectFallbackUsers(TcmAppUserProfile profile)
    {
        SysUser query = new SysUser();
        query.setUserName(profile == null ? null : profile.getUserName());
        query.setPhonenumber(profile == null ? null : profile.getPhonenumber());
        List<SysUser> users = sysUserService.selectUserList(query);
        List<TcmAppUserProfile> profiles = new ArrayList<>();
        for (SysUser user : users)
        {
            if (user == null || user.isAdmin())
            {
                continue;
            }
            profiles.add(toProfile(user));
        }
        return profiles;
    }

    private TcmAppUserProfile toProfile(SysUser user)
    {
        if (user == null)
        {
            return null;
        }

        TcmAppUserProfile profile = new TcmAppUserProfile();
        profile.setUserId(user.getUserId());
        profile.setUserName(user.getUserName());
        profile.setNickName(user.getNickName());
        profile.setPhonenumber(user.getPhonenumber());
        profile.setSex(user.getSex());
        profile.setCreateTime(user.getCreateTime());
        profile.setCollectTime(user.getCreateTime());
        return profile;
    }
}

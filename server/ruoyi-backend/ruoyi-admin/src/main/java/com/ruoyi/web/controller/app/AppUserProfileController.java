package com.ruoyi.web.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.TcmAppUserProfile;
import com.ruoyi.system.service.ITcmAppUserProfileService;

/**
 * App user collected profile.
 */
@RestController
@RequestMapping("/app/user")
public class AppUserProfileController extends BaseController
{
    @Autowired
    private ITcmAppUserProfileService appUserProfileService;

    @GetMapping("/profile")
    public AjaxResult profile()
    {
        return AjaxResult.success(appUserProfileService.selectAppUserProfileByUserId(getUserId()));
    }

    @PostMapping("/profile")
    public AjaxResult saveProfile(@RequestBody TcmAppUserProfile profile)
    {
        profile.setUserId(getUserId());
        return toAjax(appUserProfileService.saveAppUserProfile(profile));
    }
}

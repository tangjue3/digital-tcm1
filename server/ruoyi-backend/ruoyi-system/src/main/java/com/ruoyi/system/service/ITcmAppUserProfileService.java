package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TcmAppUserProfile;

/**
 * App user collected profile service.
 */
public interface ITcmAppUserProfileService
{
    public List<TcmAppUserProfile> selectAppUserProfileList(TcmAppUserProfile profile);

    public TcmAppUserProfile selectAppUserProfileByUserId(Long userId);

    public int saveAppUserProfile(TcmAppUserProfile profile);

    public int deleteAppUserProfileByUserId(Long userId);
}

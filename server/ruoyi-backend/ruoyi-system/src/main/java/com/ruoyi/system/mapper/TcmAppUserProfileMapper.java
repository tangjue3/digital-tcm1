package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmAppUserProfile;

/**
 * App user collected profile mapper.
 */
public interface TcmAppUserProfileMapper
{
    public List<TcmAppUserProfile> selectAppUserProfileList(TcmAppUserProfile profile);

    public TcmAppUserProfile selectAppUserProfileByUserId(Long userId);

    public int upsertAppUserProfile(TcmAppUserProfile profile);

    public int deleteAppUserProfileByUserId(Long userId);
}

package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.TcmAppUserProfile;
import com.ruoyi.system.mapper.TcmAppUserProfileMapper;
import com.ruoyi.system.mapper.TcmPulseRecordMapper;
import com.ruoyi.system.service.ITcmAppUserProfileService;

/**
 * App user collected profile service implementation.
 */
@Service
public class TcmAppUserProfileServiceImpl implements ITcmAppUserProfileService
{
    @Autowired
    private TcmAppUserProfileMapper appUserProfileMapper;

    @Autowired
    private TcmPulseRecordMapper pulseRecordMapper;

    @Override
    public List<TcmAppUserProfile> selectAppUserProfileList(TcmAppUserProfile profile)
    {
        return appUserProfileMapper.selectAppUserProfileList(profile);
    }

    @Override
    public TcmAppUserProfile selectAppUserProfileByUserId(Long userId)
    {
        return appUserProfileMapper.selectAppUserProfileByUserId(userId);
    }

    @Override
    public int saveAppUserProfile(TcmAppUserProfile profile)
    {
        return appUserProfileMapper.upsertAppUserProfile(profile);
    }

    @Override
    public int deleteAppUserProfileByUserId(Long userId)
    {
        pulseRecordMapper.deletePulseRecordsByUserId(userId);
        return appUserProfileMapper.deleteAppUserProfileByUserId(userId);
    }
}

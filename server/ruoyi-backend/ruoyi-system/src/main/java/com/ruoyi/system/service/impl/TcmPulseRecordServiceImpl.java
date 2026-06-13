package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TcmPulseRecord;
import com.ruoyi.system.mapper.TcmPulseRecordMapper;
import com.ruoyi.system.service.ITcmPulseRecordService;

/**
 * Pulse record service implementation.
 */
@Service
public class TcmPulseRecordServiceImpl implements ITcmPulseRecordService
{
    @Autowired
    private TcmPulseRecordMapper pulseRecordMapper;

    @Override
    public List<TcmPulseRecord> selectPulseRecordList(TcmPulseRecord record)
    {
        return pulseRecordMapper.selectPulseRecordList(record);
    }

    @Override
    public TcmPulseRecord selectLatestPulseRecordByUserId(Long userId)
    {
        return pulseRecordMapper.selectLatestPulseRecordByUserId(userId);
    }

    @Override
    public TcmPulseRecord selectPulseRecordById(Long id)
    {
        return pulseRecordMapper.selectPulseRecordById(id);
    }

    @Override
    public int insertPulseRecord(TcmPulseRecord record)
    {
        if (record.getSampledAt() == null)
        {
            record.setSampledAt(DateUtils.getNowDate());
        }
        record.setReceivedAt(DateUtils.getNowDate());
        if (StringUtils.isEmpty(record.getSource()))
        {
            record.setSource("app");
        }
        return pulseRecordMapper.insertPulseRecord(record);
    }
}

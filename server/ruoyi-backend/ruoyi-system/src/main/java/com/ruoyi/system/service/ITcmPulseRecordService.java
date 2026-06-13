package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TcmPulseRecord;

/**
 * Pulse record service.
 */
public interface ITcmPulseRecordService
{
    public List<TcmPulseRecord> selectPulseRecordList(TcmPulseRecord record);

    public TcmPulseRecord selectLatestPulseRecordByUserId(Long userId);

    public TcmPulseRecord selectPulseRecordById(Long id);

    public int insertPulseRecord(TcmPulseRecord record);
}

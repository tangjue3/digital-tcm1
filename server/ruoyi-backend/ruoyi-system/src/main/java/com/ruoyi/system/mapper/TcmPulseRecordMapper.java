package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmPulseRecord;

/**
 * Pulse record mapper.
 */
public interface TcmPulseRecordMapper
{
    public List<TcmPulseRecord> selectPulseRecordList(TcmPulseRecord record);

    public TcmPulseRecord selectLatestPulseRecordByUserId(Long userId);

    public TcmPulseRecord selectPulseRecordById(Long id);

    public int insertPulseRecord(TcmPulseRecord record);

    public int deletePulseRecordsByUserId(Long userId);
}

package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmCaseRecords;
import com.ruoyi.system.domain.vo.TcmCaseRecordsVo;
import org.apache.ibatis.annotations.Param;

/**
 * 实验案例记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public interface TcmCaseRecordsMapper 
{
    /**
     * 查询实验案例记录
     * 
     * @param id 实验案例记录主键
     * @return 实验案例记录
     */
    public TcmCaseRecords selectTcmCaseRecordsById(Long id);

    /**
     * 查询实验案例记录列表
     * 
     * @param tcmCaseRecords 实验案例记录
     * @return 实验案例记录集合
     */
    public List<TcmCaseRecords> selectTcmCaseRecordsList(TcmCaseRecords tcmCaseRecords);

    /**
     * 新增实验案例记录
     * 
     * @param tcmCaseRecords 实验案例记录
     * @return 结果
     */
    public int insertTcmCaseRecords(TcmCaseRecords tcmCaseRecords);

    /**
     * 修改实验案例记录
     * 
     * @param tcmCaseRecords 实验案例记录
     * @return 结果
     */
    public int updateTcmCaseRecords(TcmCaseRecords tcmCaseRecords);

    /**
     * 删除实验案例记录
     * 
     * @param id 实验案例记录主键
     * @return 结果
     */
    public int deleteTcmCaseRecordsById(Long id);

    /**
     * 批量删除实验案例记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTcmCaseRecordsByIds(Long[] ids);

    /**
     * 修改实验案例记录
     *
     * @param tcmCaseRecords 实验案例记录
     * @return 结果
     */
    public int updateTcmCaseRecordsByResult(TcmCaseRecords tcmCaseRecords);

    public List<TcmCaseRecords> selectTcmCaseRecordsByCaseNo(@Param("caseNo") String caseNo);

    public List<TcmCaseRecordsVo>getAllCharts();
}

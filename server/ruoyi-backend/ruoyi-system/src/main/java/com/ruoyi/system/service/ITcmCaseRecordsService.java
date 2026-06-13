package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.TcmCaseRecords;
import com.ruoyi.system.domain.vo.TcmCaseRecordsVo;

/**
 * 实验案例记录Service接口
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public interface ITcmCaseRecordsService 
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
     * 批量删除实验案例记录
     * 
     * @param ids 需要删除的实验案例记录主键集合
     * @return 结果
     */
    public int deleteTcmCaseRecordsByIds(Long[] ids);

    /**
     * 删除实验案例记录信息
     * 
     * @param id 实验案例记录主键
     * @return 结果
     */
    public int deleteTcmCaseRecordsById(Long id);

    /**
     * 实验案例记录
     *
     * @param tcmCaseRecords 实验案例记录
     * @return 结果
     */
    public int updateTcmCaseRecordsByResult(TcmCaseRecords tcmCaseRecords);

    public List<TcmCaseRecords> selectTcmCaseRecordsByCaseNo(String caseNo);

    public List<JSONObject>  selectTcmCaseRecordsListByuserId(String userId);

    public Map<String, List<TcmCaseRecordsVo>> getAllCharts();
}

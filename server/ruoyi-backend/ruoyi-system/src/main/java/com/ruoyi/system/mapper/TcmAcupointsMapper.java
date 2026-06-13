package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmAcupoints;

/**
 * 人体穴位Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-28
 */
public interface TcmAcupointsMapper 
{
    /**
     * 查询人体穴位
     * 
     * @param id 人体穴位主键
     * @return 人体穴位
     */
    public TcmAcupoints selectTcmAcupointsById(Long id);

    /**
     * 查询人体穴位列表
     * 
     * @param tcmAcupoints 人体穴位
     * @return 人体穴位集合
     */
    public List<TcmAcupoints> selectTcmAcupointsList(TcmAcupoints tcmAcupoints);

    /**
     * 新增人体穴位
     * 
     * @param tcmAcupoints 人体穴位
     * @return 结果
     */
    public int insertTcmAcupoints(TcmAcupoints tcmAcupoints);

    /**
     * 修改人体穴位
     * 
     * @param tcmAcupoints 人体穴位
     * @return 结果
     */
    public int updateTcmAcupoints(TcmAcupoints tcmAcupoints);

    /**
     * 删除人体穴位
     * 
     * @param id 人体穴位主键
     * @return 结果
     */
    public int deleteTcmAcupointsById(Long id);

    /**
     * 批量删除人体穴位
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTcmAcupointsByIds(Long[] ids);
}

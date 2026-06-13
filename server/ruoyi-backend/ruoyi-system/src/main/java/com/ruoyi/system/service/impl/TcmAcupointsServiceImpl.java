package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TcmAcupointsMapper;
import com.ruoyi.system.domain.TcmAcupoints;
import com.ruoyi.system.service.ITcmAcupointsService;

/**
 * 人体穴位Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-28
 */
@Service
public class TcmAcupointsServiceImpl implements ITcmAcupointsService 
{
    @Autowired
    private TcmAcupointsMapper tcmAcupointsMapper;

    /**
     * 查询人体穴位
     * 
     * @param id 人体穴位主键
     * @return 人体穴位
     */
    @Override
    public TcmAcupoints selectTcmAcupointsById(Long id)
    {
        return tcmAcupointsMapper.selectTcmAcupointsById(id);
    }

    /**
     * 查询人体穴位列表
     * 
     * @param tcmAcupoints 人体穴位
     * @return 人体穴位
     */
    @Override
    public List<TcmAcupoints> selectTcmAcupointsList(TcmAcupoints tcmAcupoints)
    {
        return tcmAcupointsMapper.selectTcmAcupointsList(tcmAcupoints);
    }

    /**
     * 新增人体穴位
     * 
     * @param tcmAcupoints 人体穴位
     * @return 结果
     */
    @Override
    public int insertTcmAcupoints(TcmAcupoints tcmAcupoints)
    {
        return tcmAcupointsMapper.insertTcmAcupoints(tcmAcupoints);
    }

    /**
     * 修改人体穴位
     * 
     * @param tcmAcupoints 人体穴位
     * @return 结果
     */
    @Override
    public int updateTcmAcupoints(TcmAcupoints tcmAcupoints)
    {
        return tcmAcupointsMapper.updateTcmAcupoints(tcmAcupoints);
    }

    /**
     * 批量删除人体穴位
     * 
     * @param ids 需要删除的人体穴位主键
     * @return 结果
     */
    @Override
    public int deleteTcmAcupointsByIds(Long[] ids)
    {
        return tcmAcupointsMapper.deleteTcmAcupointsByIds(ids);
    }

    /**
     * 删除人体穴位信息
     * 
     * @param id 人体穴位主键
     * @return 结果
     */
    @Override
    public int deleteTcmAcupointsById(Long id)
    {
        return tcmAcupointsMapper.deleteTcmAcupointsById(id);
    }
}

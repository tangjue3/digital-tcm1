package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TcmCaseMapper;
import com.ruoyi.system.domain.TcmCase;
import com.ruoyi.system.service.ITcmCaseService;

/**
 * 实验案例库Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@Service
public class TcmCaseServiceImpl implements ITcmCaseService 
{
    @Autowired
    private TcmCaseMapper tcmCaseMapper;

    /**
     * 查询实验案例库
     * 
     * @param id 实验案例库主键
     * @return 实验案例库
     */
    @Override
    public TcmCase selectTcmCaseById(Long id)
    {
        return tcmCaseMapper.selectTcmCaseById(id);
    }

    /**
     * 查询实验案例库列表
     * 
     * @param tcmCase 实验案例库
     * @return 实验案例库
     */
    @Override
    public List<TcmCase> selectTcmCaseList(TcmCase tcmCase)
    {
        return tcmCaseMapper.selectTcmCaseList(tcmCase);
    }

    /**
     * 新增实验案例库
     * 
     * @param tcmCase 实验案例库
     * @return 结果
     */
    @Override
    public int insertTcmCase(TcmCase tcmCase)
    {
        return tcmCaseMapper.insertTcmCase(tcmCase);
    }

    /**
     * 修改实验案例库
     * 
     * @param tcmCase 实验案例库
     * @return 结果
     */
    @Override
    public int updateTcmCase(TcmCase tcmCase)
    {
        return tcmCaseMapper.updateTcmCase(tcmCase);
    }

    /**
     * 批量删除实验案例库
     * 
     * @param ids 需要删除的实验案例库主键
     * @return 结果
     */
    @Override
    public int deleteTcmCaseByIds(Long[] ids)
    {
        return tcmCaseMapper.deleteTcmCaseByIds(ids);
    }

    /**
     * 删除实验案例库信息
     * 
     * @param id 实验案例库主键
     * @return 结果
     */
    @Override
    public int deleteTcmCaseById(Long id)
    {
        return tcmCaseMapper.deleteTcmCaseById(id);
    }
}

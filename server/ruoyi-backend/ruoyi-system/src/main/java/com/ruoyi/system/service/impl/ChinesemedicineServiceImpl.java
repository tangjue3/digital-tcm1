package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ChinesemedicineMapper;
import com.ruoyi.system.domain.Chinesemedicine;
import com.ruoyi.system.service.IChinesemedicineService;

/**
 * 中药材Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
@Service
public class ChinesemedicineServiceImpl implements IChinesemedicineService 
{
    @Autowired
    private ChinesemedicineMapper chinesemedicineMapper;

    /**
     * 查询中药材
     * 
     * @param id 中药材主键
     * @return 中药材
     */
    @Override
    public Chinesemedicine selectChinesemedicineById(Long id)
    {
        return chinesemedicineMapper.selectChinesemedicineById(id);
    }

    /**
     * 查询中药材列表
     * 
     * @param chinesemedicine 中药材
     * @return 中药材
     */
    @Override
    public List<Chinesemedicine> selectChinesemedicineList(Chinesemedicine chinesemedicine)
    {
        return chinesemedicineMapper.selectChinesemedicineList(chinesemedicine);
    }

    /**
     * 新增中药材
     * 
     * @param chinesemedicine 中药材
     * @return 结果
     */
    @Override
    public int insertChinesemedicine(Chinesemedicine chinesemedicine)
    {
        return chinesemedicineMapper.insertChinesemedicine(chinesemedicine);
    }

    /**
     * 修改中药材
     * 
     * @param chinesemedicine 中药材
     * @return 结果
     */
    @Override
    public int updateChinesemedicine(Chinesemedicine chinesemedicine)
    {
        return chinesemedicineMapper.updateChinesemedicine(chinesemedicine);
    }

    /**
     * 批量删除中药材
     * 
     * @param ids 需要删除的中药材主键
     * @return 结果
     */
    @Override
    public int deleteChinesemedicineByIds(Long[] ids)
    {
        return chinesemedicineMapper.deleteChinesemedicineByIds(ids);
    }

    /**
     * 删除中药材信息
     * 
     * @param id 中药材主键
     * @return 结果
     */
    @Override
    public int deleteChinesemedicineById(Long id)
    {
        return chinesemedicineMapper.deleteChinesemedicineById(id);
    }
}

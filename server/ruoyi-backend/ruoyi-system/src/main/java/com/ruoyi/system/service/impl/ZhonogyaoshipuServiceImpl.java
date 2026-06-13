package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ZhonogyaoshipuMapper;
import com.ruoyi.system.domain.Zhonogyaoshipu;
import com.ruoyi.system.service.IZhonogyaoshipuService;

/**
 * 中药食谱Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
@Service
public class ZhonogyaoshipuServiceImpl implements IZhonogyaoshipuService 
{
    @Autowired
    private ZhonogyaoshipuMapper zhonogyaoshipuMapper;

    /**
     * 查询中药食谱
     * 
     * @param id 中药食谱主键
     * @return 中药食谱
     */
    @Override
    public Zhonogyaoshipu selectZhonogyaoshipuById(Long id)
    {
        return zhonogyaoshipuMapper.selectZhonogyaoshipuById(id);
    }

    /**
     * 查询中药食谱列表
     * 
     * @param zhonogyaoshipu 中药食谱
     * @return 中药食谱
     */
    @Override
    public List<Zhonogyaoshipu> selectZhonogyaoshipuList(Zhonogyaoshipu zhonogyaoshipu)
    {
        return zhonogyaoshipuMapper.selectZhonogyaoshipuList(zhonogyaoshipu);
    }

    /**
     * 新增中药食谱
     * 
     * @param zhonogyaoshipu 中药食谱
     * @return 结果
     */
    @Override
    public int insertZhonogyaoshipu(Zhonogyaoshipu zhonogyaoshipu)
    {
        return zhonogyaoshipuMapper.insertZhonogyaoshipu(zhonogyaoshipu);
    }

    /**
     * 修改中药食谱
     * 
     * @param zhonogyaoshipu 中药食谱
     * @return 结果
     */
    @Override
    public int updateZhonogyaoshipu(Zhonogyaoshipu zhonogyaoshipu)
    {
        return zhonogyaoshipuMapper.updateZhonogyaoshipu(zhonogyaoshipu);
    }

    /**
     * 批量删除中药食谱
     * 
     * @param ids 需要删除的中药食谱主键
     * @return 结果
     */
    @Override
    public int deleteZhonogyaoshipuByIds(Long[] ids)
    {
        return zhonogyaoshipuMapper.deleteZhonogyaoshipuByIds(ids);
    }

    /**
     * 删除中药食谱信息
     * 
     * @param id 中药食谱主键
     * @return 结果
     */
    @Override
    public int deleteZhonogyaoshipuById(Long id)
    {
        return zhonogyaoshipuMapper.deleteZhonogyaoshipuById(id);
    }
}

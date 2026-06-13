package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CommondiseasesMapper;
import com.ruoyi.system.domain.Commondiseases;
import com.ruoyi.system.service.ICommondiseasesService;

/**
 * 常见疾病Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
@Service
public class CommondiseasesServiceImpl implements ICommondiseasesService 
{
    @Autowired
    private CommondiseasesMapper commondiseasesMapper;

    /**
     * 查询常见疾病
     * 
     * @param id 常见疾病主键
     * @return 常见疾病
     */
    @Override
    public Commondiseases selectCommondiseasesById(Long id)
    {
        return commondiseasesMapper.selectCommondiseasesById(id);
    }

    /**
     * 查询常见疾病列表
     * 
     * @param commondiseases 常见疾病
     * @return 常见疾病
     */
    @Override
    public List<Commondiseases> selectCommondiseasesList(Commondiseases commondiseases)
    {
        return commondiseasesMapper.selectCommondiseasesList(commondiseases);
    }

    /**
     * 新增常见疾病
     * 
     * @param commondiseases 常见疾病
     * @return 结果
     */
    @Override
    public int insertCommondiseases(Commondiseases commondiseases)
    {
        return commondiseasesMapper.insertCommondiseases(commondiseases);
    }

    /**
     * 修改常见疾病
     * 
     * @param commondiseases 常见疾病
     * @return 结果
     */
    @Override
    public int updateCommondiseases(Commondiseases commondiseases)
    {
        return commondiseasesMapper.updateCommondiseases(commondiseases);
    }

    /**
     * 批量删除常见疾病
     * 
     * @param ids 需要删除的常见疾病主键
     * @return 结果
     */
    @Override
    public int deleteCommondiseasesByIds(Long[] ids)
    {
        return commondiseasesMapper.deleteCommondiseasesByIds(ids);
    }

    /**
     * 删除常见疾病信息
     * 
     * @param id 常见疾病主键
     * @return 结果
     */
    @Override
    public int deleteCommondiseasesById(Long id)
    {
        return commondiseasesMapper.deleteCommondiseasesById(id);
    }
}

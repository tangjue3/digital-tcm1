package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Commondiseases;

/**
 * 常见疾病Service接口
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public interface ICommondiseasesService 
{
    /**
     * 查询常见疾病
     * 
     * @param id 常见疾病主键
     * @return 常见疾病
     */
    public Commondiseases selectCommondiseasesById(Long id);

    /**
     * 查询常见疾病列表
     * 
     * @param commondiseases 常见疾病
     * @return 常见疾病集合
     */
    public List<Commondiseases> selectCommondiseasesList(Commondiseases commondiseases);

    /**
     * 新增常见疾病
     * 
     * @param commondiseases 常见疾病
     * @return 结果
     */
    public int insertCommondiseases(Commondiseases commondiseases);

    /**
     * 修改常见疾病
     * 
     * @param commondiseases 常见疾病
     * @return 结果
     */
    public int updateCommondiseases(Commondiseases commondiseases);

    /**
     * 批量删除常见疾病
     * 
     * @param ids 需要删除的常见疾病主键集合
     * @return 结果
     */
    public int deleteCommondiseasesByIds(Long[] ids);

    /**
     * 删除常见疾病信息
     * 
     * @param id 常见疾病主键
     * @return 结果
     */
    public int deleteCommondiseasesById(Long id);
}

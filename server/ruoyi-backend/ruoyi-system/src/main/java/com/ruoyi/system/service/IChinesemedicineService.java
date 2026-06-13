package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Chinesemedicine;

/**
 * 中药材Service接口
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public interface IChinesemedicineService 
{
    /**
     * 查询中药材
     * 
     * @param id 中药材主键
     * @return 中药材
     */
    public Chinesemedicine selectChinesemedicineById(Long id);

    /**
     * 查询中药材列表
     * 
     * @param chinesemedicine 中药材
     * @return 中药材集合
     */
    public List<Chinesemedicine> selectChinesemedicineList(Chinesemedicine chinesemedicine);

    /**
     * 新增中药材
     * 
     * @param chinesemedicine 中药材
     * @return 结果
     */
    public int insertChinesemedicine(Chinesemedicine chinesemedicine);

    /**
     * 修改中药材
     * 
     * @param chinesemedicine 中药材
     * @return 结果
     */
    public int updateChinesemedicine(Chinesemedicine chinesemedicine);

    /**
     * 批量删除中药材
     * 
     * @param ids 需要删除的中药材主键集合
     * @return 结果
     */
    public int deleteChinesemedicineByIds(Long[] ids);

    /**
     * 删除中药材信息
     * 
     * @param id 中药材主键
     * @return 结果
     */
    public int deleteChinesemedicineById(Long id);
}

package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Zhonogyaoshipu;

/**
 * 中药食谱Service接口
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
public interface IZhonogyaoshipuService 
{
    /**
     * 查询中药食谱
     * 
     * @param id 中药食谱主键
     * @return 中药食谱
     */
    public Zhonogyaoshipu selectZhonogyaoshipuById(Long id);

    /**
     * 查询中药食谱列表
     * 
     * @param zhonogyaoshipu 中药食谱
     * @return 中药食谱集合
     */
    public List<Zhonogyaoshipu> selectZhonogyaoshipuList(Zhonogyaoshipu zhonogyaoshipu);

    /**
     * 新增中药食谱
     * 
     * @param zhonogyaoshipu 中药食谱
     * @return 结果
     */
    public int insertZhonogyaoshipu(Zhonogyaoshipu zhonogyaoshipu);

    /**
     * 修改中药食谱
     * 
     * @param zhonogyaoshipu 中药食谱
     * @return 结果
     */
    public int updateZhonogyaoshipu(Zhonogyaoshipu zhonogyaoshipu);

    /**
     * 批量删除中药食谱
     * 
     * @param ids 需要删除的中药食谱主键集合
     * @return 结果
     */
    public int deleteZhonogyaoshipuByIds(Long[] ids);

    /**
     * 删除中药食谱信息
     * 
     * @param id 中药食谱主键
     * @return 结果
     */
    public int deleteZhonogyaoshipuById(Long id);
}

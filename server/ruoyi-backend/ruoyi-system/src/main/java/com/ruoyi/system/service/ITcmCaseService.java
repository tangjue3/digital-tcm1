package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TcmCase;

/**
 * 实验案例库Service接口
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public interface ITcmCaseService 
{
    /**
     * 查询实验案例库
     * 
     * @param id 实验案例库主键
     * @return 实验案例库
     */
    public TcmCase selectTcmCaseById(Long id);

    /**
     * 查询实验案例库列表
     * 
     * @param tcmCase 实验案例库
     * @return 实验案例库集合
     */
    public List<TcmCase> selectTcmCaseList(TcmCase tcmCase);

    /**
     * 新增实验案例库
     * 
     * @param tcmCase 实验案例库
     * @return 结果
     */
    public int insertTcmCase(TcmCase tcmCase);

    /**
     * 修改实验案例库
     * 
     * @param tcmCase 实验案例库
     * @return 结果
     */
    public int updateTcmCase(TcmCase tcmCase);

    /**
     * 批量删除实验案例库
     * 
     * @param ids 需要删除的实验案例库主键集合
     * @return 结果
     */
    public int deleteTcmCaseByIds(Long[] ids);

    /**
     * 删除实验案例库信息
     * 
     * @param id 实验案例库主键
     * @return 结果
     */
    public int deleteTcmCaseById(Long id);
}

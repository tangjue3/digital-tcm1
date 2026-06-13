package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TcmQuestionDetail;

/**
 * 案例实验详细Service接口
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public interface ITcmQuestionDetailService 
{
    /**
     * 查询案例实验详细
     * 
     * @param id 案例实验详细主键
     * @return 案例实验详细
     */
    public TcmQuestionDetail selectTcmQuestionDetailById(Long id);

    /**
     * 查询案例实验详细列表
     * 
     * @param tcmQuestionDetail 案例实验详细
     * @return 案例实验详细集合
     */
    public List<TcmQuestionDetail> selectTcmQuestionDetailList(TcmQuestionDetail tcmQuestionDetail);

    /**
     * 新增案例实验详细
     * 
     * @param tcmQuestionDetail 案例实验详细
     * @return 结果
     */
    public int insertTcmQuestionDetail(TcmQuestionDetail tcmQuestionDetail);

    /**
     * 修改案例实验详细
     * 
     * @param tcmQuestionDetail 案例实验详细
     * @return 结果
     */
    public int updateTcmQuestionDetail(TcmQuestionDetail tcmQuestionDetail);

    /**
     * 批量删除案例实验详细
     * 
     * @param ids 需要删除的案例实验详细主键集合
     * @return 结果
     */
    public int deleteTcmQuestionDetailByIds(Long[] ids);

    /**
     * 删除案例实验详细信息
     * 
     * @param id 案例实验详细主键
     * @return 结果
     */
    public int deleteTcmQuestionDetailById(Long id);
}

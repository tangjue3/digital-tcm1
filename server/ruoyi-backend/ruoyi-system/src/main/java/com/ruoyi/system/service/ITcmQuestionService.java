package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TcmQuestion;

/**
 * 案例题库Service接口
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
public interface ITcmQuestionService 
{
    /**
     * 查询案例题库
     * 
     * @param id 案例题库主键
     * @return 案例题库
     */
    public TcmQuestion selectTcmQuestionById(Long id);

    /**
     * 查询案例题库列表
     * 
     * @param tcmQuestion 案例题库
     * @return 案例题库集合
     */
    public List<TcmQuestion> selectTcmQuestionList(TcmQuestion tcmQuestion);

    /**
     * 新增案例题库
     * 
     * @param tcmQuestion 案例题库
     * @return 结果
     */
    public int insertTcmQuestion(TcmQuestion tcmQuestion);

    /**
     * 修改案例题库
     * 
     * @param tcmQuestion 案例题库
     * @return 结果
     */
    public int updateTcmQuestion(TcmQuestion tcmQuestion);

    /**
     * 批量删除案例题库
     * 
     * @param ids 需要删除的案例题库主键集合
     * @return 结果
     */
    public int deleteTcmQuestionByIds(Long[] ids);

    /**
     * 删除案例题库信息
     * 
     * @param id 案例题库主键
     * @return 结果
     */
    public int deleteTcmQuestionById(Long id);
}

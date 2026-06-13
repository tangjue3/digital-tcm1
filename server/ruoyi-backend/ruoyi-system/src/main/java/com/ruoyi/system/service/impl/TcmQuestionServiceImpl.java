package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TcmQuestionMapper;
import com.ruoyi.system.domain.TcmQuestion;
import com.ruoyi.system.service.ITcmQuestionService;

/**
 * 案例题库Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@Service
public class TcmQuestionServiceImpl implements ITcmQuestionService 
{
    @Autowired
    private TcmQuestionMapper tcmQuestionMapper;

    /**
     * 查询案例题库
     * 
     * @param id 案例题库主键
     * @return 案例题库
     */
    @Override
    public TcmQuestion selectTcmQuestionById(Long id)
    {
        return tcmQuestionMapper.selectTcmQuestionById(id);
    }

    /**
     * 查询案例题库列表
     * 
     * @param tcmQuestion 案例题库
     * @return 案例题库
     */
    @Override
    public List<TcmQuestion> selectTcmQuestionList(TcmQuestion tcmQuestion)
    {
        return tcmQuestionMapper.selectTcmQuestionList(tcmQuestion);
    }

    /**
     * 新增案例题库
     * 
     * @param tcmQuestion 案例题库
     * @return 结果
     */
    @Override
    public int insertTcmQuestion(TcmQuestion tcmQuestion)
    {
        return tcmQuestionMapper.insertTcmQuestion(tcmQuestion);
    }

    /**
     * 修改案例题库
     * 
     * @param tcmQuestion 案例题库
     * @return 结果
     */
    @Override
    public int updateTcmQuestion(TcmQuestion tcmQuestion)
    {
        return tcmQuestionMapper.updateTcmQuestion(tcmQuestion);
    }

    /**
     * 批量删除案例题库
     * 
     * @param ids 需要删除的案例题库主键
     * @return 结果
     */
    @Override
    public int deleteTcmQuestionByIds(Long[] ids)
    {
        return tcmQuestionMapper.deleteTcmQuestionByIds(ids);
    }

    /**
     * 删除案例题库信息
     * 
     * @param id 案例题库主键
     * @return 结果
     */
    @Override
    public int deleteTcmQuestionById(Long id)
    {
        return tcmQuestionMapper.deleteTcmQuestionById(id);
    }
}

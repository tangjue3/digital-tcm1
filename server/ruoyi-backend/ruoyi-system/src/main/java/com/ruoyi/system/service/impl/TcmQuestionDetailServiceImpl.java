package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TcmQuestionDetailMapper;
import com.ruoyi.system.domain.TcmQuestionDetail;
import com.ruoyi.system.service.ITcmQuestionDetailService;

/**
 * 案例实验详细Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@Service
public class TcmQuestionDetailServiceImpl implements ITcmQuestionDetailService 
{
    @Autowired
    private TcmQuestionDetailMapper tcmQuestionDetailMapper;

    /**
     * 查询案例实验详细
     * 
     * @param id 案例实验详细主键
     * @return 案例实验详细
     */
    @Override
    public TcmQuestionDetail selectTcmQuestionDetailById(Long id)
    {
        return tcmQuestionDetailMapper.selectTcmQuestionDetailById(id);
    }

    /**
     * 查询案例实验详细列表
     * 
     * @param tcmQuestionDetail 案例实验详细
     * @return 案例实验详细
     */
    @Override
    public List<TcmQuestionDetail> selectTcmQuestionDetailList(TcmQuestionDetail tcmQuestionDetail)
    {
        return tcmQuestionDetailMapper.selectTcmQuestionDetailList(tcmQuestionDetail);
    }

    /**
     * 新增案例实验详细
     * 
     * @param tcmQuestionDetail 案例实验详细
     * @return 结果
     */
    @Override
    public int insertTcmQuestionDetail(TcmQuestionDetail tcmQuestionDetail)
    {
        return tcmQuestionDetailMapper.insertTcmQuestionDetail(tcmQuestionDetail);
    }

    /**
     * 修改案例实验详细
     * 
     * @param tcmQuestionDetail 案例实验详细
     * @return 结果
     */
    @Override
    public int updateTcmQuestionDetail(TcmQuestionDetail tcmQuestionDetail)
    {
        return tcmQuestionDetailMapper.updateTcmQuestionDetail(tcmQuestionDetail);
    }

    /**
     * 批量删除案例实验详细
     * 
     * @param ids 需要删除的案例实验详细主键
     * @return 结果
     */
    @Override
    public int deleteTcmQuestionDetailByIds(Long[] ids)
    {
        return tcmQuestionDetailMapper.deleteTcmQuestionDetailByIds(ids);
    }

    /**
     * 删除案例实验详细信息
     * 
     * @param id 案例实验详细主键
     * @return 结果
     */
    @Override
    public int deleteTcmQuestionDetailById(Long id)
    {
        return tcmQuestionDetailMapper.deleteTcmQuestionDetailById(id);
    }
}

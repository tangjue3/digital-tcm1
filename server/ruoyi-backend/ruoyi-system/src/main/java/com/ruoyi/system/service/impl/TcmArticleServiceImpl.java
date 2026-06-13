package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TcmArticleMapper;
import com.ruoyi.system.domain.TcmArticle;
import com.ruoyi.system.service.ITcmArticleService;

/**
 * 中医好文Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-05-31
 */
@Service
public class TcmArticleServiceImpl implements ITcmArticleService 
{
    @Autowired
    private TcmArticleMapper tcmArticleMapper;

    /**
     * 查询中医好文
     * 
     * @param id 中医好文主键
     * @return 中医好文
     */
    @Override
    public TcmArticle selectTcmArticleById(Long id)
    {
        return tcmArticleMapper.selectTcmArticleById(id);
    }

    /**
     * 查询中医好文列表
     * 
     * @param tcmArticle 中医好文
     * @return 中医好文
     */
    @Override
    public List<TcmArticle> selectTcmArticleList(TcmArticle tcmArticle)
    {
        return tcmArticleMapper.selectTcmArticleList(tcmArticle);
    }

    /**
     * 新增中医好文
     * 
     * @param tcmArticle 中医好文
     * @return 结果
     */
    @Override
    public int insertTcmArticle(TcmArticle tcmArticle)
    {
        tcmArticle.setCreateTime(DateUtils.getNowDate());
        return tcmArticleMapper.insertTcmArticle(tcmArticle);
    }

    /**
     * 修改中医好文
     * 
     * @param tcmArticle 中医好文
     * @return 结果
     */
    @Override
    public int updateTcmArticle(TcmArticle tcmArticle)
    {
        tcmArticle.setUpdateTime(DateUtils.getNowDate());
        return tcmArticleMapper.updateTcmArticle(tcmArticle);
    }

    /**
     * 批量删除中医好文
     * 
     * @param ids 需要删除的中医好文主键
     * @return 结果
     */
    @Override
    public int deleteTcmArticleByIds(Long[] ids)
    {
        return tcmArticleMapper.deleteTcmArticleByIds(ids);
    }

    /**
     * 删除中医好文信息
     * 
     * @param id 中医好文主键
     * @return 结果
     */
    @Override
    public int deleteTcmArticleById(Long id)
    {
        return tcmArticleMapper.deleteTcmArticleById(id);
    }
}

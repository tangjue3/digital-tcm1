package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmArticle;

/**
 * 中医好文Mapper接口
 * 
 * @author ruoyi
 * @date 2024-05-31
 */
public interface TcmArticleMapper 
{
    /**
     * 查询中医好文
     * 
     * @param id 中医好文主键
     * @return 中医好文
     */
    public TcmArticle selectTcmArticleById(Long id);

    /**
     * 查询中医好文列表
     * 
     * @param tcmArticle 中医好文
     * @return 中医好文集合
     */
    public List<TcmArticle> selectTcmArticleList(TcmArticle tcmArticle);

    /**
     * 新增中医好文
     * 
     * @param tcmArticle 中医好文
     * @return 结果
     */
    public int insertTcmArticle(TcmArticle tcmArticle);

    /**
     * 修改中医好文
     * 
     * @param tcmArticle 中医好文
     * @return 结果
     */
    public int updateTcmArticle(TcmArticle tcmArticle);

    /**
     * 删除中医好文
     * 
     * @param id 中医好文主键
     * @return 结果
     */
    public int deleteTcmArticleById(Long id);

    /**
     * 批量删除中医好文
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTcmArticleByIds(Long[] ids);
}

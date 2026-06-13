package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmShop;

/**
 * 中医房Mapper接口
 * 
 * @author ruoyi
 * @date 2024-06-03
 */
public interface TcmShopMapper 
{
    /**
     * 查询中医房
     * 
     * @param id 中医房主键
     * @return 中医房
     */
    public TcmShop selectTcmShopById(Long id);

    /**
     * 查询中医房列表
     * 
     * @param tcmShop 中医房
     * @return 中医房集合
     */
    public List<TcmShop> selectTcmShopList(TcmShop tcmShop);

    /**
     * 新增中医房
     * 
     * @param tcmShop 中医房
     * @return 结果
     */
    public int insertTcmShop(TcmShop tcmShop);

    /**
     * 修改中医房
     * 
     * @param tcmShop 中医房
     * @return 结果
     */
    public int updateTcmShop(TcmShop tcmShop);

    /**
     * 删除中医房
     * 
     * @param id 中医房主键
     * @return 结果
     */
    public int deleteTcmShopById(Long id);

    /**
     * 批量删除中医房
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTcmShopByIds(Long[] ids);
}

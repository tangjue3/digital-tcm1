package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TcmShopMapper;
import com.ruoyi.system.domain.TcmShop;
import com.ruoyi.system.service.ITcmShopService;

/**
 * 中医房Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-06-03
 */
@Service
public class TcmShopServiceImpl implements ITcmShopService 
{
    @Autowired
    private TcmShopMapper tcmShopMapper;

    /**
     * 查询中医房
     * 
     * @param id 中医房主键
     * @return 中医房
     */
    @Override
    public TcmShop selectTcmShopById(Long id)
    {
        return tcmShopMapper.selectTcmShopById(id);
    }

    /**
     * 查询中医房列表
     * 
     * @param tcmShop 中医房
     * @return 中医房
     */
    @Override
    public List<TcmShop> selectTcmShopList(TcmShop tcmShop)
    {
        return tcmShopMapper.selectTcmShopList(tcmShop);
    }

    /**
     * 新增中医房
     * 
     * @param tcmShop 中医房
     * @return 结果
     */
    @Override
    public int insertTcmShop(TcmShop tcmShop)
    {
        return tcmShopMapper.insertTcmShop(tcmShop);
    }

    /**
     * 修改中医房
     * 
     * @param tcmShop 中医房
     * @return 结果
     */
    @Override
    public int updateTcmShop(TcmShop tcmShop)
    {
        return tcmShopMapper.updateTcmShop(tcmShop);
    }

    /**
     * 批量删除中医房
     * 
     * @param ids 需要删除的中医房主键
     * @return 结果
     */
    @Override
    public int deleteTcmShopByIds(Long[] ids)
    {
        return tcmShopMapper.deleteTcmShopByIds(ids);
    }

    /**
     * 删除中医房信息
     * 
     * @param id 中医房主键
     * @return 结果
     */
    @Override
    public int deleteTcmShopById(Long id)
    {
        return tcmShopMapper.deleteTcmShopById(id);
    }
}

package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TcmVideoMapper;
import com.ruoyi.system.domain.TcmVideo;
import com.ruoyi.system.service.ITcmVideoService;

/**
 * 中医药视频Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-06-03
 */
@Service
public class TcmVideoServiceImpl implements ITcmVideoService 
{
    @Autowired
    private TcmVideoMapper tcmVideoMapper;

    /**
     * 查询中医药视频
     * 
     * @param id 中医药视频主键
     * @return 中医药视频
     */
    @Override
    public TcmVideo selectTcmVideoById(Long id)
    {
        return tcmVideoMapper.selectTcmVideoById(id);
    }

    /**
     * 查询中医药视频列表
     * 
     * @param tcmVideo 中医药视频
     * @return 中医药视频
     */
    @Override
    public List<TcmVideo> selectTcmVideoList(TcmVideo tcmVideo)
    {
        return tcmVideoMapper.selectTcmVideoList(tcmVideo);
    }

    /**
     * 新增中医药视频
     * 
     * @param tcmVideo 中医药视频
     * @return 结果
     */
    @Override
    public int insertTcmVideo(TcmVideo tcmVideo)
    {
        tcmVideo.setCreateTime(DateUtils.getNowDate());
        return tcmVideoMapper.insertTcmVideo(tcmVideo);
    }

    /**
     * 修改中医药视频
     * 
     * @param tcmVideo 中医药视频
     * @return 结果
     */
    @Override
    public int updateTcmVideo(TcmVideo tcmVideo)
    {
        tcmVideo.setUpdateTime(DateUtils.getNowDate());
        return tcmVideoMapper.updateTcmVideo(tcmVideo);
    }

    /**
     * 批量删除中医药视频
     * 
     * @param ids 需要删除的中医药视频主键
     * @return 结果
     */
    @Override
    public int deleteTcmVideoByIds(Long[] ids)
    {
        return tcmVideoMapper.deleteTcmVideoByIds(ids);
    }

    /**
     * 删除中医药视频信息
     * 
     * @param id 中医药视频主键
     * @return 结果
     */
    @Override
    public int deleteTcmVideoById(Long id)
    {
        return tcmVideoMapper.deleteTcmVideoById(id);
    }

    public int updateTcmVideoCount(Long id)
    {
        return tcmVideoMapper.updateTcmVideoCount(id);
    }
}

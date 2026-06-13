package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TcmVideo;

/**
 * 中医药视频Mapper接口
 * 
 * @author ruoyi
 * @date 2024-06-03
 */
public interface TcmVideoMapper 
{
    /**
     * 查询中医药视频
     * 
     * @param id 中医药视频主键
     * @return 中医药视频
     */
    public TcmVideo selectTcmVideoById(Long id);

    /**
     * 查询中医药视频列表
     * 
     * @param tcmVideo 中医药视频
     * @return 中医药视频集合
     */
    public List<TcmVideo> selectTcmVideoList(TcmVideo tcmVideo);

    /**
     * 新增中医药视频
     * 
     * @param tcmVideo 中医药视频
     * @return 结果
     */
    public int insertTcmVideo(TcmVideo tcmVideo);

    /**
     * 修改中医药视频
     * 
     * @param tcmVideo 中医药视频
     * @return 结果
     */
    public int updateTcmVideo(TcmVideo tcmVideo);

    /**
     * 删除中医药视频
     * 
     * @param id 中医药视频主键
     * @return 结果
     */
    public int deleteTcmVideoById(Long id);

    /**
     * 批量删除中医药视频
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTcmVideoByIds(Long[] ids);

    public int updateTcmVideoCount(Long id);
}

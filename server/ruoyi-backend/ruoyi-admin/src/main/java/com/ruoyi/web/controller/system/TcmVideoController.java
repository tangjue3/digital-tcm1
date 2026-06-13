package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.TcmVideo;
import com.ruoyi.system.service.ITcmVideoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 中医药视频Controller
 * 
 * @author ruoyi
 * @date 2024-06-03
 */
@RestController
@RequestMapping("/system/video")
@Api(tags = "中医药视频管理")
public class TcmVideoController extends BaseController
{
    @Autowired
    private ITcmVideoService tcmVideoService;

    /**
     * 查询中医药视频列表
     */
   /* @PreAuthorize("@ss.hasPermi('system:video:list')")*/
    @GetMapping("/list")
    @ApiOperation("查询中医药视频列表")
    public TableDataInfo list(TcmVideo tcmVideo)
    {
        startPage();
        List<TcmVideo> list = tcmVideoService.selectTcmVideoList(tcmVideo);
        return getDataTable(list);
    }

    /**
     * 导出中医药视频列表
     */
    /*@PreAuthorize("@ss.hasPermi('system:video:export')")*/
    @Log(title = "中医药视频", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出中医药视频列表")
    public void export(HttpServletResponse response, TcmVideo tcmVideo)
    {
        List<TcmVideo> list = tcmVideoService.selectTcmVideoList(tcmVideo);
        ExcelUtil<TcmVideo> util = new ExcelUtil<TcmVideo>(TcmVideo.class);
        util.exportExcel(response, list, "中医药视频数据");
    }

    /**
     * 获取中医药视频详细信息
     */
    /*@PreAuthorize("@ss.hasPermi('system:video:query')")*/
    @GetMapping(value = "/{id}")
    @ApiOperation("获取中医药视频详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tcmVideoService.selectTcmVideoById(id));
    }

    /**
     * 新增中医药视频
     */
   /* @PreAuthorize("@ss.hasPermi('system:video:add')")*/
    @Log(title = "中医药视频", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增中医药视频")
    public AjaxResult add(@RequestBody TcmVideo tcmVideo)
    {
        return toAjax(tcmVideoService.insertTcmVideo(tcmVideo));
    }

    /**
     * 修改中医药视频
     */
    /*@PreAuthorize("@ss.hasPermi('system:video:edit')")*/
    @Log(title = "中医药视频", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改中医药视频")
    public AjaxResult edit(@RequestBody TcmVideo tcmVideo)
    {
        return toAjax(tcmVideoService.updateTcmVideo(tcmVideo));
    }

    /**
     * 删除中医药视频
     */
    /*@PreAuthorize("@ss.hasPermi('system:video:remove')")*/
    @Log(title = "中医药视频", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除中医药视频")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tcmVideoService.deleteTcmVideoByIds(ids));
    }

    /**
     * 播放次数
     */
    @GetMapping(value = "/addCount")
    @ApiOperation("播放次数")
    public AjaxResult updateCount(Long id)
    {
        return toAjax(tcmVideoService.updateTcmVideoCount(id));
    }
}

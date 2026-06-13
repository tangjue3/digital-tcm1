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
import com.ruoyi.system.domain.TcmAcupoints;
import com.ruoyi.system.service.ITcmAcupointsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 人体穴位Controller
 * 
 * @author ruoyi
 * @date 2025-03-28
 */
@RestController
@RequestMapping("/system/acupoints")
@Api(tags = "人体穴位管理")
public class TcmAcupointsController extends BaseController
{
    @Autowired
    private ITcmAcupointsService tcmAcupointsService;

    /**
     * 查询人体穴位列表
     */

    @GetMapping("/list")
    @ApiOperation("查询人体穴位列表")
    public TableDataInfo list(TcmAcupoints tcmAcupoints)
    {
        startPage();
        List<TcmAcupoints> list = tcmAcupointsService.selectTcmAcupointsList(tcmAcupoints);
        return getDataTable(list);
    }

    /**
     * 导出人体穴位列表
     */

    @Log(title = "人体穴位", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出人体穴位列表")
    public void export(HttpServletResponse response, TcmAcupoints tcmAcupoints)
    {
        List<TcmAcupoints> list = tcmAcupointsService.selectTcmAcupointsList(tcmAcupoints);
        ExcelUtil<TcmAcupoints> util = new ExcelUtil<TcmAcupoints>(TcmAcupoints.class);
        util.exportExcel(response, list, "人体穴位数据");
    }

    /**
     * 获取人体穴位详细信息
     */

    @GetMapping(value = "/{id}")
    @ApiOperation("获取人体穴位详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tcmAcupointsService.selectTcmAcupointsById(id));
    }

    /**
     * 新增人体穴位
     */

    @Log(title = "人体穴位", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增人体穴位")
    public AjaxResult add(@RequestBody TcmAcupoints tcmAcupoints)
    {
        return toAjax(tcmAcupointsService.insertTcmAcupoints(tcmAcupoints));
    }

    /**
     * 修改人体穴位
     */

    @Log(title = "人体穴位", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改人体穴位")
    public AjaxResult edit(@RequestBody TcmAcupoints tcmAcupoints)
    {
        return toAjax(tcmAcupointsService.updateTcmAcupoints(tcmAcupoints));
    }

    /**
     * 删除人体穴位
     */

    @Log(title = "人体穴位", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除人体穴位")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tcmAcupointsService.deleteTcmAcupointsByIds(ids));
    }
}

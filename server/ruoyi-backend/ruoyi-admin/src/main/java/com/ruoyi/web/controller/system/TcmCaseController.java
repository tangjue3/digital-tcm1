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
import com.ruoyi.system.domain.TcmCase;
import com.ruoyi.system.service.ITcmCaseService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 实验案例库Controller
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/system/case")
@Api(tags = "实验案例库管理")
public class TcmCaseController extends BaseController
{
    @Autowired
    private ITcmCaseService tcmCaseService;

    /**
     * 查询实验案例库列表
     */

    @GetMapping("/list")
    @ApiOperation("查询实验案例库列表")
    public TableDataInfo list(TcmCase tcmCase)
    {
        startPage();
        List<TcmCase> list = tcmCaseService.selectTcmCaseList(tcmCase);
        return getDataTable(list);
    }

    /**
     * 导出实验案例库列表
     */

    @Log(title = "实验案例库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出实验案例库列表")
    public void export(HttpServletResponse response, TcmCase tcmCase)
    {
        List<TcmCase> list = tcmCaseService.selectTcmCaseList(tcmCase);
        ExcelUtil<TcmCase> util = new ExcelUtil<TcmCase>(TcmCase.class);
        util.exportExcel(response, list, "实验案例库数据");
    }

    /**
     * 获取实验案例库详细信息
     */

    @GetMapping(value = "/{id}")
    @ApiOperation("获取实验案例库详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tcmCaseService.selectTcmCaseById(id));
    }

    /**
     * 新增实验案例库
     */

    @Log(title = "实验案例库", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增实验案例库")
    public AjaxResult add(@RequestBody TcmCase tcmCase)
    {
        return toAjax(tcmCaseService.insertTcmCase(tcmCase));
    }

    /**
     * 修改实验案例库
     */

    @Log(title = "实验案例库", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改实验案例库")
    public AjaxResult edit(@RequestBody TcmCase tcmCase)
    {
        return toAjax(tcmCaseService.updateTcmCase(tcmCase));
    }

    /**
     * 删除实验案例库
     */

    @Log(title = "实验案例库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除实验案例库")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tcmCaseService.deleteTcmCaseByIds(ids));
    }
}

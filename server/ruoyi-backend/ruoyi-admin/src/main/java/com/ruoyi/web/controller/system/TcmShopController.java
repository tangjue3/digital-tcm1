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
import com.ruoyi.system.domain.TcmShop;
import com.ruoyi.system.service.ITcmShopService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 中医房Controller
 * 
 * @author ruoyi
 * @date 2024-06-03
 */
@RestController
@RequestMapping("/system/shop")
@Api(tags = "中医房管理")
public class TcmShopController extends BaseController
{
    @Autowired
    private ITcmShopService tcmShopService;

    /**
     * 查询中医房列表
     */
   /* @PreAuthorize("@ss.hasPermi('system:shop:list')")*/
    @GetMapping("/list")
    @ApiOperation("查询中医房列表")
    public TableDataInfo list(TcmShop tcmShop)
    {
        startPage();
        List<TcmShop> list = tcmShopService.selectTcmShopList(tcmShop);
        return getDataTable(list);
    }

    /**
     * 导出中医房列表
     */
    /*@PreAuthorize("@ss.hasPermi('system:shop:export')")*/
    @Log(title = "中医房", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出中医房列表")
    public void export(HttpServletResponse response, TcmShop tcmShop)
    {
        List<TcmShop> list = tcmShopService.selectTcmShopList(tcmShop);
        ExcelUtil<TcmShop> util = new ExcelUtil<TcmShop>(TcmShop.class);
        util.exportExcel(response, list, "中医房数据");
    }

    /**
     * 获取中医房详细信息
     */
  /*  @PreAuthorize("@ss.hasPermi('system:shop:query')")*/
    @GetMapping(value = "/{id}")
    @ApiOperation("获取中医房详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tcmShopService.selectTcmShopById(id));
    }

    /**
     * 新增中医房
     */
   /* @PreAuthorize("@ss.hasPermi('system:shop:add')")*/
    @Log(title = "中医房", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增中医房")
    public AjaxResult add(@RequestBody TcmShop tcmShop)
    {
        return toAjax(tcmShopService.insertTcmShop(tcmShop));
    }

    /**
     * 修改中医房
     */
   /* @PreAuthorize("@ss.hasPermi('system:shop:edit')")*/
    @Log(title = "中医房", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改中医房")
    public AjaxResult edit(@RequestBody TcmShop tcmShop)
    {
        return toAjax(tcmShopService.updateTcmShop(tcmShop));
    }

    /**
     * 删除中医房
     */
   /* @PreAuthorize("@ss.hasPermi('system:shop:remove')")*/
    @Log(title = "中医房", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除中医房")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tcmShopService.deleteTcmShopByIds(ids));
    }
}

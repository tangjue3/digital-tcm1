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
import com.ruoyi.system.domain.TcmArticle;
import com.ruoyi.system.service.ITcmArticleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 中医好文Controller
 * 
 * @author ruoyi
 * @date 2024-05-31
 */
@RestController
@RequestMapping("/system/article")
@Api(tags = "中医好文管理")
public class TcmArticleController extends BaseController
{
    @Autowired
    private ITcmArticleService tcmArticleService;

    /**
     * 查询中医好文列表
     */
 /*   @PreAuthorize("@ss.hasPermi('system:article:list')")*/
    @GetMapping("/list")
    @ApiOperation("查询中医好文列表")
    public TableDataInfo list(TcmArticle tcmArticle)
    {
        startPage();
        List<TcmArticle> list = tcmArticleService.selectTcmArticleList(tcmArticle);
        return getDataTable(list);
    }

    /**
     * 导出中医好文列表
     */
    /*@PreAuthorize("@ss.hasPermi('system:article:export')")*/
    @Log(title = "中医好文", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出中医好文列表")
    public void export(HttpServletResponse response, TcmArticle tcmArticle)
    {
        List<TcmArticle> list = tcmArticleService.selectTcmArticleList(tcmArticle);
        ExcelUtil<TcmArticle> util = new ExcelUtil<TcmArticle>(TcmArticle.class);
        util.exportExcel(response, list, "中医好文数据");
    }

    /**
     * 获取中医好文详细信息
     */
   /* @PreAuthorize("@ss.hasPermi('system:article:query')")*/
    @GetMapping(value = "/{id}")
    @ApiOperation("获取中医好文详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tcmArticleService.selectTcmArticleById(id));
    }

    /**
     * 新增中医好文
     */
  /*  @PreAuthorize("@ss.hasPermi('system:article:add')")*/
    @Log(title = "中医好文", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增中医好文")
    public AjaxResult add(@RequestBody TcmArticle tcmArticle)
    {
        return toAjax(tcmArticleService.insertTcmArticle(tcmArticle));
    }

    /**
     * 修改中医好文
     */
    @PreAuthorize("@ss.hasPermi('system:article:edit')")
    @Log(title = "中医好文", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改中医好文")
    public AjaxResult edit(@RequestBody TcmArticle tcmArticle)
    {
        return toAjax(tcmArticleService.updateTcmArticle(tcmArticle));
    }

    /**
     * 删除中医好文
     */
   /* @PreAuthorize("@ss.hasPermi('system:article:remove')")*/
    @ApiOperation("删除中医好文")
    @Log(title = "中医好文", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tcmArticleService.deleteTcmArticleByIds(ids));
    }
}

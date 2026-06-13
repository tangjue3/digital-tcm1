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
import com.ruoyi.system.domain.TcmQuestionDetail;
import com.ruoyi.system.service.ITcmQuestionDetailService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 案例实验详细Controller
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/system/detail")
@Api(tags = "案例实验详细管理")
public class TcmQuestionDetailController extends BaseController
{
    @Autowired
    private ITcmQuestionDetailService tcmQuestionDetailService;

    /**
     * 查询案例实验详细列表
     */

    @GetMapping("/list")
    @ApiOperation("查询案例实验详细列表")
    public TableDataInfo list(TcmQuestionDetail tcmQuestionDetail)
    {
        startPage();
        List<TcmQuestionDetail> list = tcmQuestionDetailService.selectTcmQuestionDetailList(tcmQuestionDetail);
        return getDataTable(list);
    }

    /**
     * 导出案例实验详细列表
     */

    @Log(title = "案例实验详细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出案例实验详细列表")
    public void export(HttpServletResponse response, TcmQuestionDetail tcmQuestionDetail)
    {
        List<TcmQuestionDetail> list = tcmQuestionDetailService.selectTcmQuestionDetailList(tcmQuestionDetail);
        ExcelUtil<TcmQuestionDetail> util = new ExcelUtil<TcmQuestionDetail>(TcmQuestionDetail.class);
        util.exportExcel(response, list, "案例实验详细数据");
    }

    /**
     * 获取案例实验详细详细信息
     */

    @GetMapping(value = "/{id}")
    @ApiOperation("获取案例实验详细详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tcmQuestionDetailService.selectTcmQuestionDetailById(id));
    }

    /**
     * 新增案例实验详细
     */
    @Log(title = "案例实验详细", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增案例实验详细")
    public AjaxResult add(@RequestBody TcmQuestionDetail tcmQuestionDetail)
    {
        return toAjax(tcmQuestionDetailService.insertTcmQuestionDetail(tcmQuestionDetail));
    }

    /**
     * 修改案例实验详细
     */

    @Log(title = "案例实验详细", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改案例实验详细")
    public AjaxResult edit(@RequestBody TcmQuestionDetail tcmQuestionDetail)
    {
        return toAjax(tcmQuestionDetailService.updateTcmQuestionDetail(tcmQuestionDetail));
    }

    /**
     * 删除案例实验详细
     */

    @Log(title = "案例实验详细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除案例实验详细")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tcmQuestionDetailService.deleteTcmQuestionDetailByIds(ids));
    }

    /**
     * 新增案例实验详细结果
     */
    @Log(title = "案例实验详细", businessType = BusinessType.INSERT)
    @PostMapping("/addList")
    @ApiOperation("新增案例实验详细结果")
    public AjaxResult addList(@RequestBody List<TcmQuestionDetail> tcmQuestionDetailList)
    {
        for (int i = 0; i < tcmQuestionDetailList.size(); i++) {
            TcmQuestionDetail tcmQuestionDetail=tcmQuestionDetailList.get(i);
            tcmQuestionDetailService.insertTcmQuestionDetail(tcmQuestionDetail);
        }
        return AjaxResult.success("插入成功");
    }
}

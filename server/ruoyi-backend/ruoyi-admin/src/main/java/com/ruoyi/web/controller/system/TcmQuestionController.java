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
import com.ruoyi.system.domain.TcmQuestion;
import com.ruoyi.system.service.ITcmQuestionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 案例题库Controller
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/system/question")
@Api(tags = "案例题库管理")
public class TcmQuestionController extends BaseController
{
    @Autowired
    private ITcmQuestionService tcmQuestionService;

    /**
     * 查询案例题库列表
     */

    @GetMapping("/list")
    @ApiOperation("查询案例题库列表")
    public TableDataInfo list(TcmQuestion tcmQuestion)
    {
        startPage();
        List<TcmQuestion> list = tcmQuestionService.selectTcmQuestionList(tcmQuestion);
        return getDataTable(list);
    }

    /**
     * 导出案例题库列表
     */

    @Log(title = "案例题库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出案例题库列表")
    public void export(HttpServletResponse response, TcmQuestion tcmQuestion)
    {
        List<TcmQuestion> list = tcmQuestionService.selectTcmQuestionList(tcmQuestion);
        ExcelUtil<TcmQuestion> util = new ExcelUtil<TcmQuestion>(TcmQuestion.class);
        util.exportExcel(response, list, "案例题库数据");
    }

    /**
     * 获取案例题库详细信息
     */

    @GetMapping(value = "/{id}")
    @ApiOperation("获取案例题库详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tcmQuestionService.selectTcmQuestionById(id));
    }

    /**
     * 新增案例题库
     */

    @Log(title = "案例题库", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增案例题库")
    public AjaxResult add(@RequestBody TcmQuestion tcmQuestion)
    {
        return toAjax(tcmQuestionService.insertTcmQuestion(tcmQuestion));
    }

    /**
     * 修改案例题库
     */

    @Log(title = "案例题库", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改案例题库")
    public AjaxResult edit(@RequestBody TcmQuestion tcmQuestion)
    {
        return toAjax(tcmQuestionService.updateTcmQuestion(tcmQuestion));
    }

    /**
     * 删除案例题库
     */

    @Log(title = "案例题库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除案例题库")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tcmQuestionService.deleteTcmQuestionByIds(ids));
    }

    /**
     * 根据案例编号和模块获取当前案例题
     */

    @GetMapping("/getQuestionList")
    @ApiOperation("根据案例编号和模块获取当前案例题")
    public List<TcmQuestion> getQuestionList(TcmQuestion tcmQuestion)
    {

        List<TcmQuestion> list = tcmQuestionService.selectTcmQuestionList(tcmQuestion);
        return list;
    }
}

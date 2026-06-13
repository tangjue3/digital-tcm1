package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Commondiseases;
import com.ruoyi.system.service.ICommondiseasesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 常见疾病Controller
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
@RestController
@RequestMapping("/system/commondiseases")
@Api(tags = "")
public class CommondiseasesController extends BaseController
{
    @Autowired
    private ICommondiseasesService commondiseasesService;

    /**
     * 查询常见疾病列表
     */

    @GetMapping("/list")
    public TableDataInfo list(Commondiseases commondiseases)
    {
        startPage();
        List<Commondiseases> list = commondiseasesService.selectCommondiseasesList(commondiseases);
        return getDataTable(list);
    }

    /**
     * 导出常见疾病列表
     */

    @Log(title = "常见疾病", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Commondiseases commondiseases)
    {
        List<Commondiseases> list = commondiseasesService.selectCommondiseasesList(commondiseases);
        ExcelUtil<Commondiseases> util = new ExcelUtil<Commondiseases>(Commondiseases.class);
        util.exportExcel(response, list, "常见疾病数据");
    }

    /**
     * 获取常见疾病详细信息
     */

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(commondiseasesService.selectCommondiseasesById(id));
    }

    /**
     * 新增常见疾病
     */

    @Log(title = "常见疾病", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Commondiseases commondiseases)
    {
        return toAjax(commondiseasesService.insertCommondiseases(commondiseases));
    }

    /**
     * 修改常见疾病
     */

    @Log(title = "常见疾病", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Commondiseases commondiseases)
    {
        return toAjax(commondiseasesService.updateCommondiseases(commondiseases));
    }

    /**
     * 删除常见疾病
     */

    @Log(title = "常见疾病", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(commondiseasesService.deleteCommondiseasesByIds(ids));
    }
}

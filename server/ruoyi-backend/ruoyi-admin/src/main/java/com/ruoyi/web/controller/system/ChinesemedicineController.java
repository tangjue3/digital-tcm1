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
import com.ruoyi.system.domain.Chinesemedicine;
import com.ruoyi.system.service.IChinesemedicineService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 中药材Controller
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
@RestController
@RequestMapping("/system/chinesemedicine")
@Api(tags = "")
public class ChinesemedicineController extends BaseController
{
    @Autowired
    private IChinesemedicineService chinesemedicineService;

    /**
     * 查询中药材列表
     */

    @GetMapping("/list")
    public TableDataInfo list(Chinesemedicine chinesemedicine)
    {
        startPage();
        List<Chinesemedicine> list = chinesemedicineService.selectChinesemedicineList(chinesemedicine);
        return getDataTable(list);
    }

    /**
     * 导出中药材列表
     */

    @Log(title = "中药材", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Chinesemedicine chinesemedicine)
    {
        List<Chinesemedicine> list = chinesemedicineService.selectChinesemedicineList(chinesemedicine);
        ExcelUtil<Chinesemedicine> util = new ExcelUtil<Chinesemedicine>(Chinesemedicine.class);
        util.exportExcel(response, list, "中药材数据");
    }

    /**
     * 获取中药材详细信息
     */

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(chinesemedicineService.selectChinesemedicineById(id));
    }

    /**
     * 新增中药材
     */

    @Log(title = "中药材", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Chinesemedicine chinesemedicine)
    {
        return toAjax(chinesemedicineService.insertChinesemedicine(chinesemedicine));
    }

    /**
     * 修改中药材
     */

    @Log(title = "中药材", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Chinesemedicine chinesemedicine)
    {
        return toAjax(chinesemedicineService.updateChinesemedicine(chinesemedicine));
    }

    /**
     * 删除中药材
     */

    @Log(title = "中药材", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(chinesemedicineService.deleteChinesemedicineByIds(ids));
    }
}

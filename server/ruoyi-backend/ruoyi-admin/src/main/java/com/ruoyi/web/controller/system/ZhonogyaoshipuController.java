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
import com.ruoyi.system.domain.Zhonogyaoshipu;
import com.ruoyi.system.service.IZhonogyaoshipuService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 中药食谱Controller
 * 
 * @author ruoyi
 * @date 2025-04-03
 */
@RestController
@RequestMapping("/system/zhonogyaoshipu")
@Api(tags = "")
public class ZhonogyaoshipuController extends BaseController
{
    @Autowired
    private IZhonogyaoshipuService zhonogyaoshipuService;

    /**
     * 查询中药食谱列表
     */

    @GetMapping("/list")
    public TableDataInfo list(Zhonogyaoshipu zhonogyaoshipu)
    {
        startPage();
        List<Zhonogyaoshipu> list = zhonogyaoshipuService.selectZhonogyaoshipuList(zhonogyaoshipu);
        return getDataTable(list);
    }

    /**
     * 导出中药食谱列表
     */

    @Log(title = "中药食谱", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Zhonogyaoshipu zhonogyaoshipu)
    {
        List<Zhonogyaoshipu> list = zhonogyaoshipuService.selectZhonogyaoshipuList(zhonogyaoshipu);
        ExcelUtil<Zhonogyaoshipu> util = new ExcelUtil<Zhonogyaoshipu>(Zhonogyaoshipu.class);
        util.exportExcel(response, list, "中药食谱数据");
    }

    /**
     * 获取中药食谱详细信息
     */

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(zhonogyaoshipuService.selectZhonogyaoshipuById(id));
    }

    /**
     * 新增中药食谱
     */

    @Log(title = "中药食谱", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Zhonogyaoshipu zhonogyaoshipu)
    {
        return toAjax(zhonogyaoshipuService.insertZhonogyaoshipu(zhonogyaoshipu));
    }

    /**
     * 修改中药食谱
     */

    @Log(title = "中药食谱", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Zhonogyaoshipu zhonogyaoshipu)
    {
        return toAjax(zhonogyaoshipuService.updateZhonogyaoshipu(zhonogyaoshipu));
    }

    /**
     * 删除中药食谱
     */

    @Log(title = "中药食谱", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(zhonogyaoshipuService.deleteZhonogyaoshipuByIds(ids));
    }
}

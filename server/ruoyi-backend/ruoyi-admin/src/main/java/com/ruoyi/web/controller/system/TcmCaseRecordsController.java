package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.TcmCase;
import com.ruoyi.system.service.ITcmCaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.TcmCaseRecords;
import com.ruoyi.system.service.ITcmCaseRecordsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 实验案例记录Controller
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/system/records")
@Api(tags = "实验案例记录管理")
public class TcmCaseRecordsController extends BaseController
{
    @Autowired
    private ITcmCaseRecordsService tcmCaseRecordsService;

    @Autowired
    private ITcmCaseService tcmCaseService;

    /**
     * 查询实验案例记录列表
     */

    @GetMapping("/list")
    @ApiOperation("查询实验案例记录列表")
    public TableDataInfo list(TcmCaseRecords tcmCaseRecords)
    {
        startPage();
        List<TcmCaseRecords> list = tcmCaseRecordsService.selectTcmCaseRecordsList(tcmCaseRecords);
        return getDataTable(list);
    }

    /**
     * 导出实验案例记录列表
     */

    @Log(title = "实验案例记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出实验案例记录列表")
    public void export(HttpServletResponse response, TcmCaseRecords tcmCaseRecords)
    {
        List<TcmCaseRecords> list = tcmCaseRecordsService.selectTcmCaseRecordsList(tcmCaseRecords);
        ExcelUtil<TcmCaseRecords> util = new ExcelUtil<TcmCaseRecords>(TcmCaseRecords.class);
        util.exportExcel(response, list, "实验案例记录数据");
    }

    /**
     * 获取实验案例记录详细信息
     */

    @GetMapping(value = "/{id}")
    @ApiOperation("获取实验案例记录详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tcmCaseRecordsService.selectTcmCaseRecordsById(id));
    }

    /**
     * 新增实验案例记录
     */

    @Log(title = "实验案例记录", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("新增实验案例记录")
    public AjaxResult add(@RequestBody TcmCaseRecords tcmCaseRecords)
    {
        //设置实验唯一编号=实验编号+时间戳
        tcmCaseRecords.setTime(DateUtils.getNowDate());
        tcmCaseRecords.setQuestionNo(tcmCaseRecords.getCaseNo()+"-"+DateUtils.dateTimeNow());
        return toAjax(tcmCaseRecordsService.insertTcmCaseRecords(tcmCaseRecords));
    }

    /**
     * 修改实验案例记录
     */

    @Log(title = "实验案例记录", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改实验案例记录")
    public AjaxResult edit(@RequestBody TcmCaseRecords tcmCaseRecords)
    {
        return toAjax(tcmCaseRecordsService.updateTcmCaseRecords(tcmCaseRecords));
    }

    /**
     * 删除实验案例记录
     */

    @Log(title = "实验案例记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiOperation("删除实验案例记录")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tcmCaseRecordsService.deleteTcmCaseRecordsByIds(ids));
    }

    /**
     * 最后结果保存更新
     */

    @Log(title = "更新实验案例记录", businessType = BusinessType.UPDATE)
    @PostMapping("/tcmCaseRecordsResult")
    @ApiOperation("最后结果保存更新")
    public AjaxResult updateResult(@RequestBody TcmCaseRecords tcmCaseRecords)
    {
        return toAjax(tcmCaseRecordsService.updateTcmCaseRecordsByResult(tcmCaseRecords));
    }

    /**
     * 根据案例编号 获取排行榜
     * @param caseNo
     * @return
     */
    @GetMapping("/getCharts")
    @ApiOperation("根据案例编号获取排行榜")
    public AjaxResult getChartsInfo(String caseNo)
    {
        return AjaxResult.success(tcmCaseRecordsService.selectTcmCaseRecordsByCaseNo(caseNo));
    }

    /**
     * 根据用户查看自己案例信息
     * @param userId
     * @return
     */
    @GetMapping("/getCaseByname")
    @ApiOperation("根据用户查看自己案例信息")
    public AjaxResult getCaseByname(String userId)
    {
        return AjaxResult.success(tcmCaseRecordsService.selectTcmCaseRecordsListByuserId(userId));
    }
    /**
     * 获取每个案例库的排名情况取前3
     * @param
     * @return
     */
    @GetMapping("/getAllCharts")
    @ApiOperation("获取每个案例库的排名情况取前3")
    public AjaxResult getCaseByname()
    {
        return AjaxResult.success(tcmCaseRecordsService.getAllCharts());
    }
    /**
     * 新增实验案例记录
     */

    @Log(title = "增实验案例记录返回QuestionNo", businessType = BusinessType.INSERT)
    @PostMapping("/addPlus")
    @ApiOperation("新增实验案例记录返回QuestionNo")
    public AjaxResult addPlus(@RequestBody TcmCaseRecords tcmCaseRecords)
    {
        // 前端从 AjaxResult.msg 读取 questionNo，因此这里必须用 AjaxResult.success(questionNo)。
        tcmCaseRecords.setTime(DateUtils.getNowDate());
        tcmCaseRecords.setQuestionNo(tcmCaseRecords.getCaseNo() + "-" + DateUtils.dateTimeNow());
        TcmCase tcmCase = new TcmCase();
        tcmCase.setCaseNo(tcmCaseRecords.getCaseNo());
        List<TcmCase> caseList = tcmCaseService.selectTcmCaseList(tcmCase);
        if (caseList != null && !caseList.isEmpty()) {
            tcmCaseRecords.setRemark(caseList.get(0).getCaseName());
        }
        if (tcmCaseRecordsService.insertTcmCaseRecords(tcmCaseRecords) > 0) {
            return AjaxResult.success(tcmCaseRecords.getQuestionNo());
        }
        return AjaxResult.error("新增案例实验记录失败");
    }
    /**
     * 获取实验结果
     */

    @GetMapping("/recordReult")
    @ApiOperation("获取实验结果根据questionNo")
    public TcmCaseRecords recordReult(String questionNo)
    {
        // 兼容前端直接读取 resdata.score/resdata.extend2：这里不能包 AjaxResult。
        TcmCaseRecords query = new TcmCaseRecords();
        query.setQuestionNo(questionNo);
        List<TcmCaseRecords> list = tcmCaseRecordsService.selectTcmCaseRecordsList(query);
        return list == null || list.isEmpty() ? new TcmCaseRecords() : list.get(0);
    }

}

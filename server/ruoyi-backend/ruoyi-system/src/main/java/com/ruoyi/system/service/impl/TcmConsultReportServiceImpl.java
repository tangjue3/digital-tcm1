package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TcmConsultReport;
import com.ruoyi.system.domain.TcmConsultReportAttachment;
import com.ruoyi.system.domain.TcmConsultReportDisease;
import com.ruoyi.system.domain.vo.TcmConsultReportSubmitBody;
import com.ruoyi.system.mapper.TcmConsultReportAttachmentMapper;
import com.ruoyi.system.mapper.TcmConsultReportDiseaseMapper;
import com.ruoyi.system.mapper.TcmConsultReportMapper;
import com.ruoyi.system.service.ITcmConsultReportService;

/**
 * 问诊报告服务实现
 */
@Service
public class TcmConsultReportServiceImpl implements ITcmConsultReportService
{
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter CHART_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    @Autowired
    private TcmConsultReportMapper consultReportMapper;

    @Autowired
    private TcmConsultReportDiseaseMapper consultReportDiseaseMapper;

    @Autowired
    private TcmConsultReportAttachmentMapper consultReportAttachmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TcmConsultReport submitAppConsultReport(TcmConsultReportSubmitBody body, Long userId, String operatorName)
    {
        TcmConsultReport report = buildReport(body, userId, operatorName);
        consultReportMapper.insertTcmConsultReport(report);

        List<TcmConsultReportDisease> diseases = buildDiseaseList(body.getSelectedDiseases(), report.getReportId());
        if (!diseases.isEmpty())
        {
            consultReportDiseaseMapper.batchInsertTcmConsultReportDisease(diseases);
        }

        List<TcmConsultReportAttachment> attachments = buildAttachmentList(body.getAttachments(), report.getReportId());
        if (!attachments.isEmpty())
        {
            consultReportAttachmentMapper.batchInsertTcmConsultReportAttachment(attachments);
        }

        report.setDiseaseList(diseases);
        report.setAttachmentList(attachments);
        report.setDiseaseSummary(buildDiseaseSummary(report.getChiefComplaint(), diseases));
        return report;
    }

    @Override
    public List<TcmConsultReport> selectMyConsultReportList(TcmConsultReport report, Long userId)
    {
        report.setUserId(userId);
        List<TcmConsultReport> list = consultReportMapper.selectTcmConsultReportList(report);
        fillDiseaseSummary(list);
        return list;
    }

    @Override
    public TcmConsultReport selectAppConsultReportDetail(Long reportId, Long userId)
    {
        TcmConsultReport report = consultReportMapper.selectTcmConsultReportById(reportId);
        if (report == null || !StringUtils.equals(String.valueOf(userId), String.valueOf(report.getUserId())))
        {
            return null;
        }
        fillDetail(report);
        return report;
    }

    @Override
    public Map<String, Object> getConsultReportDashboard()
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("pendingCount", consultReportMapper.countReportsByStatus("pending"));
        result.put("todayVisitCount", consultReportMapper.countTodayReports());
        result.put("highRiskCount", consultReportMapper.countHighRiskUnfinishedReports());
        result.put("criticalRiskCount", consultReportMapper.countCriticalUnfinishedReports());
        result.put("weekTrend", buildTrendPayload(7));
        result.put("monthTrend", buildTrendPayload(30));
        result.put("diseaseDistribution", buildDiseaseDistribution(7, 5));
        result.put("updatedAt", DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", DateUtils.getNowDate()));
        return result;
    }

    @Override
    public List<TcmConsultReport> selectUrgentConsultReportList(Integer limit)
    {
        Integer finalLimit = limit == null || limit <= 0 ? 5 : limit;
        List<TcmConsultReport> list = consultReportMapper.selectUrgentConsultReportList(finalLimit);
        fillDiseaseSummary(list);
        return list;
    }

    @Override
    public List<TcmConsultReport> selectConsultReportList(TcmConsultReport report)
    {
        List<TcmConsultReport> list = consultReportMapper.selectTcmConsultReportList(report);
        fillDiseaseSummary(list);
        return list;
    }

    @Override
    public TcmConsultReport selectConsultReportDetail(Long reportId)
    {
        TcmConsultReport report = consultReportMapper.selectTcmConsultReportById(reportId);
        if (report == null)
        {
            return null;
        }
        fillDetail(report);
        return report;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConsultReportStatus(TcmConsultReport report)
    {
        report.setUpdateTime(DateUtils.getNowDate());
        return consultReportMapper.updateTcmConsultReport(report);
    }

    private TcmConsultReport buildReport(TcmConsultReportSubmitBody body, Long userId, String operatorName)
    {
        TcmConsultReport report = new TcmConsultReport();
        report.setReportNo(generateReportNo());
        report.setUserId(userId);
        report.setPatientName(body.getPatientName());
        report.setGender(body.getGender());
        report.setAge(body.getAge());
        report.setVisitType(body.getVisitType());
        report.setBasicDisease(body.getBasicDisease());
        report.setAllergyHistoryType(defaultValue(body.getAllergyHistoryType(), "无"));
        report.setAllergyHistoryDetail(defaultValue(body.getAllergyHistoryDetail(), ""));
        report.setPastHistoryType(defaultValue(body.getPastHistoryType(), "无"));
        report.setPastHistoryDetail(defaultValue(body.getPastHistoryDetail(), ""));
        report.setCurrentMedicationType(defaultValue(body.getCurrentMedicationType(), "无"));
        report.setCurrentMedicationDetail(defaultValue(body.getCurrentMedicationDetail(), ""));
        report.setChiefComplaint(defaultValue(body.getChiefComplaint(), ""));
        report.setDuration(defaultValue(body.getDuration(), ""));
        report.setSeverityLevel(body.getSeverityLevel());
        report.setSuddenWorse(defaultValue(body.getSuddenWorse(), ""));
        report.setLifeImpact(joinLifeImpact(body.getLifeImpact()));
        report.setHighestRiskLevel(resolveHighestRiskLevel(body.getSelectedDiseases()));
        report.setStatus("pending");
        report.setNeedOfflineVisit(Boolean.FALSE);
        report.setNeedSupplement(Boolean.FALSE);
        report.setCreateBy(defaultValue(operatorName, "app"));
        report.setCreateTime(DateUtils.getNowDate());
        report.setUpdateBy(defaultValue(operatorName, "app"));
        report.setUpdateTime(DateUtils.getNowDate());
        report.setDelFlag("0");
        return report;
    }

    private List<TcmConsultReportDisease> buildDiseaseList(List<TcmConsultReportSubmitBody.SelectedDisease> selectedDiseases, Long reportId)
    {
        if (selectedDiseases == null || selectedDiseases.isEmpty())
        {
            return Collections.emptyList();
        }
        List<TcmConsultReportDisease> list = new ArrayList<TcmConsultReportDisease>();
        Date now = DateUtils.getNowDate();
        for (int i = 0; i < selectedDiseases.size(); i++)
        {
            TcmConsultReportSubmitBody.SelectedDisease item = selectedDiseases.get(i);
            TcmConsultReportDisease disease = new TcmConsultReportDisease();
            disease.setReportId(reportId);
            disease.setDiseaseCode(defaultValue(item.getCode(), ""));
            disease.setDiseaseName(defaultValue(item.getName(), ""));
            disease.setDiseasePath(defaultValue(item.getPath(), ""));
            disease.setRiskLevel(defaultValue(item.getRiskLevel(), "normal"));
            disease.setRiskReason(defaultValue(item.getRiskReason(), ""));
            disease.setSortOrder(i + 1);
            disease.setCreateTime(now);
            list.add(disease);
        }
        return list;
    }

    private List<TcmConsultReportAttachment> buildAttachmentList(List<TcmConsultReportSubmitBody.SelectedAttachment> attachments, Long reportId)
    {
        if (attachments == null || attachments.isEmpty())
        {
            return Collections.emptyList();
        }
        List<TcmConsultReportAttachment> list = new ArrayList<TcmConsultReportAttachment>();
        Date now = DateUtils.getNowDate();
        for (TcmConsultReportSubmitBody.SelectedAttachment item : attachments)
        {
            if (StringUtils.isEmpty(item.getFileUrl()))
            {
                continue;
            }
            TcmConsultReportAttachment attachment = new TcmConsultReportAttachment();
            attachment.setReportId(reportId);
            attachment.setFileType(defaultValue(item.getFileType(), "other"));
            attachment.setFileName(defaultValue(item.getFileName(), ""));
            attachment.setFileUrl(defaultValue(item.getFileUrl(), ""));
            attachment.setFileSize(item.getFileSize());
            attachment.setCreateTime(now);
            list.add(attachment);
        }
        return list;
    }

    private void fillDiseaseSummary(List<TcmConsultReport> reports)
    {
        if (reports == null || reports.isEmpty())
        {
            return;
        }
        List<Long> reportIds = reports.stream()
                .map(TcmConsultReport::getReportId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        Map<Long, List<TcmConsultReportDisease>> diseaseMap = groupDiseaseByReportId(reportIds);
        for (TcmConsultReport report : reports)
        {
            List<TcmConsultReportDisease> diseases = diseaseMap.getOrDefault(report.getReportId(), Collections.emptyList());
            report.setDiseaseSummary(buildDiseaseSummary(report.getChiefComplaint(), diseases));
        }
    }

    private void fillDetail(TcmConsultReport report)
    {
        List<TcmConsultReportDisease> diseases = consultReportDiseaseMapper.selectTcmConsultReportDiseaseListByReportId(report.getReportId());
        List<TcmConsultReportAttachment> attachments = consultReportAttachmentMapper.selectTcmConsultReportAttachmentListByReportId(report.getReportId());
        report.setDiseaseList(diseases);
        report.setAttachmentList(attachments);
        report.setDiseaseSummary(buildDiseaseSummary(report.getChiefComplaint(), diseases));
    }

    private Map<Long, List<TcmConsultReportDisease>> groupDiseaseByReportId(List<Long> reportIds)
    {
        if (reportIds == null || reportIds.isEmpty())
        {
            return Collections.emptyMap();
        }
        List<TcmConsultReportDisease> diseases = consultReportDiseaseMapper.selectTcmConsultReportDiseaseListByReportIds(reportIds);
        Map<Long, List<TcmConsultReportDisease>> result = new LinkedHashMap<Long, List<TcmConsultReportDisease>>();
        for (TcmConsultReportDisease disease : diseases)
        {
            result.computeIfAbsent(disease.getReportId(), key -> new ArrayList<TcmConsultReportDisease>()).add(disease);
        }
        return result;
    }

    private String buildDiseaseSummary(String chiefComplaint, List<TcmConsultReportDisease> diseases)
    {
        if (StringUtils.isNotEmpty(chiefComplaint))
        {
            return chiefComplaint;
        }
        if (diseases != null && !diseases.isEmpty())
        {
            return diseases.stream()
                    .map(TcmConsultReportDisease::getDiseaseName)
                    .filter(StringUtils::isNotEmpty)
                    .limit(2)
                    .collect(Collectors.joining("、"));
        }
        return "暂无补充说明";
    }

    private String joinLifeImpact(List<String> lifeImpact)
    {
        if (lifeImpact == null || lifeImpact.isEmpty())
        {
            return "";
        }
        return lifeImpact.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(","));
    }

    private String resolveHighestRiskLevel(List<TcmConsultReportSubmitBody.SelectedDisease> selectedDiseases)
    {
        String highest = "normal";
        if (selectedDiseases == null)
        {
            return highest;
        }
        for (TcmConsultReportSubmitBody.SelectedDisease item : selectedDiseases)
        {
            String current = defaultValue(item.getRiskLevel(), "normal");
            if (riskOrder(current) > riskOrder(highest))
            {
                highest = current;
            }
        }
        return highest;
    }

    private int riskOrder(String riskLevel)
    {
        if ("critical".equals(riskLevel))
        {
            return 4;
        }
        if ("high".equals(riskLevel))
        {
            return 3;
        }
        if ("medium".equals(riskLevel))
        {
            return 2;
        }
        return 1;
    }

    private String generateReportNo()
    {
        return "TCM" + DateUtils.dateTimeNow() + String.format("%03d", RANDOM.nextInt(1000));
    }

    private String defaultValue(String value, String fallback)
    {
        return StringUtils.isEmpty(value) ? fallback : value;
    }

    private Map<String, Object> buildTrendPayload(int days)
    {
        LocalDate today = LocalDate.now();
        String startTime = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",
                Date.from(today.minusDays(days - 1L).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        List<Map<String, Object>> rows = consultReportMapper.selectRecentReportTrend(startTime);
        Map<String, Map<String, Object>> rowMap = new HashMap<String, Map<String, Object>>();
        for (Map<String, Object> row : rows)
        {
            rowMap.put(String.valueOf(row.get("statDate")), row);
        }

        List<String> labels = new ArrayList<String>();
        List<Long> reports = new ArrayList<Long>();
        List<Long> highRisk = new ArrayList<Long>();
        for (int i = days - 1; i >= 0; i--)
        {
            LocalDate date = today.minusDays(i);
            String key = date.format(DATE_KEY_FORMATTER);
            Map<String, Object> row = rowMap.get(key);
            labels.add(date.format(CHART_LABEL_FORMATTER));
            reports.add(row == null ? 0L : toLong(row.get("reportCount")));
            highRisk.add(row == null ? 0L : toLong(row.get("highRiskCount")));
        }

        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("labels", labels);
        payload.put("reports", reports);
        payload.put("highRisk", highRisk);
        return payload;
    }

    private List<Map<String, Object>> buildDiseaseDistribution(int days, int limit)
    {
        LocalDate today = LocalDate.now();
        String startTime = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",
                Date.from(today.minusDays(days - 1L).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        List<Map<String, Object>> rows = consultReportMapper.selectRecentDiseaseDistribution(startTime, limit);
        List<Map<String, Object>> distribution = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> row : rows)
        {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("name", String.valueOf(row.get("diseaseName")));
            item.put("value", toLong(row.get("diseaseCount")));
            distribution.add(item);
        }
        return distribution;
    }

    private Long toLong(Object value)
    {
        if (value == null)
        {
            return 0L;
        }
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        String text = String.valueOf(value);
        if (StringUtils.isEmpty(text))
        {
            return 0L;
        }
        return Long.valueOf(text);
    }
}

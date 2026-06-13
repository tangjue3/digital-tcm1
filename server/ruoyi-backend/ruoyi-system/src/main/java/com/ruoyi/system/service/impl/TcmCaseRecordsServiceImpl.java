package com.ruoyi.system.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.TcmCase;
import com.ruoyi.system.domain.TcmQuestionDetail;
import com.ruoyi.system.domain.vo.TcmCaseRecordsVo;
import com.ruoyi.system.mapper.TcmQuestionDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TcmCaseRecordsMapper;
import com.ruoyi.system.domain.TcmCaseRecords;
import com.ruoyi.system.service.ITcmCaseRecordsService;

/**
 * 实验案例记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-27
 */
@Service
public class TcmCaseRecordsServiceImpl implements ITcmCaseRecordsService 
{
    private final Logger logger=LoggerFactory.getLogger(TcmCaseRecordsServiceImpl.class);
    @Autowired
    private TcmCaseRecordsMapper tcmCaseRecordsMapper;

    @Autowired
    private TcmQuestionDetailMapper tcmQuestionDetailMapper;

    /**
     * 查询实验案例记录
     * 
     * @param id 实验案例记录主键
     * @return 实验案例记录
     */
    @Override
    public TcmCaseRecords selectTcmCaseRecordsById(Long id)
    {
        return tcmCaseRecordsMapper.selectTcmCaseRecordsById(id);
    }

    /**
     * 查询实验案例记录列表
     * 
     * @param tcmCaseRecords 实验案例记录
     * @return 实验案例记录
     */
    @Override
    public List<TcmCaseRecords> selectTcmCaseRecordsList(TcmCaseRecords tcmCaseRecords)
    {
        return tcmCaseRecordsMapper.selectTcmCaseRecordsList(tcmCaseRecords);
    }

    /**
     * 新增实验案例记录
     * 
     * @param tcmCaseRecords 实验案例记录
     * @return 结果
     */
    @Override
    public int insertTcmCaseRecords(TcmCaseRecords tcmCaseRecords)
    {
        return tcmCaseRecordsMapper.insertTcmCaseRecords(tcmCaseRecords);
    }

    /**
     * 修改实验案例记录
     * 
     * @param tcmCaseRecords 实验案例记录
     * @return 结果
     */
    @Override
    public int updateTcmCaseRecords(TcmCaseRecords tcmCaseRecords)
    {
        return tcmCaseRecordsMapper.updateTcmCaseRecords(tcmCaseRecords);
    }

    /**
     * 批量删除实验案例记录
     * 
     * @param ids 需要删除的实验案例记录主键
     * @return 结果
     */
    @Override
    public int deleteTcmCaseRecordsByIds(Long[] ids)
    {
        return tcmCaseRecordsMapper.deleteTcmCaseRecordsByIds(ids);
    }

    /**
     * 删除实验案例记录信息
     * 
     * @param id 实验案例记录主键
     * @return 结果
     */
    @Override
    public int deleteTcmCaseRecordsById(Long id)
    {
        return tcmCaseRecordsMapper.deleteTcmCaseRecordsById(id);
    }
    /**
     * 修改实验案例记录
     *
     * @param tcmCaseRecords 实验案例记录
     * @return 结果
     */
    @Override
    public int updateTcmCaseRecordsByResult(TcmCaseRecords tcmCaseRecords)
    {       //计算每个模块得分
        //TcmCaseRecords
        TcmQuestionDetail tcmQuestionDetail =new TcmQuestionDetail();
        tcmQuestionDetail.setQuestionNo(tcmCaseRecords.getQuestionNo());
        List<TcmQuestionDetail>  resultList=tcmQuestionDetailMapper.selectTcmQuestionDetailList(tcmQuestionDetail);
        //根据模块进行分组
        Map<String, List<TcmQuestionDetail>> groupedMap = resultList.stream()
                .collect(Collectors.groupingBy(TcmQuestionDetail::getModule));
        JSONObject scoreMod= new JSONObject();
        double resultScore=0;
        // 遍历Map
        for (Map.Entry<String, List<TcmQuestionDetail>> entry : groupedMap.entrySet()) {
            String key = entry.getKey();
            List<TcmQuestionDetail> valueList = entry.getValue();
            //处理 望、闻 切 每个模块25分
            if(!"03".equals(key)) {
                //处理得分
                double sum = 0;
                for (TcmQuestionDetail detail : valueList) {
                    if (Objects.equals(detail.getAnswer(), detail.getQuestionsAnswer())) {
                        sum = sum + 1;
                    }
                }
                double modeScore =((double) sum / valueList.size())*25;
                DecimalFormat df = new DecimalFormat("#0.0");
                String formattedPercentage = df.format(modeScore);
                scoreMod.put(key,formattedPercentage);
                resultScore=resultScore+Double.parseDouble(formattedPercentage);
            }else {
                //问诊根据询问的问题个数计算
                int size=valueList.size();
                double modeScore=0;
                if (size == 0) {
                    modeScore=0;
                } else if (size <= 3&&size>0) {
                    modeScore=10;
                } else if (size > 3&&size <= 5){
                    modeScore=20;
                }else if (size> 5){
                    modeScore=25;
                }
                resultScore=resultScore+modeScore;
                scoreMod.put(key,String.valueOf(modeScore));
            }
            //假设不存在问诊模块也为0
            if(!scoreMod.containsKey("03")){
                scoreMod.put("03","0");
            }
        }
        //将总分和分数详细信息加入表中
        DecimalFormat df = new DecimalFormat("#0.0");
        String formattedPercentage = df.format(resultScore);
        tcmCaseRecords.setScore(formattedPercentage);
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("01",scoreMod.getDoubleValue("01"));
        jsonObject.put("02",scoreMod.getDoubleValue("02"));
        jsonObject.put("03",scoreMod.getDoubleValue("03"));
        jsonObject.put("04",scoreMod.getDoubleValue("04"));
        tcmCaseRecords.setExtend2(jsonObject.toJSONString());

        return tcmCaseRecordsMapper.updateTcmCaseRecordsByResult(tcmCaseRecords);
    }

    public List<TcmCaseRecords> selectTcmCaseRecordsByCaseNo(String caseNo) {
        return tcmCaseRecordsMapper.selectTcmCaseRecordsByCaseNo(caseNo);
    }

    public  List<JSONObject> selectTcmCaseRecordsListByuserId(String userId){
        List<JSONObject> result=new ArrayList<>();
        TcmCaseRecords  tcmCaseRecords =new TcmCaseRecords();
        tcmCaseRecords.setUserId(userId);
        List<TcmCaseRecords> list= tcmCaseRecordsMapper.selectTcmCaseRecordsList(tcmCaseRecords);
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            TcmCaseRecords tcmCaseRecords1=list.get(i);
            TcmQuestionDetail tcmQuestionDetail =new TcmQuestionDetail();
            tcmQuestionDetail.setQuestionNo(tcmCaseRecords1.getQuestionNo());
            List<TcmQuestionDetail>  resultDetailList=tcmQuestionDetailMapper.selectTcmQuestionDetailList(tcmQuestionDetail);
            jsonObject.put("caseRecord",tcmCaseRecords1);
            jsonObject.put("caseRecordDetail",resultDetailList);
            result.add(jsonObject);
        }
      return result;
    }

    public   Map<String, List<TcmCaseRecordsVo>> getAllCharts(){
        List<TcmCaseRecordsVo>  recordsVos=  tcmCaseRecordsMapper.getAllCharts();
        // 按 casename 分组，然后在每个分组内按 score 降序排序
        Map<String, List<TcmCaseRecordsVo>> groupedAndSorted = recordsVos.stream()
                .collect(Collectors.groupingBy(TcmCaseRecordsVo::getCaseName,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparingDouble(TcmCaseRecordsVo::getScore).reversed())
                                        .collect(Collectors.toList())
                        )
                ));

        return groupedAndSorted;
    }

}

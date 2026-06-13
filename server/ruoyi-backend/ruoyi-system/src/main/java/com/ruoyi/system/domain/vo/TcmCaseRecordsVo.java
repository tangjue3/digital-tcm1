package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

import java.util.Date;
import java.util.Objects;

public class TcmCaseRecordsVo {

    /** 案例名称 */
    @Excel(name = "案例名称")
    private String caseName;
    /** 实验人员 */
    @Excel(name = "实验人员")
    private String userId;

    /** 实验案例编号 */
    @Excel(name = "实验案例编号")
    private String caseNo;

    /** 实验唯一编号 */
    @Excel(name = "实验唯一编号")
    private String questionNo;

    /** 分数 */
    @Excel(name = "分数")
    private double score;

    /** 实验时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实验时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TcmCaseRecordsVo that = (TcmCaseRecordsVo) o;
        return Objects.equals(caseName, that.caseName) &&
                Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseName, score);
    }



    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }


    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}

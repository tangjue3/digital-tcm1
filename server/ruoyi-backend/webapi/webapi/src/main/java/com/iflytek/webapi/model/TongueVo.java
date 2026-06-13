package com.iflytek.webapi.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class TongueVo {
    @JSONField(name = "basic_questions")
    private List<BasicQuestion> basicQuestions;
    @JSONField(name = "addition_questions")
    private List<AdditionQuestion> additionQuestions;
    @JSONField(name = "session_id")
    private String sessionId;
}

package com.iflytek.webapi.model;

import lombok.Data;

import java.util.List;
/** 解析极速转写结果 */
@Data
public class JsonParse {
    String task_id;
    String task_status;
    String task_type;
    String force_refresh;
    Result result;
}

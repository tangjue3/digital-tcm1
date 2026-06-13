package com.iflytek.webapi.model;

import lombok.Data;

import java.util.List;

@Data
public class AdditionQuestion {
    private int id;
    private String title;
    private List<Option> options;
}

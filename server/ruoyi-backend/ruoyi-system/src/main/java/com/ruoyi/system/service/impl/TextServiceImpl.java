package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.Text;
import com.ruoyi.system.mapper.TextMapper;
import com.ruoyi.system.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextServiceImpl implements TextService {
    @Autowired
    private TextMapper textMapper;

    @Override
    public List<Text> getText() {
        // 获取综合评分最高的课程列表
        return textMapper.getText();
    }
}

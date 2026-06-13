package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.Text;
import com.ruoyi.system.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index")

public class TextController {
    @Autowired
    private TextService textService;

    @GetMapping("/text")
    public List<Text> getText() {
        return textService.getText();
    }
}

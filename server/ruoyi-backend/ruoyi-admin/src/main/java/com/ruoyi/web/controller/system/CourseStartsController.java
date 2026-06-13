package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.AllCourseStarts;
import com.ruoyi.system.service.CourseStartsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/index")
public class CourseStartsController {
    @Resource
    private CourseStartsService courseStartsService;

    /**
     * 获取综合评分排序后的课程列表
     *
     * @return 按综合评分排序的课程列表
     */
    @GetMapping("/topCourseStarts")
    public List<AllCourseStarts> getTopCourses() {
        return courseStartsService.topCourseStarts();
    }
}

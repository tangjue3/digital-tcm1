package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.AllCourseStarts;
import com.ruoyi.system.mapper.CourseStartsMapper;
import com.ruoyi.system.service.CourseStartsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CourseStartsServiceImpl implements CourseStartsService {
    @Resource
    private CourseStartsMapper courseStartsMapper;

    /**
     * 获取按综合评分排序的课程列表
     * @return 排序后的课程列表
     */
    @Override
    public List<AllCourseStarts> topCourseStarts() {
        // 获取综合评分最高的课程列表
        return courseStartsMapper.topCourseStarts();
    }
}

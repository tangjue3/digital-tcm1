package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.AllCourseStarts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseStartsMapper {
    /**
     * 根据下述规则：
     * avg_review_stars（50%），favorite_count（20%），cart_count（30%）
     * 实现课程综合评分TOP的排名展示
     */
    @Select("SELECT\n" +
            "  course_id,\n" +
            "  course_name,\n" +
            "  avg_review_stars,\n" +
            "  favorite_count,\n" +
            "  cart_count,\n" +
            "  ( avg_review_stars * 0.5 + favorite_count * 0.2 + cart_count * 0.3 ) AS composite_score \n" +
            "FROM\n" +
            "  course_stats \n" +
            "ORDER BY\n" +
            "  composite_score DESC")
    List<AllCourseStarts> topCourseStarts();
}

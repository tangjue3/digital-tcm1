package com.ruoyi.system.mapper;
import com.ruoyi.system.domain.Text;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TextMapper {
    @Select("SELECT review_txt AS context FROM review_txt_limit_10")
    List<Text> getText();
}

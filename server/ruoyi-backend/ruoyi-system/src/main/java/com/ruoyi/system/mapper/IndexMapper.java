package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.Hot;
import com.ruoyi.system.domain.HotState;
import com.ruoyi.system.domain.Stock;
import com.ruoyi.system.domain.chinamap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IndexMapper {
    @Select("SELECT * FROM ChinaMap")
    List<chinamap> getMapData();

    @Select("SELECT * FROM Stock")
    List<Stock> getStockData();

    @Select("SELECT id, Name, (status1001 + status1002 + status1003 + status1004 + status1005 + status1006) AS total " +
            "FROM Hot ORDER BY total DESC limit 5")
    List<Hot> getHotName();

    @Select("SELECT id, status1001, status1002, status1003, " +
            "status1004, status1005, status1006, " +
            "(status1001 + status1002 + status1003 + status1004 + status1005 + status1006) AS total " +
            "FROM Hot ORDER BY total DESC limit 5")
    List<HotState> getHotData();
}

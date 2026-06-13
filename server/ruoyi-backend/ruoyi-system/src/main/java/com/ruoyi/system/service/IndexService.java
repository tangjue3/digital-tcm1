package com.ruoyi.system.service;



import com.ruoyi.system.domain.Hot;
import com.ruoyi.system.domain.HotState;
import com.ruoyi.system.domain.Stock;
import com.ruoyi.system.domain.chinamap;

import java.util.List;

public interface IndexService {
    List<chinamap> getMapDta();

    List<Stock> getStockData();

    List<HotState> getHotData();

    List<Hot> getHotName();
}

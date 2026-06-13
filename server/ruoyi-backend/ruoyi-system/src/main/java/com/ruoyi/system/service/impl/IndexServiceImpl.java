package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.Hot;
import com.ruoyi.system.domain.HotState;
import com.ruoyi.system.domain.Stock;
import com.ruoyi.system.domain.chinamap;
import com.ruoyi.system.mapper.IndexMapper;
import com.ruoyi.system.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private IndexMapper indexMapper;

    @Override
    public List<chinamap> getMapDta() {
        return indexMapper.getMapData();
    }

    @Override
    public List<Stock> getStockData() {
        return indexMapper.getStockData();
    }


    @Override
    public List<Hot> getHotName() {
        return indexMapper.getHotName();
    }

    @Override
    public List<HotState> getHotData() {
//        log.info("getHotData");
        List<HotState> hotstates = indexMapper.getHotData();
//        System.out.println(hotstates);
        return hotstates;
    }
}

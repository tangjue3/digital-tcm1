package com.ruoyi.web.controller.system;


import com.ruoyi.system.domain.Hot;
import com.ruoyi.system.domain.HotState;
import com.ruoyi.system.domain.Stock;
import com.ruoyi.system.domain.chinamap;
import com.ruoyi.system.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@CrossOrigin
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/map")
    public List<chinamap> getMapDta() {

        List<chinamap> chinaMapList = indexService.getMapDta();
        return chinaMapList;
    }

    @GetMapping("/stock")
    public List<Stock> getStockData() {

        List<Stock> stockList = indexService.getStockData();
        return stockList;
    }

    @GetMapping("/hot")
    public List<Hot> getHotData() {

        List<Hot> hotList = indexService.getHotName();
        List<HotState> hotStateList = indexService.getHotData();
        for (Hot hot : hotList) {
            for (HotState hotState : hotStateList) {
                if (hot.getId() == hotState.getId()) {

//                    System.out.println(hotState);
//                    System.out.println(hot);
//                    hot.setState(hotState);
                    if (hotState.getStatus1001() <= 20) hotState.setStatus1001(20 + hotState.getStatus1001());
                    if (hotState.getStatus1002() <= 20) hotState.setStatus1002(20 + hotState.getStatus1002());
                    if (hotState.getStatus1003() <= 20) hotState.setStatus1003(20 + hotState.getStatus1003());
                    if (hotState.getStatus1004() <= 20) hotState.setStatus1004(20 + hotState.getStatus1004());
                    if (hotState.getStatus1005() <= 20) hotState.setStatus1005(20 + hotState.getStatus1005());
                    if (hotState.getStatus1006() <= 20) hotState.setStatus1006(20 + hotState.getStatus1006());
                    hot.setState(hotState);
                }
            }
        }
        return hotList;
    }
}

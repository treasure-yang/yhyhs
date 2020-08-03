package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.model.RaPweight;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Classname RaPweightTests
 * @Description TODO
 * @Date 2020/07/01 上午 10:20
 * @Created by yanglichen
 */
@SpringBootTest
public class RaPweightTests {

    @Autowired
    private RaPweightService raPweightService;

    @Test//测试降雨总面积
    void getTotalAreaByStcdList(){
        Long start = System.currentTimeMillis();
        List<String> stcdList = GetSomethingUtils.get5Stcd();
        double totalArea = raPweightService.getTotalRainfallAreaByStcdList(stcdList);
        System.out.println("总降雨面积: "+totalArea);
        Long end = System.currentTimeMillis();
        System.out.println("本次计算耗时: "+(end-start)/1000.0+"秒");
    }

    @Test //测试得到新的权重
    void getReplacWeightInRRaPweight(){
        List<String> stcdList = GetSomethingUtils.getStcdList();
        List<RaPweight> raPweightList = raPweightService.getReplacWeightInRRaPweight(stcdList);
    }
}

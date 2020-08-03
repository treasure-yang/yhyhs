package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtVoMapper;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Classname RaPDayRtVoTests
 * @Description 雨量站详细信息的测试类
 * @Date 2020/07/01 下午 3:19
 * @Created by yanglichen
 */
@SpringBootTest
public class RaPDayRtVoTests {

    @Autowired
    private RaPDayRtVoService raPDayRtVoService;
    @Autowired
    private RaPDayRtVoMapper raPDayRtVoMapper;


    @Test
    void getRainfallQByStcdAndRainfallDate(){
//        Long startTime = System.currentTimeMillis();
//        List<String> stcdList = GetSomethingUtils.getStcdList();
//        String stcd = stcdList.get(0);
//        String dateStr = "2020/5/1";
//        Double totalRainfallQ = raPDayRtVoService.
//                getRainfallQByStcdAndRainfallDate(stcd, dateStr, 100d);
//        System.err.println("该雨量站的降雨量为: "+totalRainfallQ);
//        Long endTime = System.currentTimeMillis();
//        System.out.println("本次计算耗时: "+(endTime-startTime)/1000.0+"秒");
    }

    @Test//测试总降雨量
    void getAllRainFallQByRainfallDateAndStcdList(){
        //雨量站列表
        List<String> stcdList = GetSomethingUtils.get5Stcd();
        //降雨开始的日期
        Long startTime = System.currentTimeMillis();
        Date startDate = DateUtils.transformStrTODate("2020/7/15");
        Date endDate = DateUtils.transformStrTODate("2020/7/20");
        Double totalRainfallQ = raPDayRtVoService.
                getAllRainFallQByRainfallDateAndStcdList(startDate, endDate, stcdList);
        System.err.println("该区域下的总降雨量为: "+totalRainfallQ+" mm");
        Long endTime = System.currentTimeMillis();
        System.out.println("本次计算耗时: "+(endTime-startTime)/1000.0+"秒");

    }

    @Test //计算每日笼罩面积的测试方法
    void getAllRainfallAreaByStcdAndRainfallDay(){
        Long start = System.currentTimeMillis();
        double rfThresold = 2;
        String rainfallDateDay = "2020/7/15" ;
        Date calDate = DateUtils.transformStrTODate("2020/7/20");
        List<String> stcdList = GetSomethingUtils.get5Stcd();
        List<RaPDayRtVo> raPDayRtVoList = raPDayRtVoMapper.getRaPDayRtVoListByStcdListAndDateStr(stcdList, rainfallDateDay, DateUtils.transformDateTOStr(calDate));
        List<Double> dayRainfallArea = raPDayRtVoService.
                getAllRainfallAreaByStcdAndRainfallDay(raPDayRtVoList, rainfallDateDay, rfThresold,calDate);
        List<String> timeZoneForRainfallDays = DateUtils.
                getTimeZoneByRainfallDay(DateUtils.transformStrTODate(rainfallDateDay),calDate);
        for (int i = 0; i < timeZoneForRainfallDays.size(); i++) {
//            System.out.println(timeZoneForRainfallDays.get(i)+" 大于"+rfThresold+"mm 降雨笼罩面积为: "+(dayRainfallArea.get(i)/1000000)+"平方公里");
            System.out.println(dayRainfallArea.get(i));
        }
        Long end = System.currentTimeMillis();
        System.out.println("本次计算用时: "+(end-start)/1000.0+" 秒");
    }

    @Test //最大点雨量的测试
    void getMaxRfByRaPDayRtVoList(){
        List<String> stcdList = GetSomethingUtils.getStcdList();
        String rainfallDateDay = "2020/6/1" ;
        Date calDate = DateUtils.transformStrTODate("2020/6/20");
        List<RaPDayRtVo> raPDayRtVoList = GetSomethingUtils.
                getRaPDayRtVoListByStcdListAndRainfallDate(stcdList, rainfallDateDay, calDate);
        Map<String, Object> result = raPDayRtVoService.
                getMaxRfByRaPDayRtVoList(raPDayRtVoList, stcdList);
        System.out.println("最大的雨量站为: "+result.get("stcd")+ " 最大雨量为: "+result.get("maxRf"));
    }

    @Test
    void getTotalPn(){
        //200个站3秒左右
        //100个站2秒左右
        Long start = System.currentTimeMillis();
        List<String> stcdList = GetSomethingUtils.get100Stcd();
        String rainfallDateDay = "2020/5/1" ;
        Double totalPn = raPDayRtVoService.getBeforRainfallPoint(
                stcdList, DateUtils.transformStrTODate(rainfallDateDay));
        System.out.println("本场次的 前期雨量指数为: "+ totalPn);
        Long end = System.currentTimeMillis();
        System.out.println("本次计算用时: "+(end-start)/1000.0+" 秒");
    }

    @Test
    void getAllInformation(){
        List<String> stcdList = GetSomethingUtils.getTestStcdList();
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap =
                raPDayRtVoService.getAllRaPDayRtVoInformation(stcdList);
        System.err.println(allInformationMap.get("40723000")
                .get(DateUtils.transformStrTODate("2020/7/1")));
        //测试二级缓存是否可以被利用
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap1 =
                raPDayRtVoService.getAllRaPDayRtVoInformation(stcdList);
        System.err.println(allInformationMap1.get("40723000")
                .get(DateUtils.transformStrTODate("2020/7/10")));

    }


}

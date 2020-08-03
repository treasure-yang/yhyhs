package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @createDate: 2020/07/24 上午 11:29
 * @author: yanglichen
 */
@SpringBootTest
public class CalaulteSerivceTests {
    @Autowired
    CalculateSerivce calculateSerivce;

    @Autowired
    RaPDayRtVoService raPDayRtVoService;



    @Test
    void getTotalArea(){
        List<String> stcdList = GetSomethingUtils.get5Stcd();
//        Map<String, Map<Date, RaPDayRtVo>> allInformationMap = raPDayRtVoService.getAllRaPDayRtVoInformation(stcdList);
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap = GetSomethingUtils.getAllInformationMap(stcdList);
        System.out.println(calculateSerivce.getTotalRainfallArea(allInformationMap));
    }

    @Test //测试总降雨量
    void getTotalRainfallQ(){
        List<String> stcdList = GetSomethingUtils.get5Stcd();
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap = GetSomethingUtils.getAllInformationMap(stcdList);
        Date start = DateUtils.transformStrTODate("2020/7/15");
        Date endDate = DateUtils.transformStrTODate("2020/7/20");
        //得到总降雨面积
        Double totalRainfallArea = calculateSerivce.getTotalRainfallArea(allInformationMap);
        Double totalRainfallQ = calculateSerivce.getTotalRainfallQ(allInformationMap, start, endDate, totalRainfallArea);
        System.out.println("总降雨面积为: "+ totalRainfallArea/1000000+" 平方公里");
        System.out.println("总降雨量为: "+ totalRainfallQ+ " mm");
    }

    @Test
    void getDateListRainfallArea(){
        List<String> stcdList = GetSomethingUtils.get5Stcd();
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap = GetSomethingUtils.getAllInformationMap(stcdList);
        Date startDate = DateUtils.transformStrTODate("2020/7/15");
        Date endDate = DateUtils.transformStrTODate("2020/7/20");
        Double rainfallThreshold = 2.0;
        List<Double> dateRainfallArea = calculateSerivce.getTimesRainfallArea(allInformationMap, startDate, endDate, rainfallThreshold);
        dateRainfallArea.forEach(System.out::println);
//        List<Date> dateList = DateUtils.getDateListByStartDateAndEndDate(startDate, endDate);
//        List<List<RaPDayRtVo>> raPDayRtVosList = new ArrayList<>();
//        //遍历时间轴对象
//        for (Date date : dateList) {
//            //构造二层结果集
//            List<RaPDayRtVo> raPDayRtVoList = new ArrayList<>();
//            //遍历外层Map
//            for (Map.Entry<String, Map<Date, RaPDayRtVo>> firMap : allInformationMap.entrySet()) {
//                if (firMap.getValue() != null) {
//                    //根据第一层的value得到第二层的Map对象
//                    Map<Date, RaPDayRtVo> secMap = firMap.getValue();
//                    //根据日期得到对应的雨量站实例对象
//                    if (secMap.get(date) != null) {
//                        //得到的结果不为null,放入二级列表中
//                        raPDayRtVoList.add(secMap.get(date));
//                    }
//                }
//            }
//            //二级列表放入一级列表中
//            raPDayRtVosList.add(raPDayRtVoList);
//        }
//        System.out.println("时间轴的长度: "+ dateList.size());
//        System.out.println("返回对象的长度 "+ raPDayRtVosList.size());
//        raPDayRtVosList.forEach(System.out::println);
    }

    @Test
    void getBeforeRainfallPoint(){
        List<String> stcdList = new ArrayList<>();
        stcdList.add("40640050");
        stcdList.add("40640200");
        stcdList.add("40920350");
        stcdList.add("40640350");
        stcdList.add("41625800");
        stcdList.add("40630050");
        stcdList.add("40558000");
        stcdList.add("40632450");
        stcdList.add("40628100");
        stcdList.add("40627400");
        stcdList.add("40640300");
        stcdList.add("41624800");
        stcdList.add("40634850");
        stcdList.add("40624250");
        stcdList.add("40625850");
        stcdList.add("40629150");
        stcdList.add("40624850");
        stcdList.add("40927050");
        stcdList.add("40639950");
        Date rainfallStartDate = DateUtils.transformStrTODate("2020/7/1");
        Date befforRainfallDate = DateUtils.getNextDateByTimeZone(rainfallStartDate, 14);
        List<Date> dateList = DateUtils.getDateListByStartDateAndEndDate(befforRainfallDate,rainfallStartDate);
        System.out.println("日期列表为: ");
        dateList.forEach(date->{
            System.out.println(DateUtils.transformDateTOStr(date));
        });

        Map<String, Map<Date, RaPDayRtVo>> allInformationMap = GetSomethingUtils.getAllInformationMap(stcdList);
        Double rainfallPoint = calculateSerivce.getBeforeRainfallPoint(allInformationMap, rainfallStartDate);
        System.out.println("雨前指数: "+rainfallPoint);

        System.out.println("********************************************************************");

        Double beforeRainfallPoint = raPDayRtVoService
                .getBeforRainfallPoint(stcdList, rainfallStartDate);
        System.out.println("雨前指数: "+beforeRainfallPoint);

    }


}

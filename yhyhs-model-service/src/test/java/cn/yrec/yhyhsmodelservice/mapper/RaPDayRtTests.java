package cn.yrec.yhyhsmodelservice.mapper;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtMapper;
import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.utils.TransFormUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.*;

/**
 * @Classname RaPDayRtTests
 * @Description 实时雨量站降雨信息测试类
 * @Date 2020/06/29 下午 6:10
 * @Created by yanglichen
 */
@SpringBootTest
public class RaPDayRtTests {
    @Autowired
    private RaPDayRtMapper raPDayRtMapper;

    @Test
    void getRaPDayRtByStcdAndDate(){
        String stcd = "41036650";
        String date = "2020/7/9";
        RaPDayRt raPDayRt =  raPDayRtMapper.getRaPDayRtByStcdAndTime(stcd, date);
        System.out.println(raPDayRt);
    }

    @Test
    void getHaveRainRaPDayRtListByStcdList(){
        List<String> stcdList = GetSomethingUtils.getStcdList();
        Double rfThreshold = 2.0;
        Date calDate = DateUtils.transformStrTODate("2020/6/1");
        List<RaPDayRt> raPDayRtList = raPDayRtMapper.getHaveRainRaPDayRtListByStcdList(stcdList, rfThreshold, calDate);
        List<List<RaPDayRt>> raPDayRtLists = TransFormUtils.getListRaPDayRtListByStringList(raPDayRtList, stcdList);
        raPDayRtLists.forEach(System.out::println);
    }

    @Test
    void getrainfallDurationRainPDayRtListByStcdAndDate(){
        String stcd = "41126800";
        String dateStr = "2020/5/1";
        List<RaPDayRt> raPDayRtList = raPDayRtMapper.getrainfallDurationRainPDayRtListByStcdAndDate(stcd, dateStr);
        System.out.println("数据长度为: "+raPDayRtList.size());
        raPDayRtList.forEach(System.err::println);

    }

    @Test
    void getRapDayRtListByStcdListAndNowDate(){
        List<String> stcdList = GetSomethingUtils.getStcdList();
        String nowDate = DateUtils.transformDateTOStr(new Date());
        List<RaPDayRt> raPDayRtList = raPDayRtMapper
                .getRapDayRtListByStcdListAndNowDate(stcdList, "2020/7/13");
        if (raPDayRtList==null || raPDayRtList.size()==0) {
            System.err.println("没有查询到需要的结果");
        }else{
            raPDayRtList.forEach(System.out::println);
        }
    }

    @Test //查询站列表在某个时间前的所有数据
    void getRaPdayRtListByBeginDate() {
        Long start = System.currentTimeMillis();
        List<String> stcdList = GetSomethingUtils.getTestStcdList();
        List<RaPDayRt> raPDayRtList = raPDayRtMapper.getRaPdayRtListByBeginDate(stcdList, DateUtils.transformStrTODate("2020/5/1"));
        raPDayRtList.forEach(System.out::println);
        Long end = System.currentTimeMillis();
        System.out.println("本次计算用时: "+(end-start)/1000.0+" 秒");
    }

    @Test
    void getRaPDayRtListCountByDate(){
        List<String> stcdList = GetSomethingUtils.getTestStcdList();
        Date date = DateUtils.transformStrTODate("2020/07/09");
        Double rainfallThreshold = 2.0;
        long dateTimes = date.getTime();
        while(true){
            Integer count = raPDayRtMapper.getRaPDayRtListCountByDate(stcdList, date, rainfallThreshold);
            if (count==0) {
                System.out.println(DateUtils.transformDateTOStr(new Date(dateTimes)));
                break;
            }
            dateTimes += DateUtils.ONE_DAY_MILLI_SECOND;
            date.setTime(dateTimes);
        }
    }

    @Test
    void a() {
        List<String> stcdList = GetSomethingUtils.getTestStcdList();
        Date startDate = DateUtils.transformStrTODate("2020/7/20");
        Double rainfallThreshold = 20.0;
        List<Map<String, Object>> mapList = raPDayRtMapper.getDatesCountRainfall(stcdList, startDate, rainfallThreshold);
        Map<Date, Integer> dateCountMap = new HashMap<>();
        mapList.forEach(map->{
            Date date = null;
            Integer count = null;
            /*
             每个List中的元素都是这样的一个Map
             {VALUE=32, KEY=2020-07-18 00:00:00.0}
             */
            for (Map.Entry<String, Object> m : map.entrySet()){
                if("KEY".equalsIgnoreCase(m.getKey())){
                   date = (Date) m.getValue();
                }
                if ("VALUE".equalsIgnoreCase(m.getKey())) {
                    count = Integer.valueOf(m.getValue().toString());
                }
                dateCountMap.put(date, count);
            }
        });
        System.out.println(dateCountMap.size());
        System.out.println(dateCountMap.get(DateUtils.transformStrTODate("2020/7/23")));

//        Map<Date, Integer> map1 = new HashMap<>();
//        map1.put(new Date(), 111);
//        System.out.println(map1);

        //排序Map
        Set<Date> dateSet = dateCountMap.keySet();
        List<Date> dateList = new ArrayList<>(dateSet);
        long minDateTime = Long.MAX_VALUE;
        for (Date date : dateList) {
            if(date!=null){
                if (date.getTime()<minDateTime) {
                    minDateTime = date.getTime();
                }
            }
        }
        System.out.println(DateUtils.transformDateTOStr(new Date(minDateTime)));
    }
}


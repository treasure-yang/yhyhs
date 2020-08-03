package cn.yrec.yhyhsmodelservice.mapper;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtVoMapper;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.utils.TransFormUtils;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @Classname RaPDayRtVoTests
 * @Description 雨量站详细信息的测试类
 * @Date 2020/07/01 上午 10:08
 * @Created by yanglichen
 */
@SpringBootTest
public class RaPDayRtVoTests {

    @Autowired
    private RaPDayRtVoMapper raPDayRtVoMapper;

    @Test
    void getRaPDatRtVoListByStcdAndDateStr(){
//        String stcd = "40630050";
//        String dateStr = "2020/6/18";
//        List<RaPDayRtVo> raPDayRtVoList = raPDayRtVoMapper.getRaPDayRtVoListByStcdAndDateStr(stcd, dateStr);
//        raPDayRtVoList.forEach(System.err::println);
    }

    @Test
    void getRaPDayRtVoListByStcdListAndDateStr(){
        Long start = System.currentTimeMillis();
        List<String> stcdList = GetSomethingUtils.getTestStcdList();
        stcdList.forEach(System.err::println);
        String dateStr = "2020/6/15";
        Date calDate = DateUtils.transformStrTODate("2020/6/18");
        List<RaPDayRtVo> raPDayRtVoList = raPDayRtVoMapper.getRaPDayRtVoListByStcdListAndDateStr(stcdList, dateStr, DateUtils.transformDateTOStr(calDate));
        raPDayRtVoList.forEach(System.out::println);
        List<List<RaPDayRtVo>> list = TransFormUtils.getListRaPDayRtVoListByStringList(raPDayRtVoList, stcdList );
        list.forEach(raPDayRtVos -> {
            System.out.println("列表长度为:" +raPDayRtVos.size());
            System.out.println(raPDayRtVos);
        });
        Long end = System.currentTimeMillis();
        System.out.println("本次计算用时: "+(end-start)/1000.0+" 秒");
    }

    @Test //测试输出一个时间段内的雨量站详细信息
    void getRaPDayRtVoListByStcdListAndToDate(){
        List<String> stcdList = GetSomethingUtils.get100Stcd();
        String startDateStr = "2020/7/1";
        Date startDate = DateUtils.transformStrTODate(startDateStr);
        Date endDate = DateUtils.getNextDateByTimeZone(startDate, 15);
        System.out.println(DateUtils.transformDateTOStr(startDate));
        System.out.println(DateUtils.transformDateTOStr(endDate));
        List<RaPDayRtVo> raPDayRtVoList = raPDayRtVoMapper.getRaPDayRtVoListByStcdListAndTwoDate(stcdList,startDate, endDate);
        raPDayRtVoList.forEach(System.out::println);
    }

    @Test
    void getAllInformation(){
        Long start = System.currentTimeMillis();
        List<String> stcdList = GetSomethingUtils.get5Stcd();
        Date nowDate = new Date();
        List<RaPDayRtVo> allInformation = raPDayRtVoMapper.getAllInformation(stcdList, nowDate);
        System.err.println("数据长度: "+allInformation.size());
        Long end = System.currentTimeMillis();
        System.out.println("本次计算用时: "+(end-start)/1000.0+" 秒");
        /*
         * 转换数据格式
         * 根据站码列表构造外层Map的Key
         */
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap = new HashMap<>();
        //得到按照站码分类的数据
        List<List<RaPDayRtVo>> raPDayRtVosList =
                TransFormUtils.getListRaPDayRtVoListByStringList(allInformation, stcdList);
        //遍历站码列表进行构造外层Map
        stcdList.forEach(stcd->{
            //初始化内层Map
            Map<Date, RaPDayRtVo> raPDayRtVoMap = null;
            //遍历按照站码分类的列表
            for (List<RaPDayRtVo> raPDayRtVoList : raPDayRtVosList) {
                if(raPDayRtVoList.size()!=0){
                    //找到站码列表对应的列表
                    if (raPDayRtVoList.get(0).getStcd().equalsIgnoreCase(stcd)) {
                        raPDayRtVoMap = new HashMap<>();
                        //遍历该列表对象
                        for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
                            //存放内层Map数据
                            raPDayRtVoMap.put(raPDayRtVo.getDate(), raPDayRtVo);
                        }
                    }
                }
            }
            allInformationMap.put(stcd, raPDayRtVoMap);
        });
        System.out.println(allInformationMap.size());
        System.err.println(allInformationMap.get("40723000")
                .get(DateUtils.transformStrTODate("2020/7/1")));
        Long end2Time = System.currentTimeMillis();
        System.out.println("再次计算用时: "+(end2Time-end)/1000.0+" 秒");

//        ************************************************************************************************
        List<RaPDayRtVo> raPDayRtVoList = new ArrayList<>();
        //遍历第一层Map
        for (Map.Entry<String, Map<Date, RaPDayRtVo>> firMap:allInformationMap.entrySet()){
            if (firMap.getValue()!=null) {
                System.out.println(firMap);
                //根据第一层的value得到第二层的Map
                Map<Date, RaPDayRtVo> secMap = firMap.getValue();
                //遍历第二层的Map
                for (Map.Entry<Date, RaPDayRtVo> nextMap :  secMap.entrySet()) {
                    if (nextMap.getValue()!=null){
                        RaPDayRtVo raPDayRtVo = nextMap.getValue();
                        raPDayRtVoList.add(raPDayRtVo);
                        break;
                    }
                }
            }
        }
        System.out.println("列表长度: "+raPDayRtVoList.size());
        raPDayRtVoList.forEach(System.out::println);
    }

    @Test
    void a() {

    }
}

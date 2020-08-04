package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtMapper;
import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @Classname RaPDayRtTests
 * @Description TODO
 * @Date 2020/06/30 上午 11:16
 * @Created by yanglichen
 */
@SpringBootTest
public class RaPDayRtTests {
    @Autowired
    private RaPDayRtService raPDayRtService;
    @Autowired
    private RaPDayRtMapper raPDayRtMapper;

    @Test
    void getRainfallDate(){
        Long start = System.currentTimeMillis();

        List<String> stcdList = GetSomethingUtils.getTestStcdList();
//        List<String> stcdList = new ArrayList<>();
//        stcdList.add("40720600");
//        stcdList.add("40721200");
//        stcdList.add("40723000");
//        stcdList.add("40723600");
        Double rtThreshold = 2.0;
        Date calDate = DateUtils.transformStrTODate("2020/7/19");
        //递归查询
        for (int i = 0; i < 200; i++) {
            Integer count = raPDayRtMapper.getRaPDayRtListCountByDate(stcdList, calDate,2.0);
            calDate.setTime(calDate.getTime()-DateUtils.ONE_DAY_MILLI_SECOND);
            if(count!=0){
                System.out.println(DateUtils.transformDateTOStr(calDate)+"日的条数为: "+count);
            }


        }
        Long end = System.currentTimeMillis();
        System.out.println("本次计算用时: "+(end-start)/1000.0+" 秒");
    }

    @Test
    void getTimes() {
        Date date = DateUtils.transformStrTODate("2020/08/3");
        System.out.println(date.getTime());

    }

//    @Test //得到降雨的那天
//    void getRainfallDay() {
//        List<String> stcdList = GetSomethingUtils.getTestStcdList();
//        Date date = DateUtils.transformStrTODate("2020/07/16");
//        Date time = raPDayRtService.getLastRainfallDate(stcdList, 2.0, 2, date);
//        System.err.println(new SimpleDateFormat("yyyy/MM/dd").format(time));
//    }

//    @Test //得到雨停的那天
//    void getEndRainfallDay() {
//        List<String> stcdList = GetSomethingUtils.getTestStcdList();
//        Date rainfallDate = DateUtils.transformStrTODate("2020/07/06");
//        Double rainfallThreshold = 2.0;
//        Integer timeInterval = 2;
//        Date endDate = raPDayRtService.getRainfallEndDayByStcdListAndRainfallDate(stcdList, rainfallDate, rainfallThreshold,timeInterval);
//        System.out.println(DateUtils.transformDateTOStr(endDate));
//    }

//    @Test
//    void a(){
//        Integer rainfallTolerance = 3;
//        Integer rainfallTaketime = 10;
//        //数组长度(1+容差*2)
//        int[] indexArray = new int[1 + rainfallTolerance*2];
//        //向数组中存放元素
//        indexArray[0] = rainfallTaketime;
//        for (int i = 1; i < indexArray.length; i++) {
//            //偶数就添加增大的容差,奇数添加减少的容差
//            if (i%2==0) {
//                indexArray[i] = rainfallTaketime+(i/2);
//            }else{
//                indexArray[i] = rainfallTaketime-(i/2)-1;
//            }
//        }
//        System.out.println(Arrays.toString(indexArray));
//    }

//    @Test
//    void sort(){
//        //
//        List<Integer> randomNumberList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Integer random = (int)( Math.random()*100+1);
//            randomNumberList.add(random);
//        }
//        List<Integer> newList = new ArrayList<>();
//        System.err.println(randomNumberList);
//        //排序(最小值)
//        for (int j = 0; j < 10; j++) {
//            int min = randomNumberList.get(0);
//            for (Integer integer : randomNumberList) {
//                if (integer < min) {
//                    min = integer;
//                }
//            }
//            System.out.println(min);
//            newList.add(min);
//            Iterator<Integer> integerIterator = randomNumberList.iterator();
//            while(integerIterator.hasNext()){
//                if (integerIterator.next()==min) {
//
//                    integerIterator.remove();
//                    break;
//                }
//            }
//
//        }
//        System.out.println(newList);
//    }





}

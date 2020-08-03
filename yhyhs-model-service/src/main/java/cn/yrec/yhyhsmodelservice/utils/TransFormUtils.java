package cn.yrec.yhyhsmodelservice.utils;

import cn.yrec.yhyhsmodelservice.model.RaPDay;
import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
import cn.yrec.yhyhsmodelservice.vo.YearRaPDay;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Classname TransFormUtils
 * @Description 用于类型转换的工具类
 * @Date 2020/06/29 上午 10:30
 * @Created by yanglichen
 */
public class TransFormUtils {

    /**
     * 方法描述: 根据传入某年中每月的雨量站列表得到全年雨量站实例
     * @author yanglichen
     * @date 2020-07-06 18:29
     * @param raPDayList [某年某雨量站12个月列表]
     **/
    public static YearRaPDay getYearRaPDayByRaPDays(List<RaPDay> raPDayList){
        //构造年雨量站实例
        YearRaPDay yearRaPDay = new YearRaPDay();
        //构造年降雨列表(年降雨站实例的属性)
        List<Double> dailyRainFall = new ArrayList<>();
        //对雨量站列表按照月份进行排序
        sortRaPDayByMonth(raPDayList);
        //遍历雨量站列表
        raPDayList.forEach(raPDay -> {
            //站码
            yearRaPDay.setStcd(raPDay.getStcd());
            //雨量站名称
            yearRaPDay.setStnm(raPDay.getStnm());
            //所属年份
            yearRaPDay.setYear(raPDay.getYear());
            //判断所属月份的天数
            int day = DateUtils.judgeMonthDaysByYear(Integer.parseInt(raPDay.getYear()), Integer.parseInt(raPDay.getMonth()));
            for (int i = 0; i < day; i++) {
                dailyRainFall.add(0d);
            }
        });
        return yearRaPDay;
    }

    /**
     * 方法描述: 对雨量站列表按照月份进行排序
     * @author yanglichen
     * @date 2020-07-06 18:28
     * @param raPDayList  [某年某雨量站12个月列表]
     **/
    public static void sortRaPDayByMonth(List<RaPDay> raPDayList){
        Collections.sort(raPDayList, new Comparator<RaPDay>() {
            @Override
            public int compare(RaPDay r1, RaPDay r2) {
                int diff = Integer.parseInt(r1.getMonth())-Integer.parseInt(r2.getMonth());
                return Integer.compare(diff, 0);
            }
        });
    }

    /**
     * 方法描述: 提取某年某月某雨量站的当月的信息为一个列表
     * (列表长度固定31)
     * @author yanglichen
     * @date 2020-07-06 18:28
     * @param raPDay 某年某月某雨量站的当月的信息
     * @return 某年某月当月的降雨信息
     **/
    public static List<Double> getRallListByMonthDay(RaPDay raPDay){
        //每个月的雨量信息(长度固定31)
        List<Double> monthRainList = new ArrayList<>();
        //得到雨量站类中的方法数组
        Field[] properties = raPDay.getClass().getFields();
        //新方法数组
        Field[] monthProperties = new Field[properties.length-4];
        //数组复制
        System.arraycopy(properties,4,monthProperties,0,monthProperties.length);
        //遍历属性列表,通过反射获取每个月的值
        for (Field f : monthProperties) {
            f.setAccessible(true);
            try {
                monthRainList.add((double)f.get(raPDay));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //返回月降雨列表
        return monthRainList;
    }


    /**
     * 方法描述: 根据站码列表得到雨量站集合列表
     * @author yanglichen
     * @date 2020-07-06 18:27
     * @param raPDayRtResultList 查询出来的雨量站结果列表
     * @param stcdList 站码列表
     * @return [雨量站1的列表, 雨量站2的列表,雨量站3的列表....]
     **/
    public static List<List<RaPDayRt>> getListRaPDayRtListByStringList(List<RaPDayRt> raPDayRtResultList, List<String> stcdList){
        List<List<RaPDayRt>> raPDayRtLists = new ArrayList<>();
        //遍历站码列表
        stcdList.forEach(stcd ->{
            List<RaPDayRt> raPDayRtList = new ArrayList<>();
            //遍历雨量站结果集
            raPDayRtResultList.forEach(raPDayRt -> {
                //如果站码相同就放入list中
                if (raPDayRt.getStcd().equalsIgnoreCase(stcd)) {
                    raPDayRtList.add(raPDayRt);
                }
            });
            raPDayRtLists.add(raPDayRtList);
        });
        return raPDayRtLists;
    }

    /**
     * 方法描述: 根据站码列表得到雨量站详细信息的集合列表
     * @author yanglichen
     * @date 2020-07-06 18:27
     * @param raPDayRtResultList 查询出来的雨量站结果列表
     * @param stcdList 站码列表
     * @return [雨量站1的列表, 雨量站2的列表,雨量站3的列表....]
     **/
    public static List<List<RaPDayRtVo>> getListRaPDayRtVoListByStringList(List<RaPDayRtVo> raPDayRtResultList, List<String> stcdList){
        List<List<RaPDayRtVo>> raPDayRtVoLists = new ArrayList<>();
        //遍历站码列表
        stcdList.forEach(stcd ->{
            List<RaPDayRtVo> raPDayRtVoList = new ArrayList<>();
            //遍历雨量站结果集
            raPDayRtResultList.forEach(raPDayRtVo -> {
                //如果站码相同就放入list中
                if (raPDayRtVo.getStcd().equalsIgnoreCase(stcd)) {
                    boolean f = true;
                    //放入的时候去重复
                    if (raPDayRtVoList.size()>0) {
                        for (RaPDayRtVo pDayRtVo : raPDayRtVoList) {
                            if (pDayRtVo.getDate().getTime() == raPDayRtVo.getDate().getTime()) {
                                f = false;
                                break;
                            }
                        }
                    }
                    if (f) {
                        raPDayRtVoList.add(raPDayRtVo);
                    }

                }
            });
            //对雨量站列表按照日期降序进行排序
            raPDayRtVoLists.add(raPDayRtVoList);
        });
        return raPDayRtVoLists;
    }
}

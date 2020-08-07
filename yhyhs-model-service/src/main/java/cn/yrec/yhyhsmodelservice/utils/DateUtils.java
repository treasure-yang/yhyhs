package cn.yrec.yhyhsmodelservice.utils;

import cn.yrec.yhyhsmodelservice.model.RaPDayRt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Classname DateUtils
 * @Description 用于处理日期的工具类
 * @Date 2020/06/29 上午 10:19
 * @Created by yanglichen
 */
public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    public static final Long ONE_DAY_MILLI_SECOND = 1000L * 24 * 60 *60;

    /**
     * 方法描述: 根据年,判断这个月有多少天
     * @author yanglichen
     * @date 2020-07-06 16:13
     * @param year 年
     * @param month 月
     * @return 这个月有多少天
     **/
    public static Integer judgeMonthDaysByYear(Integer year, Integer month){
        switch(month){
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if(year%400==0 || (year%4==0 && year%100!=0)){
                    return 28;
                }else{
                    return 29;
            }
        }
        return 31;
    }

    /**
     * 方法描述: 将字符串格式的时间转化为Date格式
     * @author yanglichen
     * @date 2020-07-06 16:14
     * @param dateStr 字符串格式的时间如: 2020-03-14 00:00:00
     * @return 日期
     **/
    public static Date transformStrTODate(String dateStr){
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 方法描述: 将日期转换为字符串
     * @author yanglichen
     * @date 2020-07-06 16:14
     * @param date 日期
     * @return 字符串格式的日期
     **/
    public static String transformDateTOStr(Date date){
        return sdf.format(date);
    }


    /**
     * 方法描述: 按照日期的降序对雨量站进行排序(日期大的在后)
     * @author yanglichen
     * @date 2020-07-06 16:14
     * @param raPDayRtList 雨量站列表
     **/
    public static void sortRaPDayRtByDate(List<RaPDayRt> raPDayRtList){
        Collections.sort(raPDayRtList, new Comparator<RaPDayRt>() {
            @Override
            public int compare(RaPDayRt r1, RaPDayRt r2) {
                long r1Date = r1.getDate().getTime();
                long r2Date = r2.getDate().getTime();
                int diff = (int) (r1Date-r2Date);
                return Integer.compare(diff, 0);
            }
        });
    }

    /**
     * 方法描述: 判断传入的时间距离今天有多少天
     * @author yanglichen
     * @date 2020-07-06 16:15
     * @param date 传入时间
     * @return 传入时间距离今天的时间间隔天数
     **/
    public static Integer getTimesByDate(Date date){
        Long inputDateTime = date.getTime();
        Long nowDateTime = new Date().getTime();
        return Math.toIntExact((nowDateTime - inputDateTime) / ONE_DAY_MILLI_SECOND);
    }

    /**
     * 方法描述: 判断两个日期之间间隔有多少天
     * @author yanglichen
     * @date 2020-07-15 17:47
     * @param startDate 开始日 (较新)
     * @param endDate 结束日 (较老)
     * @return 间隔天数
     **/
    public static Integer getTimesByStartDateAndEndDate(Date startDate, Date endDate){
        Long startDateTime = startDate.getTime();
        Long endDateTime = endDate.getTime();
        return Math.toIntExact((startDateTime - endDateTime) / ONE_DAY_MILLI_SECOND)+1;
    }

    /**
     * 方法描述: 根据传入的时间,装满字符串格式的时间列表(升序:日期最小的在前面)
     * @author yanglichen
     * @date 2020-07-06 16:16
     * @param rainfallDay 开始下雨的时间
     * @return 时间段列表如 :[开始下雨的时间, ... 2020/6/1, 2020/6/2, 2020/6/3.....今天]
     **/
    public static List<String> getTimeZoneByRainfallDay(Date rainfallDay, Date calDate) {
        //得到时间间隔
        Integer days = getTimesByStartDateAndEndDate(calDate, rainfallDay);
        //时间间隔天数列表
        List<String> timeZone = new ArrayList<>();
        //构造日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        //得到一个下雨那天的时间戳
        long times = rainfallDay.getTime();
        //根据时间间隔天数,向时间间隔列表中循环增加元素
        for (int i = 0; i < days; i++) {
            //根据毫秒数构造一个日期对象
            Date temp = new Date(times);
            //类型转换
            String timeStr = simpleDateFormat.format(temp);
            //添加到之间间隔列表中
            timeZone.add(timeStr);
            //增加一天
            times += ONE_DAY_MILLI_SECOND;
        }
        return timeZone;
    }

    /**
     * 方法描述: 根据开始日期和结束日期得到一个由日期组成的一个列表
     * @author yanglichen
     * @date 2020-07-06 18:14
     * @param startDay 开始时间 (较靠前)
     * @param endDay 结束时间 (之前的时间)
     * @return 例如 [2020-07-06, 2020-07-05, 2020-07-04.......2020-06-25]
     **/
    public static List<String> getTimeZoneByStartDayAndEndDay(Date startDay, Date endDay) {
        //得到时间间隔
        int days = (int) ((startDay.getTime() - endDay.getTime()) / ONE_DAY_MILLI_SECOND);
        //时间间隔天数列表
        List<String> timeZone = new ArrayList<>();
        //构造日期格式化对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        //得到一个下雨那天的时间戳
        long times = startDay.getTime();
        //根据时间间隔天数,向时间间隔列表中循环增加元素
        for (int i = 0; i < days + 1; i++) {
            //根据毫秒数构造一个日期对象
            Date temp = new Date(times);
            //类型转换
            String timeStr = simpleDateFormat.format(temp);
            //添加到之间间隔列表中
            timeZone.add(timeStr);
            //减少
            times -= ONE_DAY_MILLI_SECOND;
        }
        return timeZone;
    }

    /**
     * 方法描述: 根据传入的时间和给定的时间间隔,返回距离传入时间之前时间间隔的日期
     * @author yanglichen
     * @date 2020-07-06 16:17
     * @param startDate 传入时间
     * @param timeZone 时间间隔
     * @return 距离传入时间之前时间间隔的日期
     **/
    public static Date getNextDateByTimeZone(Date startDate, Integer timeZone){
        //开始时间的毫秒数
        long startDateTime = startDate.getTime();
        //结束时间的毫秒数 = 开始时间毫秒数 - (时间间隔-1) * 一天的毫秒数
        long endDateTime = startDate.getTime() - (timeZone) * ONE_DAY_MILLI_SECOND;
        Date endTime = new Date();
        endTime.setTime(endDateTime);
        return endTime;
    }

    /**
     * 方法描述: 去掉日期格式的小时部分
     * 如 2020-07-14 18:19 --> 2020-07-14 00:00
     * 用于对数据进行查找的时候框定范围等
     * @author yanglichen
     * @date 2020-07-14 18:19
     * @param date 传入的时间
     * @return 没有小时的时间
     **/
    public static Date getNotHourDate(Date date){
        String temp = transformDateTOStr(date);
        return transformStrTODate(temp);
    }

    /**
     * 方法描述: 根据传入的时间和给定的时间间隔,返回这个日期之后的一个日期
     * @author yanglichen
     * @date 2020-07-06 16:17
     * @param startDate 传入时间
     * @param timeZone 时间间隔
     * @return 这个日期之后的一个日期
     **/
    public static Date getAfterDateByTimeZone(Date startDate, Integer timeZone){
        //开始时间的毫秒数
        long startDateTime = startDate.getTime();
        //结束时间的毫秒数 = 开始时间毫秒数 - (时间间隔-1) * 一天的毫秒数
        long endDateTime = startDate.getTime() + (timeZone-1) * ONE_DAY_MILLI_SECOND;
        Date endTime = new Date();
        endTime.setTime(endDateTime);
        return endTime;
    }


    /**
     * 方法描述: 根据开始日期, 结束日期得到一个包前包后的时间列表
     * @author yanglichen
     * @date 2020-07-24 15:31
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 包前包后的时间列表
     **/
    public static List<Date> getDateListByStartDateAndEndDate(Date startDate, Date endDate){
        List<Date> dateList = new ArrayList<>();
        Date tempDate = new Date(startDate.getTime());
        do {
            dateList.add(new Date(tempDate.getTime()));
            tempDate.setTime(tempDate.getTime() + ONE_DAY_MILLI_SECOND);
        } while (tempDate.getTime() <= endDate.getTime());
        return dateList;
    }

    /**
     * 方法描述: 将前端的格林威治时间转换为北京时间
     * @author yanglichen
     * @date 2020-08-07 09:59
     * @param webDateString 从网页传入的格林威治时间:
     *                      例如: 2017-10-06T16:00:00.000+0000
     * @return 北京时间: 2017-10-07
     **/
    public static Date transformWebDateToBeijingDate(String webDateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+Z");
        String dateStr = "2017-10-06T16:00:00.000+0000".replace("0000", "UTC");
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

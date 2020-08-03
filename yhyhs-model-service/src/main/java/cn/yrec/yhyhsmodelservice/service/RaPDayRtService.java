package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.model.RaPDayRt;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Classname RaPDayRtService
 * @Description 雨量站业务层接口
 * @Date 2020/06/30 上午 10:52
 * @Created by yanglichen
 */
public interface RaPDayRtService {


    /**
     * 方法描述: 根据站码列表得到雨量站集合列表
     * @author yanglichen
     * @date 2020-06-30 10:09
     * @param stcdList 站码列表
     * @param rfThreshold 降雨阈值
     * @return [雨量站1的列表, 雨量站2的列表,雨量站3的列表....]
     **/
    List<List<RaPDayRt>> getListRaPDayRtListByStringList(
            List<String> stcdList, Double rfThreshold, Date date);

    /**
     * 方法描述: 根据雨量站列表和时间间隔,得到最后一次记录的雨量站实例
     * @author yanglichen
     * @date 2020-06-30 10:09
     * @param raPDayRtList 同一雨量站下的雨量站列表
     * @param timeInterval 时间间隔
     * @return 最后一次记录的雨量站实例
     **/
    RaPDayRt getLastRecordRaPDayRtByListRapDayRtAndTimeInterval(
            List<RaPDayRt> raPDayRtList, Integer timeInterval);


    /**
     * 方法描述: 雨量站列表的列表和时间间隔,得到本场次降雨的起始日
     * @author yanglichen
     * @date  2020-06-30 10:09
     * @param raPDayRtsList 雨量站组成的雨量站列表
     * @param timeInterval 时间间隔
     * @return 开始降雨的时间
     **/
    Date getLastRainDateByListRaPDayRtListAndTimeInterval(
            List<List<RaPDayRt>> raPDayRtsList, Integer timeInterval);

    /**
     * 方法描述: 封装上面三个方法的方法
     *  (方便其他类调用, 无任何新增功能)
     * @author yanglichen
     * @date 2020-07-08 09:55
     * @param stcdList 站码列表
     * @param rainfallThreshold 降雨判断的阈值
     * @param timeInterval 降雨时间间隔
     * @param date 开始计算的时间
     * @return 降雨开始的日期
     **/
    Date getRainfallDate(
            List<String> stcdList, Double rainfallThreshold, Integer timeInterval, Date date);

    /**
     * 方法描述: 判断当前时间下站码列表对应的雨量站的信息,是否存在于数据库
     *  存在: 返回当前日期
     *  今天没有: 日期往前推一天, 递归调用
     *
     * @author yanglichen
     * @date 2020-07-10 16:02
     * @param stcdList 站码列表
     * @param date 日期
     * @return 最后没有数据的日期
     **/
    Date judgeTodayRaPDayRtListInDataBase(List<String> stcdList, Date date);


    /**
     * 方法描述: 根据站码列表, 降雨起始日, 降雨结束的阈值, 找到降雨的截止日期
     * @author yanglichen
     * @date 2020-07-16 17:32
     * @param stcdList 站码列表
     * @param rainfallDate 降雨起始日
     * @param rainfallThreshold 降雨的阈值
     * @param timeInterval 降雨间隔时间
     * @return 降雨结束的日期
     **/
//    Date getRainfallEndDayByStcdListAndRainfallDate(
//            List<String> stcdList, Date rainfallDate, Double rainfallThreshold, Integer timeInterval);

    /**
     * 方法描述: 得到开始降雨的日期
     * @author yanglichen
     * @date 2020-07-17 18:38
     * @param stcdList 站码列表
     * @param rainfallThreshold 降雨的阈值
     * @param timeInterval 降雨间隔时间
     * @param date 倒推时间
     * @return
     **/
//    Date getLastRainfallDate( List<String> stcdList, Double rainfallThreshold, Integer timeInterval, Date date);

    /**
     * 方法描述: 查询日期小于当前日期 00:00之前,降雨量小于给定值的站码列表的的日期对应的计数项
     * 并将结果封装成Map
    * @author yanglichen
     * @date 2020-07-23 09:28
     * @param stcdList 站码列表
     * @param rainfallThreshold 降雨阈值
     * @return 日期对应降雨数量的计数项的Map
     *  例如: {2020/7/1 = 30, 2020/7/2 = 25 .....}
     **/
    Map<Date, Integer> getRainfallDateCount(List<String> stcdList, Double rainfallThreshold);

    /**
     * 方法描述: 根据降雨日找到开始下雨的那一天
     * @author yanglichen
     * @date 2020-07-23 09:47
     * @param rainfallDateCount 日期和满足降雨统计的雨量站的计数项的Map
     * @param rainfallEndDate 雨停的日期
     * @param timeInterval 时间间隔
     * @return 开始下雨的那一天
     **/
    Date findRainfallStartDate(
            Map<Date, Integer> rainfallDateCount, Date rainfallEndDate, Integer timeInterval);


    /**
     * 方法描述: 根据降雨日找到开始下雨的那一天
     * @author yanglichen
     * @date 2020-07-23 09:47
     * @param rainfallDateCount 日期和满足降雨统计的雨量站的计数项的Map
     * @param rainfallEndDate 雨停的日期
     * @param timeInterval 时间间隔
     * @return 开始下雨的那一天
     **/
    Date findRainfallEndDate(
            Map<Date, Integer> rainfallDateCount, Date rainfallEndDate, Integer timeInterval);

    /**
     * 方法描述: 根据上次降雨的开始日期找到在之前一场降雨的结束日期
     * @author yanglichen
     * @date 2020-07-23 15:41
     * @param rainfallDateCount 日期和满足降雨统计的雨量站的计数项的Map
     * @param rainfallStartDate 雨开始的日期
     * @return
     **/
    Date getNextRainfallEndDate(Map<Date, Integer> rainfallDateCount, Date rainfallStartDate);

    /**
     * 方法描述: 找到数据中最后的那个日期
     * @author yanglichen
     * @date 2020-07-23 15:57
     * @param rainfallDateCount 日期和满足降雨统计的雨量站的计数项的Map
     * @return 最后的日期
     **/
    Date getEndRainfallDate(Map<Date, Integer> rainfallDateCount);
}

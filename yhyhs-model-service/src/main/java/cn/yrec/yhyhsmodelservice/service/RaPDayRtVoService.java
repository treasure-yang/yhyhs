package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Classname RaPDayRtVoService
 * @Description 逐日雨量站详细信息的业务层接口
 * @Date 2020/07/01 上午 10:58
 * @Created by yanglichen
 */
public interface RaPDayRtVoService {

    /**
     * 方法描述: 根据站码和降雨起始日, 计算某雨量站在本场雨内的总降雨量
     * @author yanglichen
     * @date 2020-07-06 10:40
     * @param stcd 站码
     * @param rainfallDate 降雨起始日
     * @param calDate 计算日
     * @param totalRainfallArea 站码列表(用于计算总降雨面积)
     * @return 总降雨量
     **/
    Double getRainfallQByStcdAndRainfallDate(
            String stcd, Date rainfallDate, Date calDate, Double totalRainfallArea);


    /**
     * 方法描述: 根据降雨起始日和站码列表, 得到该站码列表下全部雨量站的总降雨和(单位mm)
     * @author yanglichen
     * @date 2020-07-01 10:39
     * @param rainfallDate 降雨起始日
     * @param calDate 计算日(雨停日)
     * @param stcdList 站码列表
     * @return 站码列表对应的雨量站下的总降雨和
     **/
    Double getAllRainFallQByRainfallDateAndStcdList(
            Date rainfallDate, Date calDate, List<String> stcdList);

    /**
     * 方法描述: 根据开始下雨的时间 和 雨量站实例列表 以及降雨雨量的阈值, 得到一个每天的雨量笼罩面积
     * @author yanglichen
     * @date 2020-07-06 10:43
     * @param raPDayRtVoList 雨量站列表
     * @param rainfallDateStr 开始下雨的时间
     * @param rfThreshold 雨量阈值
     * @return  [第1天的笼罩面积, 第2天的笼罩面积, 第3天的笼罩面积]
     **/
    List<Double> getAllRainfallAreaByStcdAndRainfallDay(
            List<RaPDayRtVo> raPDayRtVoList, String rainfallDateStr, Double rfThreshold, Date calDate);


    /**
     * 方法描述: 通过传入的雨量站详细列表信息,计算在本场次降雨中,降雨mm最多的雨量站,对应的雨量
     * @author yanglichen
     * @date 2020-07-06 10:38
     * @param raPDayRtVoList 雨量站详情列表
     * @param stcdList 雨量站站码
     * @return 本场次降雨中的某站统计的总大的实体类列表(用于求后续的最大值和获得站码)
     **/
    Map<String, Object> getMaxRfByRaPDayRtVoList(
            List<RaPDayRtVo> raPDayRtVoList, List<String> stcdList);

    /**
     * 方法描述: 根据雨量站站码列表,和开始降雨的时间,得到前期降雨指数
     * 前期降雨指数: 降雨开始前15天到降雨日期间根据降雨覆盖的雨量站的站码统计计算出来的一个指数
     *
     * @param stcdList     雨量站站码列表
     * @param rainfallDate 开始降雨的时间
     * @return 前期降雨指数
     * @author yanglichen
     * @date 2020-07-06 18:31
     **/
    Double getBeforRainfallPoint(List<String> stcdList, Date rainfallDate);


    /**
     * 方法描述: 根据站码列表和传入的日期,重雨量站小时表中查询数据
     * 并且整理到逐日降雨的统计表中
     *
     * @param stcdList     站码列表
     * @param lastDate     数据库中查询出来的的最后数据出现的日期
     * @param rainfallDate 降雨起始日
     * @param calDate 计算时间
     * @return 完整时间的降雨数据
     * @author yanglichen
     * @date 2020-07-13 09:12
     **/
    List<RaPDayRtVo> getNewRaPDayVoListByRaPDay(
            List<String> stcdList, Date lastDate, Date rainfallDate, Date calDate);


    /**
     * 方法描述: 得到站码列表在起始日之前的所有的信息
     *  封装成Map(便于后续计算降雨特征值信息的检索)
     * @author yanglichen
     * @date 2020-07-24 10:27
     * @param stcdList 站码列表
     * @return 封装站码列表全部信息的Map
     *  例如:{站码1 = {日期1=雨量站全部信息实例1, 日期2=雨量站全部信息实例2...},
     *      站码2 = {日期1=雨量站全部信息实例1, 日期2=雨量站全部信息实例2...}....}
     **/
    Map<String, Map<Date, RaPDayRtVo>> getAllRaPDayRtVoInformation(
            List<String> stcdList);
}

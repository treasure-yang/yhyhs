package cn.yrec.yhyhsmodelservice.mappers;

import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Classname RaPDayRtMapper
 * @Description 逐日雨量站降雨(实时)的mybatis接口文件
 * @Date 2020/06/29 下午 6:03
 * @Created by yanglichen
 */
public interface RaPDayRtMapper {


    /**
     * 方法描述: 根据站码和记录时间得到一个实时雨量站实例
     *
     * @param stcd 站码
     * @param date 记录时间
     * @return 雨量站实例
     * @author yanglichen
     * @date 2020-07-08 09:41
     **/
    RaPDayRt getRaPDayRtByStcdAndTime(
            @Param("STCD") String stcd, @Param("DATE") String date);

    /**
     * 方法描述: 根据站码列表和降雨统计的阈值得到一组雨量站的列表
     *
     * @param stcdList    站码列表
     * @param rtThreshold 降雨阈值
     * @param startDate   日期
     * @return 雨量站列表
     * @author yanglichen
     * @date 2020-07-08 09:40
     **/
    List<RaPDayRt> getHaveRainRaPDayRtListByStcdList(
            @Param("STCD_LIST") List<String> stcdList,
            @Param("RF") Double rtThreshold,
            @Param("START_DATE") Date startDate);

    /**
     * 方法描述: 根据站码和降雨日期查询某雨量站在降雨时间段内的降雨记录
     *
     * @param stcd            站码
     * @param rainfallDateStr 降雨开始的日期
     * @return 某雨量站在降雨时间段内的降雨记录
     * @author yanglichen
     * @date 2020-07-08 09:42
     **/
    List<RaPDayRt> getrainfallDurationRainPDayRtListByStcdAndDate(
            @Param("STCD") String stcd, @Param("RAINFALL_DATE") String rainfallDateStr);

    /**
     * 方法描述: 得到这个时间下站码列表的简单信息
     *
     * @param stcdList 站码列表
     * @param nowDate  当前时间
     * @return 得到这个时间下站码列表的简单信息
     * @author yanglichen
     * @date 2020-07-10 15:33
     **/
    List<RaPDayRt> getRapDayRtListByStcdListAndNowDate(
            @Param("STCD_LIST") List<String> stcdList, @Param("DATE") String nowDate);

    /**
     * 方法描述: 根据站码列表和某一个开始的日期,查询这些小于等于这个时间的全部信息
     *
     * @param stcdList  站码列表
     * @param beginDate 开始日期
     * @return 小于等于这个时间的全部信息
     * @author yanglichen
     * @date 2020-07-16 09:59
     **/
    List<RaPDayRt> getRaPdayRtListByBeginDate(
            @Param("STCD_LIST") List<String> stcdList, @Param("BEGIN_DATE") Date beginDate);

    /**
     * 方法描述: 根据站码列表和查询某个时间下有多少返回值
     *
     * @param stcdList          站码列表
     * @param date              日期
     * @param rainfallThreshold 降雨阈值
     * @return 站码列表对应日期下的数量
     * @author yanglichen
     * @date 2020-07-16 16:21
     **/
    Integer getRaPDayRtListCountByDate(
            @Param("STCD_LIST") List<String> stcdList,
            @Param("DATE") Date date,
            @Param("RAINFALL_THRESHOLD") Double rainfallThreshold);

    /**
     * 方法描述: 查询站码列表对应的站码在小于某一个日期,降雨量<=降雨阈值的全部信息
     * 初步封装到List<Map<>>中,将在业务层转化为Map<>对象
     *
     * @param stcdList          站码列表
     * @param startDate         开始日期
     * @param rainfallThreshold 降雨阈值
     * @return 日期: 日期对应的技术项
     * @author yanglichen
     * @date 2020-07-23 08:14
     **/
    List<Map<String, Object>> getDatesCountRainfall(
            @Param("STCD_LIST") List<String> stcdList,
            @Param("START_DATE") Date startDate,
            @Param("RAINFALL_THRESHOLD") Double rainfallThreshold);

}

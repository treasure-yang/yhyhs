package cn.yrec.yhyhsmodelservice.mappers;

import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Classname RaPDayRtVoMapper
 * @Description 逐日降雨量(实时)VO类的持久层接口
 * @Date 2020/07/01 上午 10:00
 * @Created by yanglichen
 */
public interface RaPDayRtVoMapper {

    /**
     * 方法描述: 根据站码和字符串格式的日期,查询一个3表合一信息的逐日雨量站信息
     * @author yanglichen
     * @date 2020-07-01 10:02
     * @param stcd 站码
     * @param rainfallDAte 降雨起始日
     * @param calDate 计算日
     * @return 某雨量站在这个时间下的全部信息
     **/
    List<RaPDayRtVo> getRaPDayRtVoListByStcdAndDateStr(
            @Param("STCD") String stcd, @Param("RAINFALL_DATE") Date rainfallDAte, @Param("CAL_DATE") Date calDate);

    /**
     * 方法描述: 根据雨量站站码列表,查询这段时间内发生降雨的全部雨量站的全部信息
     * @author yanglichen
     * @date 2020-07-01 10:07
     * @param stcdList 雨量站站码列表
     * @param rainfallDate 开始降雨的时间
     * @param calDate 计算时间
     * @return [雨量站1的列表, 雨量站2的列表,雨量站3的列表....]
     **/
    List<RaPDayRtVo> getRaPDayRtVoListByStcdListAndDateStr(
            @Param("STCD_LIST") List<String> stcdList, @Param("RAINFALL_DATE") String rainfallDate, @Param("CAL_DATE") String calDate);

    /**
     * 方法描述: 通过站码列表,查询开始日期和结束日期中(包前, 包后)范围内的雨量站的信息
     * @author yanglichen
     * @date 2020-07-06 15:18
     * @param stcdList 站码列表
     * @param startDate 开始日期 (新日期: 2020/6/30)
     * @param endDate 结束日期 (15天前的日期: 2020/6/15)
     * @return 站码列表对应的雨量站在这段时间内的信息
     **/
    List<RaPDayRtVo> getRaPDayRtVoListByStcdListAndTwoDate(
            @Param("STCD_LIST") List<String> stcdList,
            @Param("START_DATE")Date startDate,
            @Param("END_DATE")Date endDate);


    /**
     * 方法描述: 根据站码列表和一个时间得到小于等于这个时间下的所有的信息
     * @author yanglichen
     * @date 2020-07-24 09:16
     * @param stcdList 站码列表
     * @param startDate 日期
     * @return <= startDate 的全部信息
     **/
    List<RaPDayRtVo> getAllInformation(
            @Param("STCD_LIST") List<String> stcdList, @Param("START_DATE") Date startDate);
}

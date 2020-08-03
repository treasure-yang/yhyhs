package cn.yrec.yhyhsmodelservice.mappers;

import cn.yrec.yhyhsmodelservice.model.RaPHourRt;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 *  实时降雨小时表的Mapper接口
 * @createDate: 2020/07/14 上午 10:24
 * @author: yanglichen
 */
public interface RaPHourRtMapper {

    /**
     * 方法描述: 根据站码列表, 开始日期, 结束日期
     * 找到这段范围内的雨量站逐小时的降雨信息
     *
     * @param stcdList  站码列表
     * @param startDate 开始日期 (日期较旧)
     * @param endDate   结束日期 (日期较新)
     * @return 这段范围内的雨量站逐小时的降雨信息
     * @author yanglichen
     * @date 2020-07-14 10:27
     **/
    List<RaPHourRt> getRapHourRtListByStcdListAndDate(
            @Param("STCD_LIST") List<String> stcdList,
            @Param("START_DATE") Date startDate,
            @Param("END_DATE") Date endDate);

    /**
     * 方法描述: 根据站码和日期,得到某日,某站码的所有记录
     * 次日0:00 > 当前 >= 当日0:00
     * @param stcd      站码
     * @param startDate 开始日期 (当日00:00)
     * @param endDate   结束日期 (次日00:00)
     * @return 到某日, 某站码的所有记录
     * @author yanglichen
     * @date 2020-07-14 10:43
     **/
    List<RaPHourRt> getRaPHourRtListByStcdAndDate(
            @Param("STCD") String stcd,
            @Param("START_DATE") Date startDate,
            @Param("END_DATE") Date endDate);
}

package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import cn.yrec.yhyhsmodelservice.model.RaPHourRt;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 *  实时小时降雨表的业务层接口
 * @createDate: 2020/07/14 下午 5:53
 * @author: yanglichen
 */
public interface RaPHourRtService {

    /**
     * 方法描述: 通过传入的小时表列表和站码列表得到
     *  按照站码分类的逐日雨量站列表(用于补充当前时间下日表中没有信息的情况)
     * @author yanglichen
     * @date 2020-07-14 17:55
     * @param raPHourRtList 同一个站对应的小时的降雨记录
     * @param stcdList 站码列表
     * @return 逐日的降雨数据
     **/
    List<RaPDayRt> getReplaceRaPDayRtListByRaPHourRtList(
            List<RaPHourRt> raPHourRtList, List<String> stcdList);

    /**
     * 方法描述: 将从数据库中查询出来的结果,按照日期进行分组
     * @author yanglichen
     * @date 2020-07-15 09:36
     * @param raPHourRtList 从数据库中直接查询出来的原始小时数据
     * @return
     **/
    List<List<RaPHourRt>> getRaPHourRtsListByDate(List<RaPHourRt> raPHourRtList);


    /**
     * 方法描述: 将查询出来的结果,融合到原来的日雨量站列表中
     * @author yanglichen
     * @date 2020-07-15 15:14
     * @param raPDayRtList 日雨量站列表
     * @param raPHourRtList 查询出来的小时表的列表
     **/
    void mixRaPDayRtListByRaPHourRtList(
            List<RaPDayRt> raPDayRtList, List<RaPHourRt> raPHourRtList, List<String> stcdList);

}

package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.model.RainfallParameters;
import cn.yrec.yhyhsmodelservice.model.RainfallResult;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述: 用于整合综合计算的接口
 * @createDate: 2020/07/09 上午 8:31
 * @author: yanglichen
 */
public interface CalculateSerivce {

    /**
     * 方法描述: 根据传入的降雨参数实体类,计算降雨的结果
     * @author yanglichen
     * @date 2020-07-09 08:32
     * @param rainfallParameters 降雨参数的实体类
     * @return 降雨结果的封装类
     **/
    RainfallResult getRainfallResultByStcdList(
            RainfallParameters rainfallParameters);

    /**
     * 方法描述: 得到降雨总面积
     * @author yanglichen
     * @date 2020-07-24 10:54
     * @param allInformationMap 所有信息的Map结果集
     * @return 降雨总面积
     **/
    Double getTotalRainfallArea(Map<String, Map<Date, RaPDayRtVo>> allInformationMap);

    /**
     * 方法描述: 得到总降雨量
     * @author yanglichen
     * @date 2020-07-24 15:17
     * @param allInformationMap 所有信息的Map
     * @param rainfallStartDate 降雨起始日
     * @param rainfallEndDate 降雨结束的日期
     * @param totalRainfallArea 降雨总面积
     * @return 总降雨量
     **/
    Double getTotalRainfallQ(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap, Date rainfallStartDate, Date rainfallEndDate, Double totalRainfallArea);

    /**
     * 方法描述: 得到时段雨量笼罩面积
     * @author yanglichen
     * @date 2020-07-24 16:50
     * @param allInformationMap 所有信息的Map
     * @param rainfallStartDate 降雨起始日
     * @param rainfallEndDate 降雨结束的日期
     * @param rainfallThreshold 用于统计的阈值
     * @return 时段雨量笼罩面积
     **/
    List<Double> getTimesRainfallArea(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap, Date rainfallStartDate, Date rainfallEndDate, Double rainfallThreshold);

    /**
     * 方法描述: 得到最大点雨量,和暴雨中心
     * @author yanglichen
     * @date 2020-07-27 08:19
     * @param allInformationMap 包含所有信息的Map结果集
     * @param rainfallStartDate 降雨起始日
     * @param rainfallEndDate 降雨截止日
     * @return 封装有最大点雨量信息,暴雨中心(站码)的Map
     **/
    Map<String, Object> getMaxRainfall(Map<String, Map<Date, RaPDayRtVo>> allInformationMap, Date rainfallStartDate, Date rainfallEndDate);

    /**
     * 方法描述: 计算得到新的权重系数,并讲结果按照站码,新权重封装到一个Map中,后续操作取用数据
     * @author yanglichen
     * @date 2020-07-27 08:52
     * @param allInformationMap 包含所有信息的Map结果集
     * @return 站码和新权重的映射Map
     **/
    Map<String, Double> getNewWeiight(Map<String, Map<Date, RaPDayRtVo>> allInformationMap);

    /**
     * 方法描述: 计算前期雨量指数
     * @author yanglichen
     * @date 2020-07-27 09:02
     * @param allInformationMap 包含所有信息的Map结果集
     * @param rainfallStartDate 降雨起始日
     * @return 前期雨量指数
     **/
    Double getBeforeRainfallPoint(Map<String, Map<Date, RaPDayRtVo>> allInformationMap, Date rainfallStartDate);


    /**
     * 方法描述: 得到降雨特征值结果
     * @author yanglichen
     * @date 2020-07-24 17:22
     * @param rainfallParameters 降雨参数的实体类
     * @return 降雨结果的封装类
     **/
    RainfallResult getRainfallResult(
            RainfallParameters rainfallParameters);

    /**
     * 方法描述: 将RequestBody中的参数转化为模型需要用到的参数实例
     * @author yanglichen
     * @date 2020-07-27 16:05
     * @param requestBodyMap 客户端发送的请求数据
     * @return 计算模型需要的数据
     **/
    RainfallParameters getParametersByRequestBody(Map<String, Object> requestBodyMap);

    /**
     * 方法描述: 根前端传入的原型雨数据,构造原型雨对象
     * @author yanglichen
     * @date 2020-08-07 08:35
     * @param requestBodyMap 前端传入的Map数据
     * @return 原型雨实例
     **/
    RainfallResult getRainfallResultByRequestMap(Map<String, Object> requestBodyMap);

    /**
     * 方法描述: 根据某一场降雨得到在该场次降雨中,
     *  每个站码所记录的降雨量的总和,并组成一个Map
     * @author yanglichen
     * @date 2020-07-28 16:40
     * @param rainfallResult 某一场雨的降雨特征值信息
     * @param allInformationMap 包含所有信息的Map结果集
     * @return 在该场次降雨中,每个站码记录的降雨量的总和,并放入一个Map中
     *  例如: {站码1 = 22.0, 站码2 = 25.0, 站码3 = 28.8, ....}
     **/
    Map<String, Double> getResemblanceMap(
            RainfallResult rainfallResult, Map<String, Map<Date, RaPDayRtVo>> allInformationMap);

    /**
     * 方法描述: 根据传入的原型雨和备选雨的雨量站的降雨映射,权重映射,
     *  计算降雨的相似性
     * @author yanglichen
     * @date 2020-07-28 17:03
     * @param prototypeMap 原型雨雨量映射
     * @param otherMap 备选雨雨量映射
     * @param weightMap 权重的映射
     * @param stcdList 站码列表
     * @return 降雨的相似性
     **/
    Double getResemblace(
            Map<String, Double> prototypeMap,
            Map<String, Double> otherMap,
            Map<String, Double> weightMap,
            List<String> stcdList);

    /**
     * 方法描述: 根据相似度对备选雨结果进行排序
     * @author yanglichen
     * @date 2020-07-28 17:19
     * @param rainfallResultList 备选雨列表
     * @param prototypeRainfallResult 原型雨
     *
     * @return 排序后的RainfallResult
     **/
    List<RainfallResult> sortRainfallResultListByresesmblance(
            List<RainfallResult> rainfallResultList,
            RainfallResult prototypeRainfallResult);
}

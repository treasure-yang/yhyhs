package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.model.RaPweight;

import java.util.List;

/**
 * @Classname RaPweightService
 * @Description 降雨权重系数业务层接口
 * @Date 2020/07/01 上午 10:11
 * @Created by yanglichen
 */
public interface RaPweightService {


    /**
     * 方法描述: 根据站码列表得到,站码对应的雨量站的控制面积综合
     * @author yanglichen
     * @date 2020-07-01 10:35
     * @param stcdList 站码列表
     * @return 控制面积总和
     **/
    Double getTotalRainfallAreaByStcdList(List<String> stcdList);

    /**
     * 方法描述: 根据传入的站码列表,计算新的权重
     *  并替换到查询出的结果中,返回带有新权重的对象列表
     * @author yanglichen
     * @date 2020-07-07 15:52
     * @param stcdList 站码列表
     * @return 带有新的权重对象的列表
     **/
    List<RaPweight> getReplacWeightInRRaPweight(List<String> stcdList);
}

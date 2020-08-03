package cn.yrec.yhyhsmodelservice.mappers;

import cn.yrec.yhyhsmodelservice.model.RaPweight;

import java.util.List;

/**
 * @Classname RaPweightMapper
 * @Description 分区降雨权重系数表持久层接口
 * @Date 2020/06/30 下午 4:15
 * @Created by yanglichen
 */
public interface RaPweightMapper {
    
    /** @Description 根据站码得到该雨量站的分区降雨权重系数
      * @Param [stcd 站码]
      * @Return 分区降雨权重系数实例
      * @Date 2020/06/30 下午 4:22
      * @auther yanglichen
      */
    RaPweight getRaPweightByStcd(String stcd);

    /** @Description 根据站码列表查询一分区降雨权重系数列表
      * @Param [stcdList 站码列表]
      * @Return java.util.List<cn.yrec.yhyhsmodelservice.model.RaPweight>
      * @Date 6月30日 0030 下午 4:40
      * @auther yanglichen
      */
    List<RaPweight> getRaPweightListByStcdList(List<String> stcdList);
}

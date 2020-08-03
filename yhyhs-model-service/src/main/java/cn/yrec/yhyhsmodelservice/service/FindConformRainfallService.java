package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.model.RainfallResult;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 描述:
 *  寻找备选雨的接口
 * @createDate: 2020/07/16 上午 9:02
 * @author: yanglichen
 */
public interface FindConformRainfallService {

    /**
     * 方法描述: 根据原型雨实例(特征值计算结果)
     *  寻找历史中所有的降雨,将降雨的起始日按照降雨历时,放入Map中
     * @author yanglichen
     * @date 2020-07-16 09:05
     * @param rainfallResult 原型雨的特征值实例
     * @return 历史中不同历时对应的不同日期
     *  例:{5天:[2020/5/1, 2020/5/20], 6天:[2019/4/11, 2019/6/22].....}
     **/
    Map<Integer, List<Date>> findConfirmRainfallTakeTimeAndDateByRainfallResult(RainfallResult rainfallResult);

    /**
     * 方法描述: 通过历时和降雨起始日组成的Map得到10场次的备选雨
     * @author yanglichen
     * @date 2020-07-20 09:12
     * @param rainfallResult 原型雨实例
     * @param rainfallMap 历时和降雨起始日组成的Map
     * @return 10场次的备选雨
     **/
    List<RainfallResult> getConformRainfallSetByRainfallMap(
            RainfallResult rainfallResult, Map<Integer, List<Date>> rainfallMap);

    /**
     * 方法描述: 计算总降雨量,并添加到备选雨列表中
     * @author yanglichen
     * @date 2020-07-20 11:02
     * @param rainfallResultList 备选雨列表
     * @return 增加过总降雨量的备选雨实例列表
     **/
    List<RainfallResult> calculateTotalRainfallQ(List<RainfallResult> rainfallResultList);

    /**
     * 方法描述: 根据原型雨的总降雨量对备选雨列表进行排序
     *  如果根据历时筛选出的备选雨的数量超过了10场,则返回10场次
     *  小于10场次则全部返回
     * @author yanglichen
     * @date 2020-07-20 15:55
     * @param rainfallResultList 备选雨列表
     * @param originRainfallresult 原型雨实例
     * @return 按照降雨量和原型雨最接近的顺序返回的10场(最多)备选雨列表
     **/
    List<RainfallResult> sortRainfallResultByRainfallQ(
            List<RainfallResult> rainfallResultList, RainfallResult originRainfallresult);

    /**
     * 方法描述: 根据传入的总降雨量的容差对备选雨进行筛选
     * @author yanglichen
     * @date 2020-07-29 08:15
     * @param rainfallResultList 备选雨列表
     * @param prototypeRainfall 原型雨
     * @param rainfallQTolerance 降雨量容差
     *    (相对于原型雨来说,如果传入的是0.2,假定原型雨的总雨量是100,则80--120都是可接受的误差范围内)
     * @return 得到根据降雨量容差进行筛选得到的结果
     *  如果筛选前的<=5, 或者 筛选后的结构<=5,则不进行筛选了
     **/
    List<RainfallResult> filterRainfallResultByRainfallQ(
            List<RainfallResult> rainfallResultList, RainfallResult prototypeRainfall, Double rainfallQTolerance);

    /**
     * 方法描述: 计算所有的备选雨的特征值
     * @author yanglichen
     * @date 2020-07-27 10:36
     * @param rainfallResultList 备选雨列表
     * @return 备选雨列表
     **/
    List<RainfallResult> calAllCulate(List<RainfallResult> rainfallResultList);
}

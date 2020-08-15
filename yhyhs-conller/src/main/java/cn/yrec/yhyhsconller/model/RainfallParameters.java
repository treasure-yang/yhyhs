package cn.yrec.yhyhsconller.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 *  降雨参数实体类
 *  降雨参数和降雨的结果,存在 一 对 多 的关系,
 *  通过降雨参数的Pid 和降雨结果的 Rid 进行匹配
 * @createDate: 2020/08/14 下午 3:11:32
 * @author: yanglichen
 */
@Data
public class RainfallParameters {

    private Integer pid;//参数的id
    private Integer rid;//降雨结果的Id

    private String stcdList;//站码列表(通过Json转换)
    private Integer timeInterval;//降雨历时
    private Double rainfallThreshold;//降雨雨量的阈值
    private Double rainfallThresholdForArea;//降雨笼罩面积的阈值
    private Long calDate;//计算时间
    private Integer rainfallThlerance;//降雨历时容差
    private Double rainfallQThlerance;//降雨雨量容差

}

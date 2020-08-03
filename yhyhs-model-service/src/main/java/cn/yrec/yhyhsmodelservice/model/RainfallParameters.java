package cn.yrec.yhyhsmodelservice.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述: 用于封装计算场次降雨特征值需要传入的参数的实体类
 * @createDate: 2020/07/08 上午 9:48
 * @author: yanglichen
 */
@Data
public class RainfallParameters  implements Serializable {

    private static final long serialVersionUID = 7434503988528999221L;

    private List<String> stcdList; //站码列表
    private Integer timeInterval; //降雨时间间隔
    private Double rainfallThreshold; //降雨的阈值
    private Double rainfallThresholdFoArea; //统计日降雨笼罩面积的降雨阈值
    private long calDate; //开始计算的时间的时间戳

    private Integer rainfallTolerance; //寻找备选雨用的历时容差数(2)
    private Double rainfallQTolerance; //寻找备选雨用的雨量容差数(20%)


}

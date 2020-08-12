package cn.yrec.yhyhsmodelservice.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 描述: 场次降雨特征值实体类
 * @createDate: 2020/07/06 上午 10:27
 * @author: yanglichen
 */
@Data
public class RainfallResult  implements Serializable {

    private static final long serialVersionUID = -3221366583735838442L;

    private Date rainfallDate; //降雨开始的日期
    private Date endRainfallDate; //降雨结束的日期
    private Integer rainfallTakeTime; //降雨历时

    private Double totalRainfallArea; //总降雨面积(单位:平方米)
    private Double totalRainfallQ; //总降雨量(单位:mm)
    private Double maxRainfall; //最大点雨量
    private Double beforeRainfallPoint; //前期雨量指数
    private Double lookLikePoint; //相似性(原型雨为100)
    private Double resemblance; //相似值(原型雨为0)

    private String maxRainfallStcd; //最大点雨量的站码
    private List<Double> dateRainfallArea; //时段降雨笼罩面积(时间大的在前)


    private RainfallParameters rainfallParameters; //降雨参数实例
}

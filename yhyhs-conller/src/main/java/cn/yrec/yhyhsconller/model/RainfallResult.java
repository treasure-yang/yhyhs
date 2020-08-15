package cn.yrec.yhyhsconller.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 *  降雨结果的实体类
 * @createDate: 2020/08/14 下午 3:23:31
 * @author: yanglichen
 */
@Data
public class RainfallResult {
    private Integer rid;//降雨结果的ID
    private Integer pid;//降雨参数的ID
    private Integer prototypeId;//原型雨的ID
    private Date rainfallDate;//降雨起始日
    private Date endRainfallDate;//将于截止日
    private Integer rainfallTakeTime;//降雨历时
    private Double totalRainfallArea;//降雨笼罩面积
    private Double totalRainfallQ;//总降雨量
    private Double maxRainfall;//最大降雨量
    private Double beforeRainfallPoint;//雨前降雨指数
    private Double looklikePoint;//相对于原型雨的相似度(0-100)
    private Double resemblance;//相对于原型雨的相似值(值越小,越相似)
    private String maxRainfallStcd;//暴雨中心的站码
    private String dateRainfallArea;//时段笼罩面积(用Json数据转换)
}

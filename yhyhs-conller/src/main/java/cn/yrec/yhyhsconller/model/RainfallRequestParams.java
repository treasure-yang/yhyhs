package cn.yrec.yhyhsconller.model;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 *  用于封装访问外部GIS服务借口需要发送的数据的实体类
 * @createDate: 2020/08/14 上午 8:16:57
 * @author: yanglichen
 */
@Data
public class RainfallRequestParams {
    private Integer id;//ID
    private String basinName;//流域名
    private Date startTime;//开始时间
    private Date endTime;//结束时间
    private Double r;//触发余量值
    private Double area;//触发面积
}

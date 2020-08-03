package cn.yrec.yhyhsmodelservice.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 *
 * @createDate: 2020/07/10 上午 10:11
 * @author: yanglichen
 */
@Data
public class RaPHourRt implements Serializable {

    private static final long serialVersionUID = -4105793526266709284L;

    private String stcd; //站码
    private String stnm; //名称

    private Date cdate; //统计的时间节点

    private Double rf; //降雨量
    private Double intvtime; //时段长(1值1小时, 0.3值30分钟)
}

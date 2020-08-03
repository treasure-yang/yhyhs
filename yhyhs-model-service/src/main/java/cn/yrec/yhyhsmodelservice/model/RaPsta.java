package cn.yrec.yhyhsmodelservice.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname RaPsta
 * @Description 水量站详情
 * @Date 2020/06/28 下午 6:34
 * @Created by yanglichen
 */
@Data
public class RaPsta  implements Serializable {

    private static final long serialVersionUID = 3788439811710814979L;

    private String stcd;//站码
    private String stnm;//站名
    private String hscd;//水系
    private String rvnm;//河名
    private String stct;//站别

    private double lgtd;//经度
    private double lttd;//维度

    private String esstyr;//设立年份
    private String admag;//领导机关
}

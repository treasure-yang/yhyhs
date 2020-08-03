package cn.yrec.yhyhsmodelservice.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname RaPDayRtVo
 * @Description 逐日降雨量(实时)VO类
 *  用于封装3表联查的查询结果
 *      ra_p_day_rt 逐日降雨实时表
 *      ra_pweight 雨量站降雨权重表
 *      ra_psta 雨量站基本信息表
 * @Date 2020/07/01 上午 9:00
 * @Created by yanglichen
 */
@Data
public class RaPDayRtVo implements Serializable {

    private static final long serialVersionUID = 2914190701558010972L;

    private String stcd;//站码
    private String stnm;//站名
    private Date date;//记录日期
    private double rf;//降雨量

    private double area;//控制面积
    private double lgtd;//经度
    private double lttd;//纬度
    private double weight;//权重

    private String hncd;//水系
    private String rvnm;//河名
    private String stct;//站别
}

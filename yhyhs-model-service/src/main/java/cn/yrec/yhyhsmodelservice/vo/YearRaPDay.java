package cn.yrec.yhyhsmodelservice.vo;

import lombok.Data;

import java.util.List;

/**
 * @Classname YearRaPDay
 * @Description 年雨量站的实体类
 *  每一个年雨量站的实例,都包含该雨量站中某一年365天中在按照自然日连续的雨量统计信息
 * @Date 2020/06/29 上午 10:31
 * @Created by yanglichen
 */
@Data
public class YearRaPDay {

    private String stcd;//站码
    private String stnm;//站名
    private String year;//所属年

    /*长度:平年365, 闰年366*/
    private List<Double> dailyRainFall;//年降雨量
}

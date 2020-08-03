package cn.yrec.yhyhsmodelservice.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname RaPweight
 * @Description 分区降雨权重系数表实体类
 * @Date 2020/06/30 下午 4:10
 * @Created by yanglichen
 */
@Data
public class RaPweight  implements Serializable {

    private static final long serialVersionUID = -3875106287506351350L;

    private String stcd;//站码
    private String stnm;//站名
    private String year;//年份
    private String basin;//流域

    private double area;//面积
    private double weight;//权重


}

package cn.yrec.yhyhsmodelservice.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Classname RaPDayRt
 * @Description 逐日降雨量(实时)表的实体类
 * @Date 2020/06/29 下午 5:56
 * @Created by yanglichen
 */
@Data
public class RaPDayRt implements Serializable {


    private static final long serialVersionUID = 4784959952818555483L;

    private String stcd;//站码
    private String stnm;//雨量站名称

    private Date date;//记录时间
    private Double rf;//降雨量

}

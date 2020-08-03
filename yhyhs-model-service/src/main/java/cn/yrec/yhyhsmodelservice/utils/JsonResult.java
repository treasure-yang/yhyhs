package cn.yrec.yhyhsmodelservice.utils;

import lombok.Data;

/**
 * 描述:
 *  用于处理处理Json数据的类
 * @createDate: 2020/07/09 上午 8:23
 * @author: yanglichen
 */
@Data
public class JsonResult<T> {

    private Integer state;//状态码
    private String message;//信息
    private T data;//数据信息

    public JsonResult(Integer state) {
        this.state = state;
    }

    public JsonResult() {
    }

    public JsonResult(Throwable throwable) {
        this.message =throwable.getMessage();
    }

    public JsonResult(Integer state, T data) {
        this.state = state;
        this.data = data;
    }
}

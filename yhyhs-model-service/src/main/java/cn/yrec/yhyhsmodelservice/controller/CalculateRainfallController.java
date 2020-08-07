package cn.yrec.yhyhsmodelservice.controller;

import cn.yrec.yhyhsmodelservice.model.RainfallParameters;
import cn.yrec.yhyhsmodelservice.model.RainfallResult;
import cn.yrec.yhyhsmodelservice.service.CalculateSerivce;
import cn.yrec.yhyhsmodelservice.service.FindConformRainfallService;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述: 用于封装场次降雨特征值的实体类
 *
 * @createDate: 2020/07/08 上午 9:24
 * @author: yanglichen
 */
@RestController
@RequestMapping("/calRf")
public class CalculateRainfallController {

    @Autowired
    private CalculateSerivce calculateSerivce;

    @Autowired
    private FindConformRainfallService findConformRainfallService;


    /**
     * 方法描述: 根据站码列表计算当前场次降雨的特征值
     * @author yanglichen
     * @date 2020-07-08 09:28
     * @param rainfallParametersMap 传入的映射
     * @return 降雨特征值的实例
     **/
    @RequestMapping("/getResult")
    public JsonResult<RainfallResult> getRainfallResultByStcdList(@RequestBody Map<String, Object> rainfallParametersMap){
        //得到客户端发送的模型输入参数
        RainfallParameters rainfallParameters = calculateSerivce.getParametersByRequestBody(rainfallParametersMap);
        //得到原型雨实例
        RainfallResult rainfallResult = calculateSerivce.getRainfallResult(rainfallParameters);
        System.out.println(rainfallResult);
        return new JsonResult<>(200,rainfallResult);
    }


    @RequestMapping("/getInfo")
    public JsonResult<RainfallParameters> getRainfallParameters(){
        //给降雨参数传入
        RainfallParameters rainfallParameters = new RainfallParameters();
        rainfallParameters.setRainfallThreshold(2.0);
        rainfallParameters.setRainfallThresholdFoArea(3D);
        rainfallParameters.setTimeInterval(3);
        rainfallParameters.setStcdList(GetSomethingUtils.getTestStcdList());
        rainfallParameters.setCalDate(DateUtils.transformStrTODate("2019/7/19").getTime());
        return new JsonResult<>(200,rainfallParameters);
    }



}

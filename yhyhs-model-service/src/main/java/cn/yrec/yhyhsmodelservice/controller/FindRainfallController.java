package cn.yrec.yhyhsmodelservice.controller;

import cn.yrec.yhyhsmodelservice.model.RainfallParameters;
import cn.yrec.yhyhsmodelservice.model.RainfallResult;
import cn.yrec.yhyhsmodelservice.service.CalculateSerivce;
import cn.yrec.yhyhsmodelservice.service.FindConformRainfallService;
import cn.yrec.yhyhsmodelservice.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 描述:
 *  用于寻找备选雨的控制器
 * @createDate: 2020/08/07 上午 8:22
 * @author: yanglichen
 */
@RestController
@RequestMapping("/find")
public class FindRainfallController {

    @Autowired
    private CalculateSerivce calculateSerivce;

    @Autowired
    private FindConformRainfallService findConformRainfallService;

    /**
     * 方法描述: 根据传入的原型雨得到相似雨列表
     * @author yanglichen
     * @date 2020-08-07 08:30
     * @param prototypeRainfallMap 从前端传入的原型雨实例
     * @return 备选雨列表
     **/
    @RequestMapping("/findByRainfall")
    public JsonResult<List<RainfallResult>> getSimilarRainfallList(
            @RequestBody Map<String, Object> prototypeRainfallMap){
        //解析传入的Map对象,构造原型雨
        RainfallResult prototypeRainfall =
                calculateSerivce.getRainfallResultByRequestMap(prototypeRainfallMap);
        System.err.println(prototypeRainfall);
        //根据原型雨实例,寻找备选雨列表
        List<RainfallResult> rainfallResultList =
                findConformRainfallService.findRainfallListByRainfallResult(prototypeRainfall);
        return new JsonResult<>(200, rainfallResultList);
    }


    @RequestMapping("/findRainfall")
    public JsonResult<List<RainfallResult>> find(@RequestBody Map<String, Object> rainfallParametersMap) {
        //得到客户端发送的模型输入参数
        RainfallParameters rainfallParameters =
                calculateSerivce.getParametersByRequestBody(rainfallParametersMap);
        //得到原型雨
        RainfallResult prototypeRainfall = calculateSerivce
                .getRainfallResult(rainfallParameters);

        //得到按照相似性排序的备选雨列表
        List<RainfallResult> rainfallResultList =
                findConformRainfallService.findRainfallListByRainfallResult(prototypeRainfall);

        System.out.println("相似性排序如下");
        rainfallResultList.forEach(rainfall->{
//            rainfall.setRainfallParameters(null);
            System.out.println("相似性: "+rainfall.getResemblance());
        });
        return new JsonResult<>(200, rainfallResultList);
    }

}

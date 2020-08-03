package cn.yrec.yhyhsmodelservice.controller;

import cn.yrec.yhyhsmodelservice.model.RainfallParameters;
import cn.yrec.yhyhsmodelservice.model.RainfallResult;
import cn.yrec.yhyhsmodelservice.service.CalculateSerivce;
import cn.yrec.yhyhsmodelservice.service.FindConformRainfallService;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.utils.JsonResult;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
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

    /**
     * 方法描述: 老方法得到备选雨的历时和降雨起始日
     * @author yanglichen
     * @date 2020-07-27 15:10
     * @param rainfallParametersMap 传入的参数
     * @return 备选雨的历时和降雨起始日
     **/
    @RequestMapping("/findRainfall")
    public JsonResult<Map<Integer, List<Date>>> findRainfall(@RequestBody Map<String, Object> rainfallParametersMap){
        //得到客户端发送的模型输入参数
        RainfallParameters rainfallParameters =
                calculateSerivce.getParametersByRequestBody(rainfallParametersMap);
        //得到原型雨实例
        RainfallResult rainfallResult = calculateSerivce
                .getRainfallResultByStcdList(rainfallParameters);
        //得到备选雨的历时和降雨起始日
        Map<Integer, List<Date>> resultMap =  findConformRainfallService
                .findConfirmRainfallTakeTimeAndDateByRainfallResult(rainfallResult);
        return new JsonResult<>(200, resultMap);
    }


    /**
     * 方法描述: 老方法得到备选雨
     * @author yanglichen
     * @date 2020-07-27 15:12
     * @param rainfallParametersMap 客户端发来的请求参数
     * @return 返回备选雨实例
     **/
    @RequestMapping("/findResult")
    public JsonResult<List<RainfallResult>> findResult(@RequestBody Map<String, Object> rainfallParametersMap){
        //得到客户端发送的模型输入参数
        RainfallParameters rainfallParameters =
                calculateSerivce.getParametersByRequestBody(rainfallParametersMap);
        //得到原型雨实例
        RainfallResult rainfallResult = calculateSerivce
                .getRainfallResultByStcdList(rainfallParameters);
        //得到降雨历时和日期的映射Map
        Map<Integer, List<Date>> resultMap =
                findConformRainfallService.findConfirmRainfallTakeTimeAndDateByRainfallResult(rainfallResult);
        List<RainfallResult> rainfallResults =
                findConformRainfallService.getConformRainfallSetByRainfallMap(rainfallResult, resultMap);
        //计算雨量值
        List<RainfallResult> rainfallResultList =
                findConformRainfallService.calculateTotalRainfallQ(rainfallResults);
        System.out.println("待精筛雨量站数量: "+rainfallResults.size());
        List<RainfallResult> results =
                findConformRainfallService.sortRainfallResultByRainfallQ(rainfallResultList, rainfallResult);

        System.err.println("原型雨的总降雨量: "+rainfallResult.getTotalRainfallQ());
        results.forEach(r->{
            System.out.println("备选雨的降雨量: "+r.getTotalRainfallQ());
        });
        return new JsonResult<>(200, results);
    }


    /**
     * 方法描述: 用新的方法寻找备选雨实例
     * @author yanglichen
     * @date 2020-07-27 15:28
     * @param rainfallParametersMap
     * @return
     **/
    @RequestMapping("/findOtherResult")
    public JsonResult<List<RainfallResult>> findOtherResult(@RequestBody Map<String, Object> rainfallParametersMap){
        //得到客户端发送的模型输入参数
        RainfallParameters rainfallParameters =
                calculateSerivce.getParametersByRequestBody(rainfallParametersMap);
        //得到 原型雨实例
        RainfallResult rainfallResult = calculateSerivce
                .getRainfallResult(rainfallParameters);
        //得到历时和日期的映射Map
        Map<Integer, List<Date>> resultMap =
                findConformRainfallService.findConfirmRainfallTakeTimeAndDateByRainfallResult(rainfallResult);
        //得到备选雨实例列表
        List<RainfallResult> rainfallResults =
                findConformRainfallService.getConformRainfallSetByRainfallMap(rainfallResult, resultMap);
        //计算降雨特征值
        List<RainfallResult> resultList = findConformRainfallService.calAllCulate(rainfallResults);
        System.out.println("待精筛雨量站数量: "+resultList.size());
        List<RainfallResult> results =
                findConformRainfallService.sortRainfallResultByRainfallQ(resultList, rainfallResult);
        System.err.println("原型雨的总降雨量: "+rainfallResult.getTotalRainfallQ());
        results.forEach(r->{
            System.out.println("备选雨的降雨量: "+r.getTotalRainfallQ());
        });

        return new JsonResult<>(200, results);
    }

    @RequestMapping("/find")
    public JsonResult<List<RainfallResult>> find(@RequestBody Map<String, Object> rainfallParametersMap) {
        //得到客户端发送的模型输入参数
        RainfallParameters rainfallParameters =
                calculateSerivce.getParametersByRequestBody(rainfallParametersMap);
        //得到原型雨
        RainfallResult prototypeRainfall = calculateSerivce
                .getRainfallResult(rainfallParameters);
        //得到历时和日期的映射Map
        Map<Integer, List<Date>> resultMap =
                findConformRainfallService.findConfirmRainfallTakeTimeAndDateByRainfallResult(prototypeRainfall);
        //得到备选雨实例列表
        List<RainfallResult> rainfallResults =
                findConformRainfallService.getConformRainfallSetByRainfallMap(prototypeRainfall, resultMap);

        //计算降雨特征值
        List<RainfallResult> resultList = findConformRainfallService.calAllCulate(rainfallResults);

        //得到降雨量容差值
        Double rainfallQTolerance = rainfallParameters.getRainfallQTolerance();

        //根据总降雨量进行过滤
        List<RainfallResult> filterQResultList =
                findConformRainfallService.filterRainfallResultByRainfallQ(resultList, prototypeRainfall, rainfallQTolerance);

        //得到按照相似性排序的备选雨列表
        List<RainfallResult> rainfallResultList =
                calculateSerivce.sortRainfallResultListByresesmblance(filterQResultList, prototypeRainfall);

        System.out.println("相似性排序如下");
        rainfallResultList.forEach(rainfall->{
            rainfall.setRainfallParameters(null);
            System.out.println("相似性: "+rainfall.getResemblance());
        });
        return new JsonResult<>(200, rainfallResultList);
    }

}

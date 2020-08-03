package cn.yrec.yhyhsmodelservice.service;

import cn.yrec.yhyhsmodelservice.model.RainfallParameters;
import cn.yrec.yhyhsmodelservice.model.RainfallResult;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 描述:
 *
 * @createDate: 2020/07/17 下午 4:16
 * @author: yanglichen
 */
@SpringBootTest
public class FindConformRainfallTests {

    @Autowired
    private FindConformRainfallService findConformRainfallService;


    @Test
    void a(){
        RainfallResult rainfallResult = new RainfallResult();
        //给降雨参数传入
        RainfallParameters rainfallParameters = new RainfallParameters();
        rainfallParameters.setRainfallThreshold(3.0);
        rainfallParameters.setRainfallThresholdFoArea(3D);
        rainfallParameters.setTimeInterval(3);
        rainfallParameters.setStcdList(GetSomethingUtils.getTestStcdList());

        rainfallParameters.setCalDate(DateUtils.transformStrTODate("2019/7/19").getTime());
        rainfallResult.setRainfallParameters(rainfallParameters);
        rainfallResult.setRainfallDate(DateUtils.transformStrTODate("2020/7/9"));
        rainfallResult.setEndRainfallDate(DateUtils.transformStrTODate("2020/7/19"));
        findConformRainfallService.findConfirmRainfallTakeTimeAndDateByRainfallResult(rainfallResult);
    }
}

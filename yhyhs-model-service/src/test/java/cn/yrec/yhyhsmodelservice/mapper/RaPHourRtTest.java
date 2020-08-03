package cn.yrec.yhyhsmodelservice.mapper;

import cn.yrec.yhyhsmodelservice.mappers.RaPHourRtMapper;
import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import cn.yrec.yhyhsmodelservice.model.RaPHourRt;
import cn.yrec.yhyhsmodelservice.service.RaPHourRtService;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 *
 * @createDate: 2020/07/14 上午 10:38
 * @author: yanglichen
 */
@SpringBootTest
public class RaPHourRtTest {
    @Autowired
    private RaPHourRtMapper raPHourRtMapper;
    @Autowired
    private RaPHourRtService raPHourRtService;

    @Test
    void getRapHourRtByStcdListAndDate(){
        List<String> stcdList = GetSomethingUtils.getStcdList();
        Date startDate = DateUtils.transformStrTODate("2020/7/13");
        Date endDate = DateUtils.transformStrTODate("2020/7/16");
        List<RaPHourRt> raPHourRtList = raPHourRtMapper.getRapHourRtListByStcdListAndDate(stcdList, startDate, endDate);
//        raPHourRtList.forEach(System.out::println);

        List<List<RaPHourRt>> list = raPHourRtService.getRaPHourRtsListByDate(raPHourRtList);
        list.forEach(System.err::println);

    }

    @Test
    void a(){
        String stcd = "40624800";
        Date startDate = DateUtils.transformStrTODate("2020/7/13");
        Date endDate = DateUtils.transformStrTODate("2020/7/16");
        List<RaPHourRt> raPHourRtList = raPHourRtMapper.getRaPHourRtListByStcdAndDate(stcd, startDate, endDate);
        raPHourRtList.forEach(System.out::println);
    }


}

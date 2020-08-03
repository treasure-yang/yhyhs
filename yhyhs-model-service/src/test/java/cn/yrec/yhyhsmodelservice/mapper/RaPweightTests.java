package cn.yrec.yhyhsmodelservice.mapper;

import cn.yrec.yhyhsmodelservice.mappers.RaPweightMapper;
import cn.yrec.yhyhsmodelservice.model.RaPweight;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname RaPweightTests
 * @Date 2020/06/30 下午 4:37
 * @Created by yanglichen
 */
@SpringBootTest
public class RaPweightTests {
    @Autowired
    private RaPweightMapper raPweightMapper;

    @Test
    void getRaPweightByStcd(){
        String stcd = "41126600";
        RaPweight raPweight = raPweightMapper.getRaPweightByStcd(stcd);
        System.out.println(raPweight);
    }

    @Test
    void getRaPweightListByStcdList(){
        List<String> stcdList = new ArrayList<>();
        stcdList.add("40457800");
        stcdList.add("41121200");
        stcdList.add("41121250");
        stcdList.add("41120460");
        stcdList.add("41120550");
        stcdList.add("41120650");
        stcdList.add("41121400");
        stcdList.add("41120700");
        stcdList.add("41120750");
        stcdList.add("41120950");
        stcdList.add("41121350");
        stcdList.add("41121550");
        stcdList.add("41121850");
        stcdList.add("41121100");
        stcdList.add("41121050");
        stcdList.add("41120900");
        stcdList.add("41121800");
        stcdList.add("41121000");
        stcdList.add("41121150");
        stcdList.add("41122000");
        List<RaPweight> raPweightList = raPweightMapper.getRaPweightListByStcdList(stcdList);
        raPweightList.forEach(System.out::println);
    }


}

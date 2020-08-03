package cn.yrec.yhyhsmodelservice.service.impl;

import cn.yrec.yhyhsmodelservice.mappers.RaPweightMapper;
import cn.yrec.yhyhsmodelservice.model.RaPweight;
import cn.yrec.yhyhsmodelservice.service.RaPweightService;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname RaPweightServiceImpl
 * @Description TODO
 * @Date 2020/07/01 上午 10:14
 * @Created by yanglichen
 */
@Service
public class RaPweightServiceImpl implements RaPweightService {

//    @Autowired
//    private RaPweightMapper raPweightMapper;

    /**
     * 方法描述: 根据站码列表得到,站码对应的雨量站的控制面积综合
     * @author yanglichen
     * @date 2020-07-07 16:13
     * @param stcdList 站码列表
     * @return 站码对应的雨量站的控制面积综合
     **/
    @Override
    public Double getTotalRainfallAreaByStcdList(List<String> stcdList) {
        List<RaPweight> raPweightList = getRaPweightListByStcdList(stcdList);
        Double totalArea = 0D;
        for (RaPweight r : raPweightList) {
            totalArea += r.getArea();
        }
        return totalArea;
    }

    /**
     * 方法描述: 根据站码列表得到对应的降雨权重列表
     * @author yanglichen
     * @date 2020-07-07 16:13
     * @param stcdList 站码列表
     * @return 降雨权重系数列表
     **/
    private List<RaPweight> getRaPweightListByStcdList(List<String> stcdList){
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPweightMapper raPweightMapper = sqlSession.getMapper(RaPweightMapper.class);
        List<RaPweight> raPweightList = raPweightMapper.getRaPweightListByStcdList(stcdList);
        sqlSession.close();
        return raPweightList;
    }

    /**
     * 方法描述: 根据传入的站码列表,计算新的权重
     * 并替换到查询出的结果中,返回带有新权重的对象列表
     * @param stcdList 站码列表
     * @return 带有新的权重对象的列表
     * @author yanglichen
     * @date 2020-07-07 15:52
     **/
    @Override
    public List<RaPweight> getReplacWeightInRRaPweight(List<String> stcdList) {
        //得到雨量站权重列表
        List<RaPweight> raPweightList = getRaPweightListByStcdList(stcdList);
        //得到权重总和
        Double totalWeight = getNewTotalWeightByStcdList(raPweightList);
        //遍历权重列表
        raPweightList.forEach(raPweight -> {
            //替换权重信息
            raPweight.setWeight(raPweight.getWeight()/totalWeight);
        });
        return raPweightList;
    }

    /**
     * 方法描述: 根据雨量站的权重列表得到权重总和
     * @author yanglichen
     * @date 2020-07-07 15:58
     * @param raPweightList 雨量站列表
     * @return 权重总和
     **/
    private double getNewTotalWeightByStcdList(List<RaPweight> raPweightList){
        double totalWeight = 0D;
        //遍历权重信息列表
        for (RaPweight r : raPweightList) {
            totalWeight += r.getWeight();
        }
        return totalWeight;
    }
}

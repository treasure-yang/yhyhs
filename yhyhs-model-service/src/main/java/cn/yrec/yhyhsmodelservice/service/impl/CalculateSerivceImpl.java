package cn.yrec.yhyhsmodelservice.service.impl;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtVoMapper;
import cn.yrec.yhyhsmodelservice.model.RainfallParameters;
import cn.yrec.yhyhsmodelservice.model.RainfallResult;
import cn.yrec.yhyhsmodelservice.service.CalculateSerivce;
import cn.yrec.yhyhsmodelservice.service.RaPDayRtService;
import cn.yrec.yhyhsmodelservice.service.RaPDayRtVoService;
import cn.yrec.yhyhsmodelservice.service.RaPweightService;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 描述:
 *
 * @createDate: 2020/07/09 上午 8:36
 * @author: yanglichen
 */
@Service
public class CalculateSerivceImpl implements CalculateSerivce {

    @Autowired
    private RaPDayRtVoService raPDayRtVoService;
    @Autowired
    private RaPweightService raPweightService;
    @Autowired
    private RaPDayRtService raPDayRtService;

    /**
     * 方法描述: 根据传入的降雨参数实体类,计算降雨的结果
     *
     * @param rainfallParameters 降雨参数的实体类
     * @return 降雨结果的封装类
     * @author yanglichen
     * @date 2020-07-09 08:32
     **/
    @Override
    public RainfallResult getRainfallResultByStcdList(RainfallParameters rainfallParameters) {
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtVoMapper raPDayRtVoMapper = sqlSession.getMapper(RaPDayRtVoMapper.class);
        //构造降雨特征值实体类
        RainfallResult rainfallResult = new RainfallResult();
        //得到开始计算的时间
        Date calDate = new Date(rainfallParameters.getCalDate());
        System.out.println("计算时间: " + DateUtils.transformDateTOStr(calDate));
        //得到站码
        List<String> stcdList = rainfallParameters.getStcdList();
        //降雨阈值
        Double rainfallThreshold = rainfallParameters.getRainfallThreshold();
        //降雨间隔
        Integer timeInterval = rainfallParameters.getTimeInterval();
        //得到日期雨量站的降雨计数Map
        Map<Date, Integer> rainfallCountMap = raPDayRtService.getRainfallDateCount(stcdList, rainfallThreshold);
        //得到降雨日期
        Date rainfallStartDate = raPDayRtService.findRainfallStartDate(rainfallCountMap, calDate, timeInterval);

        System.out.println("降雨日期: " + DateUtils.transformDateTOStr(rainfallStartDate));
        Date rainfallEndDate = raPDayRtService.findRainfallEndDate(rainfallCountMap, rainfallStartDate, timeInterval);
        System.out.println("雨停日期: " + DateUtils.transformDateTOStr(rainfallEndDate));
        //判断计算的时间是不是今天
        if (!DateUtils.transformDateTOStr(calDate).equalsIgnoreCase(DateUtils.transformDateTOStr(new Date()))) {
            //不是今天 ,计算日期改成降雨结束日期
            calDate.setTime(rainfallEndDate.getTime());
        }
        //封装降雨结束的日期
        rainfallResult.setEndRainfallDate(rainfallEndDate);
        //初始化降雨历时
        Integer rainfallTakeTime = DateUtils.getTimesByStartDateAndEndDate(calDate, rainfallStartDate);
        System.out.println("历时: " + rainfallTakeTime);

        //封装降雨传入的参数
        rainfallResult.setRainfallParameters(rainfallParameters);

        //封装降雨日期
        rainfallResult.setRainfallDate(rainfallStartDate);

        //封装降雨历时
        rainfallResult.setRainfallTakeTime(rainfallTakeTime);

        //得到降雨总面积
        Double totalRainfallArea = raPweightService
                .getTotalRainfallAreaByStcdList(stcdList);
        //封装降雨总面积
        rainfallResult.setTotalRainfallArea(totalRainfallArea);

        //得到降雨总量
        Double totalRainfallQ = raPDayRtVoService
                .getAllRainFallQByRainfallDateAndStcdList(rainfallStartDate, calDate, stcdList);
        //封装降雨总量
        rainfallResult.setTotalRainfallQ(totalRainfallQ);

        //得到用于计算每日笼罩面积的降雨阈值
        Double rainfallThresholdFoArea = rainfallParameters.getRainfallThresholdFoArea();

        //得到一计算时间到降雨日期间的所有的站码下的雨量站的详细信息
        List<RaPDayRtVo> formRainfallDateRapDayRtVoList =
                raPDayRtVoMapper.getRaPDayRtVoListByStcdListAndDateStr(
                        stcdList, DateUtils.transformDateTOStr(rainfallStartDate), DateUtils.transformDateTOStr(calDate));
        sqlSession.close();
        //得到降雨期间的每日降雨笼罩面积的列表(单位:平方米)
        List<Double> dateRainfallArea =
                raPDayRtVoService.getAllRainfallAreaByStcdAndRainfallDay(
                        formRainfallDateRapDayRtVoList,
                        DateUtils.transformDateTOStr(rainfallStartDate),
                        rainfallThresholdFoArea, calDate);
        //封装每日降雨笼罩面积
        rainfallResult.setDateRainfallArea(dateRainfallArea);

        //得到最大点雨量的雨量和站码的Map
        Map<String, Object> maxRainfallMap = raPDayRtVoService
                .getMaxRfByRaPDayRtVoList(formRainfallDateRapDayRtVoList, stcdList);
        //得到最大点雨量
        Double maxRainfall = (Double) maxRainfallMap.get("maxRf");
        //封装最大点雨量
        rainfallResult.setMaxRainfall(maxRainfall);
        //得到最大点雨量对应的站码(暴雨中心的站码)
        String maxRainfallStcd = maxRainfallMap.get("stcd") + "";
        //封装最大点雨量的站码
        rainfallResult.setMaxRainfallStcd(maxRainfallStcd);

        //得到前期雨量指数
        Double beforeRainfallPoint = raPDayRtVoService
                .getBeforRainfallPoint(stcdList, rainfallStartDate);
        //封装前期雨量指数
        rainfallResult.setBeforeRainfallPoint(beforeRainfallPoint);

        return rainfallResult;
    }

    /**
     * 方法描述: 得到降雨特征值结果
     *
     * @param rainfallParameters 降雨参数的实体类
     * @return 降雨结果的封装类
     * @author yanglichen
     * @date 2020-07-24 17:22
     **/
    @Override
    public RainfallResult getRainfallResult(RainfallParameters rainfallParameters) {
        //构造降雨特征值实体类
        RainfallResult rainfallResult = new RainfallResult();
        //得到开始计算的时间
        Date calDate = new Date(rainfallParameters.getCalDate());
        System.out.println("计算时间: " + DateUtils.transformDateTOStr(calDate));
        //得到站码
        List<String> stcdList = rainfallParameters.getStcdList();
        //降雨阈值
        Double rainfallThreshold = rainfallParameters.getRainfallThreshold();
        //降雨间隔
        Integer timeInterval = rainfallParameters.getTimeInterval();
        //得到日期雨量站的降雨计数Map
        Map<Date, Integer> rainfallCountMap = raPDayRtService.getRainfallDateCount(stcdList, rainfallThreshold);

        //---------------------------------------------上面是得到属性值,下面是开始计算数据----------------------------------------//

        //得到结果大集合
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap = GetSomethingUtils.getAllInformationMap(stcdList);
        //计算降雨日期
        Date rainfallStartDate = raPDayRtService.findRainfallStartDate(rainfallCountMap, calDate, timeInterval);
        System.out.println("降雨日期: " + DateUtils.transformDateTOStr(rainfallStartDate));
        Date rainfallEndDate = raPDayRtService.findRainfallEndDate(rainfallCountMap, rainfallStartDate, timeInterval);
        System.out.println("雨停日期: " + DateUtils.transformDateTOStr(rainfallEndDate));
        //判断计算的时间是不是今天
        if (!DateUtils.transformDateTOStr(calDate).equalsIgnoreCase(DateUtils.transformDateTOStr(new Date()))) {
            //不是今天 ,计算日期改成降雨结束日期
            calDate.setTime(rainfallEndDate.getTime());
        }
        //封装降雨结束的日期
        rainfallResult.setEndRainfallDate(rainfallEndDate);
        //初始化降雨历时
        Integer rainfallTakeTime =
                DateUtils.getTimesByStartDateAndEndDate(calDate, rainfallStartDate);
        System.out.println("历时: " + rainfallTakeTime);

        //封装降雨传入的参数
        rainfallResult.setRainfallParameters(rainfallParameters);

        //封装降雨日期
        rainfallResult.setRainfallDate(rainfallStartDate);

        //封装降雨历时
        rainfallResult.setRainfallTakeTime(rainfallTakeTime);

        //计算降雨总面积
        Double totalRainfallArea = getTotalRainfallArea(allInformationMap);
        //封装降雨总面积
        rainfallResult.setTotalRainfallArea(totalRainfallArea);

        //计算降雨总量
        Double totalRainfallQ =
                getTotalRainfallQ(allInformationMap, rainfallStartDate, rainfallEndDate,totalRainfallArea);
        //封装降雨总量
        rainfallResult.setTotalRainfallQ(totalRainfallQ);

        //得到用于计算每日笼罩面积的降雨阈值
        Double rainfallThresholdFoArea = rainfallParameters.getRainfallThresholdFoArea();

        //得到降雨期间的每日降雨笼罩面积的列表(单位:平方米)
        List<Double> dateRainfallArea =
                getTimesRainfallArea(allInformationMap,rainfallStartDate,rainfallEndDate, rainfallThresholdFoArea);
        //封装每日降雨笼罩面积
        rainfallResult.setDateRainfallArea(dateRainfallArea);

        //得到最大点雨量的雨量和站码的Map
        Map<String, Object> maxRainfallMap =
                getMaxRainfall(allInformationMap, rainfallStartDate, rainfallEndDate);
        //得到最大点雨量
        Double maxRainfall = (Double) maxRainfallMap.get("maxRf");
        //封装最大点雨量
        rainfallResult.setMaxRainfall(maxRainfall);
        //得到最大点雨量对应的站码(暴雨中心的站码)
        String maxRainfallStcd = maxRainfallMap.get("maxRfStcd") + "";
        //封装最大点雨量的站码
        rainfallResult.setMaxRainfallStcd(maxRainfallStcd);

        //得到前期雨量指数
        Double beforeRainfallPoint = getBeforeRainfallPoint(allInformationMap, rainfallStartDate);
        //封装前期雨量指数
        rainfallResult.setBeforeRainfallPoint(beforeRainfallPoint);

        return rainfallResult;
    }

    /**
     * 方法描述: 得到降雨总面积
     *
     * @param allInformationMap 所以信息的Map结果集
     * @return 降雨总面积
     * @author yanglichen
     * @date 2020-07-24 10:54
     **/
    @Override
    public Double getTotalRainfallArea(Map<String, Map<Date, RaPDayRtVo>> allInformationMap) {
        /*
         * 得到降雨的总面积
         * 从每个站码集合中得到一个实例
         * 累加雨量站的实例的控制面积即可
         */
        List<RaPDayRtVo> raPDayRtVoList = getRaPDayRtVoList(allInformationMap);
        double totalArea = 0D;
        //累加控制面积
        for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
            totalArea += raPDayRtVo.getArea();
        }
        return totalArea;
    }

    /**
     * 方法描述: 得到总降雨量
     *
     * @param allInformationMap 所有信息的Map
     * @param rainfallStartDate 降雨起始日
     * @param rainfallEndDate   降雨结束的日期
     * @param totalRainfallArea 降雨总面积
     * @return 总降雨量
     * @author yanglichen
     * @date 2020-07-24 15:17
     **/
    @Override
    public Double getTotalRainfallQ(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap,
            Date rainfallStartDate, Date rainfallEndDate, Double totalRainfallArea) {
        //得到时间轴对象
        List<Date> dateList =
                DateUtils.getDateListByStartDateAndEndDate(rainfallStartDate, rainfallEndDate);
        //得到按照站码列表分组的雨量站列表对象
        List<List<RaPDayRtVo>> raPDayRtVosList =
                getRaPDayRtVosListByStcdList(allInformationMap, dateList);
        /*
         * 计算逻辑
         * 雨量站统计的降雨量 = 累加历时中的降雨量 * 自己的控制面积
         * 总降雨量 = (累加每个雨量站统计的降雨量)/总控制面积
         */
        //构造结果
        Double totalRainfallQ = 0D;
        //遍历按站码分组的雨量站列表对象
        for (List<RaPDayRtVo> raPDayRtVoList : raPDayRtVosList) {
            Double tempQ = 0D;
            //计算每个雨量站的降雨量
            if (raPDayRtVoList.size() != 0) {
                //得到控制面积
                Double raPDayRtArea = raPDayRtVoList.get(0).getArea();
                //遍历累加降雨量
                for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
                    tempQ += raPDayRtVo.getRf();
                }
                //降雨量 * 控制面积
                tempQ = tempQ * raPDayRtArea;
            }
            totalRainfallQ += tempQ;
        }
        //累加后的结果/控制面积
        totalRainfallQ = totalRainfallQ / totalRainfallArea;
        return totalRainfallQ;
    }

    /**
     * 方法描述: 得到时段雨量笼罩面积
     *
     * @param allInformationMap 所有信息的Map
     * @param rainfallStartDate 降雨起始日
     * @param rainfallEndDate   降雨结束的日期
     * @param rainfallThreshold 用于统计的阈值
     * @return 时段雨量笼罩面积
     *  日期从小到大(2020/7/1, 2020/7/2, 2020/7/3.......)
     * @author yanglichen
     * @date 2020-07-24 16:50
     **/
    @Override
    public List<Double> getTimesRainfallArea(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap,
            Date rainfallStartDate, Date rainfallEndDate, Double rainfallThreshold) {
        //构造结果集
        List<Double> timesRainfallArea = new ArrayList<>();
        //得到时间轴对象
        List<Date> dateList =
                DateUtils.getDateListByStartDateAndEndDate(rainfallStartDate, rainfallEndDate);
        //得到按照时间分组的对象
        List<List<RaPDayRtVo>> raPDayRtVosList =
                getDateListByRaPDayRtVo(allInformationMap,dateList);
        //遍历分组对象
        for (List<RaPDayRtVo> raPDayRtVoList : raPDayRtVosList) {
            //日笼罩面积
            double dayRainfallArea = 0D;
            //遍历每日的列表对象
            for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
                //如果降雨超过了阈值才进行累加
                Double dayRf = raPDayRtVo.getRf();
                if (dayRf >= rainfallThreshold) {
                    dayRainfallArea += raPDayRtVo.getArea();
                }
            }
            timesRainfallArea.add(dayRainfallArea);
        }
        return timesRainfallArea;
    }

    /**
     * 方法描述: 得到最大点雨量,和暴雨中心
     *
     * @param allInformationMap 包含所有信息的Map结果集
     * @param rainfallStartDate 降雨起始日
     * @param rainfallEndDate 降雨截止日
     * @return 封装有最大点雨量信息,暴雨中心(站码)的Map
     * @author yanglichen
     * @date 2020-07-27 08:19
     **/
    @Override
    public Map<String, Object> getMaxRainfall(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap,
            Date rainfallStartDate, Date rainfallEndDate) {
        //构造结果集
        Map<String, Object> resultMap = new HashMap<>();
        //得到时间轴对象
        List<Date> dateList =
                DateUtils.getDateListByStartDateAndEndDate(rainfallStartDate, rainfallEndDate);
        //得到按照站码分组的结果
        List<List<RaPDayRtVo>> raPDayRtVosList =
                getRaPDayRtVosListByStcdList(allInformationMap, dateList);
        //构造一个数组,数组长度等于站码的数量
        double[] maxRainfallQ = new double[raPDayRtVosList.size()];
        //遍历站码分组列表
        for (int i = 0; i < raPDayRtVosList.size(); i++) {
            double sumRf = 0;
            for (int j = 0; j < raPDayRtVosList.get(i).size(); j++) {
                sumRf += raPDayRtVosList.get(i).get(j).getRf();
            }
            maxRainfallQ[i] = sumRf;
        }
        //找到最大值对应的下标
        double maxRf = maxRainfallQ[0];
        int tempIndex = 0;
        for (int i = 0; i < maxRainfallQ.length; i++) {
            if(maxRf < maxRainfallQ[i]){
                maxRf = maxRainfallQ[i];
                tempIndex = i;
            }
        }
        /*
        这个for循环结束的时候tempIndex就是最大值对应的下标
        因为这个是按照上面的List遍历累加得到的结果
        所以这个tempIndex对应的也是List中最大雨量站的索引
        */
        String maxRfStcd = "";
        if (raPDayRtVosList.get(tempIndex).size()!=0) {
            maxRfStcd = raPDayRtVosList.get(tempIndex).get(0).getStcd();
        }
        //封装信息
        resultMap.put("maxRf", maxRf);
        resultMap.put("maxRfStcd", maxRfStcd);
        return resultMap;
    }

    /**
     * 方法描述: 计算得到新的权重系数,并将结果按照站码,新权重封装到一个Map中,后续操作取用数据
     *
     * @param allInformationMap 包含所有信息的Map结果集
     * @return 站码和新权重的映射Map
     * @author yanglichen
     * @date 2020-07-27 08:52
     **/
    @Override
    public Map<String, Double> getNewWeiight(Map<String, Map<Date, RaPDayRtVo>> allInformationMap) {
        //构造结果集
        Map<String, Double> newWeightMap = new HashMap<>();
        //得到每一个对象组成的实例
        List<RaPDayRtVo> raPDayRtVoList = getRaPDayRtVoList(allInformationMap);
        //遍历累加得到权重累加值
        double oldWeight = 0D;
        for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
            oldWeight += raPDayRtVo.getWeight();
        }
        //遍历雨量站列表,按照比例进行计算新的权重
        for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
            String stcd = raPDayRtVo.getStcd();
            double newWeight = raPDayRtVo.getWeight()/oldWeight;
            //放入Map中
            newWeightMap.put(stcd, newWeight);
        }
        return newWeightMap;
    }

    /**
     * 方法描述: 计算前期雨量指数
     *
     * @param allInformationMap 包含所有信息的Map结果集
     * @param rainfallStartDate 降雨起始日
     * @return 前期雨量指数
     * @author yanglichen
     * @date 2020-07-27 09:02
     **/
    @Override
    public Double getBeforeRainfallPoint(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap, Date rainfallStartDate) {
        //根据降雨起始日得到15天前的日期
        Date befor15DayDate = DateUtils.getNextDateByTimeZone(rainfallStartDate, 14);
        //根据降雨日期和雨前日期得到一个日期列表
        List<Date> dateList =
                DateUtils.getDateListByStartDateAndEndDate(befor15DayDate, rainfallStartDate);
        //得到这15天的结果级别(外层List是日期分组,小日期在前)
        List<List<RaPDayRtVo>> raPDayRtVosList =
                getDateListByRaPDayRtVo(allInformationMap, dateList);
        //得到站码和最新权重的映射
        Map<String, Double> weightMap = getNewWeiight(allInformationMap);
        //得到15天的每日面平均雨量
        List<Double> dayRfList = getDayRainfallQ(raPDayRtVosList, weightMap);
        //得到每日的P值 = 面平均雨量 * 0.85 ^ 第几天就是第几次方
        List<Double> dayPList = getDayPList(dayRfList);
        //累加每日的P
        double sumP = 0D;
        for (Double dayP : dayPList) {
            sumP += dayP;
        }
        return sumP;
    }

    /**
     * 方法描述: 将RequestBody中的参数转化为模型需要用到的参数实例
     *
     * @param requestBodyMap 客户端发送的请求数据
     * @return 计算模型需要的数据
     * @author yanglichen
     * @date 2020-07-27 16:05
     **/
    @Override
    public RainfallParameters getParametersByRequestBody(Map<String, Object> requestBodyMap) {
        //得到数据
        List<String> stcdList = (List<String>) requestBodyMap.get("stcdList");
        Integer timeInterval = (Integer) requestBodyMap.get("timeInterval");
        Double rainfallThreshold = (Double) requestBodyMap.get("rainfallThreshold");
        Double rainfallThresholdFoArea = (Double) requestBodyMap.get("rainfallThresholdFoArea");
        long calDateTime = (long) requestBodyMap.get("calDate");
        Integer rainfallTolerance = (Integer) requestBodyMap.get("rainfallTolerance");
        Double rainfallQTolerance = (Double) requestBodyMap.get("rainfallQTolerance");
        //封装数据
        RainfallParameters rainfallParameters = new RainfallParameters();
        rainfallParameters.setTimeInterval(timeInterval);
        rainfallParameters.setStcdList(stcdList);
        rainfallParameters.setRainfallThresholdFoArea(rainfallThresholdFoArea);
        rainfallParameters.setRainfallThreshold(rainfallThreshold);
        rainfallParameters.setCalDate(calDateTime);
        rainfallParameters.setRainfallTolerance(rainfallTolerance);
        rainfallParameters.setRainfallQTolerance(rainfallQTolerance);
        //返回数据
        return rainfallParameters;
    }

    /**
     * 方法描述: 根据某一场降雨得到在该场次降雨中,
     * 每个站码所记录的降雨量的总和,并组成一个Map
     *
     * @param rainfallResult    某一场雨的降雨特征值信息
     * @param allInformationMap 包含所有信息的Map结果集
     * @return 在该场次降雨中, 每个站码记录的降雨量的总和, 并放入一个Map中
     * 例如: {站码1 = 22.0, 站码2 = 25.0, 站码3 = 28.8, ....}
     * @author yanglichen
     * @date 2020-07-28 16:40
     **/
    @Override
    public Map<String, Double> getResemblanceMap(
            RainfallResult rainfallResult, Map<String, Map<Date, RaPDayRtVo>> allInformationMap) {

        //从降雨特征值中得到相关信息
        //降雨起始日
        Date startDate = rainfallResult.getRainfallDate();
        //降雨截止日
        Date endDate = rainfallResult.getEndRainfallDate();
        //得到降雨的时间轴
        List<Date> dateList = DateUtils.getDateListByStartDateAndEndDate(startDate, endDate);
        //根据以上信息得到按照站码分类的List<List>
        List<List<RaPDayRtVo>> raPDayRtVosList = getRaPDayRtVosListByStcdList(allInformationMap, dateList);
        //得到并返回站码和历时内对应的降雨信息
        return new HashMap<>(getStcdRainfallQMap(raPDayRtVosList));
    }

    /**
     * 方法描述: 根据传入的原型雨和备选雨的雨量站的降雨映射,权重映射,
     * 计算降雨的相似性
     *
     * @param prototypeMap 原型雨雨量映射
     * @param otherMap     备选雨雨量映射
     * @param weightMap    权重的映射
     * @param stcdList     站码列表
     * @return 降雨的相似性
     * @author yanglichen
     * @date 2020-07-28 17:03
     **/
    @Override
    public Double getResemblace(
            Map<String, Double> prototypeMap, Map<String, Double> otherMap,
            Map<String, Double> weightMap, List<String> stcdList) {
        /*
            新计算出的权重 * (累加原型雨中某站码在历时内的雨量 - 累加备选雨中某站码在历时中的雨量)²
            累加上面的结果后
            对结果开根号
        */
        //构造用于存放单个雨量站计算结果的结果集
        List<Double> reusltList = new ArrayList<>();
        //遍历站码列表得到不同的数据
        for (String stcd : stcdList) {
            //原型雨站码的降雨量
            Double prototypeQ = prototypeMap.get(stcd);
            //备选雨站码的降雨量
            Double otherQ = otherMap.get(stcd);
            //站码对应的权重
            Double stcdWeight = weightMap.get(stcd);
            double tempResult =0D;
            if(!(prototypeQ==null || otherQ==null)){
            //System.err.println("站码 "+stcd+" 原型雨降雨量: "+ prototypeQ+ " 备选雨降雨量: "+ otherQ);
                //执行计算 --> 权重 * (累加原型雨中某站码在历时内的雨量 - 累加备选雨中某站码在历时中的雨量)²
                tempResult = stcdWeight * Math.pow((prototypeQ - otherQ), 2);
            }
            reusltList.add(tempResult);
        }
        //对雨量站结果进行累加后求和
        double sumTempResult = 0D;
        for (Double cal : reusltList) {
            sumTempResult += cal;
        }
        return Math.sqrt(sumTempResult);
    }

    /**
     * 方法描述: 从Map结果集中得到一个包含每个雨量站全部信息的一个实例组成的列表
     *
     * @param allInformationMap 所有信息的Map结果集
     * @return 包含每个雨量站全部信息的一个实例组成的列表
     * @author yanglichen
     * @date 2020-07-24 11:00
     **/
    private List<RaPDayRtVo> getRaPDayRtVoList(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap) {
        List<RaPDayRtVo> raPDayRtVoList = new ArrayList<>();
        //遍历第一层Map
        for (Map.Entry<String, Map<Date, RaPDayRtVo>> firMap : allInformationMap.entrySet()) {
            if (firMap.getValue() != null) {
                //根据第一层的value得到第二层的Map
                Map<Date, RaPDayRtVo> secMap = firMap.getValue();
                //遍历第二层的Map
                for (Map.Entry<Date, RaPDayRtVo> nextMap : secMap.entrySet()) {
                    if (nextMap.getValue() != null) {
                        RaPDayRtVo raPDayRtVo = nextMap.getValue();
                        raPDayRtVoList.add(raPDayRtVo);
                        break;
                    }
                }
            }
        }
        return raPDayRtVoList;
    }

    /**
     * 方法描述: 根据相似度对备选雨结果进行排序
     *
     * @param rainfallResultList      备选雨列表
     * @param prototypeRainfallResult 原型雨
     * @return 排序后的RainfallResult
     * @author yanglichen
     * @date 2020-07-28 17:19
     **/
    @Override
    public List<RainfallResult> sortRainfallResultListByresesmblance(
            List<RainfallResult> rainfallResultList, RainfallResult prototypeRainfallResult) {

        //得到站码列表
        List<String> stcdList = prototypeRainfallResult.getRainfallParameters().getStcdList();
        //得到结果大集合
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap =
                GetSomethingUtils.getAllInformationMap(stcdList);
        //得到原型雨对应的站码和雨量的Map
        Map<String, Double> prototypeMap =
                getResemblanceMap(prototypeRainfallResult, allInformationMap);
        //得到新权重
        Map<String, Double> newWeight = getNewWeiight(allInformationMap);
        //遍历备选雨实例,计算相似值
        for (RainfallResult rainfallResult : rainfallResultList) {
            //得到备选雨对应的站码和雨量的Map
            Map<String, Double> otherMap =
                    getResemblanceMap(rainfallResult, allInformationMap);
            //得到相似值
            Double resesmblace = getResemblace(prototypeMap,otherMap,newWeight, stcdList);
            //封装相似值
            rainfallResult.setResemblance(resesmblace);
        }
        //返回排序后的结果
        return sortRainfallResultsByResemblace(rainfallResultList);
    }

    /**
     * 方法描述: 根据传入的时间轴从结果集中得到按照站码列表分组的雨量站列表对象
     *
     * @param allInformationMap 结果集大全Map
     * @param dateList          时间轴
     * @return 按照站码列表分组的雨量站列表对象
     * @author yanglichen
     * @date 2020-07-24 15:46
     **/
    private List<List<RaPDayRtVo>> getRaPDayRtVosListByStcdList(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap, List<Date> dateList) {
        List<List<RaPDayRtVo>> raPDayRtVosList = new ArrayList<>();
        //遍历外层Map
        for (Map.Entry<String, Map<Date, RaPDayRtVo>> firMap : allInformationMap.entrySet()) {
            if (firMap.getValue() != null) {
                //根据第一层的value得到第二层的Map对象
                Map<Date, RaPDayRtVo> secMap = firMap.getValue();
                //构造结果中内层的List
                List<RaPDayRtVo> raPDayRtVoList = new ArrayList<>();
                //遍历时间轴对象
                for (Date date : dateList) {
                    //根据日期找到对象
                    RaPDayRtVo raPDayRtVo = secMap.get(date);
                    if (raPDayRtVo != null) {
                        //添加到二级列表内
                        raPDayRtVoList.add(raPDayRtVo);
                    }
                }
                //添加到一级列表中
                raPDayRtVosList.add(raPDayRtVoList);
            }
        }
        return raPDayRtVosList;
    }

    /**
     * 方法描述: 根据时间轴得到一个以时间轴分组的雨量站列表对象
     *
     * @param allInformationMap 结果集大全Map
     * @param dateList          时间轴
     * @return 以时间轴分组的雨量站列表对象
     * @author yanglichen
     * @date 2020-07-24 16:55
     **/
    private List<List<RaPDayRtVo>> getDateListByRaPDayRtVo(
            Map<String, Map<Date, RaPDayRtVo>> allInformationMap, List<Date> dateList) {
        List<List<RaPDayRtVo>> raPDayRtVosList = new ArrayList<>();
        //遍历时间轴对象
        for (Date date : dateList) {
            //构造二层结果集
            List<RaPDayRtVo> raPDayRtVoList = new ArrayList<>();
            //遍历外层Map
            for (Map.Entry<String, Map<Date, RaPDayRtVo>> firMap : allInformationMap.entrySet()) {
                if (firMap.getValue() != null) {
                    //根据第一层的value得到第二层的Map对象
                    Map<Date, RaPDayRtVo> secMap = firMap.getValue();
                    //根据日期得到对应的雨量站实例对象
                    if (secMap.get(date) != null) {
                        //得到的结果不为null,放入二级列表中
                        raPDayRtVoList.add(secMap.get(date));
                    }
                }
            }
            //二级列表放入一级列表中
            raPDayRtVosList.add(raPDayRtVoList);
        }
        return raPDayRtVosList;
    }

    /**
     * 方法描述: 得到每日面雨量列表
     * @author yanglichen
     * @date 2020-07-27 09:19
     * @param raPDayRtVosList 按照日期分组的降雨列表
     * @return 每日面雨量列表
     **/
    private List<Double> getDayRainfallQ(
            List<List<RaPDayRtVo>> raPDayRtVosList, Map<String, Double> newWeight){
        List<Double> dayRfList = new ArrayList<>();
        /*面平均雨量 = 当天下所有站码的降雨 * 各自站码最新的权重系数*/
        for (List<RaPDayRtVo> raPDayRtVoList : raPDayRtVosList) {
            double dayRf = 0D;
            for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
                String stcd = raPDayRtVo.getStcd();
                //得到权重
                double weight = newWeight.get(stcd);
                //所有站码的降雨 * 各自最新的权重
                dayRf += raPDayRtVo.getRf()*weight;
            }
            dayRfList.add(dayRf);
        }
        return dayRfList;
    }

    /**
     * 方法描述: 根据每日面雨量得到每日的P值并且分装到一个列表中
     * @author yanglichen
     * @date 2020-07-27 09:36
     * @param dayRfList 每日面雨量列表
     * @return 每日的P
     **/
    private List<Double> getDayPList(List<Double> dayRfList){
        //得到每日的P值 = 面平均雨量 * (0.85 ^ 第几天就是第几次方)
        List<Double> dayPList = new ArrayList<>();
        //遍历每日面平均雨量列表(列表中的顺序是小日期在前,所以列表中的第一个元素实际上是第15天的数据)
        for (int i = 0; i < dayRfList.size(); i++) {
            double dayP = Math.pow(0.85, dayRfList.size()-i)*dayRfList.get(i);
            dayPList.add(dayP);
        }
        return dayPList;
    }

    /**
     * 方法描述: 根据按照站码分类的List<RaPDayRtVo>和站码列表
     *  得到一个站码和站码内对应总雨量的Map
     * @author yanglichen
     * @date 2020-07-28 16:52
     * @param raPDayRtVosList 按照站码分类的List<RaPDayRtVo>
     * @return 站码和站码内对应总雨量的Map
     *  例如: {站码1 = 22.0, 站码2 = 25.0, 站码3 = 28.8, ....}
     **/
    private Map<String, Double> getStcdRainfallQMap(List<List<RaPDayRtVo>> raPDayRtVosList) {
        //构造结果集
        Map<String, Double> resultMap = new HashMap<>();
        for (List<RaPDayRtVo> raPDayRtVoList : raPDayRtVosList) {
            if (raPDayRtVoList.size()!=0) {
                //得到站码
                String stcd = raPDayRtVoList.get(0).getStcd();
                //初始化累加雨量和
                double sumQ = 0D;
                //遍历二级列表求和
                for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
                    sumQ += raPDayRtVo.getRf();
                }
                //讲结果存入Map中
                resultMap.put(stcd, sumQ);
            }
        }
        return resultMap;
    }

    /**
     * 方法描述: 根据备选雨的相似值对备选雨进行排序
     *  (降序排序)
     * @author yanglichen
     * @date 2020-07-28 17:38
     * @param rainfallResultList 备选雨列表
     * @return 排序后的备选雨的列表
     **/
    private List<RainfallResult> sortRainfallResultsByResemblace(
            List<RainfallResult> rainfallResultList) {
        //构造结果集
        List<RainfallResult> rainfallResults = new ArrayList<>();
        //构造排序用的数组
        double[] tempArray = new double[rainfallResultList.size()];
        //遍历备选雨列表
        for (int i = 0; i < rainfallResultList.size(); i++) {
            tempArray[i] = rainfallResultList.get(i).getResemblance();
        }
        //对排序用的数组进行排序
        Arrays.sort(tempArray);
        //遍历排序数组
        for (double temp : tempArray) {
            //遍历备选雨列表
            for (RainfallResult result : rainfallResultList) {
                //根据相似值找到备选雨,找到了就跳出
                if (result.getResemblance()==temp) {
                    rainfallResults.add(result);
                    break;
                }
            }
        }
        return rainfallResults;
    }
}

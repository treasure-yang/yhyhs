package cn.yrec.yhyhsmodelservice.service.impl;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtMapper;
import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtVoMapper;
import cn.yrec.yhyhsmodelservice.mappers.RaPHourRtMapper;
import cn.yrec.yhyhsmodelservice.model.RaPHourRt;
import cn.yrec.yhyhsmodelservice.model.RaPweight;
import cn.yrec.yhyhsmodelservice.service.RaPDayRtService;
import cn.yrec.yhyhsmodelservice.service.RaPDayRtVoService;
import cn.yrec.yhyhsmodelservice.service.RaPHourRtService;
import cn.yrec.yhyhsmodelservice.service.RaPweightService;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.utils.TransFormUtils;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Classname RaPDayRtVoServiceImpl
 * @Description 逐日降雨VO类的业务层接口
 * @Date 2020/07/01 上午 11:12
 * @Created by yanglichen
 */
@Service
public class RaPDayRtVoServiceImpl implements RaPDayRtVoService {

//    @Autowired
//    private RaPDayRtVoMapper raPDayRtVoMapper;


    @Autowired
    private RaPweightService raPweightService;

    @Autowired
    private RaPDayRtService raPDayRtService;

    @Autowired
    private RaPHourRtMapper raPHourRtMapper;


    /**
     * 方法描述: 根据站码和降雨起始日, 计算某雨量站在本场雨内的总降雨量
     * @author yanglichen
     * @date 2020-07-06 10:40
     * @param stcd 站码
     * @param rainfallDate 降雨起始日
     * @param calDate 计算日期
     * @param totalRainfallArea 站码列表(用于计算总降雨面积)
     * @return 总降雨量
     **/
    @Override
    public Double getRainfallQByStcdAndRainfallDate(
            String stcd, Date rainfallDate, Date calDate, Double totalRainfallArea) {
        //得到降雨范围内的雨量站信息
        System.err.println("正在查询: "+stcd+" 雨量站");
        List<RaPDayRtVo> raPDayRtVoList = getRaPDatRtVoListByStcdAndDateStr(stcd, rainfallDate, calDate);
        //得到雨量站的降雨累加和
        Double totalRainfallQ = getTotalRainfallQByRaPDayRtList(raPDayRtVoList);
        //返回这个站的总降雨量
        return totalRainfallQ/totalRainfallArea;
    }

    /**
     * 方法描述: 根据降雨起始日和站码列表, 得到该站码列表下全部雨量站的总降雨和(单位mm)
     * @author yanglichen
     * @date 2020-07-01 10:39
     * @param rainfallDate 降雨起始日
     * @param stcdList 站码列表
     * @return 站码列表对应的雨量站下的总降雨和
     **/
    @Override
    public Double getAllRainFallQByRainfallDateAndStcdList(Date rainfallDate, Date calDate, List<String> stcdList) {
        Double totalRainQ = 0D;
        //得到降雨总面积
        Double totalRainfallArea = getTotalRainfallAreaByStcdList(stcdList);
        System.err.println("降雨总面积为: "+ totalRainfallArea/1000000+" 平方公里");
        //遍历站码列表
        for (String stcd :stcdList) {
            //得到一个一个站码下的降雨量
            Double stcdRainfallQ = getRainfallQByStcdAndRainfallDate(stcd, rainfallDate, calDate, totalRainfallArea);
            //累加得到区域内的总降雨量(得到单个站码的过程中已经在结果上除以了总降雨面积)
            totalRainQ += stcdRainfallQ;
        }
        return totalRainQ;
    }


    /**
     * 方法描述: 根据开始下雨的时间 和 雨量站实例列表 以及降雨雨量的阈值, 得到一个每天的雨量笼罩面积
     * @author yanglichen
     * @date 2020-07-06 10:43
     * @param raPDayRtVoList 雨量站列表
     * @param rainfallDateStr 开始下雨的时间
     * @param rfThreshold 雨量阈值
     * @return  [第1天的笼罩面积, 第2天的笼罩面积, 第3天的笼罩面积]
     **/
    @Override
    public List<Double> getAllRainfallAreaByStcdAndRainfallDay(List<RaPDayRtVo> raPDayRtVoList, String rainfallDateStr, Double rfThreshold, Date calDate) {
        //构造结果集
        List<Double> daysRainfallArea = new ArrayList<>();
        //根据下雨的起始日期,得到一个距离今天的一个升序的字符串格式的日期列表
        List<String> timeZoneForRainfallDays = DateUtils.getTimeZoneByRainfallDay(DateUtils.transformStrTODate(rainfallDateStr), calDate);
        /*
        根据日期列表筛选每日的降雨情况
        数据库中存在一些雨量站信息不完整(当当日没有产生降雨时,不会插入当天的数据到数据库中)
        所以只能通过日期对查询的雨量站信息做筛选(过滤器模式)
        */
        //构造用于存放是筛选结果的结果集(第一层List表示日期,第二层List表示每天中雨量站的列表)
        List<List<RaPDayRtVo>> raPDayRtVosList = new ArrayList<>();
        //遍历降雨时间段列表
        timeZoneForRainfallDays.forEach(rainfallDay->{
            //构造二级List
            List<RaPDayRtVo> dayRaPDayRtVoList = new ArrayList<>();
            //遍历所有的雨量站
            raPDayRtVoList.forEach(raPDayRtVo -> {
                //转换日期格式,用于下一步比较
                String raPDayRtVoDateStr = DateUtils.transformDateTOStr(raPDayRtVo.getDate());
                //如果日期相同,并且降雨量大于规定的阈值,就放入列表中
                if (raPDayRtVoDateStr.equalsIgnoreCase(rainfallDay) && raPDayRtVo.getRf() >= rfThreshold) {
                    dayRaPDayRtVoList.add(raPDayRtVo);
                }
            });
            raPDayRtVosList.add(dayRaPDayRtVoList);
        });
        //对筛选结果集中的数据进行求和,并将结果注入到最终的结果集中
        raPDayRtVosList.forEach(dayRaPDayRtVoList->{
            //遍历求和
            double sumArea = 0D;
            for (RaPDayRtVo r : dayRaPDayRtVoList) {
                sumArea += r.getArea();
            }
            //添加结果
            daysRainfallArea.add(sumArea);
        });
        return daysRainfallArea;
    }

    /**
     * 方法描述: 根据站码列表计算总降雨面积
     * @author yanglichen
     * @date 2020-07-06 10:43
     * @param stcdList 站码列表
     * @return 总降雨面积
     **/
    private Double getTotalRainfallAreaByStcdList(List<String> stcdList){
        return raPweightService.getTotalRainfallAreaByStcdList(stcdList);
    }


    /**
     * 方法描述: 根据站码和字符串格式的日期,查询一个3表合一信息的逐日雨量站信息
     * @author yanglichen
     * @date 2020-07-06 10:42
     * @param stcd 站码
     * @param rainDate 下雨日期
     * @param calDate 计算日期
     * @return 装有该站码综合信息的站码列表
     **/
    private List<RaPDayRtVo> getRaPDatRtVoListByStcdAndDateStr(String stcd, Date rainDate, Date calDate){
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtVoMapper raPDayRtVoMapper = sqlSession.getMapper(RaPDayRtVoMapper.class);
        List<RaPDayRtVo> raPDayRtVoList = raPDayRtVoMapper
                .getRaPDayRtVoListByStcdAndDateStr(stcd, rainDate, calDate);
        sqlSession.close();
        return raPDayRtVoList;
    }


    /**
     * 方法描述: 通过累加得到某雨量站在本场次降雨中的降雨量
     * @author yanglichen
     * @date 2020-07-06 10:42
     * @param raPDayRtVos 雨量站封装类的列表
     * @return 雨量站在历时中降雨量的累加和 * 控制面积
     **/
    private Double getTotalRainfallQByRaPDayRtList(List<RaPDayRtVo> raPDayRtVos){
        if (raPDayRtVos.size()==0) {
            return 0D;
        }
        Double rainfallArea = raPDayRtVos.get(0).getArea();
        Double totalRainfallQ = 0D;
        //遍历雨量站Vo类的list
        for (RaPDayRtVo raPDayRtVo : raPDayRtVos) {
            totalRainfallQ += raPDayRtVo.getRf();
        }
        return totalRainfallQ*rainfallArea;
    }


    /**
     * 方法描述: 根据站码列表和降雨日期得到包含雨量站详细信息的列表的列表
     * @author yanglichen
     * @date 2020-07-06 10:42
     * @param stcdList 站码列表
     * @param rainfallDateStr 站码列表
     * @return  [雨量站1详细信息的列表, 雨量站2详细信息的列表, 雨量站3详细信息的列表......]
     **/
    private List<List<RaPDayRtVo>> getListRaPDayRtVoListByStcdList(List<String> stcdList, String rainfallDateStr, String calDateStr){
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtVoMapper raPDayRtVoMapper = sqlSession.getMapper(RaPDayRtVoMapper.class);
        List<RaPDayRtVo> raPDayRtVoList = raPDayRtVoMapper.getRaPDayRtVoListByStcdListAndDateStr(stcdList, rainfallDateStr, calDateStr);
        List<List<RaPDayRtVo>> lists = TransFormUtils.getListRaPDayRtVoListByStringList(raPDayRtVoList, stcdList);
        sqlSession.close();
        return lists;
    }


    /**
     * 方法描述: 得到下雨的那一天距离今天有多少天
     * @author yanglichen
     * @date 2020-07-06 10:41
     * @param rainfallDay 开始下雨的那一天
     * @return 本场次于雨降雨历时
     **/
    private Integer getRainfallDaysByStartRainDay(String rainfallDay){
        Date rainfallDate = DateUtils.transformStrTODate(rainfallDay);
        return DateUtils.getTimesByDate(rainfallDate);
    }

    /**
     * 方法描述: 根据站码列表和下雨日期,得到自下雨日期开始以来,站码列表记录的雨量站的详细信息
     * @author yanglichen
     * @date 2020-07-06 10:41
     * @param stcdList 站码列表
     * @param rainfallDateStr 下雨开始的日期
     * @return 自下雨日期开始以来,站码列表记录的雨量站的详细信息
     **/
    private List<RaPDayRtVo> getRaPDayVoListByStcdList(List<String> stcdList, String rainfallDateStr, String calDateStr){
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtVoMapper raPDayRtVoMapper = sqlSession.getMapper(RaPDayRtVoMapper.class);
        List<RaPDayRtVo> raPDayRtVoList =
                raPDayRtVoMapper.getRaPDayRtVoListByStcdListAndDateStr(stcdList, rainfallDateStr, calDateStr);
        sqlSession.close();
        return raPDayRtVoList;
    }


    /**
     * 方法描述: 通过传入的雨量站详细列表信息,计算在本场次降雨中,降雨mm最多的雨量站,对应的雨量
     * @author yanglichen
     * @date 2020-07-06 10:38
     * @param raPDayRtVoList 雨量站详情列表
     * @param stcdList 雨量站站码
     * @return 本场次降雨中的某站统计的总大的降雨量
     **/
    @Override
    public Map<String, Object> getMaxRfByRaPDayRtVoList(List<RaPDayRtVo> raPDayRtVoList, List<String> stcdList) {
        Map<String, Object> result = new HashMap<>();
        //构造结果集
        Double[] rFresult = new Double[stcdList.size()];
        //讲站码总集合转化为一个按照站码分类的列表
        List<List<RaPDayRtVo>> raPDayRtVosList = TransFormUtils.getListRaPDayRtVoListByStringList(
                raPDayRtVoList, stcdList);
        //遍历分类站码列表
        for (int i = 0; i < raPDayRtVosList.size(); i++) {
            Double sumRt = getSumRfByRaPDayrRtVoList(raPDayRtVosList.get(i));
            rFresult[i] = sumRt;
        }
        //寻找最大值的下标
        double max = -1;
        int index = 0;
        for (int i = 0; i < rFresult.length; i++) {
           if ( max < rFresult[i]){
               max = rFresult[i];
               index = i;
           }
        }
        String maxStcd = raPDayRtVosList.get(index).get(0).getStcd();
        double maxRf = rFresult[index];
        result.put("stcd", maxStcd);
        result.put("maxRf", maxRf);
        return result;
    }

    /**
     * 方法描述: 根据雨量站站码列表,和开始降雨的时间,得到前期降雨指数
     * 前期降雨指数: 降雨开始前15天到降雨日期间根据降雨覆盖的雨量站的站码统计计算出来的一个指数
     * @param stcdList     雨量站站码列表
     * @param rainfallDate 开始降雨的时间
     * @return 前期降雨指数
     * @author yanglichen
     * @date 2020-07-06 18:31
     **/
    @Override
    public Double getBeforRainfallPoint(List<String> stcdList, Date rainfallDate) {
        //结果
        Double result = 0D;
        List<Double> beforRainfallDaysP = getBeforRainfallDaysP(stcdList, rainfallDate);
        beforRainfallDaysP.forEach(item->{
//            System.out.println("每日的P:\t"+item);
        });
        //遍历,准备累加计算
        for (int i = 0; i < beforRainfallDaysP.size(); i++) {
            //(每日的 * 0.85 )第几天就几次方
            Double dayPn = Math.pow(0.85 , (i+1))* beforRainfallDaysP.get(i);
//            System.err.println("每日的knp:\t"+dayPn);
            //累加
            result += dayPn;
        }
        return result;
    }

    /**
     * 方法描述: 判断站码列表在数据库中有记录的最后的日期是不是当前日期
     *
     * @param stcdList 站码列表
     * @return 判断站码列表在数据库中有记录的最后的日期是不是当前日期
     * @author yanglichen
     * @date 2020-07-13 08:46
     **/
    private Boolean judgeLastInfoDateIsNow(List<String> stcdList) {
        String nowDateStr = DateUtils.transformDateTOStr(new Date());
        Date dataBaseLastDate = raPDayRtService.judgeTodayRaPDayRtListInDataBase(stcdList, new Date());
        String lastdateStr = DateUtils.transformDateTOStr(dataBaseLastDate);
        return nowDateStr.equalsIgnoreCase(lastdateStr);
    }

    /**
     * 方法描述: 根据站码列表和传入的日期,从雨量站小时表中查询数据
     * 并且整理到逐日降雨的统计表中
     *
     * @param stcdList 站码列表
     * @param lastDate 数据库中查询出来的的最后数据出现的日期
     * @return 整编好的数据
     * @author yanglichen
     * @date 2020-07-13 09:12
     **/
    @Override
    public List<RaPDayRtVo> getNewRaPDayVoListByRaPDay(
            List<String> stcdList, Date lastDate, Date rainfallDate, Date calDate) {
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtVoMapper raPDayRtVoMapper = sqlSession.getMapper(RaPDayRtVoMapper.class);
        //重数据库查询三合一才雨量站信息
        List<RaPDayRtVo> basicList = raPDayRtVoMapper
                .getRaPDayRtVoListByStcdListAndDateStr(stcdList, DateUtils.transformDateTOStr(rainfallDate), DateUtils.transformDateTOStr(calDate));
        sqlSession.close();
        //判断查询结果是否缺少日期数据
        if (judgeLastInfoDateIsNow(stcdList)) {
            //不缺少直接返回查询结果
            return basicList;
        }else{
            //缺少,找到开始缺少的日期
            Date dataEndDate = raPDayRtService.judgeTodayRaPDayRtListInDataBase(stcdList, rainfallDate);
            //查询这个缺少日期距离距离现在这段时间中,小时表中的数据
            List<RaPHourRt> raPHourRts = raPHourRtMapper.getRapHourRtListByStcdListAndDate(stcdList, dataEndDate, new Date());
            return null;
        }

    }

    /**
     * 方法描述: 得到站码列表在起始日之前的所有的信息
     * 封装成Map(便于后续计算降雨特征值信息的检索)
     *
     * @param stcdList 站码列表
     * @return 封装站码列表全部信息的Map
     * 例如:{站码1 = {日期1=雨量站全部信息实例1, 日期2=雨量站全部信息实例2...},
     * 站码2 = {日期1=雨量站全部信息实例1, 日期2=雨量站全部信息实例2...}....}
     * @author yanglichen
     * @date 2020-07-24 10:27
     **/
    @Override
    public Map<String, Map<Date, RaPDayRtVo>> getAllRaPDayRtVoInformation(List<String> stcdList) {
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtVoMapper raPDayRtVoMapper = sqlSession.getMapper(RaPDayRtVoMapper.class);
        //得到 当日00:00 时刻的Date (防止传入不同的时间对象,导致无法使用MyBatis二级缓存的数据)
        Date now = DateUtils.getNotHourDate(new Date());
        List<RaPDayRtVo> raPDayRtVoList = raPDayRtVoMapper.getAllInformation(stcdList, now);
        sqlSession.close();
        Map<String, Map<Date, RaPDayRtVo>> allInformationMap = new HashMap<>();
        //得到按照站码分类的数据
        List<List<RaPDayRtVo>> raPDayRtVosList =
                TransFormUtils.getListRaPDayRtVoListByStringList(raPDayRtVoList, stcdList);
        //遍历站码列表进行构造外层Map
        stcdList.forEach(stcd->{
            //初始化内层Map
            Map<Date, RaPDayRtVo> raPDayRtVoMap = null;
            //遍历按照站码分类的列表
            for (List<RaPDayRtVo> raPDayRtVos : raPDayRtVosList) {
                if(raPDayRtVos.size()!=0){
                    //找到站码列表对应的列表
                    if (raPDayRtVos.get(0).getStcd().equalsIgnoreCase(stcd)) {
                        raPDayRtVoMap = new HashMap<>();
                        //遍历该列表对象
                        for (RaPDayRtVo raPDayRtVo : raPDayRtVos) {
                            //存放内层Map数据
                            raPDayRtVoMap.put(raPDayRtVo.getDate(), raPDayRtVo);
                        }
                    }
                }
            }
            allInformationMap.put(stcd, raPDayRtVoMap);
        });
        return allInformationMap;
    }

    /**
     * 方法描述: 根据站码列表得到替换过权重的雨量站权重列表
     * @author yanglichen
     * @date 2020-07-07 16:23
     * @param stcdList 站码列表
     * @return 替换过权重的雨量站权重列表
     **/
    private List<RaPweight> getRaplaceWeightListByStcdList(List<String> stcdList){
        return raPweightService.getReplacWeightInRRaPweight(stcdList);
    }

    /**
     * 方法描述: 根据雨量站站码列表,和开始降雨的时间,得到按照时间分组的一个雨量站信息列表
     * @author yanglichen
     * @date 2020-07-06 18:56
     * @param stcdList 雨量站站码列表
     * @param rainfallDate 开始降雨的时间
     * @return 按照时间分组的一个雨量站信息列表
     **/
    private List<List<RaPDayRtVo>> getDaysRaPDayRtVoList(List<String> stcdList, Date rainfallDate){
        Date endDay = DateUtils.getNextDateByTimeZone(rainfallDate, 14);
        //得到降雨日之前15天组成的字符串时间格式的列表
        // 例如: [2020-07-06, 2020-07-05, 2020-07-04.......2020-06-25]
        List<String> beforRainfall15DaysList =
                DateUtils.getTimeZoneByStartDayAndEndDay(rainfallDate, endDay);
//        System.err.println("日期为: " + beforRainfall15DaysList);
        //得到按照时间查询出来的雨量站列表
        List<RaPDayRtVo> raPDayRtVoList = getRaPDayRtVoListByStcdListAndTwoDate(stcdList, rainfallDate);
        //构造用于存放是筛选结果的结果集(第一层List表示日期,第二层List表示每天中雨量站的列表)
        List<List<RaPDayRtVo>> raPDayRtVosList = new ArrayList<>();
        //遍历时间列表做筛选
        beforRainfall15DaysList.forEach(beforRainfallDay->{
            //构造二级List 既:用于存放每日雨量站的List
            List<RaPDayRtVo> raPDayRtVos = new ArrayList<>();
            //嵌套遍历
            raPDayRtVoList.forEach(raPDayRtVo -> {
                String dateStr = DateUtils.transformDateTOStr(raPDayRtVo.getDate());
                //如果日期相同,就放进去
                if (beforRainfallDay.equalsIgnoreCase(dateStr)) {
                    raPDayRtVos.add(raPDayRtVo);
                }
            });
            raPDayRtVosList.add(raPDayRtVos);
        });
        return raPDayRtVosList;
    }

    /**
     * 方法描述: 根据站码列表,降雨开始日,得到一个顺序和时间队列相同的包含每日面雨量的列表
     * @author yanglichen
     * @date 2020-07-06 19:08
     * @param stcdList 雨量站列表
     * @param rainfallDate 降雨开始日
     * @return 和时间队列相同的包含每日面雨量的列表
     **/
    private List<Double> getBeforRainfallDaysP(List<String> stcdList, Date rainfallDate){
        //构造结果集
        List<Double> rainfallDaysPList = new ArrayList<>();
        //按日分类的雨量站信息列表
        List<List<RaPDayRtVo>> raPDayRtVosList = getDaysRaPDayRtVoList(stcdList, rainfallDate);
        //得到雨量站权重信息列表
        List<RaPweight> raPweightList = getRaplaceWeightListByStcdList(stcdList);
        //遍历按日分组的雨量站信息
        raPDayRtVosList.forEach(raPDayRtVoList -> {
            Double dayP = getBeforeRainfallDayPByRaPDayRtVoList(raPDayRtVoList, raPweightList);
            rainfallDaysPList.add(dayP);
        });
        return rainfallDaysPList;
    }



    /**
     * 方法描述: 根据传入的雨量站详细信息列表,雨量站权重列表
     * @author yanglichen
     * @date 2020-07-06 19:00
     * @param raPDayRtVoList 某日的雨量站列表
     * @param replaceWeightRaPweightList 替换过权重的雨量站权重列表
     * @return 该日的 面平均雨量
     **/
    private Double getBeforeRainfallDayPByRaPDayRtVoList(List<RaPDayRtVo> raPDayRtVoList, List<RaPweight> replaceWeightRaPweightList){
        Double totalP = 0D;
        Double dayP = 0D;
        //嵌套遍历雨量站详细信息列表
        for (RaPDayRtVo raPDayRtVo : raPDayRtVoList) {
            //遍历雨量站权重信息列表
            for (RaPweight raPweight: replaceWeightRaPweightList) {
                //如果站码相同
                if (raPDayRtVo.getStcd().equalsIgnoreCase(raPweight.getStcd())) {
                    dayP = raPweight.getWeight() * raPDayRtVo.getRf();
                }
            }
            totalP += dayP;
        }
        return totalP;
    }

    /**
     * 方法描述: 雨量站列表中的雨量站的降雨数据进行求和
     * @author yanglichen
     * @date 2020-07-06 10:40
     * @param raPDayRtVoList 雨量站详情列表
     * @return 该雨量站的总降雨量
     **/
    private Double getSumRfByRaPDayrRtVoList(List<RaPDayRtVo> raPDayRtVoList){
        double sumRt = 0;
        for (RaPDayRtVo r :  raPDayRtVoList) {
            sumRt += r.getRf();
        }
        return sumRt;
    }

    /**
     * 方法描述: 根据站码列表,降雨开始日,查询站码列表在降雨开始日前15日的降雨详细信息
     * @author yanglichen
     * @date 2020-07-06 18:42
     * @param stcdList 站码列表
     * @param startDate 降雨开始日
     * @return 站码列表在降雨开始日前15日的降雨详细信息
     **/
    private List<RaPDayRtVo> getRaPDayRtVoListByStcdListAndTwoDate(List<String> stcdList, Date startDate){
        Date endDate = DateUtils.getNextDateByTimeZone(startDate, 14);
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtVoMapper raPDayRtVoMapper = sqlSession.getMapper(RaPDayRtVoMapper.class);
        List<RaPDayRtVo> raPDayRtVoList =
                raPDayRtVoMapper.getRaPDayRtVoListByStcdListAndTwoDate(stcdList, startDate, endDate);
        sqlSession.close();
        return raPDayRtVoList;
    }



}

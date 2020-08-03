package cn.yrec.yhyhsmodelservice.service.impl;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtMapper;
import cn.yrec.yhyhsmodelservice.mappers.RaPHourRtMapper;
import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import cn.yrec.yhyhsmodelservice.model.RaPHourRt;
import cn.yrec.yhyhsmodelservice.service.RaPDayRtService;
import cn.yrec.yhyhsmodelservice.service.RaPHourRtService;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import cn.yrec.yhyhsmodelservice.utils.TransFormUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * @Classname RaPDayRtServiceImpl
 * @Description 实时雨量站业务层接口实现类
 * @Date 2020/06/30 上午 10:54
 * @Created by yanglichen
 */
@Service
public class RaPDayRtServiceImpl implements RaPDayRtService {

//    @Autowired
//    private RaPDayRtMapper raPDayRtMapper;
    @Autowired
    private RaPHourRtMapper raPHourRtMapper;
    @Autowired
    private RaPHourRtService raPHourRtService;

    @Override
    public List<List<RaPDayRt>> getListRaPDayRtListByStringList(
            List<String> stcdList, Double rfThreshold, Date date) {
        //根据站码得到查询的所有雨量站查询的结果集
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtMapper raPDayRtMapper = sqlSession.getMapper(RaPDayRtMapper.class);

        List<RaPDayRt> raPDayRtList = raPDayRtMapper.getHaveRainRaPDayRtListByStcdList(stcdList, rfThreshold, date);
        sqlSession.close();
        //今天的日期
        Date nowDate = new Date();
        String todayStr = DateUtils.transformDateTOStr(nowDate);
        //如果传入的日期不是今天
        if(DateUtils.transformDateTOStr(date).equalsIgnoreCase(todayStr)){
            //程序返回的日期
            String dateStr = DateUtils.transformDateTOStr(judgeTodayRaPDayRtListInDataBase(stcdList, nowDate));
            //判断是不是同一天
            if (dateStr.equalsIgnoreCase(todayStr)) {
                //执行日雨量站的数据融合
                List<RaPHourRt> raPHourRtList = getRapHourRtListByStcdListAndDate(stcdList, DateUtils.transformStrTODate(dateStr),DateUtils.transformStrTODate(todayStr));
                raPHourRtService.mixRaPDayRtListByRaPHourRtList(raPDayRtList,raPHourRtList,stcdList);
            }
        }
        //调用TransFormUtils中的转换方法得到结果
        return TransFormUtils.getListRaPDayRtListByStringList(raPDayRtList, stcdList);
    }

    @Override
    public RaPDayRt getLastRecordRaPDayRtByListRapDayRtAndTimeInterval(
            List<RaPDayRt> raPDayRtList, Integer timeInterval) {
        //遍历雨量站列表
        for (int i = 0; i < raPDayRtList.size(); i++) {
//            System.out.println(raPDayRtList.get(i));
            //当前雨量站记录的时间戳
            Long raPDayRtTime = raPDayRtList.get(i).getDate().getTime();
            //防止下标越界
            if (i+1<=raPDayRtList.size()-1) {
                //该雨量站下一次记录的时间戳
                Long nextRaPDayRtTime = raPDayRtList.get(i+1).getDate().getTime();
                //当前的时间 - 如果下次的时间 >= 时间范围
                if (raPDayRtTime-nextRaPDayRtTime > timeInterval * DateUtils.ONE_DAY_MILLI_SECOND) {
//                    System.err.println(raPDayRtList.get(i).getStcd()+" 名称 "+raPDayRtList.get(i).getStnm()+
//                            "当前: "+DateUtils.transformDateTOStr(raPDayRtList.get(i).getDate())+" 下次: "+DateUtils.transformDateTOStr(raPDayRtList.get(i+1).getDate())+
//                            " 间隔: "+(raPDayRtTime-nextRaPDayRtTime)/(1000L * 60 * 60 * 24)+"天");
                    //返回当前雨量站记录实例
                    return raPDayRtList.get(i);
                }
            }
        }
        return null;
    }

    @Override
    public Date getLastRainDateByListRaPDayRtListAndTimeInterval(
            List<List<RaPDayRt>> raPDayRtsList, Integer timeInterval) {
        //得到由各个雨量站记录的开始降雨时间组成的列表
        List<RaPDayRt> raPDayRtList = getLastRainRaPDayRtList(raPDayRtsList, timeInterval);
        //对列表进行排序(该排序算法,是按照日期降序)
        long minTime = System.currentTimeMillis();
        for (RaPDayRt raPDayRt : raPDayRtList) {
            if (raPDayRt.getDate().getTime() < minTime) {
                minTime = raPDayRt.getDate().getTime();
            }
        }
        Date date = new Date(minTime);
        //列表中最后一个元素所记录的日期，就是降雨开始的日期
        return date;
//        DateUtils.sortRaPDayRtByDate(raPDayRtList);
//        return raPDayRtList.get(0).getDate();
    }

    /**
     * 方法描述: 封装上面三个方法的方法
     * (方便其他类调用, 无任何新增功能)
     * @param stcdList          站码列表
     * @param rainfallThreshold 降雨判断的阈值
     * @param timeInterval      降雨历时
     * @return 降雨开始的日期
     * @author yanglichen
     * @date 2020-07-08 09:55
     **/
    @Override
    public Date getRainfallDate(
            List<String> stcdList, Double rainfallThreshold, Integer timeInterval, Date date) {
        List<List<RaPDayRt>> raPDayRtLists = getListRaPDayRtListByStringList(stcdList, rainfallThreshold, new Date(date.getTime()));
        Date resultDate = getLastRainDateByListRaPDayRtListAndTimeInterval(raPDayRtLists, timeInterval);
//        System.out.println("输出的日期是: "+DateUtils.transformDateTOStr(resultDate));
        return resultDate;
    }

    /**
     * 方法描述: 判断当前时间下站码列表对应的雨量站的信息,是否存在于数据库
     * 存在: 返回当前日期
     * 今天没有: 日期往前推一天
     *
     * @param stcdList 站码列表
     * @param date     日期
     * @return 最后没有数据的日期
     * @author yanglichen
     * @date 2020-07-10 16:02
     **/
    @Override
    public Date judgeTodayRaPDayRtListInDataBase(List<String> stcdList, Date date) {
        //死循环
        while(true){
            //得到字符串格式的日期
            String dateStr = DateUtils.transformDateTOStr(date);
            //判断当前的日期下能否查询到数据
            if (judgeCanFindRaPDayRtListByStcdAndDate(stcdList, date)) {
                /*
                    如果能到这里,说明程序已经通过日期推算找到了数据
                    那么此时返回的是有数据的那天的日期,这个日期之后就都是没有数据的日期
                    按道理来讲,这时候我们直接对日期向后加一天就可以达到效果
                    但是! 如果一开始查询的日期就有结果,那么就无需加一天了
                 */
                if (DateUtils.transformDateTOStr(date).equalsIgnoreCase(dateStr)){
                    return date;
                }else{
                    //加一天,返回
                    date.setTime(date.getTime()+DateUtils.ONE_DAY_MILLI_SECOND);
                    return date;
                }
            }else{
                Long times = DateUtils.transformStrTODate(dateStr).getTime();
                times -= DateUtils.ONE_DAY_MILLI_SECOND;
                date.setTime(times);
            }
        }
    }

    /**
     * 方法描述: 根据站码列表, 降雨起始日, 降雨结束的阈值, 找到降雨的截止日期
     *
     * @param stcdList          站码列表
     * @param rainfallDate      降雨起始日
     * @param rainfallThreshold 降雨的阈值
     * @param timeInterval 降雨间隔时间
     * @return 降雨结束的日期 (7月11还有雨, 7月12没了 :返回7月11日)
     * @author yanglichen
     * @date 2020-07-16 17:32
     **/
//    @Override
    public Date getRainfallEndDayByStcdListAndRainfallDate(
            List<String> stcdList, Date rainfallDate, Double rainfallThreshold, Integer timeInterval) {
        //降雨的结束的时间
        Long dateTimes = rainfallDate.getTime();
        /*死循环
        * 根据降雨的起始日去往后逐日查询数据库
        * 如果有一天查不到数据(返回的计数结果为0)
        * 这一天没有下雨
        * 如果没有下雨的日期计数超过了降雨时间的控制间隔
        * 那么就找到了雨停的那一天
        * 返回降雨日
        * */
        //不下雨的天数的计数
        int noRainfallDayCount = 0;
        Date tempDate = new Date(rainfallDate.getTime());
        while(true){
            Integer count = getRaPDayRtListCountByDate(stcdList, tempDate, rainfallThreshold);
            if (count==0) {
                noRainfallDayCount++;
            }
            dateTimes += DateUtils.ONE_DAY_MILLI_SECOND;
            tempDate.setTime(dateTimes);
            if (noRainfallDayCount >= timeInterval) {
                //判断返回的日期是不是大于现在今天(用于实时计算)
                if (tempDate.getTime()>=System.currentTimeMillis()) {
                    //返回日期大于今天,就将降雨的结束日期设置为今天
                    tempDate.setTime(System.currentTimeMillis());
                }
                return tempDate;
            }
        }
    }


//    @Override
    public Date getLastRainfallDate(List<String> stcdList, Double rainfallThreshold, Integer timeInterval, Date date) {
        //降雨的起始时间
        Long dateTimes = date.getTime();
        /*死循环
         * 根据降雨的起始日去往前逐日查询数据库
         * 如果有一天查不到数据(返回的计数结果为0)
         * 这一天降雨不达标
         * 如果没有下雨的日期计数超过了降雨时间的控制间隔
         * 那么就找到了雨停的那一天
         * 返回降雨日
         * */
        //不下雨的天数的计数
        int noRainfallDayCount = 0;
        Date tempDate = new Date(date.getTime());
        while(true){
            Integer count = getRaPDayRtListCountByDate(stcdList, tempDate, rainfallThreshold);
            if (count==0) {
                noRainfallDayCount++;
            }
            dateTimes -= DateUtils.ONE_DAY_MILLI_SECOND;
            tempDate.setTime(dateTimes);
            if (noRainfallDayCount == timeInterval) {
                return tempDate;
            }
        }
    }

    /**
     * 方法描述: 查询日期小于当前日期 00:00之前,降雨量小于给定值的站码列表的的日期对应的计数项
     * 并将结果封装成Map
     *
     * @param stcdList          站码列表
     * @param rainfallThreshold 降雨阈值
     * @return 日期对应降雨数量的计数项的Map
     * 例如: {2020/7/1 = 30, 2020/7/2 = 25 .....}
     * @author yanglichen
     * @date 2020-07-23 09:28
     **/
    @Override
    public Map<Date, Integer> getRainfallDateCount(List<String> stcdList, Double rainfallThreshold) {
        return getRainfallCountMap(stcdList, rainfallThreshold);
    }

    /**
     * 方法描述: 根据降雨日找到开始下雨的那一天
     * @author yanglichen
     * @date 2020-07-23 09:47
     * @param rainfallDateCount 日期和满足降雨统计的雨量站的计数项的Map
     * @param rainfallEndDate 雨停的日期
     * @param timeInterval 时间间隔
     * @return 开始下雨的那一天
     **/
    @Override
    public Date findRainfallStartDate(Map<Date, Integer> rainfallDateCount, Date rainfallEndDate, Integer timeInterval) {
        long rainfallEndDateTime = rainfallEndDate.getTime();
        Date tempDate = new Date(rainfallEndDateTime);
        int timesCount = 0;
        while(true){
            if(rainfallDateCount.get(tempDate)==null){
                timesCount++;
            }else{
                timesCount=0;
            }
            //日期减少一天
            if(timesCount>=timeInterval){
                break;
            }
            tempDate.setTime(tempDate.getTime()-DateUtils.ONE_DAY_MILLI_SECOND);
        }
        return new Date(tempDate.getTime()+timeInterval*DateUtils.ONE_DAY_MILLI_SECOND);
    }

    /**
     * 方法描述: 根据降雨日找到雨停的那一天
     *
     * @param rainfallDateCount 日期和满足降雨统计的雨量站的计数项的Map
     * @param rainfallEndDate   雨停的日期
     * @param timeInterval      时间间隔
     * @return 开始雨停的那一天
     * @author yanglichen
     * @date 2020-07-23 09:47
     **/
    @Override
    public Date findRainfallEndDate(Map<Date, Integer> rainfallDateCount, Date rainfallEndDate, Integer timeInterval) {
        long rainfallEndDateTime = rainfallEndDate.getTime();
        Date tempDate = new Date(rainfallEndDateTime);
        int timesCount = 0;
        while(true){
            if(rainfallDateCount.get(tempDate)==null){
                timesCount++;
            }else{
                timesCount=0;
            }
            //日期增加一天
            if(timesCount>=timeInterval){
                break;
            }
            tempDate.setTime(tempDate.getTime()+DateUtils.ONE_DAY_MILLI_SECOND);
        }
        return new Date(tempDate.getTime()-timeInterval*DateUtils.ONE_DAY_MILLI_SECOND);
    }

    /**
     * 方法描述: 根据上次降雨的开始日期找到在之前一场降雨的结束日期
     *
     * @param rainfallDateCount 日期和满足降雨统计的雨量站的计数项的Map
     * @param rainfallStartDate 雨开始的日期
     * @return 之前一场降雨的结束日期
     * @author yanglichen
     * @date 2020-07-23 15:41
     **/
    @Override
    public Date getNextRainfallEndDate(Map<Date, Integer> rainfallDateCount, Date rainfallStartDate) {
        Date tempDate = new Date(rainfallStartDate.getTime());
        while (true) {
            tempDate.setTime(tempDate.getTime()-DateUtils.ONE_DAY_MILLI_SECOND);
            if (rainfallDateCount.get(tempDate)!=null) {
                return new Date(tempDate.getTime());
            }
        }
    }

    /**
     * 方法描述: 找到数据中最后的那个日期
     *
     * @param rainfallDateCount 日期和满足降雨统计的雨量站的计数项的Map
     * @return 最后的日期
     * @author yanglichen
     * @date 2020-07-23 15:57
     **/
    @Override
    public Date getEndRainfallDate(Map<Date, Integer> rainfallDateCount) {
        Set<Date> dateSet = rainfallDateCount.keySet();
        List<Date> dateList = new ArrayList<>();
        dateList.addAll(dateSet);
        long minDateTime = Long.MAX_VALUE;
        for (Date date : dateList) {
            if(date!=null){
                if (date.getTime()<minDateTime) {
                    minDateTime = date.getTime();
                }
            }
        }
        return new Date(minDateTime);
    }

    /**
     * 方法描述: 判断查询某个日期下是否根据站码列表能查询到数据
     * @author yanglichen
     * @date 2020-07-10 16:33
     * @param stcdList 站码列表
     * @param date 查询的日期
     * @return 能: true 否: false
     **/
    private Boolean judgeCanFindRaPDayRtListByStcdAndDate(List<String> stcdList, Date date){
        String dateStr = DateUtils.transformDateTOStr(date);
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtMapper raPDayRtMapper = sqlSession.getMapper(RaPDayRtMapper.class);
        List<RaPDayRt> raPDayRtList = raPDayRtMapper
                .getRapDayRtListByStcdListAndNowDate(stcdList,dateStr);
        sqlSession.close();
        return raPDayRtList.size() != 0;
    }


    /**
     * 方法描述: 根据雨量站列表的列表和时间结果得到包含每个雨量站所记录的降雨起始日的列表
     * @author yanglichen
     * @date 2020-07-06 10:06
     * @param raPDayRtsList 雨量站组成的雨量站列表
     * @param timeInterval 时间间隔
     * @return 每个雨量站符合时间间隔条件的降雨起始日
     * [雨量站1降雨起始日, 雨量站2降雨起始日,雨量站3降雨起始日.....]
     **/
    private List<RaPDayRt> getLastRainRaPDayRtList(List<List<RaPDayRt>> raPDayRtsList, Integer timeInterval){
        //构造结果集
        List<RaPDayRt> raPDayRtList = new ArrayList<>();
        //遍历雨量站列表的列表
        raPDayRtsList.forEach(raPDayRts -> {
            //得到雨量站记录列表中符合时间间隔条件的雨量站信息
            RaPDayRt raPDayRt = getLastRecordRaPDayRtByListRapDayRtAndTimeInterval(raPDayRts, timeInterval);
            if (raPDayRt!=null) {
                //添加到结果集中
                raPDayRtList.add(raPDayRt);
            }
        });
        //返回结果集
        return raPDayRtList;
    }

    /**
     * 方法描述: 根据站码列表, 开始日期, 结束日期
     * 找到这段范围内的雨量站逐小时的降雨信息
     *
     * @param stcdList  站码列表
     * @param startDate 开始日期 (日期较旧)
     * @param endDate   结束日期 (日期较新)
     * @return 这段范围内的雨量站逐小时的降雨信息
     * @author yanglichen
     * @date 2020-07-14 10:27
     **/
    private List<RaPHourRt> getRapHourRtListByStcdListAndDate(List<String> stcdList, Date startDate, Date endDate){
        return raPHourRtMapper.getRapHourRtListByStcdListAndDate(stcdList, startDate, endDate);
    }


    /**
     * 方法描述: 根据雨量站的列表,日期,阈值,查询有多少数据
     * @author yanglichen
     * @date 2020-07-16 17:39
     * @param stcdList 站码列表
     * @param date 降雨日期
     * @param rainfallThreshold 降雨的阈值
     * @return 数据条数
     **/
    private Integer getRaPDayRtListCountByDate(List<String> stcdList, Date date, Double rainfallThreshold) {
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtMapper raPDayRtMapper = sqlSession.getMapper(RaPDayRtMapper.class);
        Integer count = raPDayRtMapper.getRaPDayRtListCountByDate(stcdList, date, rainfallThreshold);
        sqlSession.close();
        return count;
    }

    /**
     * 方法描述: 查询站码列表对应的站码在小于某一个日期,降雨量<=降雨阈值的全部信息
     * 初步封装到List<Map<>>中,将在业务层转化为Map<>对象
     * @author yanglichen
     * @date 2020-07-23 09:40
     * @param stcdList 站码列表
     * @param rainfallThreshold 降雨阈值
     * @return 将持久层查询得到的结果(List<Map<>>)转化为Map<>对象
     **/
    private Map<Date, Integer> getRainfallCountMap(List<String> stcdList, Double rainfallThreshold){
        Map<Date, Integer> dateCountMap = new HashMap<>();
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtMapper raPDayRtMapper = sqlSession.getMapper(RaPDayRtMapper.class);
        Date nowDateNoHour = DateUtils.getNotHourDate(new Date());
        //查询数据库得到结果级
        List<Map<String, Object>> mapList = raPDayRtMapper.getDatesCountRainfall(stcdList, nowDateNoHour, rainfallThreshold);
        sqlSession.close();
        mapList.forEach(map->{
            Date date = null;
            Integer count = null;
            /*
             每个List中的元素都是这样的一个Map
             {VALUE=32, KEY=2020-07-18 00:00:00.0}
             */
            for (Map.Entry<String, Object> m : map.entrySet()){
                if("KEY".equalsIgnoreCase(m.getKey())){
                    date = (Date) m.getValue();
                }
                if ("VALUE".equalsIgnoreCase(m.getKey())) {
                    count = Integer.valueOf(m.getValue().toString());
                }
                dateCountMap.put(date, count);
            }
        });
        return dateCountMap;
    }
}

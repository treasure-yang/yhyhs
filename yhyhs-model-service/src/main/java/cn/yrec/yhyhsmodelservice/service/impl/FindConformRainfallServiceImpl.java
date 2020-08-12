package cn.yrec.yhyhsmodelservice.service.impl;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtMapper;
import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import cn.yrec.yhyhsmodelservice.model.RainfallParameters;
import cn.yrec.yhyhsmodelservice.model.RainfallResult;
import cn.yrec.yhyhsmodelservice.service.CalculateSerivce;
import cn.yrec.yhyhsmodelservice.service.FindConformRainfallService;
import cn.yrec.yhyhsmodelservice.service.RaPDayRtService;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataUnit;

import java.util.*;

/**
 * 描述:
 * 寻找备选雨的接口的实现类
 *
 * @createDate: 2020/07/16 上午 9:44
 * @author: yanglichen
 */
@Service
public class FindConformRainfallServiceImpl implements FindConformRainfallService {

    @Autowired
    private RaPDayRtService raPDayRtService;
    @Autowired
    private CalculateSerivce calculateSerivce;

    /**
     * 方法描述: 根据原型雨实例(特征值计算结果)
     * 寻找历史中所有的降雨,将降雨的起始日按照降雨历时,放入Map中
     *
     * @param rainfallResult 原型雨的特征值实例
     * @return 历史中不同历时对应的不同日期
     * 例:{5天:[2020/5/1, 2020/5/20], 6天:[2019/4/11, 2019/6/22].....}
     * @author yanglichen
     * @date 2020-07-16 09:05
     **/
    @Override
    public Map<Integer, List<Date>> findConfirmRainfallTakeTimeAndDateByRainfallResult(RainfallResult rainfallResult) {
        //得到降雨结束的日期
        Date endRainfallDate = new Date(rainfallResult.getEndRainfallDate().getTime());
        //得到降雨的间隔时间
        Integer timeInterval = rainfallResult.getRainfallParameters().getTimeInterval();
        //降雨统计阈值
        Double rainfallThreshold = rainfallResult.getRainfallParameters().getRainfallThreshold();
        //得到站码列表
        List<String> stcdList = rainfallResult.getRainfallParameters().getStcdList();
        return recursiveGetRainfallTakeTime(stcdList, rainfallThreshold, timeInterval, endRainfallDate);
    }

    /**
     * 方法描述: 根据站码列表和某一个开始的日期,查询这些小于等于这个时间的全部信息
     *
     * @param stcdList   站码列表
     * @param begainDate 开始日期
     * @return 小于等于这个时间的全部信息
     * @author yanglichen
     * @date 2020-07-16 10:19
     **/
    private List<RaPDayRt> getRaPDayRtListByBeginDate(List<String> stcdList, Date begainDate) {
        //创建SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = GetSomethingUtils.getSqlSessionFactory();
        //构造SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //构造Mapper对象
        RaPDayRtMapper raPDayRtMapper = sqlSession.getMapper(RaPDayRtMapper.class);
        List<RaPDayRt> raPDayRtList = raPDayRtMapper.getRaPdayRtListByBeginDate(stcdList, begainDate);
        sqlSession.close();
        return raPDayRtList;
    }

    /**
     * 方法描述: 根据原型雨在数据库中寻找所有的备选雨
     * 方法(伪递归: 根据一场雨的开始时间,寻找上一场雨的结束时间,在根据结束时间,找到开始时间,
     * 再根据这次寻找到的上一次雨的开始时间,去继续向上寻找)
     *
     * @param stcdList          站码列表
     * @param rainfallThreshold 降雨阈值
     * @param timeInterval      场次雨判断间隔
     * @param endRainfallDate   某场雨的结束时间
     * @return 历史中不同历时对应的不同日期
     * 例:{5天:[2020/5/1, 2020/5/20], 6天:[2019/4/11, 2019/6/22].....}
     * @author yanglichen
     * @date 2020-07-17 17:02
     **/
    private Map<Integer, List<Date>> recursiveGetRainfallTakeTime(
            List<String> stcdList, Double rainfallThreshold, Integer timeInterval, Date endRainfallDate) {
        //构造结果
        Map<Integer, List<Date>> map = new HashMap<>();
        Map<Date, Integer> dateCountMap = raPDayRtService.getRainfallDateCount(stcdList, rainfallThreshold);
        Date endDate = raPDayRtService.getEndRainfallDate(dateCountMap);
        while (true) {
//            System.err.println("结束的日期为:"+ DateUtils.transformDateTOStr(endDate));
//            System.err.println("正在寻找备选雨");
            //得到降雨开始的日期
            Date beforRainfallStartDate = raPDayRtService
                    .findRainfallStartDate(dateCountMap, endRainfallDate, timeInterval);
            //得到降雨结束的日期
            Date rainfallEndDate = raPDayRtService
                    .findRainfallEndDate(dateCountMap, beforRainfallStartDate, timeInterval);
            //得到降雨历时
            Integer rainfallTakeTimes = DateUtils.getTimesByStartDateAndEndDate(rainfallEndDate, beforRainfallStartDate);
            //存放一轮数据
            if (map.get(rainfallTakeTimes) != null) {
                map.get(rainfallTakeTimes).add(beforRainfallStartDate);
            } else {
                List<Date> dateList = new ArrayList<>();
                dateList.add(beforRainfallStartDate);
                map.put(rainfallTakeTimes, dateList);
            }
            //替换时间,得到下一次降雨的结束日期
            Date nextRainfallEndDate = raPDayRtService.getNextRainfallEndDate(dateCountMap, beforRainfallStartDate);
            //跳出条件:到头了
            if (nextRainfallEndDate.getTime() == endDate.getTime()) {
                break;
            }
            endRainfallDate.setTime(nextRainfallEndDate.getTime());
        }
//        System.err.println("map中的数据长度为: "+map.size());
        return map;
    }

    /**
     * 方法描述: 通过历时和降雨起始日组成的Map得到
     *
     * @param rainfallMap    历时和降雨起始日组成的Map
     * @param rainfallResult 原型雨实例
     * @return 10场次的备选雨
     * @author yanglichen
     * @date 2020-07-20 09:12
     **/
    @Override
    public List<RainfallResult> getConformRainfallSetByRainfallMap(
            RainfallResult rainfallResult, Map<Integer, List<Date>> rainfallMap) {
        //得到去重的Set集合
        Set<RainfallResult> rainfallResultSet = getConformRainfallByTakeTimes(rainfallResult, rainfallMap);
        //构造结果集
        //添加到List中(方便后续遍历)
        return new ArrayList<>(rainfallResultSet);
    }

    /**
     * 方法描述: 计算总降雨量,并添加到备选雨列表中
     *
     * @param rainfallResultList 备选雨列表
     * @return 增加过总降雨量的备选雨实例列表
     * @author yanglichen
     * @date 2020-07-20 11:02
     **/
    @Override
    public List<RainfallResult> calculateTotalRainfallQ(List<RainfallResult> rainfallResultList) {
        List<RainfallResult> rainfallResults = new ArrayList<>();
        //遍历备选雨特征值计算结果列表
        for (RainfallResult rainfallResult : rainfallResultList) {
            //降雨起始日
            Date rainfallEndDate = rainfallResult.getEndRainfallDate();
            //更新参数
            rainfallResult.getRainfallParameters().setCalDate(rainfallEndDate.getTime());
            //调用计算降雨特征值的方法计算特征值
            RainfallResult result = calculateSerivce.getRainfallResultByStcdList(rainfallResult.getRainfallParameters());
            rainfallResults.add(result);
        }
        return rainfallResults;
    }


    /**
     * 方法描述: 根据原型雨的总降雨量对备选雨列表进行排序
     * 如果根据历时筛选出的备选雨的数量超过了10场,则返回10场次
     * 小于10场次则全部返回
     *
     * @param rainfallResultList   备选雨列表
     * @param originRainfallresult 原型雨实例
     * @return 按照降雨量和原型雨最接近的顺序返回的10场(最多)备选雨列表
     * @author yanglichen
     * @date 2020-07-20 15:55
     **/
    @Override
    public List<RainfallResult> sortRainfallResultByRainfallQ(List<RainfallResult> rainfallResultList, RainfallResult originRainfallresult) {
        //得到原型雨的总降雨量
        Double originTotalRainfallQ = originRainfallresult.getTotalRainfallQ();
        //构造结果集
        List<RainfallResult> rainfallResults = new ArrayList<>();
        //依据对原型雨雨量差距最小的原则进行排序
        //得到循环的次数
        int times = rainfallResultList.size();
        for (int i = 0; i < times; i++) {
            //初始化最小值
            Double minRainfallQ = Double.MAX_VALUE;
            //嵌套遍历(寻找真的最小值)
            for (RainfallResult rainfallResult : rainfallResultList) {
                //得到最小值
                Double tempAbsQ = Math.abs(originTotalRainfallQ - rainfallResult.getTotalRainfallQ());
                if (tempAbsQ < minRainfallQ) {
                    minRainfallQ = tempAbsQ;
                }
            }
            //从这个循环出来的时候,已经找到了最小的值,但是还不知道这个值是谁的
            //初始化记录值
            //再次遍历,寻找这个值的主人
            for (RainfallResult rainfallResult : rainfallResultList) {
                Double tempAbsQ = Math.abs(originTotalRainfallQ - rainfallResult.getTotalRainfallQ());
                //找到了
                if (tempAbsQ.equals(minRainfallQ)) {
                    //添加到集合中
                    rainfallResults.add(rainfallResult);
                    break;
                }
            }
            //构造迭代器对数据进行删除
            Iterator<RainfallResult> rainfallResultIterator = rainfallResultList.iterator();
            while (rainfallResultIterator.hasNext()) {
                if (Math.abs(originTotalRainfallQ - rainfallResultIterator.next().getTotalRainfallQ()) == minRainfallQ) {
                    rainfallResultIterator.remove();
                }
            }
            //控制是否10场次降雨
            if (rainfallResults.size() >= 10) {
                break;
            }
        }
        return rainfallResults;
    }

    /**
     * 方法描述: 计算所有的备选雨的特征值
     *
     * @param rainfallResultList 备选雨列表
     * @return 备选雨列表
     * @author yanglichen
     * @date 2020-07-27 10:36
     **/
    @Override
    public List<RainfallResult> calAllCulate(List<RainfallResult> rainfallResultList) {
        //构造新的结果集
        List<RainfallResult> rainfallResults = new ArrayList<>();
        //遍历备选雨特征值计算结果列表
        for (RainfallResult rainfallResult : rainfallResultList) {
            //降雨结束的日期
            Date rainfallEndDate = rainfallResult.getEndRainfallDate();
            //更新参数
            rainfallResult.getRainfallParameters().setCalDate(rainfallEndDate.getTime());
            //调用计算降雨特征值的方法计算特征值
            RainfallResult result = calculateSerivce.getRainfallResult(rainfallResult.getRainfallParameters());
            rainfallResults.add(result);
        }
        return rainfallResults;
    }

    /**
     * 方法描述: 根据传入的总降雨量的容差对备选雨进行筛选
     *
     * @param rainfallResultList 备选雨列表
     * @param prototypeRainfall  原型雨
     * @param rainfallQTolerance 降雨量容差
     *                           (相对于原型雨来说,如果传入的是0.2,假定原型雨的总雨量是100,则80--120都是可接受的误差范围内)
     * @return 得到根据降雨量容差进行筛选得到的结果
     * 如果筛选前的数量<=5, 或者 (筛选后的结构<=5 且 筛选前结果 <=5),则不进行筛选了
     * @author yanglichen
     * @date 2020-07-29 08:15
     **/
    @Override
    public List<RainfallResult> filterRainfallResultByRainfallQ(
            List<RainfallResult> rainfallResultList, RainfallResult prototypeRainfall, Double rainfallQTolerance) {
        //构造结果集
        List<RainfallResult> rainfallResults = new ArrayList<>();
        //如果待过滤的备选雨的数量 <= 5, 那就不过滤了
        if (rainfallResultList.size() <= 5) {
            rainfallResults.addAll(rainfallResultList);
            System.err.println("待筛选的结果不够5条, 没有过滤");
            return rainfallResults;
        }
        //得到原型雨的降雨量
        Double prototypeRainfallQ = prototypeRainfall.getTotalRainfallQ();
        //得到最大容差范围 (原型雨*(1+容差))
        Double maxQ =
                prototypeRainfallQ * (1 + rainfallQTolerance);
        //得到最小容差范围 (原型雨*(1-容差))
        Double minQ =
                prototypeRainfallQ * (1 - rainfallQTolerance);
        //遍历待筛选的备选雨列表
        for (RainfallResult result : rainfallResultList) {
            Double resultQ = result.getTotalRainfallQ();
            //如果总降雨量 >= 最小值 && 总降雨量 <= 最大值.就添加到列表中
            if (resultQ >= minQ && resultQ <= maxQ) {
                System.out.println("正在筛选: " + result);
                rainfallResults.add(result);
            }
        }
        //判断过滤后的列表的数量 如果 筛选后的结构<=5 且 筛选前结果 <=5,则不进行筛选了
        if (rainfallResults.size() <= 5 && rainfallResultList.size() <= 5) {
            System.err.println("筛选后的结果不够5条, 没有过滤");
            rainfallResults.addAll(rainfallResultList);
        }
        return rainfallResults;
    }

    /**
     * 方法描述: 根据降雨历时和总降雨量寻找备选雨
     * (如果根据给定的条件找出的降雨场次数 < 20场次, 首先只增加历时范围(最大到3)
     * 如果历时增加到了3,备选雨场次数 < 20 ,增加总雨量的控制参数(每次0.05 最大增加到0.5)
     * 以上过程中只要当备选雨场次数 >= 20 ,则随时结束循环
     *
     * @param rainfallResultList 备选雨列表
     * @param prototypeRainfall  原型雨实例
     * @param rainfallTaketimes  历时范围
     * @param rainfallQTolerance 降雨量容差
     * @return 根据降雨相似度排序后的备选雨列表(只要前5条)
     * @author yanglichen
     * @date 2020-08-11 08:06
     **/
    @Override
    public List<RainfallResult> filterRainfallResultByTaketimesAndRainfallQ(
            List<RainfallResult> rainfallResultList, RainfallResult prototypeRainfall,
            int rainfallTaketimes, double rainfallQTolerance) {
        //构造结果集
        List<RainfallResult> resultList = new ArrayList<>();
        //得到历时和降雨日期对应的Map
        Map<Integer, List<Date>> mapByRainfallTaketimeAndRainfallDate =
                findConfirmRainfallTakeTimeAndDateByRainfallResult(prototypeRainfall);
        while (true) {
            //得到备选雨列表
            List<RainfallResult> rainfallResults =
                    getRainfallListByRainfallTakeTimeAndRainfallQ(
                            mapByRainfallTaketimeAndRainfallDate, prototypeRainfall,
                            rainfallTaketimes, rainfallQTolerance);
            //备选雨列表添加到List中
            if (resultList.size()==0) {
                resultList.addAll(rainfallResults);
            }
            //遍历得到的备选雨列表
            for (RainfallResult rainfallResult : rainfallResults) {
                //得到降雨日的毫秒数
                long startDate = rainfallResult.getRainfallDate().getTime();
                //设置标志量
                boolean flage = true;
                //遍历结果集中的所有元素
                for (RainfallResult result : resultList) {
                    //得到降雨日的毫秒数
                    long resultStartDate = result.getRainfallDate().getTime();
                    //判断是否相等
                    if (resultStartDate==startDate) {
                        flage = false;
                        break;
                    }
                }
                if (flage) {
                    resultList.add(rainfallResult);
                }
            }

            //判断set中的数量的大小是否<20
            if (resultList.size() < 20) {
                //判断历时范围是否<3
                if (rainfallTaketimes < 3) {
                    //对历时进行加一
                    rainfallTaketimes++;
                } else {
                    //对雨量控制参数+0.05
                    rainfallQTolerance += 0.05;
                }
                //判断小于20场中的跳出情况(历时范围达到3, 雨量容差达到0.5)
                if (rainfallTaketimes >= 3 && rainfallQTolerance >= 0.5) {
                    break;
                }
            }else{
                //如果数量已经达到了20就直接跳出
                break;
            }
        }
        /* 当程序运行到这里set中已经有了一定的数据
        * 此时数据有两种情况
        * 1.条件没跑完就已经找到了20条
        * 2.条件跑到头了,但是还没有20条,可能只有10条甚至更少
        * 即将对set中的所有数据进行相似值的计算,并输出到List中
        */
        //根据原型雨对所有备选雨计算相似度
        List<RainfallResult> rainfallResults =
                calculateSerivce.sortRainfallResultListByresesmblance(resultList, prototypeRainfall);
        //得到最低的相似度(最后的哪一个就是相似度最低的)
        double minLookLike = rainfallResults.get(rainfallResults.size()-1).getResemblance();
        //遍历计算相似度
        for (RainfallResult rainfallResult : rainfallResults) {
            double resemblance =  rainfallResult.getResemblance();
            double lookLikePoint = (1-(resemblance/minLookLike))*100;
            System.err.println("相似值为: "+ resemblance+ " 相似度为: "+lookLikePoint);
            //赋值相似度
            rainfallResult.setLookLikePoint(lookLikePoint);
        }
        //给最后一个删了
        rainfallResults.remove(rainfallResults.size()-1);
        //只要前10场次
        List<RainfallResult> finalResult = new ArrayList<>();
        //判断总场次,如果不足10场次直接返回
        if (rainfallResults.size()<=10) {
            return rainfallResults;
        }
        //比10场次多就要前10场次
        for (int i = 0; i < 10; i++) {
            finalResult.add(rainfallResults.get(i));
        }
        return finalResult;
    }

    /**
     * 方法描述: 根据选定的原型雨寻找备选雨列表
     *
     * @param prototypeRainfall 原型雨列表
     * @return 备选雨列表
     * @author yanglichen
     * @date 2020-08-07 09:10
     **/
    @Override
    public List<RainfallResult> findRainfallListByRainfallResult(RainfallResult prototypeRainfall) {
        //得到历时和日期的映射Map
        Map<Integer, List<Date>> resultMap =
                findConfirmRainfallTakeTimeAndDateByRainfallResult(prototypeRainfall);
        //得到备选雨实例列表
        List<RainfallResult> rainfallResults =
                getConformRainfallSetByRainfallMap(prototypeRainfall, resultMap);

        //计算降雨特征值
        List<RainfallResult> resultList = calAllCulate(rainfallResults);

        //得到降雨量容差值
        Double rainfallQTolerance = prototypeRainfall.getRainfallParameters().getRainfallQTolerance();
        //得到历时范围
        int rainfallTaketims =  prototypeRainfall.getRainfallParameters().getTimeInterval();

        //根据总降雨量进行过滤
        List<RainfallResult> filterQResultList =
//                filterRainfallResultByRainfallQ(resultList, prototypeRainfall, rainfallQTolerance);
                    filterRainfallResultByTaketimesAndRainfallQ(resultList, prototypeRainfall, rainfallTaketims, rainfallQTolerance);
        //得到按照相似性排序的备选雨列表
//        return calculateSerivce.sortRainfallResultListByresesmblance(filterQResultList, prototypeRainfall);
        return filterQResultList;
    }


    /**
     * 方法描述: 根据给定的降雨历时,降雨总雨量范围得到降雨列表
     * @author yanglichen
     * @date 2020-08-11 08:23
     * @param prototypeRainfall  原型雨实例
     * @param rainfallTaketimes  历时范围
     * @param rainfallQTolerance 降雨量容差
     * @return 备选雨列表
     **/
    private List<RainfallResult> getRainfallListByRainfallTakeTimeAndRainfallQ(
            Map<Integer, List<Date>> mapByRainfallTaketimeAndRainfallDate, RainfallResult prototypeRainfall,
            int rainfallTaketimes, double rainfallQTolerance) {
        //原型雨的降雨降雨参数
        RainfallParameters parameter = prototypeRainfall.getRainfallParameters();
        //得到原型雨的历时
        int prototypeRainfallTaketimes = prototypeRainfall.getRainfallTakeTime();
        //根据给定的降雨历时范围和原型雨的历时构造一个数组
        int[] rainfallTaketimeArray =
                getRainfallTaketimesToleranceArray(prototypeRainfallTaketimes, rainfallTaketimes);
        List<RainfallResult> taketimesResult = new ArrayList<>();
        //根据给定的降雨历时范围和余量容差,直接进行过滤
        for (int taketime : rainfallTaketimeArray) {
            //根据历时得到降雨开始日期的列表
            List<Date> rainfallStartDateList = mapByRainfallTaketimeAndRainfallDate.get(taketime);
            if (rainfallStartDateList != null) {
                //遍历日期列表
                for (Date rainfallStartDate : rainfallStartDateList) {
                    //构造备选雨实例
                    RainfallResult rainfallResult = new RainfallResult();
                    //封装降雨参数
                    rainfallResult.setRainfallParameters(parameter);
                    //封装降雨起始日
                    rainfallResult.setRainfallDate(rainfallStartDate);
                    //封装降雨历时
                    rainfallResult.setRainfallTakeTime(taketime);
                    //根据降雨起始日和历时得到降雨截止日并封装
                    Date endRainfallDate = DateUtils.getAfterDateByTimeZone(rainfallStartDate, taketime);
                    rainfallResult.setEndRainfallDate(endRainfallDate);
                    //添加到历时的列表中
                    taketimesResult.add(rainfallResult);
                }
            }
        }
        //为根据历时筛选出来的列表计算总降雨量
        List<RainfallResult> rainfallResults = calAllCulate(taketimesResult);
        //得到备选雨的总降雨量
        double rainfallQ = prototypeRainfall.getTotalRainfallQ();
        //得到根据总雨量阈值得到的总雨量的最小值
        double rainfallMinQ = rainfallQ * (1 - rainfallQTolerance);
        //得到根据总雨量阈值得到的总雨量的最大值
        double rainfallMaxQ = rainfallQ * (1 + rainfallQTolerance);
        //构造最终结果集
        List<RainfallResult> finalRainfallResultList = new ArrayList<>();
        //遍历根据历时筛选出来的列表,将雨量不满足的备选雨进行过滤
        for (RainfallResult rainfallResult : rainfallResults) {
            //得到备选雨的降雨量
            double rainfallResultQ = rainfallResult.getTotalRainfallQ();
            //如果 备选雨雨量 >= 最小值 并且 备选雨雨量 <= 最大值
            if (rainfallResultQ >= rainfallMinQ && rainfallResultQ <= rainfallMaxQ) {
                finalRainfallResultList.add(rainfallResult);
            }
        }
        return finalRainfallResultList;
    }



    /**
     * 方法描述: 通过降雨历时的值,重Map中找到符合降雨历时范围的数据
     * 并存放在Set<RainfallResult> 用Set是为了防止数据重复
     *
     * @param rainfallResult  原型雨实例
     * @param rainfallDaysMap 备选雨的降雨历时 & 备选雨的降雨起止日期
     * @return 符合降雨历时的Set集合
     * @author yanglichen
     * @date 2020-07-20 07:59
     **/
    private Set<RainfallResult> getConformRainfallByTakeTimes(RainfallResult rainfallResult, Map<Integer, List<Date>> rainfallDaysMap) {
        //降雨参数
        RainfallParameters parameter = rainfallResult.getRainfallParameters();
        //降雨历时
        Integer rainfallTaketime = rainfallResult.getRainfallTakeTime();
        //降雨历时容差
        Integer rainfallTolerance = rainfallResult.getRainfallParameters().getRainfallTolerance();
        //构造结果集
        Set<RainfallResult> rainfallResultSet = new LinkedHashSet<>();
        //得到由历时容差值组成的数组
        int[] indexArray = getRainfallTaketimesToleranceArray(rainfallTaketime, rainfallTolerance);
        System.out.println(Arrays.toString(indexArray));
        //遍历容差值,得到数据
        for (int value : indexArray) {
            List<Date> tempList = rainfallDaysMap.get(value);
            if (tempList != null) {
                //遍历历时中对应的数据
                for (Date rainfallDate : tempList) {
                    RainfallResult result = new RainfallResult();
                    //封装降雨参数
                    result.setRainfallParameters(parameter);
                    //降雨开始日
                    result.setRainfallDate(rainfallDate);
                    //降雨历时
                    result.setRainfallTakeTime(value);
                    //降雨结束日
                    result.setEndRainfallDate(DateUtils.getAfterDateByTimeZone(rainfallDate, value));
                    //封装降雨时间间隔
                    rainfallResultSet.add(result);
                }
            }
               /*根据历时容差寻找降雨的时候,
                 不做结果数量的限制,
                 有多少,存多少
                 后续根据雨量进行二次筛选
                */
        }
        return rainfallResultSet;
    }

    /**
     * 方法描述: 根据降雨历时,时间容差范围,得到一个由这些历时容差范围组成的数组
     * 并且: 数组的下标随着范围的增大而增大
     *
     * @param rainfallTaketime  降雨历时
     * @param rainfallTolerance 降雨历时容差范围
     * @return 由这些历时容差范围组成的数组
     * 如: [5, 4, 6, 7, 9]
     * @author yanglichen
     * @date 2020-07-20 08:37
     **/
    private int[] getRainfallTaketimesToleranceArray(Integer rainfallTaketime, Integer rainfallTolerance) {
        //数组长度(1+容差*2)
        int[] indexArray = new int[1 + rainfallTolerance * 2];
        //向数组中存放元素
        indexArray[0] = rainfallTaketime;
        for (int i = 1; i < indexArray.length; i++) {
            //偶数就添加增大的容差
            if (i % 2 == 0) {
                indexArray[i] = rainfallTaketime + (i / 2);
            } else {
                //奇数添加减少的容差
                indexArray[i] = rainfallTaketime - (i / 2) - 1;
            }
        }
        return indexArray;
    }

}

package cn.yrec.yhyhsmodelservice.utils;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtVoMapper;
import cn.yrec.yhyhsmodelservice.service.RaPDayRtVoService;
import cn.yrec.yhyhsmodelservice.vo.RaPDayRtVo;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 该类主要为工厂类,为全局提供一些对象
 *  例如:测试用的站码列表,MyBatis缓存用到的SqlSession等
 *      例如:站码列表, 雨量站列表 等
 * @Date 2020/07/01 上午 10:28
 * @Created by yanglichen
 */
@Component
public class GetSomethingUtils{


    @Autowired
    private RaPDayRtVoMapper raPDayRtVoMapper;

    @Autowired
    private RaPDayRtVoService raPDayRtVoService;

    private static GetSomethingUtils getSomethingUtils;

    private static SqlSessionFactory sqlSessionFactory = null;

    private static Map<String, Map<Date, RaPDayRtVo>> allInfomationMap = null;


    /**
     * 方法描述: 获得站码列表
     * @author yanglichen
     * @date 2020-07-06 10:47
     * @return 雨量站站码列表
     **/
    public static List<String> getStcdList(){
        List<String> stcdList = new ArrayList<>();
        //20个站
        stcdList.add("40721200");
        stcdList.add("41631000");
        stcdList.add("41227850");
        stcdList.add("40923350");
        stcdList.add("40723000");
        stcdList.add("41640000");
        stcdList.add("41228800");
        stcdList.add("40927750");
        stcdList.add("40724200");
        stcdList.add("40557300");
        stcdList.add("41651400");
        stcdList.add("41230050");
        stcdList.add("41036650");
        stcdList.add("40726600");
        stcdList.add("40624800");
        stcdList.add("41721400");
        stcdList.add("41231350");
        stcdList.add("41121400");
        stcdList.add("40729000");
        stcdList.add("40627300");
        return stcdList;
    }

    /**
     * 方法描述: 得到窟野河的用于测试的站码
     * @author yanglichen
     * @date 2020-07-29 15:12
     * @return 窟野河测试站码
     **/
    public static List<String> getTestStcdList(){
        List<String> stcdList = new ArrayList<>();
        stcdList.add("40721200");
        stcdList.add("40723000");
        stcdList.add("40723600");
        stcdList.add("40724200");
        stcdList.add("40724800");
        stcdList.add("40725400");
        stcdList.add("40726000");
        stcdList.add("40726600");
        stcdList.add("40727200");
        stcdList.add("40728400");
        stcdList.add("40729000");
        stcdList.add("40729600");
        stcdList.add("40730200");
        stcdList.add("40730800");
        stcdList.add("40731400");
        stcdList.add("40732000");
        stcdList.add("40733200");
        stcdList.add("40734400");
        stcdList.add("40735000");
        stcdList.add("40735600");
        stcdList.add("40736200");
        stcdList.add("40736800");
        stcdList.add("40737400");
        stcdList.add("40738600");
        stcdList.add("40739800");
        stcdList.add("40740400");
        stcdList.add("40741000");
        stcdList.add("40741600");
        stcdList.add("40742200");
        stcdList.add("40743400");
        stcdList.add("40744000");
        stcdList.add("40744600");
        stcdList.add("40745800");
        stcdList.add("40750600");
        stcdList.add("40752400");
        stcdList.add("40753000");
        stcdList.add("40753600");
        stcdList.add("40754200");

        return stcdList;
    }

    /**
     * 方法描述: 得到100个站码列表
     * @author yanglichen
     * @date 2020-07-07 08:58
     * @return
     **/
    public static List<String> get100Stcd(){
        List<String> stcdList = new ArrayList<>();
        stcdList.add("40721200");
        stcdList.add("41631000");
        stcdList.add("41227850");
        stcdList.add("40923350");
        stcdList.add("40723000");
        stcdList.add("41640000");
        stcdList.add("41228800");
        stcdList.add("40927750");
        stcdList.add("40724200");
        stcdList.add("40557300");
        stcdList.add("41651400");
        stcdList.add("41230050");
        stcdList.add("41036650");
        stcdList.add("40726600");
        stcdList.add("40624800");
        stcdList.add("41721400");
        stcdList.add("41231350");
        stcdList.add("41121400");
        stcdList.add("40729000");
        stcdList.add("40627300");
        stcdList.add("41729000");
        stcdList.add("41234600");
        stcdList.add("41124850");
        stcdList.add("40732000");
        stcdList.add("40628700");
        stcdList.add("41737200");
        stcdList.add("41324200");
        stcdList.add("41126250");
        stcdList.add("40735600");
        stcdList.add("40633000");
        stcdList.add("41129150");
        stcdList.add("40740400");
        stcdList.add("40634950");
        stcdList.add("41424100");
        stcdList.add("41137300");
        stcdList.add("40822700");
        stcdList.add("40744000");
        stcdList.add("40636650");
        stcdList.add("41426850");
        stcdList.add("41221900");
        stcdList.add("40825200");
        stcdList.add("40745800");
        stcdList.add("40638500");
        stcdList.add("41520800");
        stcdList.add("41223700");
        stcdList.add("40826400");
        stcdList.add("40753600");
        stcdList.add("40639950");
        stcdList.add("41622200");
        stcdList.add("41224800");
        stcdList.add("40920350");
        stcdList.add("40820400");
        stcdList.add("41227100");
        stcdList.add("40920900");
        stcdList.add("40820800");
        stcdList.add("40721200");
        stcdList.add("41636000");
        stcdList.add("41228250");
        stcdList.add("40926700");
        stcdList.add("40723600");
        stcdList.add("40546450");
        stcdList.add("41646400");
        stcdList.add("41229450");
        stcdList.add("40928400");
        stcdList.add("40725400");
        stcdList.add("40622350");
        stcdList.add("41654200");
        stcdList.add("41230450");
        stcdList.add("41120900");
        stcdList.add("40727200");
        stcdList.add("40625980");
        stcdList.add("41726000");
        stcdList.add("41232200");
        stcdList.add("41122150");
        stcdList.add("40730200");
        stcdList.add("40627700");
        stcdList.add("41730800");
        stcdList.add("41321150");
        stcdList.add("41125650");
        stcdList.add("40734400");
        stcdList.add("40629150");
        stcdList.add("41420950");
        stcdList.add("41126850");
        stcdList.add("40737400");
        stcdList.add("40633450");
        stcdList.add("41422950");
        stcdList.add("41134800");
        stcdList.add("40821600");
        stcdList.add("40742200");
        stcdList.add("40635600");
        stcdList.add("41425050");
        stcdList.add("41139250");
        stcdList.add("40823250");
        stcdList.add("40744600");
        stcdList.add("40637900");
        stcdList.add("41427600");
        stcdList.add("41223100");
        stcdList.add("40825650");
        stcdList.add("40752400");
        stcdList.add("40638900");
        return stcdList;
    }

    /**
     * 方法描述: 得到200个站码列表
     * @author yanglichen
     * @date 2020-07-07 08:51
     * @return
     **/
    public static List<String> get200Stcd(){
        List<String> stcdList = new ArrayList<>();
        stcdList.add("40457800");
        stcdList.add("40527150");
        stcdList.add("40538470");
        stcdList.add("40543200");
        stcdList.add("40546050");
        stcdList.add("40546450");
        stcdList.add("40547500");
        stcdList.add("40549000");
        stcdList.add("40553150");
        stcdList.add("40553300");
        stcdList.add("40554500");
        stcdList.add("40555050");
        stcdList.add("40556800");
        stcdList.add("40557650");
        stcdList.add("40558000");
        stcdList.add("40558500");
        stcdList.add("40559300");
        stcdList.add("40621100");
        stcdList.add("40621750");
        stcdList.add("40622350");
        stcdList.add("40622850");
        stcdList.add("40623750");
        stcdList.add("40623800");
        stcdList.add("40623900");
        stcdList.add("40624150");
        stcdList.add("40624200");
        stcdList.add("40624800");
        stcdList.add("40624850");
        stcdList.add("40624900");
        stcdList.add("40625200");
        stcdList.add("40625850");
        stcdList.add("40625950");
        stcdList.add("40625980");
        stcdList.add("40626000");
        stcdList.add("40626050");
        stcdList.add("40626600");
        stcdList.add("40626800");
        stcdList.add("40626900");
        stcdList.add("40627100");
        stcdList.add("40627150");
        stcdList.add("40627300");
        stcdList.add("40627350");
        stcdList.add("40627400");
        stcdList.add("40627450");
        stcdList.add("40627500");
        stcdList.add("40627550");
        stcdList.add("40627650");
        stcdList.add("40627700");
        stcdList.add("40627950");
        stcdList.add("40628000");
        stcdList.add("40628100");
        stcdList.add("40628200");
        stcdList.add("40628250");
        stcdList.add("40628350");
        stcdList.add("40628550");
        stcdList.add("40628650");
        stcdList.add("40628700");
        stcdList.add("40628750");
        stcdList.add("40628800");
        stcdList.add("40628850");
        stcdList.add("40628900");
        stcdList.add("40628950");
        stcdList.add("40629000");
        stcdList.add("40629150");
        stcdList.add("40629200");
        stcdList.add("40629250");
        stcdList.add("40629500");
        stcdList.add("40630050");
        stcdList.add("40632250");
        stcdList.add("40632450");
        stcdList.add("40632900");
        stcdList.add("40633000");
        stcdList.add("40633050");
        stcdList.add("40633200");
        stcdList.add("40633250");
        stcdList.add("40633300");
        stcdList.add("40633400");
        stcdList.add("40633450");
        stcdList.add("40633500");
        stcdList.add("40633600");
        stcdList.add("40633700");
        stcdList.add("40633800");
        stcdList.add("40634200");
        stcdList.add("40634750");
        stcdList.add("40634800");
        stcdList.add("40634850");
        stcdList.add("40634950");
        stcdList.add("40635000");
        stcdList.add("40635050");
        stcdList.add("40635100");
        stcdList.add("40635250");
        stcdList.add("40635350");
        stcdList.add("40635450");
        stcdList.add("40635600");
        stcdList.add("40635750");
        stcdList.add("40636000");
        stcdList.add("40636050");
        stcdList.add("40636200");
        stcdList.add("40636250");
        stcdList.add("40636300");
        stcdList.add("40636350");
        stcdList.add("40636450");
        stcdList.add("40636650");
        stcdList.add("40636950");
        stcdList.add("40637000");
        stcdList.add("40637050");
        stcdList.add("40637400");
        stcdList.add("40637650");
        stcdList.add("40637750");
        stcdList.add("40637900");
        stcdList.add("40637950");
        stcdList.add("40638050");
        stcdList.add("40638100");
        stcdList.add("40638150");
        stcdList.add("40638200");
        stcdList.add("40638300");
        stcdList.add("40638400");
        stcdList.add("40638450");
        stcdList.add("40638500");
        stcdList.add("40638550");
        stcdList.add("40638600");
        stcdList.add("40638650");
        stcdList.add("40638700");
        stcdList.add("40638750");
        stcdList.add("40638850");
        stcdList.add("40638900");
        stcdList.add("40639000");
        stcdList.add("40639050");
        stcdList.add("40639200");
        stcdList.add("40639600");
        stcdList.add("40639700");
        stcdList.add("40639750");
        stcdList.add("40639800");
        stcdList.add("40639900");
        stcdList.add("40639950");
        stcdList.add("40640050");
        stcdList.add("40640200");
        stcdList.add("40640250");
        stcdList.add("40640300");
        stcdList.add("40640350");
        stcdList.add("40640550");
        stcdList.add("40640650");
        stcdList.add("40641000");
        stcdList.add("40641050");
        stcdList.add("40720600");
        stcdList.add("40721200");
        stcdList.add("40723000");
        stcdList.add("40723600");
        stcdList.add("40724200");
        stcdList.add("40724800");
        stcdList.add("40725400");
        stcdList.add("40726000");
        stcdList.add("40726600");
        stcdList.add("40727200");
        stcdList.add("40728400");
        stcdList.add("40729000");
        stcdList.add("40729600");
        stcdList.add("40730200");
        stcdList.add("40730800");
        stcdList.add("40731400");
        stcdList.add("40732000");
        stcdList.add("40733200");
        stcdList.add("40734400");
        stcdList.add("40735000");
        stcdList.add("40735600");
        stcdList.add("40736200");
        stcdList.add("40736800");
        stcdList.add("40737400");
        stcdList.add("40738600");
        stcdList.add("40739800");
        stcdList.add("40740400");
        stcdList.add("40741000");
        stcdList.add("40741600");
        stcdList.add("40742200");
        stcdList.add("40743400");
        stcdList.add("40744000");
        stcdList.add("40744600");
        stcdList.add("40745800");
        stcdList.add("40750600");
        stcdList.add("40752400");
        stcdList.add("40753000");
        stcdList.add("40753600");
        stcdList.add("40754200");
        stcdList.add("40756600");
        stcdList.add("40820050");
        stcdList.add("40820100");
        stcdList.add("40820200");
        stcdList.add("40820300");
        stcdList.add("40820400");
        stcdList.add("40820450");
        stcdList.add("40820500");
        stcdList.add("40820550");
        stcdList.add("40820600");
        stcdList.add("40820700");
        stcdList.add("40820800");
        stcdList.add("40820900");
        stcdList.add("40821000");
        stcdList.add("40821150");
        return stcdList;
    }

    /**
     * 方法描述: 根据雨量站列表,和开始下雨的时间,得到在此期间产生降雨的雨量站的详细信息
     * @author yanglichen
     * @date 2020-07-06 10:49
     * @param stcdList 站码列表
     * @param rainfallDateStr 开始下雨的时间
     * @return 此期间产生降雨的雨量站的详细信息
     **/
    public static List<RaPDayRtVo> getRaPDayRtVoListByStcdListAndRainfallDate(List<String> stcdList, String rainfallDateStr, Date calDate){
        return getSomethingUtils.raPDayRtVoMapper.getRaPDayRtVoListByStcdListAndDateStr(stcdList, rainfallDateStr, DateUtils.transformDateTOStr(calDate));
    }


    @PostConstruct
    public void init(){
        getSomethingUtils = this;
    }

    /**
     * 方法描述: 以单例模式获取一个MyBatis的SqlSession对象
     * @author yanglichen
     * @date 2020-07-21 17:23
     * @return SqlSession对象
     **/
    public static SqlSessionFactory getSqlSessionFactory() {
        //如果对象为孔泽创建对象
        if (sqlSessionFactory == null) {
            try {
                //读取配置文件
                InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
                //初始化myBatis,创建SqlSessionFactory类实例
                //创建Session实例
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
                //关闭流
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sqlSessionFactory;
        }
        return sqlSessionFactory;
    }

    /**
     * 方法描述: 得到5个站码列表
     *  (网速不中的时候就用这个跑单元测试)
     * @author yanglichen
     * @date 2020-07-24 09:30
     * @return 得到5个窟野河的站码列表
     **/
    public static List<String> get5Stcd(){
        List<String> stcdList = new ArrayList<>();
        stcdList.add("40721200");
        stcdList.add("40723000");
        stcdList.add("40723600");
        stcdList.add("40724200");
        stcdList.add("40724800");
        return stcdList;
    }

    /**
     * 方法描述: 使用单例模式, 根据站码列表得到一个包含所有结果的Map对象
     * @author yanglichen
     * @date 2020-07-24 15:10
     * @param stcdList 站码列表
     * @return 包含所有结果的Map对象
     **/
    public static Map<String, Map<Date, RaPDayRtVo>> getAllInformationMap(List<String> stcdList){
        //判断Map结果集是否为空
        if (allInfomationMap == null) {
            //为空,调用方法,注入属性
            allInfomationMap = getSomethingUtils.raPDayRtVoService.getAllRaPDayRtVoInformation(stcdList);
        }
        /*
        如果当传入的站码列表发生改变的时候,
        结果集也应该发生相应的改变
        要重新根据站码列表构造为结果集注入属性
        判断站码列表是否改变的依据
        满足一下任意一个条件即可
            1.列表长度不一样
            2.列表内元素不一样
        */
        //得到结果集的站码列表
        Set<String> mapStcdSet = allInfomationMap.keySet();
        //初始化标识符
        boolean flage = false;
        //如果列表长度相同,则遍历当前的map中的站码
        if (mapStcdSet.size()==stcdList.size()) {
            for (String stcd : mapStcdSet) {
                //如果map中的站码有不存在于传入的List中的站码
                if (!stcdList.contains(stcd)) {
                    //标志量设为true
                    flage = true;
                    //后续不在比较,跳出循环
                    break;
                }
            }
        //如果列表长度都不同,直接将标志量设为true
        }else {
            flage = true;
        }
        //如果标志量为真(传入的站码列表发生了变化)
        if (flage) {
            //为map注入新的属性值
            allInfomationMap = getSomethingUtils.raPDayRtVoService.getAllRaPDayRtVoInformation(stcdList);
        }
        return allInfomationMap;
    }



}

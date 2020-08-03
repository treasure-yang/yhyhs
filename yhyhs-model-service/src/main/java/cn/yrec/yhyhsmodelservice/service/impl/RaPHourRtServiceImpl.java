package cn.yrec.yhyhsmodelservice.service.impl;

import cn.yrec.yhyhsmodelservice.mappers.RaPHourRtMapper;
import cn.yrec.yhyhsmodelservice.model.RaPDayRt;
import cn.yrec.yhyhsmodelservice.model.RaPHourRt;
import cn.yrec.yhyhsmodelservice.service.RaPHourRtService;
import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述:
 *  实时小时降雨表的业务层接口的实现类
 * @createDate: 2020/07/14 下午 5:57
 * @author: yanglichen
 */
@Service
public class RaPHourRtServiceImpl implements RaPHourRtService {
    @Autowired
    private RaPHourRtMapper raPHourRtMapper;

    /**
     * 方法描述: 通过传入的小时表列表和站码列表得到
     *  按照站码分类的逐日雨量站列表(用于补充当前时间下日表中没有信息的情况)
     * @author yanglichen
     * @date 2020-07-14 17:55
     * @param raPHourRtList 同一个站对应的小时的降雨记录
     * @param stcdList 站码列表
     * @return 逐日的降雨数据
     **/
    @Override
    public List<RaPDayRt> getReplaceRaPDayRtListByRaPHourRtList(
            List<RaPHourRt> raPHourRtList, List<String> stcdList) {
        //构造结果
        List<RaPDayRt> raPDayRtList = new ArrayList<>();
//        //对大集合的小时数据进行按照站码进行分组
//        List<List<RaPHourRt>> raPHourRtsList = getRaPHourListByRaPHoursList(raPHourRtList, stcdList);
//        raPHourRtsList.forEach(raPDayRts -> {
//            RaPDayRt raPDayRt = getReplaceRaPDayRtByRaPHourRtList(raPDayRts);
//            raPDayRtList.add(raPDayRt);
//        });



        return raPDayRtList;
    }

    /**
     * 方法描述: 根据站码列表, 开始日期, 结束日期
     * 找到这段范围内的雨量站逐小时的降雨信息
     * @param stcdList  站码列表
     * @param startDate 开始日期 (日期较旧)
     * @param endDate   结束日期 (日期较新)
     * @return 这段范围内的雨量站逐小时的降雨信息
     * @author yanglichen
     * @date 2020-07-14 10:27
     **/
    private List<RaPHourRt> getRapHourRtByStcdListAndDate(List<String> stcdList, Date startDate, Date endDate) {
        return raPHourRtMapper.getRapHourRtListByStcdListAndDate(stcdList, startDate, endDate);
    }

    /**
     * 方法描述: 通过传入的小时的降雨统计信息
     * 整编得到一个日降雨信息
     * @author yanglichen
     * @date 2020-07-14 18:12
     * @param raPHourRtList 某站的小时降雨信息
     * @return 某站的日降雨信息(同于填充当日日表中没有记录的情况)
     **/
    private RaPDayRt getReplaceRaPDayRtByRaPHourRtList(List<RaPHourRt> raPHourRtList) {
        //构造结果
        RaPDayRt raPDayRt = new RaPDayRt();
        RaPHourRt temp = raPHourRtList.get(0);
        //获得站码
        String stcd = temp.getStcd();
        //获得名称
        String stnm = temp.getStnm();
        //获得日期(这个日期是有小时记录的,两次转换,去掉小时部分)
        Date date = DateUtils.getNotHourDate(temp.getCdate());
        //总降雨量
        double totalRf = 0D;
        //遍历传入的小时表
        for (RaPHourRt hour :
                raPHourRtList) {
            //累加小时表中的降雨量
            totalRf += hour.getRf();
        }
        //封装数据
        raPDayRt.setStcd(stcd);
        raPDayRt.setStnm(stnm);
        raPDayRt.setDate(date);
        raPDayRt.setRf(totalRf);
        return raPDayRt;
    }

    /**
     * 方法描述: 将按日期分组的小时表的信息转化为逐日降雨信息
     * @author yanglichen
     * @date 2020-07-15 11:06
     * @param raPHourRtsList 按日期分组的小时表列表
     * @param stcdList 站码列表
     * @return 逐日降雨信息列表
     **/
    private List<RaPDayRt> getRaPDayRtListByRaHourRtsList(List<List<RaPHourRt>> raPHourRtsList, List<String> stcdList) {
        if (raPHourRtsList==null || raPHourRtsList.size() == 0){
            return null;
        }
        //构造结果集
        List<RaPDayRt> raPDayRtList = new ArrayList<>();
        //遍历按日期分组的小时表信息
        raPHourRtsList.forEach(raPHourRtList -> {
            /*raPHourRtList里面是每日的各个雨量站记录的小时信息
            * 1.拆分出站码列表
            * 2.对各个同站码不同时间的雨量进行累加得到一个日信息
            * */
            //站码列表
            stcdList.forEach(stcd->{
                List<RaPHourRt> tempList = new ArrayList<>();
                //遍历每日的雨量站的小时数据
                raPHourRtList.forEach(raPHourRt ->{
                    if (raPHourRt.getStcd().equalsIgnoreCase(stcd)) {
                        tempList.add(raPHourRt);
                    }
                });
                //有时候有的站码下没有小时雨量的记录,所以整合那个雨量站
                if(tempList.size() > 0){
                    RaPDayRt raPDayRt = getReplaceRaPDayRtByRaPHourRtList(tempList);
                    raPDayRtList.add(raPDayRt);
                }
            });

        });
        return raPDayRtList;
    }


    /**
     * 方法描述: 将从数据库中查询出来的结果,按照日期进行分组
     * @author yanglichen
     * @date 2020-07-15 09:36
     * @param raPHourRtList 从数据库中直接查询出来的原始小时数据
     * @return 按照日期分组的雨量站表
     **/
    public List<List<RaPHourRt>> getRaPHourRtsListByDate(List<RaPHourRt> raPHourRtList){
        if (raPHourRtList.size()==0){
            return null;
        }
        //构造结果集
        List<List<RaPHourRt>> raPHourRtsList = new ArrayList<>();
        /*因为从数据库中查询数据的时候按照了时间进行了降序排序
        * 所以列表中的第一个元素的时间是最新的时间
        * 列表中最后一个的元素的时间是最老的时间
        * 两个时间转化成当日00:00后做差
        * 如果差==0,则查询的数据里面只包含一天的数据
        * */
        Date startDate = raPHourRtList.get(0).getCdate();
        Date endDate = raPHourRtList.get(raPHourRtList.size()-1).getCdate();
        long startDateTimes = DateUtils.getNotHourDate(startDate).getTime();
        long endDateTimes = DateUtils.getNotHourDate(endDate).getTime();
        long temp = startDateTimes-endDateTimes;
        int index = 0;
        if (temp!=0) {
            //计算间隔的天数
            index = (int) (temp / DateUtils.ONE_DAY_MILLI_SECOND);
        }
        //相对于开始时间的前一天
        Date tempEndDate = DateUtils.getNotHourDate(endDate);
        //得到了间隔的天数,也就得到了外层List的长度,也就得到了循环的天数
        for (int i = 0; i < index+1; i++) {
            //从结束的那一天往前推一天得到开始的那一天
            Date tempStartDate = DateUtils.getNextDateByTimeZone(tempEndDate,-1);
            //统计天数的列表
            List<RaPHourRt> raPHourRts = new ArrayList<>();
            //嵌套遍历
            Date finalTempEndDate = tempEndDate;
            raPHourRtList.forEach(raPHourRt -> {
                //如果某一个站的  日期 >= endDate && 日期 < startDate 则这个站就是endDaate一天里面记录的
                //                2020/7/11 00:00 <= 日期 < 2020/7/12 00:00
                long raPHourRtDate = raPHourRt.getCdate().getTime();
                if (raPHourRtDate >= finalTempEndDate.getTime() && raPHourRtDate < tempStartDate.getTime()) {
                    raPHourRts.add(raPHourRt);
                }
            });
            //结束日期往前推一天
            tempEndDate = DateUtils.getNextDateByTimeZone(tempEndDate,-1);
            raPHourRtsList.add(raPHourRts);
        }
        return raPHourRtsList;
    }

    /**
     * 方法描述: 将查询出来的结果,融合到原来的日雨量站列表中
     *
     * @param raPDayRtList  日雨量站列表
     * @param raPHourRtList 查询出来的小时表的列表
     * @param stcdList 站码列表
     * @author yanglichen
     * @date 2020-07-15 15:14
     **/
    @Override
    public void mixRaPDayRtListByRaPHourRtList(
            List<RaPDayRt> raPDayRtList, List<RaPHourRt> raPHourRtList, List<String> stcdList) {
        List<List<RaPHourRt>> tempRaPhourRtList = getRaPHourRtsListByDate(raPHourRtList);
        List<RaPDayRt> tempRaPDayRtList = getRaPDayRtListByRaHourRtsList(tempRaPhourRtList,stcdList);
        if(tempRaPDayRtList!=null){
            System.err.println("执行了拼接融合");
            //添加结果
            raPDayRtList.addAll(tempRaPDayRtList);
        }
    }

}

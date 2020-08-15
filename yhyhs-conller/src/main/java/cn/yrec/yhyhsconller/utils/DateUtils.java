package cn.yrec.yhyhsconller.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述:
 *  处理时间的工具类
 * @createDate: 2020/08/14 上午 9:16:52
 * @author: yanglichen
 */
public class DateUtils {
    /**
     * 方法描述: 讲传入的时间转换为字符串格式的时间
     * @author yanglichen
     * @date 2020-08-14 09:17
     * @param date 日期
     * @return 字符串格式的时间
     **/
    public static String transfromToStrTime(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(transfromToStrTime(date));
    }

    /**
     * 方法描述: 将前端的格林威治时间转换为北京时间
     * @author yanglichen
     * @date 2020-08-07 09:59
     * @param webDateString 从网页传入的格林威治时间:
     *                      例如: 2017-10-06T16:00:00.000+0000
     * @return 北京时间: 2017-10-07
     **/
    public static Date transformWebDateToBeijingDate(String webDateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+Z");
        String dateStr = webDateString.replace("0000", "UTC");
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

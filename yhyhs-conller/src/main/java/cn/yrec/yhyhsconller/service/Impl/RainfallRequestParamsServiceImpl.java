package cn.yrec.yhyhsconller.service.Impl;

import cn.yrec.yhyhsconller.mappers.RainfallRequestParamsMapper;
import cn.yrec.yhyhsconller.model.RainfallRequestParams;
import cn.yrec.yhyhsconller.service.RainfallRequestParamsService;
import cn.yrec.yhyhsconller.utils.DateUtils;
import cn.yrec.yhyhsconller.utils.HttpUtils;
import cn.yrec.yhyhsconller.vo.ResponseResultEntrty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *  处理发送请求用到的业务层
 * @createDate: 2020/08/14 上午 8:54:55
 * @author: yanglichen
 */
@Service
public class RainfallRequestParamsServiceImpl implements RainfallRequestParamsService {


    @Autowired
    private RainfallRequestParamsMapper rainfallRequestParamsMapper;
    /**
     * 方法描述:
     * 查询数据库,得到符合发送Post请求中携带的form-data格式的数据
     *
     * @return 发送Post请求中携带的form-data格式的数据
     * @author yanglichen
     * @date 2020-08-14 08:53
     **/
    @Override
    public List<String[]> getSendPostParams() {
        //构造结果
        List<String[]> requestParamsList = new ArrayList<>();
        //从数据库中检索最新的一条数据
        RainfallRequestParams rainfallRequestParams =
                rainfallRequestParamsMapper.findNearestParams();
        //转换数据格式
        String[] requestParams = transformRequestParams(rainfallRequestParams);
        requestParamsList.add(requestParams);
        return requestParamsList;
    }

    @Override
    public String sendPostTakeFromData() {
        String url = "http://47.103.73.45:9009/rainstorm/getstationsbytrigger";
        List<String[]> params = getSendPostParams();
        return HttpUtils.sendPostByFormData(url, params);
    }

    /**
     * 方法描述: 得到发送请求后返回的Json数据,并解析成一个实体类列表的列表
     * 如果存在 2 个暴雨中心,则外部列表的长度为 2
     * 内层列表为暴雨中心对应的流域
     * 如果存在 3 个流域,则内层列表的长度为 3
     *
     * @return 按照暴雨中心分类的流域对象
     * @author yanglichen
     * @date 2020-08-14 11:25
     **/
    @Override
    public List<List<ResponseResultEntrty>> getResponseBySendPostTakeFromData() {
        //得到响应的Json数据字符串
        String responseJSonStr = sendPostTakeFromData();
        //构造返回结果
        List<List<ResponseResultEntrty>> resultEntrtyList = new ArrayList<>();
        //将Json字符串解析为Json列表对象
        JSONArray jsonArray = JSON.parseArray(responseJSonStr);
        //遍历Json列表对象
        for (Object jsonArrayItem : jsonArray) {
            //每一个 jsonArrayItem 都是一个 暴雨中心的数据(也是Json列表对象,可能包含多个流域信息)
            //将 jsonArrayItem 转化为List<ResponseResultEntrty>对象
            List<ResponseResultEntrty> entrtyList =
                    JSON.parseArray(jsonArrayItem.toString(), ResponseResultEntrty.class);
            //放入外部列表中
            resultEntrtyList.add(entrtyList);
        }
        return resultEntrtyList;
    }

    /**
     * 方法描述: 得到通过发送Post请求得到的站码列表
     * 如果出现 2 个暴雨中心,外部的List的长度为 2
     *
     * @return 暴雨中心列表
     * @author yanglichen
     * @date 2020-08-14 11:49
     **/
    @Override
    public List<List<String>> getStcdListBySendPost() {
        //构造结果集对象
        List<List<String>> stcdsList = new ArrayList<>();
        //得到按照暴雨中心分类的流域对象
        List<List<ResponseResultEntrty>> responseEntryList =
                getResponseBySendPostTakeFromData();
        //遍历暴雨中心分类的流域对象
        for (List<ResponseResultEntrty> resultEntrties : responseEntryList) {
            //每一个 resultEntrties 都是一个暴雨中心对象
            List<String> rainfallStcdList = new ArrayList<>();
            //提取站码列表
            for (ResponseResultEntrty entrty : resultEntrties) {
                //提取站码,添加到列表中
                rainfallStcdList.addAll(entrty.getStations());
            }
            stcdsList.add(rainfallStcdList);
        }
        return stcdsList;
    }

    /**
     * 方法描述:
     * 转换数据的格式
     * List<String[]> 中的数据
     * String[] 的长度为2
     * 对应from-data数据中的 Key Value
     * 对于本项目中要发送的数据的格式
     * 示例如下:
     *  key: trigger
     *  value: {"basinName":"黄河流域","startTime":"2020-08-04 08:00:00","endTime":"2020-08-05 08:00:00","r":100,"area":5000}
     *  所以需要对需要发送的数据进行数据格式的转换
     * @author yanglichen
     * @date 2020-08-14 09:00
     * @param rainfallRequestParams 请求参数实例
     * @return 转换后的数据
     **/
    private String[] transformRequestParams(RainfallRequestParams rainfallRequestParams){
        //构造结果集
        String[] requestParams = new String[2];
        //对数据进行赋值
        requestParams[0] = "trigger";
        Map<String, Object> tempMap = new LinkedHashMap<>();
        tempMap.put("\"basinName\"", "\""+rainfallRequestParams.getBasinName()+"\"");
        tempMap.put("\"startTime\"", "\""+DateUtils.transfromToStrTime(rainfallRequestParams.getStartTime())+"\"");
        tempMap.put("\"endTime\"", "\""+DateUtils.transfromToStrTime(rainfallRequestParams.getEndTime())+"\"");
        tempMap.put("\"r\"", rainfallRequestParams.getR());
        tempMap.put("\"area\"", rainfallRequestParams.getArea());
        requestParams[1] = tempMap.toString().replaceAll("=", ":");
        return requestParams;
    }
}

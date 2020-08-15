package cn.yrec.yhyhsconller.service;

import cn.yrec.yhyhsconller.vo.ResponseResultEntrty;

import java.util.List;

/**
 * 描述:
 *  访问外部GIS服务接口时, 需要携带的数据
 *  的业务层接口
 * @createDate: 2020/08/14 上午 8:47:33
 * @author: yanglichen
 */
public interface RainfallRequestParamsService {

    /**
     * 方法描述:
     *  查询数据库,得到符合发送Post请求中携带的form-data格式的数据
     * @author yanglichen
     * @date 2020-08-14 08:53
     * @return 发送Post请求中携带的form-data格式的数据
     **/
    List<String[]> getSendPostParams();

    /**
     * 方法描述: 从数据库中查询需要携带的数据后,对目标地址发送请求得到相应的字符串格式的Json数据
     * @author yanglichen
     * @date 2020-08-14 09:27
     * @return
     **/
    String sendPostTakeFromData();

    /**
     * 方法描述: 得到发送请求后返回的Json数据,并解析成一个实体类列表的列表
     *  如果存在 2 个暴雨中心,则外部列表的长度为 2
     *  内层列表为暴雨中心对应的流域
     *  如果存在 3 个流域,则内层列表的长度为 3
     * @author yanglichen
     * @date 2020-08-14 11:25
     * @return 按照暴雨中心分类的流域对象
     **/
    List<List<ResponseResultEntrty>> getResponseBySendPostTakeFromData();

    /**
     * 方法描述: 得到通过发送Post请求得到的站码列表
     *  如果出现 2 个暴雨中心,外部的List的长度为 2
     * @author yanglichen
     * @date 2020-08-14 11:49
     * @return 暴雨中心列表
     **/
    List<List<String>> getStcdListBySendPost();
}

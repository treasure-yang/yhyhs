package cn.yrec.yhyhsconller.mappers;

import cn.yrec.yhyhsconller.model.RainfallRequestParams;

/**
 * 描述:
 *      访问外部GIS服务接口时, 需要携带的数据
 *      的持久层接口
 * @createDate: 2020/08/14 上午 8:13:04
 * @author: yanglichen
 */
public interface RainfallRequestParamsMapper {

    /**
     * 方法描述: 查询当前一共有多少条数据量
     * @author yanglichen
     * @date 2020-08-14 08:23
     * @return 当前库表中的数据条数
     **/
    Integer count();

    /**
     * 方法描述: 查找最近的一条数据
     * @author yanglichen
     * @date 2020-08-14 08:38
     * @return 最近的一条数据
     **/
    RainfallRequestParams findNearestParams();
}

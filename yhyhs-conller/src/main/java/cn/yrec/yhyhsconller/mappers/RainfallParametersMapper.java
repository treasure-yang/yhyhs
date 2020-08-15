package cn.yrec.yhyhsconller.mappers;

import cn.yrec.yhyhsconller.model.RainfallParameters;

/**
 * 描述:
 *      对降雨参数表的操作的接口
 * @createDate: 2020/08/14 下午 3:34:18
 * @author: yanglichen
 */
public interface RainfallParametersMapper {

    /**
     * 方法描述: 查询数量
     * @author yanglichen
     * @date 2020-08-14 15:37
     * @return 库表中的数量
     **/
    Integer count();

    /**
     * 方法描述: 插入数据
     * @author yanglichen
     * @date 2020-08-14 15:40
     * @param rainfallParameters 降雨参数实例
     * @return 受影响的行数
     **/
    Integer insert(RainfallParameters rainfallParameters);
}

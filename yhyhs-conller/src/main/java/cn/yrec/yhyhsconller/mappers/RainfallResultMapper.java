package cn.yrec.yhyhsconller.mappers;

import cn.yrec.yhyhsconller.model.RainfallResult;

/**
 * 描述:
 *      对降雨结果进行操作的持久层接口文件
 * @createDate: 2020/08/13 下午 4:21:53
 * @author: yanglichen
 */
public interface RainfallResultMapper {
    /**
     * 方法描述: 查询结果中的数据数量
     * @author yanglichen
     * @date 2020-08-13 16:47
     * @return
     **/
    Integer countResult();
    
    /**
     * 方法描述: 将降雨结果插入数据库
     * @author yanglichen
     * @date 2020-08-14 16:34 
     * @param rainfallResult 降雨结果
     * @return 受影响的行数
     **/
    Integer insert(RainfallResult rainfallResult);

    /**
     * 方法描述: 根据rid查找
     * @author yanglichen
     * @date 2020-08-14 17:51
     * @param rid rid
     * @return 实例
     **/
    RainfallResult findByRid(Integer rid);
}

package cn.yrec.yhyhsconller.vo;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 *  访问外部GIS接口后返回的数据格式是Json数据,需要对Json数据进行处理从而得到站码列表
 *  返回的数据格式例如:(站码列表经过简化)
 * [
 *     [
 *         {
 *             "basinName": "延河流域",
 *             "area": 4259.21,
 *             "stations": [
 *                 "40638420",
 *                 "40638450",
 *                 "40638400"
 *             ]
 *         },
 *         {
 *             "basinName": "清涧河流域",
 *             "area": 1270.11,
 *             "stations": [
 *                 "40605580",
 *                 "40634750",
 *                 "40634752"
 *             ]
 *         },
 *         {
 *             "basinName": "北洛河流域",
 *             "area": 1341.13,
 *             "stations": [
 *                 "41324500",
 *                 "41300900",
 *                 "41301104"
 *             ]
 *         }
 *     ]
 * ]
 * 数据格式解析:
 *  List<List<Entrty>>
 *         外层 List 中的元素表示暴雨中心的个数, 如果同时出现了 2个暴雨中心,则 List<Entrty>的个数为 2
 *         内层 List 中的元素表示 每个子流域
 * @createDate: 2020/08/14 上午 9:29:52
 * @author: yanglichen
 */
@Data
public class ResponseResultEntrty {

    private String basinName;//流域名称
    private double area;//面积
    private List<String> stations;//站码列表
}

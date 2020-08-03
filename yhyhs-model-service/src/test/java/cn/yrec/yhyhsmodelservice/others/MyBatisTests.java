package cn.yrec.yhyhsmodelservice.others;

import cn.yrec.yhyhsmodelservice.mappers.RaPDayRtMapper;
import cn.yrec.yhyhsmodelservice.model.RaPDayRt;


import cn.yrec.yhyhsmodelservice.utils.DateUtils;
import cn.yrec.yhyhsmodelservice.utils.GetSomethingUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.ibatis.session.SqlSessionFactory;


import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 描述:
 *
 * @createDate: 2020/07/21 上午 9:24
 * @author: yanglichen
 */
@SpringBootTest
public class MyBatisTests {


    @Test
    void myBatisConfig() throws IOException {
//        //读取配置文件
//        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
//        //初始化myBatis,创建SqlSessionFactory类实例
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//        inputStream.close();
//        //创建Session实例
//        SqlSession sqlSession = sqlSessionFactory.openSession();
        SqlSessionFactory factory = GetSomethingUtils.getSqlSessionFactory();
        SqlSession sqlSession = factory.openSession();
        RaPDayRtMapper raPDayRtMapper = sqlSession.getMapper(RaPDayRtMapper.class);
        //使用同一个Session实例,发送相同的请求
        RaPDayRt raPDayRt = raPDayRtMapper.getRaPDayRtByStcdAndTime("40720600", "2020/7/1");
        System.err.println(raPDayRt);
        RaPDayRt raPDayRt1 = raPDayRtMapper.getRaPDayRtByStcdAndTime("40720600", "2020/7/1");
        System.err.println(raPDayRt1);


        SqlSessionFactory factory1 = GetSomethingUtils.getSqlSessionFactory();
        SqlSession sqlSession1 = factory1.openSession();
        RaPDayRtMapper raPDayRtMapper1 = sqlSession1.getMapper(RaPDayRtMapper.class);
        RaPDayRt raPDayRt2 = raPDayRtMapper1.getRaPDayRtByStcdAndTime("40720600", "2020/7/1");
        System.err.println(raPDayRt2);
    }

    @Test
    void secendLeverCacheTest() {
        //获取SqlSession对象
        SqlSessionFactory factory = GetSomethingUtils.getSqlSessionFactory();
        SqlSession sqlSession = factory.openSession();

        RaPDayRtMapper raPDayRtMapper = sqlSession.getMapper(RaPDayRtMapper.class);
        List<String> stcdList = GetSomethingUtils.getTestStcdList();
        Date startDaste = DateUtils.transformStrTODate("2020/7/1");
        Date startDaste1 = DateUtils.transformStrTODate("2019/5/1");
        Date startDaste2 = DateUtils.transformStrTODate("2019/1/1");
        List<RaPDayRt> raPDayRtList = raPDayRtMapper.getRaPdayRtListByBeginDate(stcdList, startDaste2);
        System.out.println("1查询的结果数量: "+raPDayRtList.size());
        sqlSession.close();

        SqlSession sqlSession1 = factory.openSession();
        RaPDayRtMapper raPDayRtMapper1 = sqlSession1.getMapper(RaPDayRtMapper.class);
        List<RaPDayRt> raPDayRtList1 = raPDayRtMapper1.getRaPdayRtListByBeginDate(stcdList, startDaste1);
        System.out.println("2查询的结果数量: "+raPDayRtList1.size());
        List<RaPDayRt> raPDayRtList2 = raPDayRtMapper1.getRaPdayRtListByBeginDate(stcdList, startDaste2);
        System.out.println("3查询的结果数量: "+raPDayRtList2.size());
    }

    @Test
    void testMath() {
        System.out.println(Math.pow(2,3));
    }
}

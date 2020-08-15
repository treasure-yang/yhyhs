package cn.yrec.yhyhsconller.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 描述:
 *  用于发送Http请求的工具类
 *  发送Get请求,Post请求
 *  并得到返回的数据
 * @createDate: 2020/08/13 上午 8:33:17
 * @author: yanglichen
 */
public class HttpUtils {
    public static String sendGet(String url, String param) {
        System.out.println("正在发送Get请求到: "+url);
        //发送get请求得到的数据
        StringBuilder responseStr = new StringBuilder();
        //初始化用于读取输入流中文本的对象
        BufferedReader reader = null;
        try{
            //判断param是否有值
            String urlAndParam = "";
            if (!param.trim().equals("")) {
                urlAndParam = url+"?"+param;
            }else {
                urlAndParam = url;
            }
            //构造Url对象
            URL urlObj = new URL(urlAndParam);
            System.out.println("实际请求的URL: "+urlObj.toString());
            //得到一个连接的对象
            URLConnection connection = urlObj.openConnection();
            //设置连接所需的通用的消息头
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-agent", "PostmanRuntime/7.26.2");
            connection.setConnectTimeout(300000); //30秒连接超时
            connection.setReadTimeout(300000);    //30秒读取超时
            //建立实际的连接
            connection.connect();
            //读取URL的响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = reader.readLine())!=null){
                responseStr.append(line);
            }
        }catch (Exception e){
            System.out.println("发送Get请求出现异常");
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseStr.toString();
    }


    /**
     * 方法描述: 测试发送Post请求
     *  通过构造站码
     * @author yanglichen
     * @date 2020-08-13 15:39
     * @return 得到
     **/
    public static void testDoPostByJson(){
        Map<String, Object> params = new LinkedHashMap<>();
        List<String> stcdList = new ArrayList<>();
        stcdList.add("41324500");
        stcdList.add("41300900");
        stcdList.add("41301104");
        stcdList.add("41324400");
        stcdList.add("41324570");
        stcdList.add("41324560");
        stcdList.add("41324245");
        stcdList.add("40921700");
        stcdList.add("41300700");
        stcdList.add("41300701");
        stcdList.add("41324260");
        stcdList.add("41324390");
        stcdList.add("41324265");
        stcdList.add("41300640");
        stcdList.add("41324270");
        stcdList.add("41323900");
        stcdList.add("41324240");
        stcdList.add("41323950");
        stcdList.add("41323870");
        stcdList.add("41323954");
        stcdList.add("41301802");
        stcdList.add("41323980");
        stcdList.add("41323890");
        stcdList.add("41324230");
        stcdList.add("41323770");
        stcdList.add("41324060");
        stcdList.add("41323860");
        stcdList.add("41323780");
        stcdList.add("41323970");
        stcdList.add("41323480");
        stcdList.add("41323460");
        stcdList.add("41300580");
        stcdList.add("41323850");
        stcdList.add("41323450");
        stcdList.add("41323760");
        stcdList.add("41323430");
        stcdList.add("41324050");
        stcdList.add("41323960");
        stcdList.add("41323470");
        stcdList.add("41324204");
        stcdList.add("41324200");
        stcdList.add("41323800");
        stcdList.add("41323440");
        stcdList.add("41323880");
        stcdList.add("41323202");
        stcdList.add("41323420");
        stcdList.add("41323300");
        stcdList.add("41300600");
        stcdList.add("41323500");
        stcdList.add("41323710");
        stcdList.add("41301600");
        stcdList.add("41323552");
        stcdList.add("41323700");
        stcdList.add("41300620");
        stcdList.add("41323652");
        stcdList.add("41300500");
        stcdList.add("41322952");
        stcdList.add("41301580");
        stcdList.add("41222852");
        stcdList.add("41322802");
        stcdList.add("41322752");
        stcdList.add("41322102");
        stcdList.add("41322850");
        stcdList.add("41322500");
        stcdList.add("41301400");
        stcdList.add("41213070");
        stcdList.add("41322552");
        stcdList.add("41213080");
        stcdList.add("41322000");
        stcdList.add("41300420");
        stcdList.add("41223987");
        stcdList.add("41223992");
        stcdList.add("41223862");
        stcdList.add("41322402");
        stcdList.add("41312080");
        stcdList.add("41321200");
        stcdList.add("41223857");
        stcdList.add("41321902");
        stcdList.add("41223867");
        stcdList.add("41223852");
        stcdList.add("41223872");
        stcdList.add("41223847");
        stcdList.add("41322250");
        stcdList.add("41223877");
        stcdList.add("41301360");
        stcdList.add("41322302");
        stcdList.add("41321852");
        stcdList.add("41223982");
        stcdList.add("41223882");
        stcdList.add("41223962");
        stcdList.add("41223957");
        stcdList.add("41321752");
        stcdList.add("41223977");
        stcdList.add("41223952");
        stcdList.add("41321730");
        stcdList.add("41321700");
        stcdList.add("41223972");
        stcdList.add("41312010");
        stcdList.add("41321100");
        stcdList.add("41223967");
        stcdList.add("41321650");
        stcdList.add("41223610");
        stcdList.add("41223490");
        stcdList.add("41321602");
        stcdList.add("41320440");
        stcdList.add("41322200");
        stcdList.add("41320430");
        stcdList.add("41322150");
        stcdList.add("41321550");
        stcdList.add("41223590");
        stcdList.add("41223520");
        stcdList.add("41321402");
        stcdList.add("41223530");
        stcdList.add("41300300");
        stcdList.add("41223200");
        stcdList.add("41320920");
        stcdList.add("41320950");
        stcdList.add("41320902");
        stcdList.add("41320752");
        stcdList.add("41301300");
        stcdList.add("41320502");
        stcdList.add("41300100");
        stcdList.add("41321150");
        stcdList.add("41320652");
        stcdList.add("41320202");
        stcdList.add("41320200");
        stcdList.add("41320550");
        stcdList.add("41321000");
        stcdList.add("41320150");
        stcdList.add("41320400");
        stcdList.add("41320352");
        stcdList.add("41320102");
        stcdList.add("41320070");
        params.put("stcdList",stcdList);
        params.put("timeInterval", 2);
        params.put("rainfallThreshold", 25.0);
        params.put("rainfallThresholdFoArea", 0.0);
        params.put("calDate", 1596988800000L);
        params.put("rainfallTolerance", 2);
        params.put("rainfallQTolerance", 0.1);
        String JsonParams = JSON.toJSONString(params);
        String url = "http://localhost:8008/calRf/getResult";
        String responseStr = sendPostByJson(url, JsonParams);
        System.out.println(responseStr);
    }

    public static String testDoGet(){
        String url = "http://localhost:8008/calRf/getInfo";
        String json = sendGet(url, "");
        JSONObject jsonObject = JSON.parseObject(json);
        String result = jsonObject.getString("data");
        return result;
    }

    public static void main(String[] args) {
        String param = testDoGet();
        String url = "http://localhost:8008/calRf/getResult";
        String response = sendPostByJson(url, param);
        System.out.println(response);
    }

    public static String sendPostByFormData(String url, List<String[]> paramsStrList ){
        System.out.println("正在发送Post请求");
        //初始化输出流
        OutputStream outputStream = null;
        //初始化可变字符序列(用于提交请求数据)
        StringBuilder buffer = new StringBuilder();
        //字符流缓冲区(用于读取返回的数据)
        BufferedReader reader = null;
        //初始化可变字符序列(用于接收返回的数据)
        StringBuilder responseStr = new StringBuilder();
        try {
            URL targetUrl = new URL(url);
            //得到连接对象
            URLConnection connection = targetUrl.openConnection();
            //设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //构造准备发送的from表单数据
            //遍历传入的表单数据的列表
            for (String[] paramsStr : paramsStrList) {
                //固定格式Countent-Type后面的内容为固定格式 boundary=----这个为分界线,可随意修改,但是必须下面的都保持一致
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----yhyhs");
                buffer.append("------yhyhs\r\n");
                //name="对应from表单中的key"(双引号不可省略)
                buffer.append("Content-Disposition: form-data; name=");
                //拼接name
                buffer.append("\"").append(paramsStr[0]).append("\"");
                buffer.append("\r\n\r\n");
                //拼接对应的属性值
                buffer.append(paramsStr[1]);
                buffer.append("\r\n");
            }
            //最后的分界线不参与循环防止重复
            buffer.append("------yhyhs--\r\n");
            //得到输出流对象
            outputStream = connection.getOutputStream();
            //将数据转化成字节后写入到输入流中
            outputStream.write(buffer.toString().getBytes());
            //建立连接
            connection.connect();
            //将连接对象返回的输入流注入到字符缓冲流中
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //将得到的字符流写入到字符序列中
            String line;
            while((line=reader.readLine())!=null){
                responseStr.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回返回的数据
        return responseStr.toString();
    }


    public static String sendPostByJson(String url, String param){
        System.out.println("正在发送Post请求 -- 携带数据格式: JSON");
        //初始化文本输出流对象
        PrintWriter out = null;
        //字符流缓冲区
        BufferedReader reader = null;
        //初始化返回值
        StringBuilder responseStr = new StringBuilder();
        try{
            URL targetUrl = new URL(url);
            //打开连接
            URLConnection connection = targetUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/json");
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //获取连接对象的输出流
            out = new PrintWriter(connection.getOutputStream());
            //发送请求的参数
            out.print(param);
            //flush输出流的缓冲
            out.flush();
            //得到相应的流
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line=reader.readLine())!=null){
                responseStr.append(line);
            }
        }catch (Exception e){
            System.out.println("发送 Post 请求出现异常!");
            e.printStackTrace();
        }finally {
            if (out!=null) {
                out.close();
            }
            if (reader!=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseStr.toString();
    }

}

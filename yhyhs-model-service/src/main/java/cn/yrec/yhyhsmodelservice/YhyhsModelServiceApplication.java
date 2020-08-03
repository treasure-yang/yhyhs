package cn.yrec.yhyhsmodelservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("cn.yrec.yhyhsmodelservice.mappers")
public class YhyhsModelServiceApplication extends SpringBootServletInitializer {


    public static void main(String[] args) {
        SpringApplication.run(YhyhsModelServiceApplication.class, args);
    }
}

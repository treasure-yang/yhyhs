package cn.yrec.yhyhsconller;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.yrec.yhyhsconller.mappers")
public class YhyhsConllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YhyhsConllerApplication.class, args);
    }

}

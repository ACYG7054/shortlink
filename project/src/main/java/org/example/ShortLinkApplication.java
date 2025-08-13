package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("org.example.dao.mapper")
public class ShortLinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkApplication.class,args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            System.out.println("===============================================");
            System.out.println("短链接服务启动成功！");
            System.out.println("===============================================");
        };
    }
}

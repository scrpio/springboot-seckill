package com.shop.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author scorpio
 */
@SpringBootApplication(scanBasePackages = "com.shop.seckill")
@MapperScan("com.shop.seckill.dao")
public class SpringbootSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSeckillApplication.class, args);
    }

}

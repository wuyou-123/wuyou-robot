package pers.wuyou.robot.core;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wuyou
 */
@EnableSimbot
@MapperScan("pers.wuyou.robot.**.mapper")
@SpringBootApplication(scanBasePackages = "pers.wuyou.robot")
public class WuyouRobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(WuyouRobotApplication.class, args);
    }

}

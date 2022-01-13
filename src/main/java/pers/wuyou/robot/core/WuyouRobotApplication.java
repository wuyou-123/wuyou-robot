package pers.wuyou.robot.core;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author wuyou
 */
@SpringBootApplication(scanBasePackages = "pers.wuyou.robot")
@EnableSimbot
public class WuyouRobotApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(WuyouRobotApplication.class, args);
        RobotCore.initRobot(context);
    }

}

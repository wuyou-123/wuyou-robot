package pers.wuyou.robot;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pers.wuyou.robot.util.RobotUtil;

/**
 * @author wuyou
 */
@SpringBootApplication
@EnableSimbot
public class WuyouRobotApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(WuyouRobotApplication.class, args);
        RobotUtil.initRobot(context);
    }

}

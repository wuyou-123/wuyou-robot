package pers.wuyou.robot.annotation;

import love.forte.common.utils.annotation.AnnotateMapping;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.api.message.assists.Permissions;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;

import java.lang.annotation.*;

/**
 * @author wuyou
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Listen(PrivateMsg.class)
@SuppressWarnings("unused")
public @interface RobotListen {

    /**
     * 监听类型
     */
    @AnnotateMapping(value = Listen.class)
    Class<? extends MsgGet> value();

    /**
     * 描述信息
     */
    String desc() default "";

    /**
     * 执行监听器需要的权限
     */
    Permissions permissions() default Permissions.MEMBER;

    /**
     * 没有权限时的提示信息
     */
    String noPermissionTip() default "操作失败,您没有权限";

    /**
     * TODO: 群开机判断
     * 是否在当前群开机的时候执行,仅当监听类型是{@link love.forte.simbot.api.message.events.GroupMsg}时有效
     */
    boolean isBoot() default false;

}

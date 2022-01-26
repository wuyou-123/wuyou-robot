package pers.wuyou.robot.core.listener;

import catcode.Neko;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.annotation.Priority;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.constant.PriorityConstant;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.annotation.RobotListen;

/**
 * 撤回监听器
 *
 * @author wuyou
 */
@Listener
public class RecallListener {
    @RobotListen(GroupMsg.class)
    @Filter(value = "撤回", trim = true)
    @Priority(PriorityConstant.LAST)
    public void recall(GroupMsg msg) {
        final Neko quote = msg.getMsgContent().getCats("quote").get(0);
        final String id = quote.get("id");
        if (id != null) {
            RobotCore.setter().setMsgRecall(() -> () -> id);
        }
    }
}

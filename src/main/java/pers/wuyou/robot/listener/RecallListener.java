package pers.wuyou.robot.listener;

import catcode.Neko;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.api.message.events.GroupMsg;
import pers.wuyou.robot.annotation.RobotListen;
import pers.wuyou.robot.common.RobotCore;

/**
 * 撤回监听器
 *
 * @author wuyou
 */
@Listener
public class RecallListener {
    @RobotListen(GroupMsg.class)
    @Filter(value = "撤回", trim = true)
    public void recall(GroupMsg msg) {
        final Neko quote = msg.getMsgContent().getCats("quote").get(0);
        final String id = quote.get("id");
        if (id != null) {
            RobotCore.setter().setMsgRecall(() -> () -> id);
        }
    }
}

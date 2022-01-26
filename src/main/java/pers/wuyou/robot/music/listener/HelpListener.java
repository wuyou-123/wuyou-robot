package pers.wuyou.robot.music.listener;

import love.forte.simbot.annotation.ContextValue;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.api.message.events.GroupMsg;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.util.SenderUtil;

/**
 * @author wuyou
 */
@Listener
public class HelpListener {
    @RobotListen(GroupMsg.class)
    public void help(@ContextValue(ContextType.GROUP) String group){
        SenderUtil.sendGroupMsg(group, "");
    }
}

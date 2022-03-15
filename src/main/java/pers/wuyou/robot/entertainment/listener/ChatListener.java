package pers.wuyou.robot.entertainment.listener;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.annotation.ContextValue;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.api.message.events.GroupMsg;
import pers.wuyou.robot.common.util.TianApiTool;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.util.CatUtil;
import pers.wuyou.robot.core.util.SenderUtil;

/**
 * @author wuyou
 * @date 2022/3/15 17:35
 */
@Listener
@Slf4j
public class ChatListener {
    private final TianApiTool tianApiTool;

    public ChatListener(TianApiTool tianApiTool) {
        this.tianApiTool = tianApiTool;
    }

    @RobotListen(value = GroupMsg.class, isBoot = true)
    @Filter(atBot = true, anyAt = true)
    public void chat(@ContextValue(ContextType.GROUP) String groupCode, @ContextValue(ContextType.QQ) String qq, @ContextValue(ContextType.MESSAGE) String message) {
        final String str = tianApiTool.chatApi(CatUtil.UTILS.remove(message), qq);
        SenderUtil.sendGroupMsg(groupCode, CatUtil.at(qq) + str);
    }

}

package pers.wuyou.robot.core.listener;

import love.forte.simbot.annotation.ContextValue;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.api.message.events.FriendAddRequest;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.util.SenderUtil;

/**
 * @author wuyou
 */
@Listener
public class RequestListener {
    @RobotListen(FriendAddRequest.class)
    public void friendAddRequest(FriendAddRequest request, @ContextValue(ContextType.QQ) String qq, @ContextValue(ContextType.NAME) String name) {
        RobotCore.setter().acceptFriendAddRequest(request.getFlag(), "", false);
        SenderUtil.sendPrivateMsg(RobotCore.getADMINISTRATOR().get(0),
                String.format("已经添加[%s](%s)为好友,验证消息为%s", qq, name, "".equals(request.getText()) ? "空" : ": " + request.getText()));
    }
}

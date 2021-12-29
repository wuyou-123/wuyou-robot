package pers.wuyou.robot.listener;

import love.forte.simbot.annotation.ContextValue;
import love.forte.simbot.api.message.events.FriendAddRequest;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.annotation.ContextType;
import pers.wuyou.robot.annotation.RobotListen;
import pers.wuyou.robot.util.RobotUtil;
import pers.wuyou.robot.util.SenderUtil;

/**
 * @author wuyou
 */
@Component
public class RequestListener {
    @RobotListen(FriendAddRequest.class)
    public void friendAddRequest(FriendAddRequest request, @ContextValue(ContextType.QQ) String qq, @ContextValue(ContextType.NAME) String name) {
        RobotUtil.setter().acceptFriendAddRequest(request.getFlag(), "", false);
        SenderUtil.sendPrivateMsg(RobotUtil.ADMINISTRATOR.get(0),
                String.format("已经添加[%s](%s)为好友,验证消息为%s", qq, name, "".equals(request.getText()) ? "空" : ": " + request.getText()));
    }
}

package pers.wuyou.robot.listener;

import love.forte.simbot.annotation.ContextValue;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.ListenGroup;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.api.message.assists.Permissions;
import love.forte.simbot.api.message.events.GroupMsg;
import pers.wuyou.robot.annotation.ContextType;
import pers.wuyou.robot.annotation.RobotListen;
import pers.wuyou.robot.util.GroupUtil;

/**
 * 监听群开关机
 *
 * @author wuyou
 */
@Listener
@ListenGroup("core")
public class BootListener {

    @RobotListen(value = GroupMsg.class, permissions = Permissions.ADMINISTRATOR)
    @Filter(value = "开机", trim = true)
    public void boot(@ContextValue(ContextType.GROUP) String groupCode) {
        GroupUtil.setGroupState(groupCode, true);
    }

    @RobotListen(value = GroupMsg.class, permissions = Permissions.ADMINISTRATOR)
    @Filter(value = "关机", trim = true)
    public void down(@ContextValue(ContextType.GROUP) String groupCode) {
        GroupUtil.setGroupState(groupCode, false);
    }
}

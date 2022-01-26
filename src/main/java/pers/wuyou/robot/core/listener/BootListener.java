package pers.wuyou.robot.core.listener;

import love.forte.simbot.annotation.*;
import love.forte.simbot.api.message.assists.Permissions;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.constant.PriorityConstant;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.service.GroupBootStateService;

import java.util.List;

/**
 * 监听群开关机
 *
 * @author wuyou
 */
@Listener
@ListenGroup("core")
public class BootListener {
    private final GroupBootStateService groupBootStateService;

    public BootListener(GroupBootStateService groupBootStateService) {
        this.groupBootStateService = groupBootStateService;
    }

    @RobotListen(value = GroupMsg.class, permissions = Permissions.ADMINISTRATOR)
    @Filter(value = "开机", trim = true)
    @Priority(PriorityConstant.LAST)
    public void boot(@ContextValue(ContextType.GROUP) String groupCode, @ContextValue(ContextType.AT_LIST) List<String> atList) {
        if (atList.isEmpty() || atList.contains(RobotCore.getDefaultBotCode())) {
            groupBootStateService.setGroupState(groupCode, true);
        }
    }

    @RobotListen(value = GroupMsg.class, permissions = Permissions.ADMINISTRATOR)
    @Filter(value = "关机", trim = true)
    @Priority(PriorityConstant.LAST)
    public void down(@ContextValue(ContextType.GROUP) String groupCode, @ContextValue(ContextType.AT_LIST) List<String> atList) {
        if (atList.isEmpty() || atList.contains(RobotCore.getDefaultBotCode())) {
            groupBootStateService.setGroupState(groupCode, false);
        }
    }
}

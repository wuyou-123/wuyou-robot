package pers.wuyou.robot.util;

import org.springframework.stereotype.Component;
import pers.wuyou.robot.service.GroupBootStateService;

/**
 * @author wuyou
 */
@Component
public class GroupUtil {
    private static GroupBootStateService groupBootStateService;

    private GroupUtil(GroupBootStateService groupBootStateService) {
        GroupUtil.groupBootStateService = groupBootStateService;
    }

    public static boolean getGroupState(String groupCode) {
        return groupBootStateService.getGroupBootState(groupCode);
    }

    public static void setGroupState(String groupCode, boolean state) {
        groupBootStateService.bootOrShutDown(groupCode, state);
    }
}

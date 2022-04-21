package pers.wuyou.robot.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.wuyou.robot.core.entity.GroupBootState;

/**
 * @author wuyou
 * @since 2021-08-05
 */
public interface GroupBootStateService extends IService<GroupBootState> {

    /**
     * 开机或关机
     *
     * @param groupCode 群号
     * @param state     开关机状态
     */
    void setGroupState(String groupCode, boolean state);

}

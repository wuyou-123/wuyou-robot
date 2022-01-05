package pers.wuyou.robot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.wuyou.robot.entity.GroupBootState;

/**
 * <p>
 * 服务类
 * </p>
 *
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
    void bootOrShutDown(String groupCode, boolean state);

    /**
     * 获取群开关机状态
     *
     * @param groupCode 群号
     * @return 开关机状态
     */
    boolean getGroupBootState(String groupCode);
}

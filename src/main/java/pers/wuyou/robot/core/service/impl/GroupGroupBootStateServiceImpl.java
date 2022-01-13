package pers.wuyou.robot.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.entity.GroupBootState;
import pers.wuyou.robot.core.mapper.GroupBootStateMapper;
import pers.wuyou.robot.core.service.GroupBootStateService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-05
 */
@Service
@Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class GroupGroupBootStateServiceImpl extends ServiceImpl<GroupBootStateMapper, GroupBootState> implements GroupBootStateService {
    private final GroupBootStateMapper groupBootStateMapper;

    @Autowired
    public GroupGroupBootStateServiceImpl(GroupBootStateMapper groupBootStateMapper) {
        this.groupBootStateMapper = groupBootStateMapper;
    }

    @Override
    public void setGroupState(String groupCode, boolean state) {
        RobotCore.getBOOT_MAP().put(groupCode, state);
        LambdaQueryWrapper<GroupBootState> wrapper = new LambdaQueryWrapper<GroupBootState>().eq(GroupBootState::getGroupCode, groupCode);
        GroupBootState groupBootState = groupBootStateMapper.selectOne(wrapper);
        if (groupBootState == null) {
            groupBootState = new GroupBootState();
            groupBootState.setGroupCode(groupCode);
            groupBootState.setState(state);
            groupBootStateMapper.insert(groupBootState);
            return;
        }
        groupBootState.setState(state);
        groupBootStateMapper.update(groupBootState, wrapper);
    }

    @Override
    public boolean getGroupState(String groupCode) {
        LambdaQueryWrapper<GroupBootState> wrapper = new LambdaQueryWrapper<GroupBootState>().eq(GroupBootState::getGroupCode, groupCode);
        GroupBootState info = groupBootStateMapper.selectOne(wrapper);
        return info != null && info.getState();
    }

}

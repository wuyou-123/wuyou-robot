package pers.wuyou.robot.entity;

import lombok.Getter;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import pers.wuyou.robot.util.RobotUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wuyou
 */
@Getter
public class AccountInfo {
    private String code;
    private String nickname;
    private String avatarUrl;
    private List<String> groupList;

    public AccountInfo(String code, String group) {
        if (code == null || group == null) {
            return;
        }
        this.code = code;
        GroupMemberInfo memberInfo = RobotUtil.getter().getMemberInfo(group, code);
        this.nickname = memberInfo.getAccountNickname();
        this.avatarUrl = memberInfo.getAccountAvatar();
        this.groupList = new ArrayList<>(Collections.singleton(group));
    }

    public void addGroup(String group) {
        this.groupList.add(group);
    }
}

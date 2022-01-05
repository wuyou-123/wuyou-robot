package pers.wuyou.robot.entity;

import lombok.Getter;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import pers.wuyou.robot.common.RobotCore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wuyou
 */
@Getter
public class AccountInfo {
    private String code;
    private String nickname;
    private String avatarUrl;
    private Set<String> groupSet;

    public AccountInfo(String code, String group) {
        if (code == null || group == null) {
            return;
        }
        this.code = code;
        GroupMemberInfo memberInfo = RobotCore.getter().getMemberInfo(group, code);
        this.nickname = memberInfo.getAccountNickname();
        this.avatarUrl = memberInfo.getAccountAvatar();
        this.groupSet = new HashSet<>(Collections.singleton(group));
    }

    public void addGroup(String group) {
        this.groupSet.add(group);
    }
}

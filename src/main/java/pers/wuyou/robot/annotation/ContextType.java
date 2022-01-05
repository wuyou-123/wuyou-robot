package pers.wuyou.robot.annotation;

import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.assists.FlagContent;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.BotInfo;
import love.forte.simbot.api.message.containers.GroupCodeContainer;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.sender.Getter;
import pers.wuyou.robot.entity.Message;
import pers.wuyou.robot.util.CatUtil;

/**
 * @author wuyou
 */
public @interface ContextType {
    /**
     * 获取发送消息的人的QQ号
     * <p>
     * value: {@link AccountInfo#getAccountCode()}
     */
    String QQ = "qq";
    /**
     * 获取发送消息的群号
     * <p>
     * value: {@link GroupInfo#getGroupCode()}
     */
    String GROUP = "group";
    /**
     * 获取发送的消息内容
     * <p>
     * value: {@link MessageContent#getMsg()}
     */
    String MESSAGE = "message";
    /**
     * 获取bot的账号
     * <p>
     * value: {@link BotInfo#getAccountCode()}
     *
     * @see #QQ
     */
    String BOT_CODE = "botCode";
    /**
     * 获取bot的账号头像
     * <p>
     * value: {@link BotInfo#getAccountAvatar()}
     */
    String BOT_AVATAR = "botAvatar";
    /**
     * 获取bot的账号名称
     * <p>
     * value: {@link BotInfo#getAccountRemarkOrNickname()}
     */
    String BOT_NAME = "botName";

    /**
     * 获取发送消息的账号
     * <p>
     * value: {@link AccountInfo#getAccountCode()}
     *
     * @see #QQ
     */
    String CODE = QQ;
    /**
     * 获取发送消息的账号头像
     * <p>
     * value: {@link AccountInfo#getAccountAvatar()}
     */
    String AVATAR = "avatar";
    /**
     * 获取发送消息的账号名称
     * <p>
     * value: {@link AccountInfo#getAccountRemarkOrNickname()}
     */
    String NAME = "name";
    /**
     * 获取发送消息的账号备注
     * <p>
     * value: {@link AccountInfo#getAccountRemark()}
     */
    String REMARK = "remark";
    /**
     * 获取发送消息的账号昵称
     * <p>
     * value: {@link AccountInfo#getAccountNickname()}
     */
    String NICK_NAME = "nickname";
    /**
     * 获取发送消息的群号
     * <p>
     * value: {@link GroupInfo#getGroupCode()}
     *
     * @see #GROUP
     */
    String GROUP_CODE = GROUP;
    /**
     * 获取发送消息的群头像
     * <p>
     * value: {@link GroupInfo#getGroupAvatar()}
     */
    String GROUP_AVATAR = "groupAvatar";
    /**
     * 获取发送消息的群名称
     * <p>
     * value: {@link GroupInfo#getGroupName()}
     */
    String GROUP_NAME = "groupName";
    /**
     * 获取发送消息的艾特列表
     * <p>
     * value: {@link CatUtil#getAtList(MessageGet)}
     */
    String AT_LIST = "atList";
    /**
     * 获取发送消息的艾特列表
     * <p>
     * value: {@link CatUtil#getAts(MessageGet)}
     */
    String AT_SET = "atSet";
    /**
     * 获取发送消息的成员对象
     * <p>
     * value: {@link Getter#getMemberInfo(GroupCodeContainer)}
     */
    String MEMBER = "member";
    /**
     * 获取消息的flagId
     * <p>
     * value: {@link FlagContent#getId()}
     */
    String FLAG = "flag";
    /**
     * 获取消息实例
     * <p>
     * value: {@link Message}
     */
    String MESSAGE_ENTITY = "messageEntity";
    /**
     * 获取消息id
     * <p>
     * value: {@link MessageGet#getId()}
     */
    String ID = "id";
}

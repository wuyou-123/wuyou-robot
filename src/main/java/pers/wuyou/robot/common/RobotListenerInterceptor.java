package pers.wuyou.robot.common;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.containers.GroupContainer;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.intercept.InterceptionType;
import love.forte.simbot.listener.ListenerInterceptContext;
import love.forte.simbot.listener.ListenerInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.annotation.ContextType;
import pers.wuyou.robot.annotation.RobotListen;
import pers.wuyou.robot.util.CatUtil;
import pers.wuyou.robot.util.RobotUtil;
import pers.wuyou.robot.util.SenderUtil;
import pers.wuyou.robot.util.StringUtil;

/**
 * @author wuyou
 */
@Component
@Slf4j
public class RobotListenerInterceptor implements ListenerInterceptor {

    @NotNull
    @Override
    public InterceptionType intercept(@NotNull ListenerInterceptContext context) {
        final RobotListen annotation = context.getListenerFunction().getAnnotation(RobotListen.class);
        if (context.getMsgGet() instanceof GroupMsg) {
            // TODO: 添加群开关机状态判断
            final GroupMsg groupMsg = (GroupMsg) context.getMsgGet();
            setGroupInfo(context, groupMsg);
            context.getListenerContext().instant(ContextType.AT_LIST, groupMsg.getMsgContent().getCats(Constant.AT));
            context.getListenerContext().instant(ContextType.AT_SET, CatUtil.getAts(groupMsg));
            if (!groupMsg.getAccountInfo().getAnonymous()) {
                final GroupMemberInfo groupMemberInfo = RobotUtil.getter().getMemberInfo(groupMsg);
                context.getListenerContext().instant(ContextType.MEMBER, groupMemberInfo);
                if (annotation != null && groupMemberInfo.getPermission().getLevel() < annotation.permissions().getLevel()) {
                    SenderUtil.sendGroupMsg(groupMsg, annotation.noPermissionTip());
                    if (log.isInfoEnabled()) {
                        log.info(String.format("执行监听器%s(%s)失败, 权限不足", context.getListenerFunction().getName(), annotation.desc()));
                    }
                    return InterceptionType.INTERCEPT;
                }
            }
        }
        if (context.getMsgGet() instanceof MessageGet) {
            context.getListenerContext().instant(ContextType.MESSAGE, ((MessageGet) context.getMsgGet()).getMsg());
        }
        context.getListenerContext().instant(ContextType.QQ, context.getMsgGet().getAccountInfo().getAccountCode());
        context.getListenerContext().instant(ContextType.CODE, context.getMsgGet().getAccountInfo().getAccountCode());
        context.getListenerContext().instant(ContextType.AVATAR, StringUtil.isNullReturnEmpty(context.getMsgGet().getAccountInfo().getAccountAvatar()));
        context.getListenerContext().instant(ContextType.NAME, StringUtil.isNullReturnEmpty(context.getMsgGet().getAccountInfo().getAccountRemarkOrNickname()));
        context.getListenerContext().instant(ContextType.REMARK, StringUtil.isNullReturnEmpty(context.getMsgGet().getAccountInfo().getAccountRemark()));
        context.getListenerContext().instant(ContextType.NICK_NAME, StringUtil.isNullReturnEmpty(context.getMsgGet().getAccountInfo().getAccountNickname()));
        context.getListenerContext().instant(ContextType.BOT_CODE, StringUtil.isNullReturnEmpty(context.getMsgGet().getBotInfo().getBotCode()));
        context.getListenerContext().instant(ContextType.BOT_AVATAR, StringUtil.isNullReturnEmpty(context.getMsgGet().getBotInfo().getBotAvatar()));
        context.getListenerContext().instant(ContextType.BOT_NAME, StringUtil.isNullReturnEmpty(context.getMsgGet().getBotInfo().getBotName()));
        if (log.isInfoEnabled()) {
            if (annotation != null) {
                log.info(String.format("执行了监听器%s(%s)", context.getListenerFunction().getName(), annotation.desc()));
            } else {
                log.info(String.format("执行了监听器%s", context.getListenerFunction().getName()));
            }
        }
        return InterceptionType.PASS;
    }

    private void setGroupInfo(ListenerInterceptContext context, GroupContainer groupMsg) {
        context.getListenerContext().instant(ContextType.GROUP, groupMsg.getGroupInfo().getGroupCode());
        context.getListenerContext().instant(ContextType.GROUP_CODE, groupMsg.getGroupInfo().getGroupCode());
        context.getListenerContext().instant(ContextType.GROUP_AVATAR, StringUtil.isNullReturnEmpty(groupMsg.getGroupInfo().getGroupAvatar()));
        context.getListenerContext().instant(ContextType.GROUP_NAME, StringUtil.isNullReturnEmpty(groupMsg.getGroupInfo().getGroupName()));
    }
}

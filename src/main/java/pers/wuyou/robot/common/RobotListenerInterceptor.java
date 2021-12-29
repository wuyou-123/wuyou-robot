package pers.wuyou.robot.common;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.containers.GroupContainer;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.intercept.InterceptionType;
import love.forte.simbot.listener.ListenerContext;
import love.forte.simbot.listener.ListenerFunction;
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
        final ListenerFunction listenerFunction = context.getListenerFunction();
        final ListenerContext listenerContext = context.getListenerContext();
        final MsgGet msgGet = context.getMsgGet();
        final RobotListen annotation = listenerFunction.getAnnotation(RobotListen.class);
        if (msgGet instanceof GroupMsg) {
            // TODO: 添加群开关机状态判断
            final GroupMsg groupMsg = (GroupMsg) msgGet;
            setGroupInfo(listenerContext, groupMsg);
            listenerContext.instant(ContextType.AT_LIST, groupMsg.getMsgContent().getCats(Constant.AT));
            listenerContext.instant(ContextType.AT_SET, CatUtil.getAts(groupMsg));
            if (!groupMsg.getAccountInfo().getAnonymous()) {
                final GroupMemberInfo groupMemberInfo = RobotUtil.getter().getMemberInfo(groupMsg);
                listenerContext.instant(ContextType.MEMBER, groupMemberInfo);
                if (annotation != null && groupMemberInfo.getPermission().getLevel() < annotation.permissions().getLevel()) {
                    SenderUtil.sendGroupMsg(groupMsg, annotation.noPermissionTip());
                    if (log.isInfoEnabled()) {
                        log.info(String.format("执行监听器%s(%s)失败, 权限不足", listenerFunction.getName(), annotation.desc()));
                    }
                    return InterceptionType.INTERCEPT;
                }
            }
        }
        if (msgGet instanceof MessageGet) {
            listenerContext.instant(ContextType.MESSAGE, ((MessageGet) msgGet).getMsg());
        }
        listenerContext.instant(ContextType.QQ, msgGet.getAccountInfo().getAccountCode());
        listenerContext.instant(ContextType.CODE, msgGet.getAccountInfo().getAccountCode());
        listenerContext.instant(ContextType.AVATAR, StringUtil.isNullReturnEmpty(msgGet.getAccountInfo().getAccountAvatar()));
        listenerContext.instant(ContextType.NAME, StringUtil.isNullReturnEmpty(msgGet.getAccountInfo().getAccountRemarkOrNickname()));
        listenerContext.instant(ContextType.REMARK, StringUtil.isNullReturnEmpty(msgGet.getAccountInfo().getAccountRemark()));
        listenerContext.instant(ContextType.NICK_NAME, StringUtil.isNullReturnEmpty(msgGet.getAccountInfo().getAccountNickname()));
        listenerContext.instant(ContextType.BOT_CODE, StringUtil.isNullReturnEmpty(msgGet.getBotInfo().getBotCode()));
        listenerContext.instant(ContextType.BOT_AVATAR, StringUtil.isNullReturnEmpty(msgGet.getBotInfo().getBotAvatar()));
        listenerContext.instant(ContextType.BOT_NAME, StringUtil.isNullReturnEmpty(msgGet.getBotInfo().getBotName()));
        if (log.isInfoEnabled()) {
            if (annotation != null) {
                log.info(String.format("执行了监听器%s(%s)", listenerFunction.getName(), annotation.desc()));
            } else {
                log.info(String.format("执行了监听器%s", listenerFunction.getName()));
            }
        }
        return InterceptionType.PASS;
    }

    private void setGroupInfo(ListenerContext listenerContext, GroupContainer groupMsg) {
        listenerContext.instant(ContextType.GROUP, groupMsg.getGroupInfo().getGroupCode());
        listenerContext.instant(ContextType.GROUP_CODE, groupMsg.getGroupInfo().getGroupCode());
        listenerContext.instant(ContextType.GROUP_AVATAR, StringUtil.isNullReturnEmpty(groupMsg.getGroupInfo().getGroupAvatar()));
        listenerContext.instant(ContextType.GROUP_NAME, StringUtil.isNullReturnEmpty(groupMsg.getGroupInfo().getGroupName()));
    }
}

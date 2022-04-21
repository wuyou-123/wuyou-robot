package pers.wuyou.robot.core.common;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.assists.Permissions;
import love.forte.simbot.api.message.containers.AccountInfo;
import love.forte.simbot.api.message.containers.GroupContainer;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MessageRecallEventGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.intercept.InterceptionType;
import love.forte.simbot.listener.ListenerContext;
import love.forte.simbot.listener.ListenerFunction;
import love.forte.simbot.listener.ListenerInterceptContext;
import love.forte.simbot.listener.ListenerInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.entity.Message;
import pers.wuyou.robot.core.util.CatUtil;
import pers.wuyou.robot.core.util.SenderUtil;
import pers.wuyou.robot.core.util.StringUtil;

/**
 * @author wuyou
 */
@Component
@Slf4j
public class RobotListenerInterceptor implements ListenerInterceptor {

    @NotNull
    @Override
    public InterceptionType intercept(@NotNull ListenerInterceptContext context) {
        long start = System.currentTimeMillis();
        final ListenerFunction listenerFunction = context.getListenerFunction();
        final ListenerContext listenerContext = context.getListenerContext();
        final MsgGet msgGet = context.getMsgGet();
        final AccountInfo accountInfo = msgGet.getAccountInfo();
        final RobotListen annotation = listenerFunction.getAnnotation(RobotListen.class);
        if(accountInfo.getAccountCode().equals(RobotCore.getDefaultBotCode())){
            return InterceptionType.INTERCEPT;
        }
        if (!isBoot(context)) {
            return InterceptionType.INTERCEPT;
        }
        if (msgGet instanceof GroupMsg) {
            final GroupMsg groupMsg = (GroupMsg) msgGet;
            setGroupInfo(listenerContext, groupMsg);
            listenerContext.instant(ContextType.AT_LIST, CatUtil.getAtList(groupMsg));
            listenerContext.instant(ContextType.AT_SET, CatUtil.getAts(groupMsg));
            if (annotation != null && !groupMsg.getAccountInfo().getAnonymous() && annotation.permissions().getLevel() > Permissions.MEMBER.getLevel()) {
                final GroupMemberInfo groupMemberInfo = RobotCore.getter().getMemberInfo(groupMsg);
                if (Boolean.FALSE.equals(RobotCore.isBotAdministrator(accountInfo.getAccountCode()))
                        && groupMemberInfo.getPermission().getLevel() < annotation.permissions().getLevel()) {
                    SenderUtil.sendGroupMsg(groupMsg, annotation.noPermissionTip());
                    log.info(String.format("执行监听器%s(%s)失败, 权限不足", listenerFunction.getName(), annotation.desc()));
                    return InterceptionType.INTERCEPT;
                }
            }
        }
        if (msgGet instanceof MessageGet) {
            listenerContext.instant(ContextType.MESSAGE, ((MessageGet) msgGet).getMsg());
            listenerContext.instant(ContextType.TEXT, ((MessageGet) msgGet).getText());
            listenerContext.instant(ContextType.FLAG, ((MessageGet) msgGet).getFlag().getFlag().getId());
            listenerContext.instant(ContextType.MESSAGE_ENTITY, new Message((MessageGet) msgGet));
        }
        if (msgGet instanceof MessageRecallEventGet) {
            listenerContext.instant(ContextType.ID, msgGet.getId().substring(4));
        } else {
            listenerContext.instant(ContextType.ID, msgGet.getId());
        }
        listenerContext.instant(ContextType.QQ, accountInfo.getAccountCode());
        listenerContext.instant(ContextType.CODE, accountInfo.getAccountCode());
        listenerContext.instant(ContextType.AVATAR, StringUtil.isNullReturnEmpty(accountInfo.getAccountAvatar()));
        listenerContext.instant(ContextType.NAME, StringUtil.isNullReturnEmpty(accountInfo.getAccountRemarkOrNickname()));
        listenerContext.instant(ContextType.REMARK, StringUtil.isNullReturnEmpty(accountInfo.getAccountRemark()));
        listenerContext.instant(ContextType.NICK_NAME, StringUtil.isNullReturnEmpty(accountInfo.getAccountNickname()));
        listenerContext.instant(ContextType.BOT_CODE, StringUtil.isNullReturnEmpty(msgGet.getBotInfo().getBotCode()));
        listenerContext.instant(ContextType.BOT_AVATAR, StringUtil.isNullReturnEmpty(msgGet.getBotInfo().getBotAvatar()));
        listenerContext.instant(ContextType.BOT_NAME, StringUtil.isNullReturnEmpty(msgGet.getBotInfo().getBotName()));
        if (annotation != null) {
            log.info(String.format("执行了监听器%s(%s)", listenerFunction.getName(), annotation.desc()));
        } else {
            log.info(String.format("执行了监听器%s", listenerFunction.getName()));
        }
        log.info("执行拦截器耗时: " + (System.currentTimeMillis() - start));
        return InterceptionType.PASS;
    }

    private boolean isBoot(ListenerInterceptContext context) {
        final ListenerFunction listenerFunction = context.getListenerFunction();
        final MsgGet msgGet = context.getMsgGet();
        final RobotListen annotation = listenerFunction.getAnnotation(RobotListen.class);
        if (msgGet instanceof GroupMsg) {
            final GroupMsg groupMsg = (GroupMsg) msgGet;
            if (isBoot(context.getListenerFunction(), annotation, groupMsg.getGroupInfo().getGroupCode())) {
                assert annotation != null;
                log.info(String.format("执行监听器%s(%s)失败, 当前群未开机", listenerFunction.getName(), annotation.desc()));
                return false;
            }
        }
        return true;
    }

    private void setGroupInfo(ListenerContext listenerContext, GroupContainer groupMsg) {
        listenerContext.instant(ContextType.GROUP, groupMsg.getGroupInfo().getGroupCode());
        listenerContext.instant(ContextType.GROUP_CODE, groupMsg.getGroupInfo().getGroupCode());
        listenerContext.instant(ContextType.GROUP_AVATAR, StringUtil.isNullReturnEmpty(groupMsg.getGroupInfo().getGroupAvatar()));
        listenerContext.instant(ContextType.GROUP_NAME, StringUtil.isNullReturnEmpty(groupMsg.getGroupInfo().getGroupName()));
    }

    private boolean isBoot(ListenerFunction function, RobotListen annotation, String group) {
        return function.getGroups().stream().noneMatch(item -> item.getName().equals(Constant.CORE))
                && annotation != null && annotation.isBoot() && !RobotCore.getBOOT_MAP().get(group);
    }
}

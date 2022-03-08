package pers.wuyou.robot.core.util;

import catcode.Neko;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.assists.Flag;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.entity.BotSendMessage;
import pers.wuyou.robot.core.service.BotSendMessageService;

import java.util.Date;
import java.util.NoSuchElementException;

/**
 * 发送消息的工具类
 *
 * @author admin
 */
@Component
@Slf4j
@SuppressWarnings("unused")
public class SenderUtil {
    private static Sender sender;
    private static BotSendMessageService botSendMessageService;


    @Autowired
    public SenderUtil(BotManager manager, BotSendMessageService botSendMessageService) {
        setBotSendMessageService(botSendMessageService);
        setSender(manager.getDefaultBot().getSender().SENDER);
    }

    private static void setSender(Sender sender) {
        SenderUtil.sender = sender;
    }

    private static void setBotSendMessageService(BotSendMessageService botSendMessageService) {
        SenderUtil.botSendMessageService = botSendMessageService;
    }

    /**
     * 发送消息
     *
     * @param type    类型,group或private
     * @param code    群号或QQ号
     * @param message 消息内容
     */
    private static void sendMsg(SendType type, String code, String message) {
        sendMsg(type, code, "", message);
    }

    /**
     * 发送消息
     *
     * @param msgGet  msg
     * @param message 消息内容
     */
    public static void sendMsg(MessageGet msgGet, String message) {
        if (msgGet instanceof GroupMsg) {
            sendMsg(SendType.GROUP, ((GroupMsg) msgGet).getGroupInfo().getGroupCode(), "", message);
            return;
        }
        sendMsg(SendType.PRIVATE, msgGet.getAccountInfo().getAccountCode(), "", message);
    }

    /**
     * 发送群消息
     *
     * @param msgGet msg
     * @param msg    消息内容
     */
    public static void sendMsg(MessageGet msgGet, MessageContent msg) {
        sendMsg(msgGet, msg.getMsg());
    }

    /**
     * 发送群消息
     *
     * @param msgGet msg
     * @param neko   猫猫码
     */
    public static void sendMsg(MessageGet msgGet, Neko neko) {
        sendMsg(msgGet, neko.toString());
    }


    /**
     * 发送消息
     *
     * @param type    类型,group或private
     * @param code    群号或QQ号
     * @param message 消息内容
     */
    private static void sendMsg(SendType type, String code, String group, String message) {
        final BotSendMessage botSendMessage = BotSendMessage.builder()
                .content(message)
                .sendType(type.name())
                .botCode(RobotCore.getDefaultBotCode())
                .targetCode(code)
                .isSent(false)
                .sendTime(new Date())
                .build();
        if (message == null || message.isEmpty()) {
            return;
        }
        if (code == null || code.isEmpty()) {
            return;
        }
        try {
            Flag<?> flag = null;
            switch (type) {
                case GROUP:
                    flag = sender.sendGroupMsg(code, message).get();
                    break;
                case PRIVATE:
                    if (group != null && !group.isEmpty()) {
                        flag = sender.sendPrivateMsg(code, group, message).get();
                    } else {
                        flag = sender.sendPrivateMsg(code, message).get();
                    }
                    break;
                default:
            }
            assert flag != null;
            final String id = flag.getFlag().getId();
            botSendMessage.setFlag(id.substring(0, id.lastIndexOf("-")));
            botSendMessage.setIsSent(true);
            botSendMessage.setSendTime(new Date());
        } catch (NoSuchElementException e) {
            String prefix = "尝试给";
            if (message.startsWith(prefix)) {
                sendPrivateMsg(RobotCore.getADMINISTRATOR().get(0), String.format("%s%s[%s]发送消息失败, 消息内容已打印到日志中", prefix, type, code));
                log.error(message);
            } else {
                sendPrivateMsg(RobotCore.getADMINISTRATOR().get(0), String.format("%s%s[%s]发送消息: %s 失败", prefix, type, code, message));
            }
        }
        saveMessage(botSendMessage);
    }

    private static void saveMessage(BotSendMessage botSendMessage) {
//        RobotCore.THREAD_POOL.execute(() -> botSendMessageService.save(botSendMessage));
    }

    /**
     * 发送群消息
     *
     * @param msg     groupMsg 对象
     * @param message 消息内容
     */
    public static void sendGroupMsg(GroupMsg msg, String message) {
        sendMsg(SendType.GROUP, msg.getGroupInfo().getGroupCode(), message);
    }

    /**
     * 发送群消息
     *
     * @param group 群号
     * @param msg   消息内容
     */
    public static void sendGroupMsg(String group, MessageContent msg) {
        sendGroupMsg(group, msg.getMsg());
    }

    /**
     * 发送群消息
     *
     * @param group 群号
     * @param neko  猫猫码
     */
    public static void sendGroupMsg(String group, Neko neko) {
        sendGroupMsg(group, neko.toString());
    }

    /**
     * 发送群消息
     *
     * @param group   群号
     * @param message 消息内容
     */
    public static void sendGroupMsg(String group, String message) {
        sendMsg(SendType.GROUP, group, message);
    }

    /**
     * 发送私聊消息
     *
     * @param qq      QQ号
     * @param message 消息内容
     */
    public static void sendPrivateMsg(String qq, MessageContent message) {
        sendPrivateMsg(qq, message.getMsg());
    }

    /**
     * 发送私聊消息
     *
     * @param qq   QQ号
     * @param neko 猫猫码
     */
    public static void sendPrivateMsg(String qq, Neko neko) {
        sendPrivateMsg(qq, neko.toString());
    }

    /**
     * 发送私聊消息
     *
     * @param qq      QQ号
     * @param message 消息内容
     */
    public static void sendPrivateMsg(String qq, String message) {
        sendMsg(SendType.PRIVATE, qq, message);
    }

    /**
     * 发送私聊消息
     *
     * @param qq      QQ号
     * @param message 消息内容
     */
    public static void sendPrivateMsg(String qq, String group, String message) {
        sendMsg(SendType.PRIVATE, qq, group, message);
    }

    public enum SendType {
        /**
         * 消息类型
         */
        GROUP, PRIVATE
    }

}

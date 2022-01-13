package pers.wuyou.robot.core.util;

import catcode.Neko;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.RobotCore;

import java.util.NoSuchElementException;

/**
 * 发送消息的工具类
 *
 * @author admin
 */
@Component
@SuppressWarnings("unused")
public class SenderUtil {
    private static Sender sender;


    @Autowired
    public SenderUtil(BotManager manager) {
        setSender(manager.getDefaultBot().getSender().SENDER);
    }

    private static void setSender(Sender sender) {
        SenderUtil.sender = sender;
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
    private static synchronized void sendMsg(SendType type, String code, String group, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        if (code == null || code.isEmpty()) {
            return;
        }
        try {
            switch (type) {
                case GROUP:
                    sender.sendGroupMsg(code, message);
                    break;
                case PRIVATE:
                    if (group != null && !group.isEmpty()) {
                        sender.sendPrivateMsg(code, group, message);
                    } else {
                        sender.sendPrivateMsg(code, message);
                    }
                    break;
                default:
            }
        } catch (NoSuchElementException e) {
            sendPrivateMsg(RobotCore.getADMINISTRATOR().get(0), String.format("尝试给%s[%s]发送消息: %s 失败", type, code, message));
        }

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

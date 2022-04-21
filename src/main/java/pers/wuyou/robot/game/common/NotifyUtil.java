package pers.wuyou.robot.game.common;

import lombok.Setter;
import pers.wuyou.robot.core.util.SenderUtil;

/**
 * @author wuyou
 */
public class NotifyUtil {
    @Setter
    private static int level = 1;

    /**
     * 通知玩家及房间
     */
    public static void notify(BasePlayer<?> player, NotifyType.Notify notify, int level) {
        final BaseRoom<?> room = player.getRoom();
        final String msg = notify.getMsg();
        // 发送给其他人的消息
        final String s1 = msg.replace("${player}", player.toString()).replace("${tip}", notify.getMsg1());
        // 发送给当前玩家的消息
        final String s2 = msg.replace("${player}", "你").replace("${tip}", notify.getMsg2());
        for (BasePlayer<?> p : room.getPlayerList()) {
            sendPrivateMsg(p, p.equals(player) ? s2 : s1);
        }
        notifyRoom(player, s1, level);
    }

    /**
     * 通知房间
     */
    public static void notifyRoom(BasePlayer<?> player, NotifyType.Notify notify) {
        final String msg = notify.getMsg();
        final String s1 = msg.replace("${player}", player.toString()).replace("${tip}", notify.getMsg1());
        notifyRoom(player, s1, level);
    }


    /**
     * 通知玩家离开房间
     */
    public static void notifyPlayerExit(BasePlayer<?> player) {
        if (player.getRoom().getStatus().equals(RoomStatus.PLAYER_READY)) {
            notify(player, NotifyType.PLAYER_EXIT, 1);
        } else {
//            notify(player, NotifyType.PLAYER_EXIT_WITH_AGAIN, 1);
            notify(player, NotifyType.PLAYER_EXIT, 1);
        }
    }

    /**
     * 通知玩家离开房间
     */
    public static void notifyPlayerExit(BasePlayer<?> player, boolean sendGroup) {
//      if (player.getRoom().getStatus().equals(RoomStatus.PLAYER_READY)) {
        if (sendGroup) {
            notifyRoom(player, NotifyType.PLAYER_EXIT);
        } else {
            notify(player, NotifyType.PLAYER_EXIT, 1);
        }
//      } else {
//          notify(player, NotifyType.PLAYER_EXIT_WITH_AGAIN, 1);
//          notify(player, NotifyType.PLAYER_EXIT, 1);
//      }
    }

    /**
     * 通知玩家及房间
     */
    public static void notify(BaseRoom<?> room, NotifyType.Notify notify) {
        notify(room, notify.getMsg(), 1);
    }

    /**
     * 通知玩家
     */
    public static void notifyPlayer(BasePlayer<?> player, String msg) {
        sendPrivateMsg(player, msg);
    }

    /**
     * 通知玩家
     */
    public static void notifyPlayer(BasePlayer<?> player, NotifyType.Notify notify) {
        sendPrivateMsg(player, notify.getMsg());
    }

    /**
     * 通知房间
     */
    public static void notifyRoom(BaseRoom<?> room, String msg) {
        notifyRoom(room, msg, 1);
    }

    /**
     * 通知房间
     */
    public static void notifyRoom(BaseRoom<?> room, NotifyType.Notify notify) {
        notifyRoom(room, notify.getMsg(), 1);
    }

    /**
     * 通知房间
     */
    public static void notifyRoom(String groupCode, String msg) {
        SenderUtil.sendGroupMsg(groupCode, msg);
    }


    /**
     * 玩家在房间内聊天
     */
    public static void notifyPlayerSpeak(BasePlayer<?> player, String message) {
        notify(player, NotifyType.PLAYER_SPEAK, 3);
        for (BasePlayer<?> p : player.getRoom().getPlayerList()) {
            notifyPlayer(p, message);
        }
    }

    /**
     * 只通知玩家
     *
     * @param sendGroup 如果为true,则也通知群
     */
    protected static void sendPrivateMsg(BasePlayer<?> player, String message, boolean sendGroup) {
        SenderUtil.sendPrivateMsg(player.getId(), player.getRoomId(), message);
        if (!player.isPrivateMessage() && sendGroup) {
            SenderUtil.sendGroupMsg(player.getRoomId(), message);
        }
    }

    /**
     * 只通知玩家
     */
    protected static void sendPrivateMsg(BasePlayer<?> player, NotifyType.Notify notify) {
        sendPrivateMsg(player, notify.getMsg(), true);
    }

    /**
     * 通知玩家及房间
     */
    protected static void notify(BaseRoom<?> room, String msg, int level) {
        for (BasePlayer<?> p : room.getPlayerList()) {
            sendPrivateMsg(p, msg);
        }
        notifyRoom(room, msg, level);
    }

    /**
     * 只通知玩家
     */
    protected static void sendPrivateMsg(BasePlayer<?> player, String message) {
        sendPrivateMsg(player, message, false);
    }

    /**
     * 只通知房间
     */
    protected static void notifyRoom(BaseRoom<?> room, String msg, int level) {
        if (NotifyUtil.level >= level) {
            SenderUtil.sendGroupMsg(room.getId(), msg);
        }
    }

    /**
     * 只通知房间
     */
    protected static void notifyRoom(BasePlayer<?> player, String msg, int level) {
        notifyRoom(player.getRoom(), msg, level);
    }

    /**
     * 只通知房间
     */
    protected static void notifyRoom(BasePlayer<?> player, String msg) {
        notifyRoom(player.getRoom(), msg, 1);
    }

}

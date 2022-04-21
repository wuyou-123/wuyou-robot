package pers.wuyou.robot.game.gobang.util;

import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.util.CatUtil;
import pers.wuyou.robot.core.util.SenderUtil;
import pers.wuyou.robot.game.common.*;
import pers.wuyou.robot.game.gobang.common.GobangNotifyType;
import pers.wuyou.robot.game.gobang.common.GobangRoomStatus;
import pers.wuyou.robot.game.gobang.entity.GobangPlayer;

/**
 * @author wuyou
 */
public class GobangNotifyUtil extends NotifyUtil {

    private GobangNotifyUtil() {
    }

    public static void notifyPlayerChooseMode(GobangPlayer player) {
        if (player.getRoom().getStatus().equals(GobangRoomStatus.WAIT_PLAYER_AGAIN)) {
            notifyPlayer(player, GobangNotifyType.CHOOSE_MODE);
        } else {
            notifyRoom(player.getRoom(), GobangNotifyType.CHOOSE_MODE);
        }
    }

    public static void notifyPlayerChooseFirstHand(GobangPlayer player, boolean isInPrivate) {
        if (!isInPrivate) {
            notifyRoom(player.getRoom(), "单人模式请在私聊中继续游戏");
        }
        notifyPlayer(player, GobangNotifyType.CHOOSE_FIRST_HAND);
    }

    public static void notifyRoomWaitOtherPlayer(GobangPlayer player) {
        notifyRoom(player, "创建房间成功");
        notifyRoom(player.getRoom(), GobangNotifyType.WAIT_OTHER_PLAYER);
    }

    public static void notifyPlayerJoinSuccess(BasePlayer<?> player) {
        notifyRoom(player, "加入房间成功");
        notifyPlayerStatus(player);
    }

    /**
     * 通知玩家准备
     */
    public static void notifyPlayerReady(BasePlayer<?> player) {
        notifyRoom(player, NotifyType.PLAYER_READY);
        notifyPlayerStatus(player);
    }

    /**
     * 通知玩家当前状态
     */
    public static void notifyPlayerStatus(BasePlayer<?> player) {
        final BaseRoom<?> room = player.getRoom();
        String msg = room.getPlayerStatus();
        notifyRoom(room, msg, 1);
        if (msg.contains(PlayerGameStatus.NO_READY)) {
            notifyRoom(room, "群聊或私聊发送\"准备\"后准备", 1);
        }
    }

    public static void notifyFirstHand(GobangPlayer firstPlayer) {
        notifyRoom(firstPlayer.getRoom(), "现在是" + CatUtil.at(firstPlayer.getId()) + "的先手");
    }

    public static void notifyPlayerChooseChess(GobangPlayer player) {
        if (player.getRoom().playWithBot()) {
            notifyPlayer(player, "请落子");
        } else {
            notifyRoom(player.getRoom(), "现在轮到 " + CatUtil.at(player.getId()) + "了, 你是" + player.getColor().toString() + "棋子, 请落子");
        }
    }

    public static void notifyPlayerLose(GobangPlayer player) {
        if (player.getRoom().playWithBot()) {
            // 电脑赢了
            notifyPlayer(player, "哈哈, 我赢了");
            notifyAdmin("bot win, " + player + " lose" + player.getRoom().getBoardImage());
        } else {
            // 当前玩家输了
            notifyRoom(player.getRoom(), player + "赢了");
            notifyAdmin(player + " win, " + player.getNext() + " lose" + player.getRoom().getBoardImage());
        }
    }

    public static void notifyPlayerWin(GobangPlayer player) {
        if (player.getRoom().playWithBot()) {
            // 电脑输了
            notifyPlayer(player, "呜呜呜,我输了");
            notifyAdmin(player + " win, bot lose" + player.getRoom().getBoardImage());
        } else {
            // 当前玩家赢了
            notifyRoom(player.getRoom(), player + "赢了");
            notifyAdmin(player + " win, " + player.getNext() + " lose" + player.getRoom().getBoardImage());
        }
    }

    public static void notifyPlayerWillLose(GobangPlayer player) {
        if (player.getRoom().playWithBot()) {
            notifyPlayer(player, "嘿嘿, 你要输了哦");
            notifyAdmin("bot will win, " + player + " will lose" + player.getRoom().getBoardImage());
        } else {
            // 当前玩家要输了
            notifyRoom(player.getRoom(), player.getNext() + "要赢了");
            notifyAdmin(player.getNext() + " will win, " + player + " will lose" + player.getRoom().getBoardImage());
        }
    }

    public static void notifyPlayerWillWin(GobangPlayer player) {
        if (player.getRoom().playWithBot()) {
            notifyPlayer(player, "啊, 我是不是要输了");
            notifyAdmin(player + " will win, bot will lose" + player.getRoom().getBoardImage());
        } else {
            // 当前玩家要赢了
            notifyRoom(player.getRoom(), player.getNext() + "要赢了");
            notifyAdmin(player.getNext() + " will win, " + player + " will lose" + player.getRoom().getBoardImage());
        }
    }

    private static void notifyAdmin(String str) {
        SenderUtil.sendPrivateMsg(RobotCore.getADMINISTRATOR().get(0), str);
    }

    public static void notifyPlayerCannotChess(GobangPlayer player) {
        if (player.getRoom().playWithBot()) {
            notifyPlayer(player, "不可以下在这里哦");
        } else {
            // 当前玩家要赢了
            notifyRoom(player, CatUtil.at(player.getId()) + "不可以下在这里哦");
        }
    }
}

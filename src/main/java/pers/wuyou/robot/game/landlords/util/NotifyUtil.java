package pers.wuyou.robot.game.landlords.util;

import cn.hutool.core.thread.ThreadUtil;
import lombok.Setter;
import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.entity.PokerSell;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.NotifyType;
import pers.wuyou.robot.game.landlords.enums.PlayerGameStatus;
import pers.wuyou.robot.game.landlords.enums.RoomStatus;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.util.SenderUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuyou
 */
public class NotifyUtil {
    @Setter
    private static int level = 1;

    private NotifyUtil() {
    }

    /**
     * 通知玩家离开房间
     */
    public static void notifyPlayerExit(Player player) {
        if (player.getRoom().getStatus() == RoomStatus.PLAYER_READY) {
            notify(player, NotifyType.PLAYER_EXIT, 1);
        } else {
            notify(player, NotifyType.PLAYER_EXIT_WITH_AGAIN, 1);
        }
    }

    /**
     * 通知玩家出的牌不合法
     */
    public static void notifyPlayerPlayPokerInvalid(Player player) {
        sendPrivateMsg(player, NotifyType.PLAYER_PLAY_INVALID);
        notifyPlayerPlayPoker(player);
//        player.setStatus(PlayerGameStatus.CHOOSE);
    }

    /**
     * 通知玩家选择的牌型不匹配
     */
    public static void notifyPlayerTypePokerInvalid(Player player) {
        final Room room = player.getRoom();
        sendPrivateMsg(player, String.format("牌型不匹配, 应该选择%s", room.getLastPlayPoker().getSellType().getMsg()), true);
    }

    /**
     * 通知当前玩家不能跳过
     */
    public static void notifyPlayerCantPass(Player player) {
        sendPrivateMsg(player, NotifyType.PLAYER_CANT_PASS);
    }

    /**
     * 通知玩家当前玩家跳过
     */
    public static void notifyPlayerPass(Player player) {
        final Room room = player.getRoom();
        notify(player, NotifyType.PLAYER_PASS, 2);
        final Player next = player.getNext();
        NotifyUtil.notify(next, NotifyType.NOTIFY_PLAYER_PLAY, 2);
        NotifyUtil.notifyPlayerPokerCount(next);
        NotifyUtil.notifyPlayerPlayPoker(next);
        List<PokerSell> sells = PokerHelper.validSells(PokerHelper.checkPokerType(room.getLastSellPokers()), next.getPokers());
        if (sells.isEmpty()) {
            NotifyUtil.notifyPlayerNoPokerBiggerThanEveryone(next);
        }

    }

    /**
     * 通知玩家出的牌比之前的小
     */
    public static void notifyPlayerPlayPokerLess(Player player) {
        sendPrivateMsg(player, NotifyType.PLAYER_PLAY_LESS);
        notifyPlayerPlayPoker(player);
//        player.setStatus(PlayerGameStatus.CHOOSE);
    }

    /**
     * 通知玩家出的牌不匹配
     *
     * @param currentPokerShell 玩家出的牌
     */
    public static void notifyPlayerPlayPokerMisMatch(Player player, PokerSell currentPokerShell) {
        final Room room = player.getRoom();
        final String preType = room.getLastPlayPoker().getSellType().getMsg();
        final int preCount = room.getLastSellPokers().size();
        final String playType = currentPokerShell.getSellType().getMsg();
        final int playCount = currentPokerShell.getSellPokers().size();
        sendPrivateMsg(player, String.format(
                "你出的牌是%s (%s), 但是之前的牌是%s (%s). 不匹配!",
                playType, playCount, preType, preCount
        ), true);

        notifyPlayerPlayPoker(player);
//        player.setStatus(PlayerGameStatus.CHOOSE);

    }

    /**
     * 通知玩家不能出牌
     */
    public static void notifyPlayerCantPlay(Player player) {
        sendPrivateMsg(player, "现在不是你的回合,不允许出牌!", true);
    }

    /**
     * 通知玩家不是你的回合
     */
    public static void notifyPlayerNotYourRound(Player player) {
        sendPrivateMsg(player, "现在不是你的回合!", true);
    }

    /**
     * 通知玩家选择编号
     */
    public static void notifyPlayerChoosePokers(Player player, List<PokerSell> list) {
        if (list.isEmpty()) {
            sendPrivateMsg(player, "没有可以匹配到的牌");
            return;
        }
        final StringBuilder sb = new StringBuilder("请选择你要出的牌\n");
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(". ").append(PokerHelper.textOnlyNoType(list.get(i).getSellPokers())).append("\n");
        }
        sendPrivateMsg(player, sb.toString());
    }

    /**
     * 通知玩家选择编号
     *
     * @see #notifyPlayerChoosePokers(Player, List)
     */
    @SuppressWarnings("unchecked")
    public static void notifyPlayerChoosePokers(Player player) {
        final List<PokerSell> list = (List<PokerSell>) GameManager.getPlayerData(player, "list");
        notifyPlayerChoosePokers(player, list);
    }

    /**
     * 通知玩家出牌
     */
    public static void notifyPlayerPlayPoker(Player player) {
        sendPrivateMsg(player, "轮到你出牌了, 你的牌如下: ");
        sendPrivateMsg(player, PokerHelper.getPoker(player));
        sendPrivateMsg(player, "请输入您想出的牌");
        player.setStatus(PlayerGameStatus.CHOOSE);

    }

    /**
     * 其他玩家出牌
     */
    public static void notifyPlayPoker(Player player) {
        final Room room = player.getRoom();
        final PokerSell lastPlayPoker = room.getLastPlayPoker();
        if (lastPlayPoker != null) {
            notify(player, NotifyType.PLAYER_PLAY, 2);
            notifyPokers(room, lastPlayPoker.getSellPokers(), 2);
            switch (player.getPokers().size()) {
                case 1:
                    notify(player, NotifyType.PLAYER_ONLY_ONE_POKER, 2);
                    break;
                case 2:
                    notify(player, NotifyType.PLAYER_ONLY_TWO_POKER, 2);
                    break;
                default:
            }
        }

    }

    /**
     * 玩家获胜
     */
    public static void notifyPlayWin(Player player) {
        final Room room = player.getRoom();
        final List<String> winners = room.getPlayerList().stream().filter(item -> item.getType() == player.getType()).map(Player::getName).collect(Collectors.toList());
        notify(room, String.format("玩家 %s[%s]赢得了比赛", winners, player.getType().getName()), 1);
        notify(player, NotifyType.GAME_END, 1);

    }

    /**
     * 通知玩家没有牌能打过大家
     */
    public static void notifyPlayerNoPokerBiggerThanEveryone(Player player) {
        final Room room = player.getRoom();
        if (!room.getLastPlayer().equals(player)) {
            sendPrivateMsg(player, "没有牌能大过大家...");
        }
    }

    /**
     * 通知玩家上下家以及剩余牌数
     */
    public static void notifyPlayerPokerCount(Player player) {
        final Player pre = player.getPre();
        final Player next = player.getNext();
        sendPrivateMsg(player,
                String.format("上家 %s [%s](剩余%s张)\n下家 %s [%s](剩余%s张)",
                        pre.getName(), pre.getType().getName(), pre.getPokers().size(),
                        next.getName(), next.getType().getName(), next.getPokers().size()
                )
        );
    }

    /**
     * 通知玩家准备
     */
    public static void notifyPlayerReady(Player player) {
        notify(player, NotifyType.PLAYER_READY, 1);
        notifyPlayerStatus(player);
    }

    /**
     * 通知玩家游戏开始
     */
    public static void notifyPlayerStart(Player player) {
        sendPrivateMsg(player, "游戏开始! 你的牌如下: ");
        sendPrivateMsg(player, PokerHelper.getPoker(player));
    }

    /**
     * 通知玩家加入房间成功并发送房间内玩家状态
     */
    public static void notifyPlayerCreateSuccess(Player player) {
        notifyRoom(player, "创建房间成功", 1);
        notifyPlayerStatus(player);
    }

    /**
     * 通知玩家加入房间成功并发送房间内玩家状态
     */
    public static void notifyPlayerJoinSuccess(Player player) {
        notifyRoom(player, "加入房间成功", 1);
        notifyPlayerStatus(player);
    }

    /**
     * 通知玩家当前状态
     */
    public static void notifyPlayerStatus(Player player) {
        final Room room = player.getRoom();
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("当前房间内有%s人\n", room.getPlayerList().size()));
        for (Player p : room.getPlayerList()) {
            sb.append(p).append(" ").append(p.getStatus().getMsg()).append("\n");
        }
        final String msg = sb.toString();
        notify(room, msg, 1);
        if (msg.contains(PlayerGameStatus.NO_READY.getMsg())) {
            notifyRoom(room, "私聊机器人发送\"准备\"后准备", 1);
        }
    }

    /**
     * 通知玩家及房间
     */
    public static void notify(Player player, NotifyType notifyType, int level) {
        final Room room = player.getRoom();
        final String msg = notifyType.getMsg();
        // 发送给其他人的消息
        final String s1 = msg.replace("${player}", player.toString()).replace("${tip}", notifyType.getMsg1());
        // 发送给当前玩家的消息
        final String s2 = msg.replace("${player}", "你").replace("${tip}", notifyType.getMsg2());
        for (Player p : room.getPlayerList()) {
            sendPrivateMsg(p, p.equals(player) ? s2 : s1);
        }
        notifyRoom(player, s1, level);
    }

    /**
     * 发送扑克牌
     */
    public static void notifyPokers(Room room, List<Poker> pokers, int level) {
        final String msg = PokerHelper.getPoker(pokers);
        notify(room, msg, level);
        ThreadUtil.sleep(500);
    }

    /**
     * 通知玩家
     */
    public static void notifyPlayer(Player player, String msg) {
        sendPrivateMsg(player, msg);
    }

    /**
     * 通知房间
     */
    public static void notifyRoom(Room room, String msg) {
        notifyRoom(room, msg, 1);
    }

    /**
     * 通知房间
     */
    public static void notifyRoom(String groupCode, String msg) {
        SenderUtil.sendGroupMsg(groupCode, msg);
    }

    /**
     * 通知玩家及房间
     */
    private static void notify(Room room, String msg, int level) {
        for (Player p : room.getPlayerList()) {
            sendPrivateMsg(p, msg);
        }
        notifyRoom(room, msg, level);
    }

    /**
     * 通知玩家及房间
     *
     * @see #notifyRoom(Room, String, int)
     */
    public static void notify(Room room, NotifyType notifyType, int level) {
        notify(room, notifyType.getMsg(), level);
    }

    /**
     * 只通知房间
     */
    private static void notifyRoom(Room room, String msg, int level) {
        if (NotifyUtil.level >= level) {
            SenderUtil.sendGroupMsg(room.getId(), msg);
        }
    }

    /**
     * 只通知房间
     *
     * @see #notifyRoom(Room, String, int)
     */
    private static void notifyRoom(Player player, String msg, int level) {
        notifyRoom(player.getRoom(), msg, level);
    }

    /**
     * 只通知玩家
     *
     * @param sendGroup 如果为true,则也通知群
     */
    private static void sendPrivateMsg(Player player, String message, boolean sendGroup) {
        SenderUtil.sendPrivateMsg(player.getId(), player.getRoomId(), message);
        if (!player.isCurrentMessageIsPrivate() && sendGroup) {
            SenderUtil.sendGroupMsg(player.getRoomId(), message);
        }
    }

    /**
     * 只通知玩家
     *
     * @see #sendPrivateMsg(Player, String, boolean)
     */
    private static void sendPrivateMsg(Player player, NotifyType notifyType) {
        sendPrivateMsg(player, notifyType.getMsg(), true);
    }

    /**
     * 只通知玩家
     *
     * @see #sendPrivateMsg(Player, String, boolean)
     */
    private static void sendPrivateMsg(Player player, String message) {
        sendPrivateMsg(player, message, false);
    }

}

package pers.wuyou.robot.game.landlords.util;

import cn.hutool.core.thread.ThreadUtil;
import pers.wuyou.robot.game.common.*;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.entity.PokerSell;
import pers.wuyou.robot.game.landlords.common.LandlordsNotifyType;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuyou
 */
public class LandlordsNotifyUtil extends NotifyUtil {

    private LandlordsNotifyUtil() {
    }

    /**
     * 通知玩家加入房间成功并发送房间内玩家状态
     */
    public static void notifyPlayerCreateSuccess(BasePlayer<?> player) {
        notifyRoom(player, "创建房间成功", 1);
        notifyPlayerStatus(player);
    }

    /**
     * 通知玩家加入房间成功并发送房间内玩家状态
     */
    public static void notifyPlayerJoinSuccess(BasePlayer<?> player) {
        notifyRoom(player, "加入房间成功", 1);
        notifyPlayerStatus(player);
    }

    /**
     * 通知玩家准备
     */
    public static void notifyPlayerReady(BasePlayer<?> player) {
        notify(player, NotifyType.PLAYER_READY, 1);
        notifyPlayerStatus(player);
    }

    /**
     * 通知玩家当前状态
     */
    public static void notifyPlayerStatus(BasePlayer<?> player) {
        final BaseRoom<?> room = player.getRoom();
        String msg = room.getPlayerStatus();
        notify(room, msg, 1);
        if (msg.contains(PlayerGameStatus.NO_READY)) {
            notifyRoom(room, "私聊机器人发送\"准备\"后准备", 1);
        }
    }

    /**
     * 通知玩家出的牌不合法
     */
    public static void notifyPlayerPlayPokerInvalid(LandlordsPlayer player) {
        sendPrivateMsg(player, LandlordsNotifyType.PLAYER_PLAY_INVALID);
        notifyPlayerPlayPoker(player);
    }

    /**
     * 通知玩家选择的牌型不匹配
     */
    public static void notifyPlayerTypePokerInvalid(LandlordsPlayer player) {
        final LandlordsRoom room = player.getRoom();
        sendPrivateMsg(player, String.format("牌型不匹配, 应该选择%s", room.getLastPlayPoker().getSellType().getMsg()), true);
    }

    /**
     * 通知当前玩家不能跳过
     */
    public static void notifyPlayerCantPass(LandlordsPlayer player) {
        sendPrivateMsg(player, LandlordsNotifyType.PLAYER_CANT_PASS);
    }

    /**
     * 通知玩家当前玩家跳过
     */
    public static void notifyPlayerPass(LandlordsPlayer player) {
        final LandlordsRoom room = player.getRoom();
        notify(player, LandlordsNotifyType.PLAYER_PASS, 2);
        final LandlordsPlayer next = player.getNext();
        LandlordsNotifyUtil.notify(next, LandlordsNotifyType.NOTIFY_PLAYER_PLAY, 2);
        LandlordsNotifyUtil.notifyPlayerPokerCount(next);
        LandlordsNotifyUtil.notifyPlayerPlayPoker(next);
        List<PokerSell> sells = PokerHelper.validSells(PokerHelper.checkPokerType(room.getLastSellPokers()), next.getPokers());
        if (sells.isEmpty()) {
            LandlordsNotifyUtil.notifyPlayerNoPokerBiggerThanEveryone(next);
        }

    }

    /**
     * 通知玩家出的牌比之前的小
     */
    public static void notifyPlayerPlayPokerLess(LandlordsPlayer player) {
        sendPrivateMsg(player, LandlordsNotifyType.PLAYER_PLAY_LESS);
        notifyPlayerPlayPoker(player);
    }

    /**
     * 通知玩家出的牌不匹配
     *
     * @param currentPokerShell 玩家出的牌
     */
    public static void notifyPlayerPlayPokerMisMatch(LandlordsPlayer player, PokerSell currentPokerShell) {
        final LandlordsRoom room = player.getRoom();
        final String preType = room.getLastPlayPoker().getSellType().getMsg();
        final int preCount = room.getLastSellPokers().size();
        final String playType = currentPokerShell.getSellType().getMsg();
        final int playCount = currentPokerShell.getSellPokers().size();
        sendPrivateMsg(player, String.format(
                "你出的牌是%s (%s), 但是之前的牌是%s (%s). 不匹配!",
                playType, playCount, preType, preCount
        ), true);

        notifyPlayerPlayPoker(player);

    }

    /**
     * 通知玩家不是你的回合
     */
    public static void notifyPlayerNotYourRound(LandlordsPlayer player) {
        sendPrivateMsg(player, "现在不是你的回合!", true);
    }

    /**
     * 通知玩家选择编号
     */
    public static void notifyPlayerChoosePokers(LandlordsPlayer player, List<PokerSell> list) {
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
     */
    @SuppressWarnings("unchecked")
    public static void notifyPlayerChoosePokers(LandlordsPlayer player) {
        final List<PokerSell> list = (List<PokerSell>) player.getRoom().getPlayerData(player, "list");
        notifyPlayerChoosePokers(player, list);
    }

    /**
     * 通知玩家出牌
     */
    public static void notifyPlayerPlayPoker(LandlordsPlayer player) {
        sendPrivateMsg(player, "轮到你出牌了, 你的牌如下: ");
        sendPrivateMsg(player, PokerHelper.getPoker(player));
        sendPrivateMsg(player, "请输入您想出的牌");
        player.setStatus(LandlordsPlayerGameStatus.CHOOSE);

    }

    /**
     * 其他玩家出牌
     */
    public static void notifyPlayPoker(LandlordsPlayer player) {
        final LandlordsRoom room = player.getRoom();
        final PokerSell lastPlayPoker = room.getLastPlayPoker();
        if (lastPlayPoker != null) {
            notify(player, LandlordsNotifyType.PLAYER_PLAY, 2);
            notifyPokers(room, lastPlayPoker.getSellPokers(), 2);
            switch (player.getPokers().size()) {
                case 1:
                    notify(player, LandlordsNotifyType.PLAYER_ONLY_ONE_POKER, 2);
                    break;
                case 2:
                    notify(player, LandlordsNotifyType.PLAYER_ONLY_TWO_POKER, 2);
                    break;
                default:
            }
        }

    }

    /**
     * 玩家获胜
     */
    public static void notifyPlayWin(LandlordsPlayer player) {
        final LandlordsRoom room = player.getRoom();
        final List<String> winners = room.getPlayerList().stream().filter(item -> item.getType() == player.getType()).map(BasePlayer::getName).collect(Collectors.toList());
        notify(room, String.format("玩家 %s[%s]赢得了比赛", winners, player.getType().getName()), 1);
        notify(player, LandlordsNotifyType.GAME_END, 1);

    }

    /**
     * 通知玩家没有牌能打过大家
     */
    public static void notifyPlayerNoPokerBiggerThanEveryone(LandlordsPlayer player) {
        final LandlordsRoom room = player.getRoom();
        if (!room.getLastPlayer().equals(player)) {
            sendPrivateMsg(player, "没有牌能大过大家...");
        }
    }

    /**
     * 通知玩家上下家以及剩余牌数
     */
    public static void notifyPlayerPokerCount(LandlordsPlayer player) {
        final LandlordsPlayer pre = player.getPre();
        final LandlordsPlayer next = player.getNext();
        sendPrivateMsg(player,
                String.format("上家 %s [%s](剩余%s张)%n下家 %s [%s](剩余%s张)",
                        pre.getName(), pre.getType().getName(), pre.getPokers().size(),
                        next.getName(), next.getType().getName(), next.getPokers().size()
                )
        );
    }

    /**
     * 通知玩家游戏开始
     */
    public static void notifyPlayerStart(LandlordsPlayer player) {
        sendPrivateMsg(player, "游戏开始! 你的牌如下: ");
        sendPrivateMsg(player, PokerHelper.getPoker(player));
    }


    /**
     * 发送扑克牌
     */
    public static void notifyPokers(LandlordsRoom room, List<Poker> pokers, int level) {
        final String msg = PokerHelper.getPoker(pokers);
        notify(room, msg, level);
        ThreadUtil.sleep(500);
    }


}

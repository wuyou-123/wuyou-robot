package pers.wuyou.robot.game.landlords.common;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.GameEventManager;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.entity.PokerSell;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author wuyou
 */
public class MessageDispenser {
    private static final List<String> CALL_LANDLORDS_CMD_LIST = Arrays.asList("抢", "抢地主", "是", "y", "yes", "确认");
    private static final List<String> NOT_CALL_LANDLORDS_CMD_LIST = Arrays.asList("不抢", "不抢地主", "不", "否", "n", "no");
    private static final List<String> PASS_CMD_LIST = Arrays.asList("不要", "不出", "过", "要不起", "no", "pass", "p");
    private static final List<String> EXIT_CMD_LIST = Arrays.asList("退出", "退出游戏", "离开", "离开房间", "不玩了", "我不玩了", "exit", "e");
    private static final List<String> READY_CMD_LIST = Arrays.asList("继续", "准备", "再来一把", "再来", "好了", "ok", "ready", "again");

    private MessageDispenser() {
    }

    public static boolean callLandlords(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        String qq = data.get(Constant.ACCOUNT_CODE).toString();
        LandlordsRoom room = (LandlordsRoom) Game.getPlayer(qq).getRoom();
        final LandlordsPlayer currentPlayer = room.getCurrentPlayer();
        if (CALL_LANDLORDS_CMD_LIST.contains(message)) {
            if (currentPlayer != null && currentPlayer.equals(qq)) {
                data.put("select", true);
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_CALL_LANDLORDS, data);
            } else {
                LandlordsNotifyUtil.notifyPlayerNotYourRound((LandlordsPlayer) Game.getPlayer(qq));
            }
            return false;
        }
        if (NOT_CALL_LANDLORDS_CMD_LIST.contains(message)) {
            if (currentPlayer != null && currentPlayer.equals(qq)) {
                data.put("select", false);
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_CALL_LANDLORDS, data);
            } else {
                LandlordsNotifyUtil.notifyPlayerNotYourRound((LandlordsPlayer) Game.getPlayer(qq));
            }
            return false;
        }
        return true;
    }

    public static boolean playerPoker(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        if (EXIT_CMD_LIST.contains(message)) {
            return true;
        }
        final Character[] pokerList = PokerHelper.parsePoker(message);
        String qq = data.get(Constant.ACCOUNT_CODE).toString();
        LandlordsRoom room = (LandlordsRoom) Game.getPlayer(qq).getRoom();
        final LandlordsPlayer currentPlayer = room.getCurrentPlayer();
        if (currentPlayer == null || !currentPlayer.equals(qq)) {
            return true;
        }
        data.put("pokerList", pokerList);
        LandlordsPlayer player = (LandlordsPlayer) Game.getPlayer(qq);
        if (player.getStatus().equals(LandlordsPlayerGameStatus.CHOOSE_TIP)) {
            // 玩家在选择提示内容
            playerChoose(data);
            return false;
        }
        if (pokerList == null) {
            // 发的消息不是任何牌型
            if (PASS_CMD_LIST.contains(message)) {
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_PASS, data);
                return false;
            }
            data.put(Constant.MESSAGE, message);
            GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_MESSAGE_ON_ROUND, data);
            return false;
        }
        GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAY_POKER, data);
        return false;

    }

    @SuppressWarnings("unchecked")
    private static void playerChoose(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        if (EXIT_CMD_LIST.contains(message)) {
            GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_EXIT, data);
            return;
        }
        final Character[] pokerList = PokerHelper.parsePoker(message);
        String qq = data.get(Constant.ACCOUNT_CODE).toString();
        LandlordsPlayer player = (LandlordsPlayer) Game.getPlayer(qq);
        try {
            final List<PokerSell> list = (List<PokerSell>) player.getPlayerData("list");
            int choose = Integer.parseInt(message);
            if (list == null) {
                throw new NumberFormatException();
            }
            if (choose < 1 || choose > list.size()) {
                if (pokerList != null) {
                    GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAY_POKER, data);
                    return;
                }
                LandlordsNotifyUtil.notifyPlayer(player, String.format("输入的编号必须是从1到%s.", list.size()));
            } else {
                final PokerSell pokerSell = list.get(choose - 1);
                data.put("currentPokerShell", pokerSell);
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAY_POKER, data);
            }
        } catch (NumberFormatException e) {
            if (pokerList != null) {
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAY_POKER, data);
                return;
            }
            if (PASS_CMD_LIST.contains(message)) {
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_PASS, data);
                return;
            }
            data.put(Constant.MESSAGE, message);
            GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_MESSAGE_ON_ROUND, data);
        }
    }

    public static void otherMsg(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        if (EXIT_CMD_LIST.contains(message)) {
            GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_EXIT, data);
            return;
        }
        // 发送房间内消息
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        LandlordsPlayer player = (LandlordsPlayer) Game.getPlayer(accountCode);
        if (player.isPrivateMessage()) {
            LandlordsNotifyUtil.notifyPlayerSpeak(player, data.get(Constant.MESSAGE).toString());
        }
    }

    public static boolean playerReady(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        if (READY_CMD_LIST.contains(message)) {
            GameEventManager.call(LandlordsGameEventCode.CODE_GAME_PLAYER_READY, data);
            return false;
        }
        return true;
    }
}

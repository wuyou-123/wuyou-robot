package pers.wuyou.robot.game.landlords.common;

import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.PokerSell;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.GameEventCode;
import pers.wuyou.robot.game.landlords.enums.PlayerGameStatus;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;

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

    public static void callLandlords(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        String qq = data.get(Constant.ACCOUNT_CODE).toString();
        Room room = GameManager.getRoomByAccountCode(qq);
        final Player currentPlayer = room.getCurrentPlayer();
        if (CALL_LANDLORDS_CMD_LIST.contains(message)) {
            if (currentPlayer != null && currentPlayer.equals(qq)) {
                data.put("select", true);
                GameEventManager.call(GameEventCode.CODE_GAME_PLAYER_CALL_LANDLORDS, data);
            } else {
                NotifyUtil.notifyPlayerNotYourRound(GameManager.getPlayer(qq));
            }
        }
        if (NOT_CALL_LANDLORDS_CMD_LIST.contains(message)) {
            if (currentPlayer != null && currentPlayer.equals(qq)) {
                data.put("select", false);
                GameEventManager.call(GameEventCode.CODE_GAME_PLAYER_CALL_LANDLORDS, data);
            } else {
                NotifyUtil.notifyPlayerNotYourRound(GameManager.getPlayer(qq));
            }
        }
    }

    public static void playerPoker(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        final Character[] pokerList = PokerHelper.parsePoker(message);
        String qq = data.get(Constant.ACCOUNT_CODE).toString();
        Room room = GameManager.getRoomByAccountCode(qq);
        final Player currentPlayer = room.getCurrentPlayer();
        if (currentPlayer == null || !currentPlayer.equals(qq)) {
            if (pokerList != null) {
                // 现在不是该玩家的回合
                NotifyUtil.notifyPlayerCantPlay(GameManager.getPlayer(qq));
            }
            return;
        }
        data.put("pokerList", pokerList);
        Player player = GameManager.getPlayer(qq);
        if (player.getStatus() == PlayerGameStatus.CHOOSE_TIP) {
            // 玩家在选择提示内容
            playerChoose(data);
            return;
        }
        if (pokerList == null) {
            // 发的消息不是任何牌型
            if (PASS_CMD_LIST.contains(message)) {
                GameEventManager.call(GameEventCode.CODE_GAME_PLAYER_PASS, data);
                return;
            }
            data.put(Constant.MESSAGE, message);
            GameEventManager.call(GameEventCode.CODE_GAME_PLAYER_MESSAGE_ON_ROUND, data);
            return;
        }
        GameEventManager.call(GameEventCode.CODE_GAME_PLAY_POKER, data);

    }

    @SuppressWarnings("unchecked")
    private static void playerChoose(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        final Character[] pokerList = PokerHelper.parsePoker(message);
        String qq = data.get(Constant.ACCOUNT_CODE).toString();
        Player player = GameManager.getPlayer(qq);
        try {
            final List<PokerSell> list = (List<PokerSell>) GameManager.getPlayerData(player, "list");
            int choose = Integer.parseInt(message);
            if (choose < 1 || choose > list.size()) {
                if (pokerList != null) {
                    GameEventManager.call(GameEventCode.CODE_GAME_PLAY_POKER, data);
                    return;
                }
                NotifyUtil.notifyPlayer(player, String.format("输入的编号必须是从1到%s.", list.size()));
            } else {
                final PokerSell pokerSell = list.get(choose - 1);
                data.put("currentPokerShell", pokerSell);
                GameEventManager.call(GameEventCode.CODE_GAME_PLAY_POKER, data);
            }
        } catch (NumberFormatException e) {
            if (pokerList != null) {
                GameEventManager.call(GameEventCode.CODE_GAME_PLAY_POKER, data);
                return;
            }
            if (PASS_CMD_LIST.contains(message)) {
                GameEventManager.call(GameEventCode.CODE_GAME_PLAYER_PASS, data);
                return;
            }
            data.put(Constant.MESSAGE, message);
            GameEventManager.call(GameEventCode.CODE_GAME_PLAYER_MESSAGE_ON_ROUND, data);
        }
    }

    public static void otherMsg(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        if (EXIT_CMD_LIST.contains(message)) {
            GameEventManager.call(GameEventCode.CODE_GAME_PLAYER_EXIT, data);
        }

    }

    public static void playerReady(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        if (READY_CMD_LIST.contains(message)) {
            GameEventManager.call(GameEventCode.CODE_GAME_PLAYER_READY, data);
        }

    }
}

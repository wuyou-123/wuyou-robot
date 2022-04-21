package pers.wuyou.robot.game.gobang.common;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.GameEventManager;
import pers.wuyou.robot.game.gobang.entity.GobangPlayer;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.entity.Step;
import pers.wuyou.robot.game.gobang.enums.PieceColor;
import pers.wuyou.robot.game.gobang.enums.RoomMode;
import pers.wuyou.robot.game.gobang.util.GobangNotifyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuyou
 */
public class MessageDispenser {
    private static final String PATTERN = "^(\\d|1[0-4])\\s*[^\\da-z]*\\s*([a-o])$|^([a-o])\\s*[^\\da-z]*\\s*(\\d|1[0-4])$";
    private static final List<String> EXIT_CMD_LIST = Arrays.asList("退出", "退出游戏", "离开", "离开房间", "不玩了", "我不玩了", "exit", "e");
    private static final List<String> READY_CMD_LIST = Arrays.asList("继续", "准备", "再来一把", "再来", "好了", "ok", "ready", "again");

    private MessageDispenser() {
    }


    public static void otherMsg(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        if (EXIT_CMD_LIST.contains(message)) {
            GameEventManager.call(GobangGameEventCode.CODE_GAME_PLAYER_EXIT, data);
            return;
        }
        // 发送房间内消息
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        GobangPlayer player = (GobangPlayer) Game.getPlayer(accountCode);
        if (player.isPrivateMessage()) {
            GobangNotifyUtil.notifyPlayerSpeak(player, data.get(Constant.MESSAGE).toString());
        }
    }

    public static boolean playerReady(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        if (READY_CMD_LIST.contains(message)) {
            GameEventManager.call(GobangGameEventCode.CODE_GAME_PLAYER_READY, data);
            return false;
        }
        return true;
    }

    public static boolean chooseMode(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        String group = data.get(Constant.GROUP_CODE).toString();
        GobangPlayer player = (GobangPlayer) Game.getPlayer(accountCode);
        switch (message) {
            case "1":
                // 单人模式
                GobangNotifyUtil.notifyPlayerChooseFirstHand(player, false);
                player.setStatus(GobangPlayerGameStatus.CHOOSE_FIRST_HAND);
                player.getRoom().setMode(RoomMode.PLAY_WITH_BOT);
                return false;
            case "2":
                // 双人模式
                // 查找其他空闲房间
                GobangRoom freeRoom = (GobangRoom) Game.getRooms(group, Game.GameType.GOBANG).stream().filter(item -> !item.isFull() && !((GobangRoom) item).playWithBot() && player.getRoom() != item).findFirst().orElse(null);
                if (freeRoom != null) {
                    Game.removeRoom(player.getRoom());
                    freeRoom.getPlayerList().add(player);
                    player.setRoom(freeRoom);
                    for (GobangPlayer gobangPlayer : freeRoom.getPlayerList()) {
                        gobangPlayer.setStatus(GobangPlayerGameStatus.NO_READY);
                    }
                    GobangNotifyUtil.notifyPlayerJoinSuccess(player);
                } else {
                    GobangNotifyUtil.notifyRoomWaitOtherPlayer(player);
                    player.setStatus(GobangPlayerGameStatus.WAIT_OTHER_JOIN);
                    player.getRoom().setMode(RoomMode.PLAY_WITH_HUMAN);
                }
                return false;
            default:
                return true;
        }
    }

    public static boolean chooseFirstHand(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        GobangPlayer player = (GobangPlayer) Game.getPlayer(accountCode);
        switch (message) {
            case "1":
                // 玩家先手
                player.setColor(PieceColor.BLACK);
                break;
            case "2":
                // 电脑先手
                player.setColor(PieceColor.WHITE);
                break;
            default:
                return true;
        }
        GameEventManager.call(GobangGameEventCode.CODE_GAME_START, data);
        return false;
    }

    public static boolean chooseChess(Map<String, Object> data) {
        String message = data.get(Constant.MESSAGE).toString().toLowerCase(Locale.ROOT);
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        GobangPlayer player = (GobangPlayer) Game.getPlayer(accountCode);
        if (!player.getStatus().equals(GobangPlayerGameStatus.CHOOSE_CHESS)) {
            return true;
        }
        Pattern r = Pattern.compile(PATTERN);
        Matcher m = r.matcher(message);
        if (m.matches()) {
            int x, y;
            if (m.group(1) != null) {
                x = m.group(2).charAt(0) - 97;
                y = Integer.parseInt(m.group(1));
            } else {
                x = m.group(3).charAt(0) - 97;
                y = Integer.parseInt(m.group(4));
            }
            player.setLastStep(new Step(x, y, player.getRole()));
            GameEventManager.call(GobangGameEventCode.CODE_GAME_PLAYER_CHESS, data);
            return false;
        }
        return true;
    }
}

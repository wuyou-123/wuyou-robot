package pers.wuyou.robot.game.landlords.common;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.GameEventCode;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;
import pers.wuyou.robot.util.RobotUtil;
import pers.wuyou.robot.util.SenderUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyou
 */
@Slf4j
public class GameEventManager {
    private final static Map<GameEventCode, GameEvent> GAME_EVENT_MAP = new HashMap<>();
    private final static String GAME_EVENT_PACKAGE = "pers.wuyou.robot.game.landlords.game.Game";

    private GameEventManager() {
    }

    private static GameEvent get(GameEventCode code) {
        GameEvent listener = null;
        try {
            if (GameEventManager.GAME_EVENT_MAP.containsKey(code)) {
                listener = GameEventManager.GAME_EVENT_MAP.get(code);
            } else {
                String eventListener = GAME_EVENT_PACKAGE + code.getCode();
                Class<?> listenerClass = Class.forName(eventListener);
                listener = (GameEvent) listenerClass.getDeclaredConstructor().newInstance();
                GameEventManager.GAME_EVENT_MAP.put(code, listener);
            }
            return listener;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listener;
    }

    public static void call(GameEventCode code, Room room, Map<String, Object> data) {
        log.info(String.format("斗地主 -- 房间%s --- %s", room == null ? "" : room.getId(), code.getDesc()));
        RobotUtil.THREAD_POOL.execute(() -> {
            try {
                ThreadUtil.sleep(200);
                get(code).call(room, data);
            } catch (Exception e) {
                e.printStackTrace();
                String groupCode = room != null ? room.getId() : data.getOrDefault(Constant.GROUP_CODE, "").toString();
                final String messageStr = "群主已禁止群成员发起临时会话";
                String message = e.getMessage();
                if (message != null && message.contains(messageStr)) {
                    message = "群主已禁止群成员发起临时会话, 您可以添加机器人为好友后继续操作, 当前机器人账号: " + RobotUtil.getDefaultBotCode();
                }
                SenderUtil.sendGroupMsg(groupCode, message);
                String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
                Player player = GameManager.getPlayer(accountCode);
                NotifyUtil.notifyRoom(groupCode, String.format("玩家 %s 退出了房间", player));
                GameManager.removePlayer(player);
                if (room != null) {
                    room.gameEnd();
                }
            }
        });
    }

    public static void call(GameEventCode code, Room room) {
        call(code, room, null);
    }

    public static void call(GameEventCode code, Map<String, Object> data) {
        final String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        if (accountCode != null) {
            final Room room = GameManager.getRoomByAccountCode(accountCode);
            call(code, room, data);
            return;
        }
        call(code, null, data);
    }

    public static void callIgnoreRoom(GameEventCode code, Map<String, Object> data) {
        call(code, null, data);
    }
}

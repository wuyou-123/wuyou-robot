package pers.wuyou.robot.game.common;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.util.SenderUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyou
 */
@Slf4j
public class GameEventManager {
    private static final Map<String, GameEvent> GAME_EVENT_MAP = new HashMap<>();

    private GameEventManager() {
    }

    private static GameEvent get(GameEventCode code, Game.GameType gameType) {
        GameEvent gameEvent = null;
        try {
            if (GameEventManager.GAME_EVENT_MAP.containsKey(getKey(code, gameType))) {
                gameEvent = GameEventManager.GAME_EVENT_MAP.get(getKey(code, gameType));
            } else {
                String eventListener = gameType.getPackagePrefix() + code.getCode();
                Class<?> listenerClass = Class.forName(eventListener);
                gameEvent = (GameEvent) listenerClass.getDeclaredConstructor().newInstance();
                GameEventManager.GAME_EVENT_MAP.put(getKey(code, gameType), gameEvent);
            }
            return gameEvent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameEvent;
    }

    private static String getKey(GameEventCode code, Game.GameType gameType) {
        return code.getCode() + gameType.getName();
    }

    public static void call(GameEventCode code, BaseRoom<?> room, @Nullable Map<String, Object> data) {
        assert room != null || data != null;
        final Game.GameType gameType = room == null ? (Game.GameType) data.get(Constant.GAME_TYPE) : room.getGameType();
        log.info(String.format("%s -- 房间%s --- %s", gameType.getName(), room == null ? "" : room.getId(), data == null ? code.getDesc() : String.format(code.getDesc(), data.get(Constant.ACCOUNT_CODE))));
        RobotCore.THREAD_POOL.execute(() -> {
            try {
                ThreadUtil.sleep(200);
                GameEvent gameEvent = get(code, gameType);
                gameEvent.call(room, data);
            } catch (Exception e) {
                e.printStackTrace();
                String groupCode = room != null ? room.getId() : data.getOrDefault(Constant.GROUP_CODE, "").toString();
                final String messageStr = "群主已禁止群成员发起临时会话";
                String message = e.getMessage();
                if (message != null && message.contains(messageStr)) {
                    message = "群主已禁止群成员发起临时会话, 您可以添加机器人为好友后继续操作, 当前机器人账号: " + RobotCore.getDefaultBotCode();
                }
                SenderUtil.sendGroupMsg(groupCode, message);
                assert data != null;
                String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
                BasePlayer<?> player = Game.getPlayer(accountCode);
                NotifyUtil.notifyRoom(groupCode, String.format("玩家 %s 退出了房间", player));
                Game.removePlayer(player);
                if (room != null) {
                    room.gameEnd();
                }
            }
        });
    }

    public static void call(GameEventCode code, BaseRoom<?> room) {
        call(code, room, null);
    }

    public static void call(GameEventCode code, Map<String, Object> data) {
        final String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        if (accountCode != null) {
            final BaseRoom<?> room = Game.getPlayer(accountCode).getRoom();
            call(code, room, data);
            return;
        }
        call(code, null, data);
    }

    public static void callIgnoreRoom(GameEventCode code, Map<String, Object> data) {
        call(code, null, data);
    }
}

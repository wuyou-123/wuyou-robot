package pers.wuyou.robot.game.common;

import lombok.Getter;
import pers.wuyou.robot.game.gobang.GobangGameManager;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.landlords.LandlordsGameManager;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游戏总控制类
 *
 * @author wuyou
 * @date 2022/4/6 11:04
 */
public class Game {
    private static final List<BaseRoom<?>> ROOM_LIST = new ArrayList<>();
    private static final List<BasePlayer<?>> PLAYER_LIST = new ArrayList<>();

    private Game() {
    }

    public static List<BaseRoom<?>> getRooms(String id, GameType gameType) {
        return ROOM_LIST.stream().filter(room -> room.getId().equals(id) && room.getGameManager().getClass() == gameType.gameManager).collect(Collectors.toList());
    }
    public static BaseRoom<?> getRoom(String id, GameType gameType) {
        return ROOM_LIST.stream().filter(room -> room.getId().equals(id) && room.getGameManager().getClass() == gameType.gameManager).findFirst().orElse(null);
    }

    public static BaseRoom<?> getRoom(String id, String qq, GameType gameType) {
        return ROOM_LIST.stream().filter(room -> room.getId().equals(id) &&
                        room.getGameManager().getClass() == gameType.gameManager &&
                        room.getPlayerList().contains(PLAYER_LIST.stream()
                                .filter(item -> item.getId().equals(qq))
                                .findFirst().orElse(null)))
                .findFirst().orElse(null);
    }

    public static void removeRoom(String id, GameType gameType) {
        BaseRoom<?> r = ROOM_LIST.stream().filter(room -> room.getId().equals(id) && room.getGameManager().getClass() == gameType.gameManager).findFirst().orElse(null);
        if (r == null) {
            return;
        }
        for (BasePlayer<?> player : r.getPlayerList()) {
            PLAYER_LIST.remove(player);
        }
        ROOM_LIST.remove(r);
    }
    public static void removeRoom(BaseRoom<?> room) {
        ROOM_LIST.remove(room);
    }

    public static BaseRoom<?> addRoom(String id, GameType gameType) {
        switch (gameType) {
            case GOBANG:
                final GobangRoom gobangRoom = new GobangRoom(id);
                ROOM_LIST.add(gobangRoom);
                return gobangRoom;
            case LANDLORDS:
                final LandlordsRoom landlordsRoom = new LandlordsRoom(id);
                ROOM_LIST.add(landlordsRoom);
                return landlordsRoom;
            default:
                throw new GameException();
        }
    }

    public static BasePlayer<?> getPlayer(String id) {
        return PLAYER_LIST.stream().filter(item -> item.getId().equals(id)).findFirst().orElse(null);
    }

    public static void addPlayer(BasePlayer<?> player) {
        PLAYER_LIST.add(player);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BasePlayer<?>> void removePlayer(T player) {
        final BaseRoom<T> room = (BaseRoom<T>) player.getRoom();
        room.playerExit(player);
        PLAYER_LIST.remove(player);
        if (room.getPlayerList().isEmpty()) {
            ROOM_LIST.remove(room);
        }

    }

    @Getter
    public enum GameType {
        /**
         * 五子棋
         */
        GOBANG("五子棋", "pers.wuyou.robot.game.gobang.game.event.Game", GobangGameManager.class),
        /**
         * 斗地主
         */
        LANDLORDS("斗地主", "pers.wuyou.robot.game.landlords.game.event.Game", LandlordsGameManager.class);
        private final String name;
        private final String packagePrefix;
        private final Class<? extends BaseGameManager> gameManager;

        GameType(String name, String packagePrefix, Class<? extends BaseGameManager> gameManager) {
            this.name = name;
            this.packagePrefix = packagePrefix;
            this.gameManager = gameManager;
        }
    }
}

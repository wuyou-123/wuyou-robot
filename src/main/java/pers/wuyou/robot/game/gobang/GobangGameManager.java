package pers.wuyou.robot.game.gobang;

import org.springframework.stereotype.Service;
import pers.wuyou.robot.game.common.BaseGameManager;
import pers.wuyou.robot.game.common.BasePlayer;
import pers.wuyou.robot.game.gobang.entity.GobangPlayer;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.exception.GobangException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wuyou
 * @date 2022/4/4 14:29
 */
@Service
public class GobangGameManager extends BaseGameManager {
    /**
     * 游戏名
     */
    public static final String GAME_NAME = "gobang";
    /**
     * 最大玩家数量
     */
    public static final int MAX_PLAYER_COUNT = 2;

    private static GobangGameManager manager;

    private static final Map<String, String> PLAYER_FIRST_HAND_MAP = new HashMap<>();

    public GobangGameManager() {
        manager = this;
    }

    public static boolean checkResource() {
        return manager.checkRes("classpath:**/generateBoard.py", "classpath:**/board.jpg");
    }

    public static GobangPlayer getFirstHand(GobangRoom room) {
        if (room.getPlayerList().size() != MAX_PLAYER_COUNT) {
            throw new GobangException("房间人数异常");
        }
        String key = room.getPlayerList().stream().map(BasePlayer::getId).collect(Collectors.joining("-"));
        if (PLAYER_FIRST_HAND_MAP.get(key) == null) {
            PLAYER_FIRST_HAND_MAP.put(key, room.getPlayerList().get(0).getId());
        }else {
            return room.getPlayerList().stream().filter(player -> player.getId().equals(PLAYER_FIRST_HAND_MAP.get(key))).findFirst().orElse(null);
        }
        return room.getPlayerList().get(0);
    }

    @Override
    public String getGameName() {
        return GAME_NAME;
    }
}

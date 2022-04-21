package pers.wuyou.robot.game.common;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuyou
 * @date 2022/4/4 11:31
 */
@Data
public abstract class BaseRoom<P extends BasePlayer<?>> {
    private static final String PLAYER_NOT_IN_ROOM = "玩家不在房间中";
    /**
     * 用户携带数据
     */
    private static final Map<String, Map<String, Object>> PLAYER_DATA_MAP = new HashMap<>();
    /**
     * 玩家列表
     */
    @NotNull
    protected final List<P> playerList = new ArrayList<>();
    /**
     * 房间当前游戏
     */
    @NotNull
    protected final BaseGameManager gameManager;
    /**
     * 房间当前游戏
     */
    @NotNull
    protected final Integer maxPlayerCount;
    /**
     * 当前房间的类型
     */
    @NotNull
    private final Game.GameType gameType;
    /**
     * 房间号,也就是群号
     */
    @NotNull
    protected String id;
    /**
     * 房间的名称
     */
    @NotNull
    protected String name;
    /**
     * 当前玩家索引
     */
    protected int currentPlayerIndex = -1;
    /**
     * 最后一次出牌玩家索引
     */
    protected int lastPlayPlayerIndex = -1;
    /**
     * 房间当前状态
     */
    @NotNull
    protected String status;

    public BaseRoom(@NotNull String id, @NotNull String name, @NotNull Integer maxPlayerCount, @NotNull Game.GameType gameType, @NotNull BaseGameManager gameManager) {
        this.id = id;
        this.name = name;
        this.gameManager = gameManager;
        this.maxPlayerCount = maxPlayerCount;
        this.gameType = gameType;
        this.status = RoomStatus.PLAYER_READY;
    }

    public boolean isFull() {
        return playerList.size() >= maxPlayerCount;
    }

    /**
     * 获取玩家携带数据
     *
     * @param player 玩家对象
     * @return 获取到的对象
     */
    public Object getPlayerData(@NotNull BasePlayer<?> player, String key) {
        PLAYER_DATA_MAP.putIfAbsent(player.getId(), new HashMap<>(4));
        return PLAYER_DATA_MAP.get(player.getId()).get(key);
    }

    /**
     * 获取玩家携带数据map
     *
     * @param player 玩家对象
     * @return 获取到的对象
     */
    public Map<String, Object> getPlayerDataMap(@NotNull BasePlayer<?> player) {
        PLAYER_DATA_MAP.putIfAbsent(player.getId(), new HashMap<>(4));
        return PLAYER_DATA_MAP.get(player.getId());
    }

    /**
     * 根据账号号获取玩家
     *
     * @param accountCode 玩家账号
     * @return 获取到的玩家
     */
    public P getPlayer(String accountCode) {
        return playerList.stream().filter(item -> item.getId().equals(accountCode)).findFirst().orElse(null);
    }

    public P getCurrentPlayer() {
        if (currentPlayerIndex == -1) {
            return null;
        }
        return playerList.get(currentPlayerIndex);
    }

    public void setCurrentPlayer(P player) {
        final int playerIndex = playerList.indexOf(player);
        if (playerIndex == -1) {
            throw new GameException(PLAYER_NOT_IN_ROOM);
        }
        this.currentPlayerIndex = playerIndex;
    }

    public P getLastPlayer() {
        if (lastPlayPlayerIndex == -1) {
            return null;
        }
        return playerList.get(lastPlayPlayerIndex);
    }

    public void setLastPlayer(P player) {
        final int playerIndex = playerList.indexOf(player);
        if (playerIndex == -1) {
            throw new GameException(PLAYER_NOT_IN_ROOM);
        }
        this.lastPlayPlayerIndex = playerIndex;
    }

    public P addPlayer(P player) {
        playerList.add(player);
        Game.addPlayer(player);
        return player;
    }

    @Override
    public String toString() {
        return String.format("房间名: %s 房间号: %s 当前游戏: %s 玩家数: %s 状态: %s", name, id, gameManager.getGameName(), playerList.size(), status);
    }

    public void playerExit(P player) {
        final int playerIndex = playerList.indexOf(player);
        if (playerIndex == -1) {
            throw new GameException(PLAYER_NOT_IN_ROOM);
        }
        playerList.remove(player);
        reset();
    }

    public void reset() {
        lastPlayPlayerIndex = -1;
        currentPlayerIndex = -1;
        status = RoomStatus.PLAYER_READY;
//        playerList.clear();
    }

    public void gameEnd() {
        reset();
        for (P player : playerList) {
            player.setStatus(PlayerGameStatus.NO_READY);
        }
    }

    public String getPlayerStatus() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("当前房间内有%s人%n", playerList.size()));
        for (BasePlayer<?> p : playerList) {
            sb.append(p).append(" ").append(p.getStatus()).append("\n");
        }
        return sb.toString();
    }
}

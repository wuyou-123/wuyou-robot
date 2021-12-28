package pers.wuyou.robot.game.landlords.entity;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.enums.PlayerGameStatus;
import pers.wuyou.robot.game.landlords.enums.PlayerType;
import pers.wuyou.robot.game.landlords.enums.RoomStatus;
import pers.wuyou.robot.game.landlords.exception.PlayerException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author wuyou
 */
@Data
public class Room {
    /**
     * 玩家列表
     */
    @NotNull
    private final List<Player> playerList = new LinkedList<>();
    /**
     * 房间号,也就是群号
     */
    @NotNull
    private String id;
    /**
     * 当前玩家索引
     */
    private int currentPlayerIndex = -1;
    /**
     * 最后一次出牌玩家索引
     */
    private int lastPlayPlayerIndex = -1;
    /**
     * 最后一次出牌牌型
     */
    private PokerSell lastPlayPoker;
    /**
     * 房间当前状态
     */
    @NotNull
    private RoomStatus status;
    /**
     * 叫地主次数
     */
    private int callLandlordsCount;
    /**
     * 三张地主牌
     */
    private List<Poker> landlordPokers = new ArrayList<>(3);

    public Room(@NotNull String id) {
        this.id = id;
        this.status = RoomStatus.PLAYER_READY;
    }

    public Player getCurrentPlayer() {
        if (currentPlayerIndex == -1) {
            return null;
        }
        return playerList.get(currentPlayerIndex);
    }

    public void setCurrentPlayer(Player player) {
        final int currentPlayerIndex = playerList.indexOf(player);
        if (currentPlayerIndex == -1) {
            throw new PlayerException("玩家不在房间中");
        }
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Player getLastPlayer() {
        if (lastPlayPlayerIndex == -1) {
            return null;
        }
        return playerList.get(lastPlayPlayerIndex);
    }

    public void setLastPlayer(Player player) {
        final int lastPlayPlayerIndex = playerList.indexOf(player);
        if (lastPlayPlayerIndex == -1) {
            throw new PlayerException("玩家不在房间中");
        }
        this.lastPlayPlayerIndex = lastPlayPlayerIndex;
    }

    public void addPlayer(Player player) {
        playerList.add(player);
    }

    public boolean canStart() {
        return playerList.stream().filter(item -> item.getStatus() == PlayerGameStatus.READY).count() == GameManager.MAX_PLAYER_COUNT;
    }

    public void start() {
        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            player.setPre(playerList.get(i + 1 == 3 ? 0 : i + 1));
            player.setNext(playerList.get(i - 1 == -1 ? 2 : i - 1));
        }
    }

    public void reset() {
        callLandlordsCount = 0;
        lastPlayPlayerIndex = -1;
        currentPlayerIndex = -1;
        status = RoomStatus.NO_START;
        for (Player player : playerList) {
            player.setCalledLandlords(null);
        }
    }

    public void callLandlords() {
        for (Player player : playerList) {
            player.setType(PlayerType.FARMER);
        }
        getCurrentPlayer().setType(PlayerType.LANDLORDS);
        getCurrentPlayer().setCalledLandlords(true);
    }

    public Player getLandlords() {
        return playerList.stream().filter(item -> item.getType() == PlayerType.LANDLORDS).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return String.format("房间: %s 玩家数: %s 状态: %s", id, playerList.size(), status.getMsg());
    }

    public void playerExit(Player player) {
        final int playerIndex = playerList.indexOf(player);
        if (playerIndex == -1) {
            throw new PlayerException("玩家不在房间中");
        }
        playerList.remove(player);
        reset();
    }

    public void gameEnd() {
        reset();
        for (Player player : playerList) {
            player.setStatus(PlayerGameStatus.NO_READY);
        }

    }

    public List<Poker> getLastSellPokers() {
        return lastPlayPoker.getSellPokers();
    }
}

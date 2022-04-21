package pers.wuyou.robot.game.landlords.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.BaseRoom;
import pers.wuyou.robot.game.landlords.LandlordsGameManager;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;
import pers.wuyou.robot.game.landlords.enums.PlayerType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyou
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LandlordsRoom extends BaseRoom<LandlordsPlayer> {
    /**
     * 最后一次出牌牌型
     */
    private PokerSell lastPlayPoker;
    /**
     * 叫地主次数
     */
    private int callLandlordsCount;
    /**
     * 三张地主牌
     */
    private List<Poker> landlordPokers = new ArrayList<>(3);

    public LandlordsRoom(@NotNull String id) {
        super(id, "斗地主", 3, Game.GameType.LANDLORDS, new LandlordsGameManager());
    }

    public boolean canStart() {
        return playerList.stream().filter(item -> item.getStatus().equals(LandlordsPlayerGameStatus.READY)).count() == LandlordsGameManager.MAX_PLAYER_COUNT;
    }

    public void start() {
        for (int i = 0; i < playerList.size(); i++) {
            LandlordsPlayer player = playerList.get(i);
            player.setPre(playerList.get(i + 1 == maxPlayerCount ? 0 : i + 1));
            player.setNext(playerList.get(i - 1 == -1 ? maxPlayerCount - 1 : i - 1));
        }
    }

    @Override
    public void reset() {
        super.reset();
        callLandlordsCount = 0;
        for (LandlordsPlayer player : playerList) {
            player.setCalledLandlords(null);
        }
    }

    public void callLandlords() {
        for (LandlordsPlayer player : playerList) {
            player.setType(PlayerType.FARMER);
        }
        getCurrentPlayer().setType(PlayerType.LANDLORDS);
        getCurrentPlayer().setCalledLandlords(true);
    }

    public LandlordsPlayer getLandlords() {
        return playerList.stream().filter(item -> item.getType() == PlayerType.LANDLORDS).findFirst().orElse(null);
    }


    public List<Poker> getLastSellPokers() {
        return lastPlayPoker.getSellPokers();
    }
}

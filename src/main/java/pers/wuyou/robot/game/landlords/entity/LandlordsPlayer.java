package pers.wuyou.robot.game.landlords.entity;

import lombok.Getter;
import lombok.Setter;
import pers.wuyou.robot.game.common.BasePlayer;
import pers.wuyou.robot.game.landlords.enums.PlayerType;

import java.util.List;
import java.util.Objects;

/**
 * @author wuyou
 */
@Getter
@Setter
public class LandlordsPlayer extends BasePlayer<LandlordsRoom> {
    protected PlayerType type;
    private List<Poker> pokers;
    private Boolean calledLandlords;

    public LandlordsPlayer(String accountCode, String name, LandlordsRoom room) {
        super(accountCode, name, room);
    }

    public boolean isNotWantCallLandlords() {
        return calledLandlords != null && !calledLandlords;
    }

    @Override
    public LandlordsPlayer getPre() {
        return (LandlordsPlayer) this.pre;
    }

    @Override
    public LandlordsPlayer getNext() {
        return (LandlordsPlayer) this.next;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isPlaying, pokers, status, type, calledLandlords);
    }

}

package pers.wuyou.robot.game.gobang.entity;

import lombok.Getter;
import lombok.Setter;
import pers.wuyou.robot.game.common.BasePlayer;
import pers.wuyou.robot.game.gobang.enums.PieceColor;

import java.util.Objects;

/**
 * @author wuyou
 */
@Getter
@Setter
public class GobangPlayer extends BasePlayer<GobangRoom> {
    private Boolean firstHand;
    private PieceColor color;
    private Step lastStep;

    public GobangPlayer(String accountCode, String name, GobangRoom room) {
        super(accountCode, name, room);
    }

    public int getRole() {
        return color == PieceColor.BLACK ? Role.BLACK : Role.WHITE;
    }

    @Override
    public GobangPlayer getPre() {
        return (GobangPlayer) this.pre;
    }

    @Override
    public GobangPlayer getNext() {
        return (GobangPlayer) this.next;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isPlaying, status, firstHand);
    }

}

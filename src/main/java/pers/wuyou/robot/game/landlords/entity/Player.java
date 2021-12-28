package pers.wuyou.robot.game.landlords.entity;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import pers.wuyou.robot.game.landlords.enums.PlayerGameStatus;
import pers.wuyou.robot.game.landlords.enums.PlayerType;

import java.util.List;
import java.util.Objects;

/**
 * @author wuyou
 */
@Data
public class Player {
    @NotNull
    private String id;
    @NotNull
    private String name;
    private Room room;
    private boolean isPlaying;
    private List<Poker> pokers;
    private PlayerGameStatus status;
    private PlayerType type;
    private Player pre;
    private Player next;
    private Boolean calledLandlords;
    private boolean currentMessageIsPrivate;

    public Player(@NotNull String id, @NotNull String name, @NotNull Room room) {
        this.id = id;
        this.name = name;
        this.status = PlayerGameStatus.NO_READY;
        this.room = room;
        room.addPlayer(this);
    }

    public String getRoomId() {
        if (room == null) {
            return "";
        }
        return room.getId();
    }

    public boolean isInRoom(String roomId) {
        if (room == null) {
            return false;
        }
        return room.getId().equals(roomId);
    }

    public boolean isNotWantCallLandlords() {
        return calledLandlords != null && !calledLandlords;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", name, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;
        return Objects.equals(id, other.id);
    }

    public boolean equals(String qq) {
        return id.equals(qq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isPlaying, pokers, status, type, calledLandlords);
    }
}

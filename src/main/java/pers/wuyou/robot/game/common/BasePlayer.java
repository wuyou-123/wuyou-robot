package pers.wuyou.robot.game.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

/**
 * 玩家对象
 *
 * @author wuyou
 * @date 2022/4/4 11:42
 */
@Data
@NoArgsConstructor
public abstract class BasePlayer<T extends BaseRoom<?>> {
    @NotNull
    protected String id;
    @NotNull
    protected String name;
    protected T room;
    protected boolean isPlaying;
    protected String status;
    protected BasePlayer<T> pre;
    protected BasePlayer<T> next;
    protected boolean privateMessage;

    public BasePlayer(@NotNull String id, @NotNull String name, @NotNull T room) {
        this.id = id;
        this.name = name;
        this.status = PlayerGameStatus.NO_READY;
        this.room = room;
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
        BasePlayer<?> other = (BasePlayer<?>) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", name, id);
    }


    public boolean equals(String qq) {
        return id.equals(qq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isPlaying, status);
    }

    /**
     * 获取玩家携带数据
     *
     * @param key key
     * @return 获取到的对象
     */
    public Object getPlayerData(String key) {
        return room.getPlayerData(this, key);
    }

    /**
     * 获取玩家携带数据
     *
     * @return 获取到的对象
     */
    public Map<String, Object> getPlayerDataMap() {
        return room.getPlayerDataMap(this);
    }

}

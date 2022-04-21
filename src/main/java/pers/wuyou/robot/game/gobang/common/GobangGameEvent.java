package pers.wuyou.robot.game.gobang.common;

import pers.wuyou.robot.game.common.GameEvent;
import pers.wuyou.robot.game.common.BaseRoom;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;

import java.util.Map;

/**
 * @author wuyou
 */
public interface GobangGameEvent extends GameEvent {
    /**
     * 执行游戏事件
     *
     * @param room 游戏房间
     * @param data 携带数据
     */
    @Override
    default void call(BaseRoom<?> room, Map<String, Object> data) {
        call((GobangRoom) room, data);
    }

    /**
     * 执行游戏事件
     *
     * @param room 游戏房间
     * @param data 携带数据
     */
    void call(GobangRoom room, Map<String, Object> data);
}

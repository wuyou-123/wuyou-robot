package pers.wuyou.robot.game.landlords.common;

import pers.wuyou.robot.game.landlords.entity.Room;

import java.util.Map;

/**
 * @author wuyou
 */
public interface GameEvent {
    /**
     * 执行游戏事件
     * @param room 游戏房间
     * @param data 携带数据
     */
    void call(Room room, Map<String, Object> data);
}

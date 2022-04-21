package pers.wuyou.robot.game.landlords.common;

import pers.wuyou.robot.game.common.RoomStatus;

/**
 * @author wuyou
 */
public interface LandlordsRoomStatus extends RoomStatus {
    /**
     * 房间当前状态
     */
    String CALL_LANDLORDS = "抢地主";
    String POKER_PLAY = "出牌";
}

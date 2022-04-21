package pers.wuyou.robot.game.gobang.common;

import pers.wuyou.robot.game.common.RoomStatus;

/**
 * @author wuyou
 */
public interface GobangRoomStatus extends RoomStatus {
    String PLAYING = "游戏中";
    String WAIT_PLAYER_AGAIN = "等待玩家再来一局";
}

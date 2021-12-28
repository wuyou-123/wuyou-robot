package pers.wuyou.robot.game.landlords.enums;

import lombok.Getter;

/**
 * @author wuyou
 */
@Getter
public enum RoomStatus {
    /**
     * 房间当前状态
     */
    CLIENT_EXIT("玩家退出"),

    CLIENT_HEAD_BEAT("不出"),

    GAME_STARTING("游戏开始"),

    CALL_LANDLORDS("抢地主"),

    POKER_PLAY("出牌"),

    GAME_POKER_PLAY_REDIRECT("出牌重定向"),

    GAME_POKER_PLAY_PASS("不出"),

    PLAYER_READY("玩家准备"),

    GAME_END("游戏结束");

    private final String msg;

    RoomStatus(String msg) {
        this.msg = msg;
    }
}

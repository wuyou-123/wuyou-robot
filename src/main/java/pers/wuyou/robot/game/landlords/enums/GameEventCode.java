package pers.wuyou.robot.game.landlords.enums;

import lombok.Getter;

/**
 * @author wuyou
 */
@Getter
public enum GameEventCode {
    /**
     * 游戏状态
     */
    CODE_ROOM_JOIN("PlayerJoin", "玩家加入房间"),
    CODE_GAME_START("Start", "开始游戏"),
    CODE_GAME_PLAYER_CALL_LANDLORDS("PlayerCallLandlords", "玩家抢地主"),
    CODE_GAME_CALL_LANDLORDS_END("CallLandlordsEnd", "抢地主结束"),
    CODE_GAME_PLAY_POKER("PlayerPlayPoker", "玩家出牌"),
    CODE_GAME_PLAYER_PASS("PlayerPass", "玩家不出牌"),
    CODE_GAME_PLAYER_EXIT("PlayerExit", "玩家退出"),
    CODE_GAME_PLAYER_READY("PlayerReady", "玩家准备"),
    CODE_GAME_PLAYER_MESSAGE_ON_ROUND("PlayerMessageOnRound", "玩家出牌时发送消息"),
    ;
    private final String code;
    private final String desc;

    GameEventCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

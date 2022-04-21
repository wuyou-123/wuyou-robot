package pers.wuyou.robot.game.landlords.common;

import pers.wuyou.robot.game.common.GameEventCode;

/**
 * @author wuyou
 */
public interface LandlordsGameEventCode {
    /**
     * 游戏状态
     */
    GameEventCode CODE_ROOM_JOIN = new GameEventCode("PlayerJoin", "玩家加入房间");
    GameEventCode CODE_GAME_START = new GameEventCode("Start", "开始游戏");
    GameEventCode CODE_GAME_PLAYER_CALL_LANDLORDS = new GameEventCode("PlayerCallLandlords", "玩家抢地主");
    GameEventCode CODE_GAME_CALL_LANDLORDS_END = new GameEventCode("CallLandlordsEnd", "抢地主结束");
    GameEventCode CODE_GAME_PLAY_POKER = new GameEventCode("PlayerPlayPoker", "玩家出牌");
    GameEventCode CODE_GAME_PLAYER_PASS = new GameEventCode("PlayerPass", "玩家不出牌");
    GameEventCode CODE_GAME_PLAYER_EXIT = new GameEventCode("PlayerExit", "玩家退出");
    GameEventCode CODE_GAME_PLAYER_READY = new GameEventCode("PlayerReady", "玩家准备");
    GameEventCode CODE_GAME_PLAYER_MESSAGE_ON_ROUND = new GameEventCode("PlayerMessageOnRound", "玩家出牌时发送消息");

}

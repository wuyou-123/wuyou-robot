package pers.wuyou.robot.game.gobang.common;

import pers.wuyou.robot.game.common.GameEventCode;

/**
 * @author wuyou
 */
public interface GobangGameEventCode {
    /**
     * 游戏状态
     */
    GameEventCode CODE_ROOM_JOIN = new GameEventCode("PlayerJoin", "玩家%s加入房间");
    GameEventCode CODE_GAME_START = new GameEventCode("Start", "开始游戏");
    GameEventCode CODE_GAME_PLAYER_EXIT = new GameEventCode("PlayerExit", "玩家%s退出");
    GameEventCode CODE_GAME_PLAYER_READY = new GameEventCode("PlayerReady", "玩家%s准备");
    GameEventCode CODE_GAME_PLAYER_CHESS = new GameEventCode("PlayerChess", "玩家%s落子");
}

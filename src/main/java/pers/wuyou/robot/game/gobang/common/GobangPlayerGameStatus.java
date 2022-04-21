package pers.wuyou.robot.game.gobang.common;

import pers.wuyou.robot.game.common.PlayerGameStatus;

/**
 * 玩家游戏状态
 *
 * @author wuyou
 */
public interface GobangPlayerGameStatus extends PlayerGameStatus {
    String CHOOSE_MODE = "选择游戏模式";
    String CHOOSE_FIRST_HAND = "选择先手";
    String WAIT_OTHER_JOIN = "等待其他玩家加入";
    String PLAYING = "游戏中";
    String CHOOSE_CHESS = "选择棋子";
    String WAIT_OTHER_CHOOSE = "等待其他玩家落子";
}

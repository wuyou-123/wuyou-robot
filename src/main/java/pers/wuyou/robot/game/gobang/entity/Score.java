package pers.wuyou.robot.game.gobang.entity;

/**
 * 棋型表示
 * 用一个6位数表示棋型，从高位到低位分别表示
 * 连五，活四，眠四，活三，活二/眠三，活一/眠二, 眠一
 *
 * @author wuyou
 * @date 2022/3/31 15:09
 */
public interface Score {
    int ONE = 10;
    int TWO = 100;
    int THREE = 1000;
    int FOUR = 100000;
    int FIVE = 10000000;
    int BLOCKED_ONE = 1;
    int BLOCKED_TWO = 10;
    int BLOCKED_THREE = 100;
    int BLOCKED_FOUR = 10000;
}

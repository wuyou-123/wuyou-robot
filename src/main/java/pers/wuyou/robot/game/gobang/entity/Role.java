package pers.wuyou.robot.game.gobang.entity;

/**
 * @author wuyou
 * @date 2022/3/31 15:08
 */
public class Role {
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int EMPTY = 0;

    /**
     * 翻转角色
     */
    public static int reverse(int r) {
        return r ^ 2 + 1;
    }
}

package pers.wuyou.robot.game.gobang.enums;

/**
 * @author wuyou
 */
public enum PieceColor {
    /**
     * 黑色
     */
    BLACK,
    /**
     * 白色
     */
    WHITE;

    public String toString() {
        return this == BLACK ? "黑色" : "白色";
    }

}

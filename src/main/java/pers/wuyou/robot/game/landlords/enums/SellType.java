package pers.wuyou.robot.game.landlords.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author wuyou
 */

@Getter
public enum SellType {
    /**
     * 牌的类型
     */
    ILLEGAL("非合法"),

    SINGLE("单个牌"),

    DOUBLE("对子牌"),

    THREE("三张牌"),

    THREE_ZONES_SINGLE("三带单"),

    THREE_ZONES_DOUBLE("三带对"),

    FOUR_ZONES_SINGLE("四带单"),

    FOUR_ZONES_DOUBLE("四带对"),

    SINGLE_STRAIGHT("单顺子"),

    DOUBLE_STRAIGHT("双顺子"),

    THREE_STRAIGHT("三顺子"),

    FOUR_STRAIGHT("四顺子"),

    THREE_STRAIGHT_WITH_SINGLE("飞机带单牌"),

    THREE_STRAIGHT_WITH_DOUBLE("飞机带对牌"),

    FOUR_STRAIGHT_WITH_SINGLE("四顺子带单"),

    FOUR_STRAIGHT_WITH_DOUBLE("四顺子带对"),

    BOMB("炸弹"),

    KING_BOMB("王炸"),
    ;

    private final String msg;

    SellType(String msg) {
        this.msg = msg;
    }

    public boolean isBomb() {
        return this == BOMB || this == KING_BOMB;
    }

    public int index() {
        return Arrays.asList(values()).indexOf(this);
    }

}

package pers.wuyou.robot.game.landlords.enums;

import lombok.Getter;

/**
 * Poker type Spade、 Heart、 Diamond、 Club
 *
 * @author nico
 */
@Getter
public enum PokerType {
    /**
     * 扑克牌花色
     */
    BLANK(" "),

    DIAMOND("♦"),

    CLUB("♣"),

    SPADE("♠"),

    HEART("♥");

    private final String name;

    PokerType(String name) {
        this.name = name;
    }

}

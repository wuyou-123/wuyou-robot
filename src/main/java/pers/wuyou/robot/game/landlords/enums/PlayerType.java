package pers.wuyou.robot.game.landlords.enums;

import lombok.Getter;

/**
 * @author wuyou
 */
@Getter
public enum PlayerType {
    /**
     * 玩家类型,农民或地主
     */
    FARMER("农民"),
    LANDLORDS("地主");

    private final String name;

    PlayerType(String name) {
        this.name = name;
    }
}

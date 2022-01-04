package pers.wuyou.robot.game.landlords.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 玩家游戏状态
 *
 * @author wuyou
 */
@Getter
@AllArgsConstructor
public enum PlayerGameStatus {
    /**
     * 选择要出的牌
     */
    CHOOSE("选择中"),
    /**
     * 选择提示的组合
     */
    CHOOSE_TIP("选择中"),
    /**
     * 叫地主
     */
    CALL_LANDLORDS("选择中"),
    /**
     * 未准备
     */
    NO_READY("未准备"),
    /**
     * 已准备
     */
    READY("已准备"),
    /**
     * 等待其他人抢地主
     */
    WAIT_OTHER_CALL_LANDLORDS("等待中"),
    /**
     * 等待其他人出牌
     */
    WAIT_OTHER_CHOOSE("等待中"),
    ;
    final String msg;


}

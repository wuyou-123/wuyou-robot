package pers.wuyou.robot.game.landlords.enums;

import lombok.Getter;

/**
 * 提示内容模板
 *
 * @author wuyou
 */
@Getter
public enum NotifyType {
    /**
     * 现在轮到 ${玩家} 了, ${提示}
     * 请耐心等待他/她选择是否抢地主
     * 请确认你是否抢地主
     */
    CALL_LANDLORDS("现在轮到 ${player} 了, ${tip}", "请耐心等待他/她选择是否抢地主", "请确认你是否抢地主"),
    /**
     * 下一位玩家是 ${玩家}, ${提示}
     * 请等待他/她出牌
     * 请选择你要出的牌
     */
    NOTIFY_PLAYER_PLAY("下一位玩家是 ${player}, ${tip}", "请等待他/她出牌", "请选择你要出的牌"),
    /**
     * ${玩家} 出牌
     */
    PLAYER_PLAY("${player} 出牌"),
    /**
     * ${玩家} 只剩一张牌了!
     */
    PLAYER_ONLY_ONE_POKER("${player} 只剩一张牌了!"),
    /**
     * ${玩家} 只剩两张牌了!
     */
    PLAYER_ONLY_TWO_POKER("${player} 只剩两张牌了!"),
    /**
     * 你出的牌不合法, 不能出这副牌
     */
    PLAYER_PLAY_INVALID("你出的牌不合法, 不能出这副牌"),
    /**
     * ${玩家} 跳过了
     */
    PLAYER_PASS("${player} 跳过了"),
    /**
     * 不允许不出牌!
     */
    PLAYER_CANT_PASS("不允许不出牌!"),
    /**
     * 你出的牌比之前的牌小, 不能出这副牌
     */
    PLAYER_PLAY_LESS("你出的牌比之前的牌小, 不能出这副牌"),
    /**
     * 游戏结束, 发送"准备"可重新开始
     */
    GAME_END("游戏结束, 发送\"准备\"可重新开始"),
    /**
     * 玩家 ${玩家} 退出了房间
     */
    PLAYER_EXIT("玩家 ${player} 退出了房间"),
    /**
     * 玩家 ${玩家} 退出了房间! 游戏结束, 发送"准备"可重新开始
     */
    PLAYER_EXIT_WITH_AGAIN("玩家 ${player} 退出了房间! 游戏结束, 发送\"准备\"可重新开始"),
    /**
     * 玩家 ${玩家} 准备了游戏! 请耐心等待游戏开始
     */
    PLAYER_READY("玩家 ${player} 准备了游戏! 请耐心等待游戏开始"),
    /**
     * ${玩家} 已经成为地主并获得了额外的三张牌!
     */
    BE_LANDLORDS("${player} 已经成为地主并获得了额外的三张牌!"),
    /**
     * ${玩家} 选择抢地主!
     */
    WANT_BE_LANDLORDS("${player} 选择抢地主!"),
    /**
     * ${玩家} 没有抢地主
     */
    NOT_WANT_BE_LANDLORDS("${player} 没有抢地主"),
    /**
     * 没有玩家抢地主, 重新发牌!
     */
    NO_PLAYER_CALL_LANDLORDS("没有玩家抢地主, 重新发牌!"),
    ;
    /**
     * 提示内容
     */
    private final String msg;
    /**
     * 当被发送的玩家不是当前玩家时的提示文字
     */
    private final String msg1;
    /**
     * 当被发送的玩家是当前玩家时的提示文字
     */
    private final String msg2;

    NotifyType(String msg, String msg1, String msg2) {
        this.msg = msg;
        this.msg1 = msg1;
        this.msg2 = msg2;
    }

    NotifyType(String msg) {
        this.msg = msg;
        this.msg1 = "";
        this.msg2 = "";
    }

}
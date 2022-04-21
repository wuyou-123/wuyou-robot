package pers.wuyou.robot.game.landlords.common;

import lombok.Getter;
import pers.wuyou.robot.game.common.NotifyType;

/**
 * 提示内容模板
 *
 * @author wuyou
 */
@Getter
public class LandlordsNotifyType extends NotifyType {
    /**
     * 现在轮到 ${玩家} 了, ${提示}
     * 请耐心等待他/她选择是否抢地主
     * 请确认你是否抢地主
     */
    public static final Notify CALL_LANDLORDS = new Notify("现在轮到 ${player} 了, ${tip}", "请耐心等待他/她选择是否抢地主", "请确认你是否抢地主");

    /**
     * 下一位玩家是 ${玩家}, ${提示}
     * 请等待他/她出牌
     * 请选择你要出的牌
     */
    public static final Notify NOTIFY_PLAYER_PLAY = new Notify("下一位玩家是 ${player}, ${tip}", "请等待他/她出牌", "请选择你要出的牌");

    /**
     * ${玩家} 出牌
     */
    public static final Notify PLAYER_PLAY = new Notify("${player} 出牌");

    /**
     * ${玩家} 只剩一张牌了!
     */
    public static final Notify PLAYER_ONLY_ONE_POKER = new Notify("${player} 只剩一张牌了!");

    /**
     * ${玩家} 只剩两张牌了!
     */
    public static final Notify PLAYER_ONLY_TWO_POKER = new Notify("${player} 只剩两张牌了!");

    /**
     * 你出的牌不合法, 不能出这副牌
     */
    public static final Notify PLAYER_PLAY_INVALID = new Notify("你出的牌不合法, 不能出这副牌");

    /**
     * ${玩家} 跳过了
     */
    public static final Notify PLAYER_PASS = new Notify("${player} 跳过了");

    /**
     * 不允许不出牌!
     */
    public static final Notify PLAYER_CANT_PASS = new Notify("不允许不出牌!");

    /**
     * 你出的牌比之前的牌小, 不能出这副牌
     */
    public static final Notify PLAYER_PLAY_LESS = new Notify("你出的牌比之前的牌小, 不能出这副牌");

    /**
     * ${玩家} 已经成为地主并获得了额外的三张牌!
     */
    public static final Notify BE_LANDLORDS = new Notify("${player} 已经成为地主并获得了额外的三张牌!");

    /**
     * ${玩家} 选择抢地主!
     */
    public static final Notify WANT_BE_LANDLORDS = new Notify("${player} 选择抢地主!");

    /**
     * ${玩家} 没有抢地主
     */
    public static final Notify NOT_WANT_BE_LANDLORDS = new Notify("${player} 没有抢地主");

    /**
     * 没有玩家抢地主, 重新发牌!
     */
    public static final Notify NO_PLAYER_CALL_LANDLORDS = new Notify("没有玩家抢地主, 重新发牌!");

}
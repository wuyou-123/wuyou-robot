package pers.wuyou.robot.game.gobang.common;

import lombok.Getter;
import pers.wuyou.robot.game.common.NotifyType;

/**
 * 提示内容模板
 *
 * @author wuyou
 */
@Getter
public class GobangNotifyType extends NotifyType {
    /**
     * 请选择游戏模式
     */
    public static final Notify CHOOSE_MODE = new Notify("请选择游戏模式: \n1. 人机模式\n2. 对战模式");

    public static final Notify CHOOSE_FIRST_HAND = new Notify("请选择先手: \n1. 玩家先手\n2. 电脑先手");

    public static final Notify WAIT_OTHER_PLAYER = new Notify("请等待其他玩家进入游戏");
}
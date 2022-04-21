package pers.wuyou.robot.game.common;

import lombok.Getter;

/**
 * @author wuyou
 */
public class NotifyType {

    /**
     * 游戏结束, 发送"准备"可重新开始
     */
    public static final Notify GAME_END = new Notify("游戏结束, 发送\"准备\"可重新开始");

    /**
     * 玩家 ${玩家} 退出了房间
     */
    public static final Notify PLAYER_EXIT = new Notify("玩家 ${player} 退出了房间");

    /**
     * 玩家 ${玩家} 准备了游戏! 请耐心等待游戏开始
     */
    public static final Notify PLAYER_READY = new Notify("玩家 ${player} 准备了游戏! 请耐心等待游戏开始");

    /**
     * ${玩家} 说
     */
    public static final Notify PLAYER_SPEAK = new Notify("${player} 说");

    @Getter
    public static class Notify {
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

        public Notify(String msg, String msg1, String msg2) {
            this.msg = msg;
            this.msg1 = msg1;
            this.msg2 = msg2;
        }

        public Notify(String msg) {
            this.msg = msg;
            this.msg1 = "";
            this.msg2 = "";
        }
    }
}

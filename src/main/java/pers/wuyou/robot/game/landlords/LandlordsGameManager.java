package pers.wuyou.robot.game.landlords;

import pers.wuyou.robot.game.common.BaseGameManager;

/**
 * @author wuyou
 */
public class LandlordsGameManager extends BaseGameManager {
    /**
     * 游戏名
     */
    public static final String GAME_NAME = "landlords";
    /**
     * 最大玩家数量
     */
    public static final int MAX_PLAYER_COUNT = 3;

    private static LandlordsGameManager manager;

    public LandlordsGameManager() {
        manager = this;
    }

    public static boolean checkResource() {
        return manager.checkRes("classpath:**/generatePoker.py", "classpath:**/poker/*.jpg");
    }

    @Override
    public String getGameName() {
        return GAME_NAME;
    }

}

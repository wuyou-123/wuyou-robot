package pers.wuyou.robot.game.common;

import pers.wuyou.robot.core.exception.RobotException;

/**
 * @author wuyou
 * @date 2022/4/4 13:22
 */
public class GameException extends RobotException {
    public GameException() {
        super();
    }

    public GameException(String message) {
        super(message);
    }
}

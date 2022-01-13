package pers.wuyou.robot.core.exception;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class RobotException extends RuntimeException {
    public RobotException() {
    }

    public RobotException(String message) {
        super(message);
    }

    public RobotException(String message, Throwable cause) {
        super(message, cause);
    }

    public RobotException(Throwable cause) {
        super(cause);
    }

    public RobotException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

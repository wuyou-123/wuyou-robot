package pers.wuyou.robot.core.exception;

/**
 * @author wuyou
 */
public class ResourceNotFoundException extends RobotException {
    public ResourceNotFoundException() {
        super("资源文件未找到,请确认项目文件是否完整");
    }
}

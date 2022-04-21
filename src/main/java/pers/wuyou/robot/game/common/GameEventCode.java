package pers.wuyou.robot.game.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wuyou
 * @date 2022/4/8 11:07
 */
@Data
@AllArgsConstructor
public class GameEventCode {
    private final String code;
    private final String desc;
}

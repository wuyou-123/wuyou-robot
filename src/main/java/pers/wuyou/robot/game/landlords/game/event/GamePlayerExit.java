package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.Map;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerExit implements LandlordsGameEvent {
    @Override
    public void call(LandlordsRoom room, Map<String, Object> data) {
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        LandlordsPlayer player = (LandlordsPlayer) Game.getPlayer(accountCode);
        LandlordsNotifyUtil.notifyPlayerExit(player);
        Game.removePlayer(player);
        room.gameEnd();
    }
}

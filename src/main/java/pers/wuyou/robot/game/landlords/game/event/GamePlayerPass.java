package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.Map;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerPass implements LandlordsGameEvent {
    @Override
    public void call(LandlordsRoom room, Map<String, Object> data) {
        final LandlordsPlayer player = room.getCurrentPlayer();
        if (!player.equals(room.getLastPlayer())) {
            LandlordsPlayer next = player.getNext();
            room.setCurrentPlayer(next);
            LandlordsNotifyUtil.notifyPlayerPass(player);
        } else {
            LandlordsNotifyUtil.notifyPlayerCantPass(player);
        }
    }
}

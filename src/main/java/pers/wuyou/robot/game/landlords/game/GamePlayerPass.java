package pers.wuyou.robot.game.landlords.game;

import pers.wuyou.robot.game.landlords.common.GameEvent;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;

import java.util.Map;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerPass implements GameEvent {
    @Override
    public void call(Room room, Map<String, Object> data) {
        final Player player = room.getCurrentPlayer();
        if (!player.equals(room.getLastPlayer())) {
            Player next = player.getNext();
            room.setCurrentPlayer(next);
            NotifyUtil.notifyPlayerPass(player);
        } else {
            NotifyUtil.notifyPlayerCantPass(player);
        }
    }
}

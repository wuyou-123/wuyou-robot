package pers.wuyou.robot.game.landlords.game;

import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.common.Constant;
import pers.wuyou.robot.game.landlords.common.GameEvent;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;

import java.util.Map;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerExit implements GameEvent {
    @Override
    public void call(Room room, Map<String, Object> data) {
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        Player player = GameManager.getPlayer(accountCode);
        NotifyUtil.notifyPlayerExit(player);
        GameManager.removePlayer(player);
        room.gameEnd();
    }
}

package pers.wuyou.robot.game.gobang.game.event;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.gobang.common.GobangGameEvent;
import pers.wuyou.robot.game.gobang.entity.GobangPlayer;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.util.GobangNotifyUtil;

import java.util.Map;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerExit implements GobangGameEvent {
    @Override
    public void call(GobangRoom room, Map<String, Object> data) {
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        GobangPlayer player = (GobangPlayer) Game.getPlayer(accountCode);
        GobangNotifyUtil.notifyPlayerExit(player, true);
        Game.removePlayer(player);
        room.gameEnd();
    }
}

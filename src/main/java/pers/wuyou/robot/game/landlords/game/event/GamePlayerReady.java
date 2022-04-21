package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.landlords.LandlordsGameManager;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.common.GameEventManager;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEventCode;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.Map;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerReady implements LandlordsGameEvent {
    @Override
    public void call(LandlordsRoom room, Map<String, Object> data) {
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        LandlordsPlayer player = (LandlordsPlayer) Game.getPlayer(accountCode);
        player.setStatus(LandlordsPlayerGameStatus.READY);
        if (room.getPlayerList().size() == LandlordsGameManager.MAX_PLAYER_COUNT && room.canStart()) {
            room.start();
            LandlordsNotifyUtil.notifyRoom(room, "玩家已满, 开始游戏! \n" +
                    "注: 当前为测试阶段, 如果遇到问题或有其他建议请反馈到QQ1097810498");
            GameEventManager.call(LandlordsGameEventCode.CODE_GAME_START, room);
        } else {
            LandlordsNotifyUtil.notifyPlayerReady(player);
        }
    }
}

package pers.wuyou.robot.game.gobang.game.event;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.GameEventManager;
import pers.wuyou.robot.game.gobang.GobangGameManager;
import pers.wuyou.robot.game.gobang.common.GobangGameEvent;
import pers.wuyou.robot.game.gobang.common.GobangGameEventCode;
import pers.wuyou.robot.game.gobang.common.GobangPlayerGameStatus;
import pers.wuyou.robot.game.gobang.common.GobangRoomStatus;
import pers.wuyou.robot.game.gobang.entity.GobangPlayer;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.util.GobangNotifyUtil;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;

import java.util.Map;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerReady implements GobangGameEvent {
    @Override
    public void call(GobangRoom room, Map<String, Object> data) {
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        GobangPlayer player = (GobangPlayer) Game.getPlayer(accountCode);
        player.setStatus(LandlordsPlayerGameStatus.READY);
        if (room.playWithBot()) {
            GobangNotifyUtil.notifyPlayerChooseFirstHand(player, true);
            room.setStatus(GobangRoomStatus.PLAYER_READY);
            player.setStatus(GobangPlayerGameStatus.CHOOSE_FIRST_HAND);
        } else {
            if (room.getPlayerList().size() == GobangGameManager.MAX_PLAYER_COUNT && room.canStart()) {
                GobangNotifyUtil.notifyRoom(room, "玩家已满, 开始游戏! \n" +
                        "注: 当前为测试阶段, 如果遇到问题或有其他建议请反馈到QQ1097810498");
                GameEventManager.call(GobangGameEventCode.CODE_GAME_START, room);
            } else {
                GobangNotifyUtil.notifyPlayerReady(player);
            }
        }
    }
}

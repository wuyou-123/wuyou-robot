package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.landlords.LandlordsGameManager;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.common.GameEventManager;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEventCode;
import pers.wuyou.robot.game.landlords.common.LandlordsNotifyType;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.Map;

/**
 * 抢地主阶段
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerCallLandlords implements LandlordsGameEvent {

    @Override
    public void call(LandlordsRoom room, Map<String, Object> data) {
        LandlordsPlayer player = room.getCurrentPlayer();
        boolean isCallLandlords = (boolean) data.get("select");
        player.setCalledLandlords(isCallLandlords);
        room.setCallLandlordsCount(room.getCallLandlordsCount() + 1);
        if (isCallLandlords) {
            // 设置地主
            room.callLandlords();
        }
        if (room.getCallLandlordsCount() >= LandlordsGameManager.MAX_PLAYER_COUNT + 1) {
            // 第四轮抢地主,直接计算地主
            GameEventManager.call(LandlordsGameEventCode.CODE_GAME_CALL_LANDLORDS_END, room);
            return;
        }
        // 下一位不想的话就跳到下下位,也就是上一位
        final LandlordsPlayer nextPlayer = player.getNext();
        final LandlordsPlayer prePlayer = player.getPre();
        LandlordsPlayer next = nextPlayer.isNotWantCallLandlords() ? prePlayer : nextPlayer;
        if (isCallLandlords) {
            LandlordsNotifyUtil.notify(player, LandlordsNotifyType.WANT_BE_LANDLORDS, 2);
            if (nextPlayer.isNotWantCallLandlords() && prePlayer.isNotWantCallLandlords()) {
                // 如果自己和下一位都没抢,那么直接计算地主
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_CALL_LANDLORDS_END, room);
                return;
            }
        } else {
            LandlordsNotifyUtil.notify(player, LandlordsNotifyType.NOT_WANT_BE_LANDLORDS, 2);
            if (nextPlayer.isNotWantCallLandlords() && prePlayer.getCalledLandlords() != null) {
                // 如果有另一位玩家也没抢,那么直接计算地主
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_CALL_LANDLORDS_END, room);
                return;
            }
            if (prePlayer.isNotWantCallLandlords() && nextPlayer.getCalledLandlords() != null) {
                // 如果有另一位玩家也没抢,那么直接计算地主
                GameEventManager.call(LandlordsGameEventCode.CODE_GAME_CALL_LANDLORDS_END, room);
                return;
            }
        }
        LandlordsNotifyUtil.notify(next, LandlordsNotifyType.CALL_LANDLORDS, 2);
        next.setStatus(LandlordsPlayerGameStatus.CALL_LANDLORDS);
        room.setCurrentPlayer(next);
    }
}

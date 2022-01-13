package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.common.GameEvent;
import pers.wuyou.robot.game.landlords.common.GameEventManager;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.GameEventCode;
import pers.wuyou.robot.game.landlords.enums.NotifyType;
import pers.wuyou.robot.game.landlords.enums.PlayerGameStatus;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;

import java.util.Map;

/**
 * 抢地主阶段
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerCallLandlords implements GameEvent {

    @Override
    public void call(Room room, Map<String, Object> data) {
        Player player = room.getCurrentPlayer();
        boolean isCallLandlords = (boolean) data.get("select");
        player.setCalledLandlords(isCallLandlords);
        room.setCallLandlordsCount(room.getCallLandlordsCount() + 1);
        if (isCallLandlords) {
            // 设置地主
            room.callLandlords();
        }
        if (room.getCallLandlordsCount() >= GameManager.MAX_PLAYER_COUNT + 1) {
            // 第四轮抢地主,直接计算地主
            GameEventManager.call(GameEventCode.CODE_GAME_CALL_LANDLORDS_END, room);
            return;
        }
        // 下一位不想的话就跳到下下位,也就是上一位
        final Player nextPlayer = player.getNext();
        final Player prePlayer = player.getPre();
        Player next = nextPlayer.isNotWantCallLandlords() ? prePlayer : nextPlayer;
        if (isCallLandlords) {
            NotifyUtil.notify(player, NotifyType.WANT_BE_LANDLORDS, 2);
            if (nextPlayer.isNotWantCallLandlords() && prePlayer.isNotWantCallLandlords()) {
                // 如果自己和下一位都没抢,那么直接计算地主
                GameEventManager.call(GameEventCode.CODE_GAME_CALL_LANDLORDS_END, room);
                return;
            }
        } else {
            NotifyUtil.notify(player, NotifyType.NOT_WANT_BE_LANDLORDS, 2);
            if (nextPlayer.isNotWantCallLandlords() && prePlayer.getCalledLandlords() != null) {
                // 如果有另一位玩家也没抢,那么直接计算地主
                GameEventManager.call(GameEventCode.CODE_GAME_CALL_LANDLORDS_END, room);
                return;
            }
            if (prePlayer.isNotWantCallLandlords() && nextPlayer.getCalledLandlords() != null) {
                // 如果有另一位玩家也没抢,那么直接计算地主
                GameEventManager.call(GameEventCode.CODE_GAME_CALL_LANDLORDS_END, room);
                return;
            }
        }
        NotifyUtil.notify(next, NotifyType.CALL_LANDLORDS, 2);
        next.setStatus(PlayerGameStatus.CALL_LANDLORDS);
        room.setCurrentPlayer(next);
    }
}

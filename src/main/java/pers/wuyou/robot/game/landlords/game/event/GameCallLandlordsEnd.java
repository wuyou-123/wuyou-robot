package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.landlords.common.GameEvent;
import pers.wuyou.robot.game.landlords.common.GameEventManager;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.GameEventCode;
import pers.wuyou.robot.game.landlords.enums.NotifyType;
import pers.wuyou.robot.game.landlords.enums.RoomStatus;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;

import java.util.List;
import java.util.Map;

/**
 * 抢地主阶段
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GameCallLandlordsEnd implements GameEvent {

    @Override
    public void call(Room room, Map<String, Object> data) {
        Player player = room.getLandlords();
        if (player == null) {
            NotifyUtil.notify(room, NotifyType.NO_PLAYER_CALL_LANDLORDS, 1);
            GameEventManager.call(GameEventCode.CODE_GAME_START, room);
            return;
        }
        player.getPokers().addAll(room.getLandlordPokers());
        PokerHelper.sortPoker(player.getPokers());
        NotifyUtil.notify(player, NotifyType.BE_LANDLORDS, 2);
        List<Poker> landlordPokers = room.getLandlordPokers();
        NotifyUtil.notifyPokers(room, landlordPokers, 2);
        NotifyUtil.notify(player, NotifyType.NOTIFY_PLAYER_PLAY, 2);
        room.setCurrentPlayer(player);
        room.setStatus(RoomStatus.POKER_PLAY);
        // 通知玩家出牌
        NotifyUtil.notifyPlayerPlayPoker(player);
    }
}

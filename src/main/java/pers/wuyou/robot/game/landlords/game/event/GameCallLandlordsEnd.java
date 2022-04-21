package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.common.GameEventManager;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEventCode;
import pers.wuyou.robot.game.landlords.common.LandlordsNotifyType;
import pers.wuyou.robot.game.landlords.common.LandlordsRoomStatus;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.List;
import java.util.Map;

/**
 * 抢地主阶段
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GameCallLandlordsEnd implements LandlordsGameEvent {

    @Override
    public void call(LandlordsRoom room, Map<String, Object> data) {
        LandlordsPlayer player = room.getLandlords();
        if (player == null) {
            LandlordsNotifyUtil.notify(room, LandlordsNotifyType.NO_PLAYER_CALL_LANDLORDS);
            GameEventManager.call(LandlordsGameEventCode.CODE_GAME_START, room);
            return;
        }
        player.getPokers().addAll(room.getLandlordPokers());
        PokerHelper.sortPoker(player.getPokers());
        LandlordsNotifyUtil.notify(player, LandlordsNotifyType.BE_LANDLORDS, 2);
        List<Poker> landlordPokers = room.getLandlordPokers();
        LandlordsNotifyUtil.notifyPokers(room, landlordPokers, 2);
        LandlordsNotifyUtil.notify(player, LandlordsNotifyType.NOTIFY_PLAYER_PLAY, 2);
        room.setCurrentPlayer(player);
        room.setStatus(LandlordsRoomStatus.POKER_PLAY);
        // 通知玩家出牌
        LandlordsNotifyUtil.notifyPlayerPlayPoker(player);
    }
}

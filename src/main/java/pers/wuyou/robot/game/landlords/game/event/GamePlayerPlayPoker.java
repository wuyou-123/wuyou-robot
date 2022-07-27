package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.entity.PokerSell;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.common.LandlordsNotifyType;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;
import pers.wuyou.robot.game.landlords.common.LandlordsRoomStatus;
import pers.wuyou.robot.game.landlords.enums.SellType;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.List;
import java.util.Map;

/**
 * 玩家出牌
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerPlayPoker implements LandlordsGameEvent {

    @Override
    public void call(LandlordsRoom room, Map<String, Object> data) {
        LandlordsPlayer player = room.getCurrentPlayer();
        player.setStatus(LandlordsPlayerGameStatus.CHOOSE);
        final Character[] pokerList = (Character[]) data.get("pokerList");
        PokerSell currentPokerShell = data.get("currentPokerShell") != null ? (PokerSell) data.get("currentPokerShell") : null;
        if (currentPokerShell == null) {
            int[] indexes = PokerHelper.getIndexes(pokerList, player.getPokers());
            if (!PokerHelper.checkPokerIndex(indexes, player.getPokers())) {
                // 出的牌不合法
                LandlordsNotifyUtil.notifyPlayerPlayPokerInvalid(player);
                return;
            }
            List<Poker> currentPokers = PokerHelper.getPoker(indexes, player.getPokers());
            currentPokerShell = PokerHelper.checkPokerType(currentPokers);
        }
        if (currentPokerShell.getSellType() == SellType.ILLEGAL) {
            // 出的牌不合法
            LandlordsNotifyUtil.notifyPlayerPlayPokerInvalid(player);
            return;
        }
        // 出的牌合法
        if (room.getLastPlayer() != null && !room.getLastPlayer().equals(player)) {
            PokerSell lastPokerShell = room.getLastPlayPoker();
            if (!lastPokerShell.match(currentPokerShell) && !currentPokerShell.getSellType().isBomb()) {
                // 出的牌不匹配
                LandlordsNotifyUtil.notifyPlayerPlayPokerMisMatch(player, currentPokerShell);
                return;
            } else if (lastPokerShell.getScore() >= currentPokerShell.getScore()) {
                // 出的牌比之前的小
                LandlordsNotifyUtil.notifyPlayerPlayPokerLess(player);
                return;
            }
        }
        LandlordsPlayer next = player.getNext();

        room.setLastPlayPoker(currentPokerShell);
        player.getPokers().removeAll(currentPokerShell.getSellPokers());
        LandlordsNotifyUtil.notifyPlayPoker(player);
        if (player.getPokers().isEmpty()) {
            // 游戏结束
            LandlordsNotifyUtil.notifyPlayWin(player);
            room.gameEnd();
            room.setStatus(LandlordsRoomStatus.NO_START);
        } else {
            player.getPlayerDataMap().remove("list");
            room.setLastPlayer(player);
            room.setCurrentPlayer(next);
            // 通知下一位玩家出牌
            LandlordsNotifyUtil.notify(next, LandlordsNotifyType.NOTIFY_PLAYER_PLAY, 2);
            LandlordsNotifyUtil.notifyPlayerPokerCount(next);
            room.setCurrentPlayer(next);
            LandlordsNotifyUtil.notifyPlayerPlayPoker(next);
            List<PokerSell> sells = PokerHelper.validSells(PokerHelper.checkPokerType(room.getLastSellPokers()), next.getPokers());
            if (sells.isEmpty()) {
                LandlordsNotifyUtil.notifyPlayerNoPokerBiggerThanEveryone(next);
            }
        }
    }
}

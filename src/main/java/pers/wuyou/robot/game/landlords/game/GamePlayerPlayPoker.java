package pers.wuyou.robot.game.landlords.game;

import pers.wuyou.robot.game.landlords.common.GameEvent;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.entity.PokerSell;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.NotifyType;
import pers.wuyou.robot.game.landlords.enums.PlayerGameStatus;
import pers.wuyou.robot.game.landlords.enums.RoomStatus;
import pers.wuyou.robot.game.landlords.enums.SellType;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;

import java.util.List;
import java.util.Map;

/**
 * 玩家出牌
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerPlayPoker implements GameEvent {

    @Override
    public void call(Room room, Map<String, Object> data) {
        Player player = room.getCurrentPlayer();
        player.setStatus(PlayerGameStatus.CHOOSE);
        final Character[] pokerList = (Character[]) data.get("pokerList");
        PokerSell currentPokerShell = data.get("currentPokerShell") != null ? (PokerSell) data.get("currentPokerShell") : null;
        if (currentPokerShell == null) {
            int[] indexes = PokerHelper.getIndexes(pokerList, player.getPokers());
            if (!PokerHelper.checkPokerIndex(indexes, player.getPokers())) {
                // 出的牌不合法
                NotifyUtil.notifyPlayerPlayPokerInvalid(player);
                return;
            }
            assert indexes != null;
            List<Poker> currentPokers = PokerHelper.getPoker(indexes, player.getPokers());
            currentPokerShell = PokerHelper.checkPokerType(currentPokers);
        }
        if (currentPokerShell.getSellType() == SellType.ILLEGAL) {
            // 出的牌不合法
            NotifyUtil.notifyPlayerPlayPokerInvalid(player);
            return;
        }
        // 出的牌合法
        if (room.getLastPlayer() != null && !room.getLastPlayer().equals(player)) {
            PokerSell lastPokerShell = room.getLastPlayPoker();
            if (!lastPokerShell.match(currentPokerShell) && !currentPokerShell.getSellType().isBomb()) {
                // 出的牌不匹配
                NotifyUtil.notifyPlayerPlayPokerMisMatch(player, currentPokerShell);
                return;
            } else if (lastPokerShell.getScore() >= currentPokerShell.getScore()) {
                // 出的牌比之前的小
                NotifyUtil.notifyPlayerPlayPokerLess(player);
                return;
            }
        }
        Player next = player.getNext();

        room.setLastPlayer(player);
        room.setLastPlayPoker(currentPokerShell);
        room.setCurrentPlayer(next);

        player.getPokers().removeAll(currentPokerShell.getSellPokers());
        NotifyUtil.notifyPlayPoker(player);
        if (!player.getPokers().isEmpty()) {
            NotifyUtil.notify(next, NotifyType.NOTIFY_PLAYER_PLAY, 2);
            NotifyUtil.notifyPlayerPokerCount(next);
        }

        if (player.getPokers().isEmpty()) {
            // 游戏结束
            NotifyUtil.notifyPlayWin(player);
            room.gameEnd();
            room.setStatus(RoomStatus.GAME_END);
        } else {
            // 通知下一位玩家出牌
            room.setCurrentPlayer(next);
            NotifyUtil.notifyPlayerPlayPoker(next);
            List<PokerSell> sells = PokerHelper.validSells(PokerHelper.checkPokerType(room.getLastSellPokers()), next.getPokers());
            if (sells.isEmpty()) {
                NotifyUtil.notifyPlayerNoPokerBiggerThanEveryone(next);
            }
        }
    }
}

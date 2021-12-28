package pers.wuyou.robot.game.landlords.game;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import pers.wuyou.robot.game.landlords.common.GameEvent;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.NotifyType;
import pers.wuyou.robot.game.landlords.enums.PlayerGameStatus;
import pers.wuyou.robot.game.landlords.enums.PlayerType;
import pers.wuyou.robot.game.landlords.enums.RoomStatus;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;

import java.util.List;
import java.util.Map;

/**
 * 游戏开始,分配扑克牌
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GameStart implements GameEvent {
    @Override
    public void call(Room room, Map<String, Object> data) {
        room.reset();
        List<Player> playerList = room.getPlayerList();

        // 生成扑克牌
        List<List<Poker>> pokersList = PokerHelper.distributePoker();
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).setPokers(pokersList.get(i));
        }
        // 设置地主的三张牌
        room.setLandlordPokers(pokersList.get(3));

        // 随机设置一位玩家第一个出牌
        int startIndex = RandomUtil.randomInt(0, 3);
        room.setCurrentPlayerIndex(startIndex);
        for (Player p : playerList) {
            p.setType(PlayerType.FARMER);
            NotifyUtil.notifyPlayerStart(p);
        }
        ThreadUtil.sleep(200);
        Player currentPlayer = room.getCurrentPlayer();
        NotifyUtil.notify(currentPlayer, NotifyType.CALL_LANDLORDS, 2);
        currentPlayer.setStatus(PlayerGameStatus.CALL_LANDLORDS);
        room.setStatus(RoomStatus.CALL_LANDLORDS);
    }
}

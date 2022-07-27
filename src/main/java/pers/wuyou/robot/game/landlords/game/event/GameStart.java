package pers.wuyou.robot.game.landlords.game.event;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.BaseGameManager;
import pers.wuyou.robot.game.landlords.LandlordsGameManager;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.common.LandlordsNotifyType;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;
import pers.wuyou.robot.game.landlords.enums.PlayerType;
import pers.wuyou.robot.game.landlords.common.LandlordsRoomStatus;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.List;
import java.util.Map;

/**
 * 游戏开始,分配扑克牌
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GameStart implements LandlordsGameEvent {
    @Override
    public void call(LandlordsRoom room, Map<String, Object> data) {
        BaseGameManager.checkEnv();
        if (!LandlordsGameManager.checkResource()) {
            LandlordsNotifyUtil.notifyRoom(room, "资源解压失败, 游戏结束!");
            for (LandlordsPlayer player : room.getPlayerList()) {
                Game.removePlayer(player);
            }
            room.gameEnd();
            return;
        }
        List<LandlordsPlayer> playerList = room.getPlayerList();

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
        for (LandlordsPlayer p : playerList) {
            p.setType(PlayerType.FARMER);
            p.setCalledLandlords(null);
            LandlordsNotifyUtil.notifyPlayerStart(p);
        }
        ThreadUtil.sleep(200);
        LandlordsPlayer currentPlayer = room.getCurrentPlayer();
        LandlordsNotifyUtil.notify(currentPlayer, LandlordsNotifyType.CALL_LANDLORDS, 2);
        currentPlayer.setStatus(LandlordsPlayerGameStatus.CALL_LANDLORDS);
        room.setStatus(LandlordsRoomStatus.CALL_LANDLORDS);
        room.setCallLandlordsCount(0);
    }
}

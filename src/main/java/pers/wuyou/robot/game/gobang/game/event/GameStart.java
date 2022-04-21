package pers.wuyou.robot.game.gobang.game.event;

import lombok.var;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.BaseGameManager;
import pers.wuyou.robot.game.gobang.GobangGameManager;
import pers.wuyou.robot.game.gobang.common.GobangGameEvent;
import pers.wuyou.robot.game.gobang.common.GobangPlayerGameStatus;
import pers.wuyou.robot.game.gobang.common.GobangRoomStatus;
import pers.wuyou.robot.game.gobang.common.Open26;
import pers.wuyou.robot.game.gobang.entity.GobangPlayer;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.entity.Step;
import pers.wuyou.robot.game.gobang.enums.PieceColor;
import pers.wuyou.robot.game.gobang.exception.GobangException;
import pers.wuyou.robot.game.gobang.util.GobangNotifyUtil;

import java.util.List;
import java.util.Map;

/**
 * 游戏开始,分配扑克牌
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GameStart implements GobangGameEvent {
    @Override
    public void call(GobangRoom room, Map<String, Object> data) {
        BaseGameManager.checkEnv();
        room.start();
        if (!GobangGameManager.checkResource()) {
            GobangNotifyUtil.notifyRoom(room, "资源解压失败, 游戏结束!");
            for (GobangPlayer player : room.getPlayerList()) {
                Game.removePlayer(player);
            }
            room.gameEnd();
            return;
        }
        List<GobangPlayer> playerList = room.getPlayerList();
        room.setStatus(GobangRoomStatus.PLAYING);
        for (GobangPlayer player : playerList) {
            player.setStatus(GobangPlayerGameStatus.PLAYING);
        }
        if (room.playWithBot()) {
            room.setBoard(null);
            room.setLastStep(null);
            room.init();
            // 人机对战
            GobangPlayer player = playerList.get(0);
            Open26.Moon open = room.getAi().start(player.getColor() != PieceColor.BLACK);
            // 说明使用了开局
            if (open != null) {
                // 电脑先手
                int[][] b = open.getBoard();
                room.setBoard(b);
                room.setLastStep(open.getStep());
                Step second = null, third = null;
                for (var i = 0; i < b.length; i++) {
                    for (var j = 0; j < b.length; j++) {
                        if (i == 7 && j == 7) {
                            continue;
                        }
                        int r = b[i][j];
                        if (r == 1) {
                            third = new Step(i, j, 1);
                        }
                        if (r == 2) {
                            second = new Step(i, j, 2);
                        }
                    }
                }
                if (second != null) {
                    room.addStep(second);
                }
                if (third != null) {
                    room.addStep(third);
                }
                room.sendBoard();
            } else {
                // 玩家先手
                room.sendBoard();
            }
            player.setStatus(GobangPlayerGameStatus.CHOOSE_CHESS);
            GobangNotifyUtil.notifyPlayerChooseChess(player);
        } else {
            // 人人对战
            GobangPlayer firstPlayer = GobangGameManager.getFirstHand(room);
            GobangPlayer otherPlayer = room.getPlayerList().stream().filter(item -> item != firstPlayer).findFirst().orElse(null);
            if (otherPlayer == null) {
                throw new GobangException("玩家不存在");
            }
            firstPlayer.setFirstHand(true);
            firstPlayer.setColor(PieceColor.BLACK);
            otherPlayer.setFirstHand(false);
            otherPlayer.setColor(PieceColor.WHITE);
            room.setCurrentPlayer(firstPlayer);
            GobangNotifyUtil.notifyFirstHand(firstPlayer);
            GobangNotifyUtil.notifyPlayerChooseChess(firstPlayer);
            firstPlayer.setStatus(GobangPlayerGameStatus.CHOOSE_CHESS);
            otherPlayer.setStatus(GobangPlayerGameStatus.WAIT_OTHER_CHOOSE);
            room.setBoard(null);
            room.setLastStep(null);
            room.init();
            room.sendBoard();
        }
    }
}

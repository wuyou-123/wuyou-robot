package pers.wuyou.robot.game.gobang.game.event;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.NotifyType;
import pers.wuyou.robot.game.gobang.common.GobangGameEvent;
import pers.wuyou.robot.game.gobang.common.GobangPlayerGameStatus;
import pers.wuyou.robot.game.gobang.common.GobangRoomStatus;
import pers.wuyou.robot.game.gobang.entity.*;
import pers.wuyou.robot.game.gobang.util.GobangNotifyUtil;

import java.util.Map;

/**
 * 玩家落子
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerChess implements GobangGameEvent {
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    @Override
    public void call(GobangRoom room, Map<String, Object> data) {
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        GobangPlayer player = (GobangPlayer) Game.getPlayer(accountCode);
        room.setCurrentPlayer(player);
        Step currentStep = player.getLastStep();
        room.setLastStep(currentStep);
        if (room.getBoard()[currentStep.getX()][currentStep.getY()] != Role.EMPTY) {
            GobangNotifyUtil.notifyPlayerCannotChess(player);
            return;
        }
        Step d = room.getAi().turn(currentStep);
        // 提前获取棋盘图片
        room.getBoardImage();
        int score = d.getScore();
        room.setScore(score);
        Step position = new Step(d.getX(), d.getY(), player.getRole());
        int step = d.getStep();
        room.setStep(step);
        position.setRole(player.getRole());
        room.addStep(position);
        if (score >= Score.FIVE / 2) {
            if (step <= 1) {
                room.sendBoard();
                GobangNotifyUtil.notifyPlayerLose(player);
                room.gameEnd();
                if (room.playWithBot()) {
                    GobangNotifyUtil.notifyPlayer(player, NotifyType.GAME_END);
                    room.setStatus(GobangRoomStatus.WAIT_PLAYER_AGAIN);
                } else {
                    GobangNotifyUtil.notifyRoom(player, NotifyType.GAME_END);
                }
                return;
            } else if (step <= 6 && room.getLastScore() < Score.FIVE / 2) {
                GobangNotifyUtil.notifyPlayerWillLose(player);
            }
        } else if (score <= -Score.FIVE / 2) {
            if (step <= 1) {
                room.sendBoard();
                GobangNotifyUtil.notifyPlayerWin(player);
                room.gameEnd();
                if (room.playWithBot()) {
                    GobangNotifyUtil.notifyPlayer(player, NotifyType.GAME_END);
                    room.setStatus(GobangRoomStatus.WAIT_PLAYER_AGAIN);
                } else {
                    GobangNotifyUtil.notifyRoom(player, NotifyType.GAME_END);
                }
                return;
            } else if (step <= 6 && room.getLastScore() > -Score.FIVE / 2) {
                GobangNotifyUtil.notifyPlayerWillWin(player);
            }
        }
        room.setLastScore(score);
        GobangNotifyUtil.notifyPlayerChooseChess(player.getNext());
        player.setStatus(GobangPlayerGameStatus.WAIT_OTHER_CHOOSE);
        player.getNext().setStatus(GobangPlayerGameStatus.CHOOSE_CHESS);
        room.sendBoard();
    }
}

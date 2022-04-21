package pers.wuyou.robot.game.gobang.game.event;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.NotifyUtil;
import pers.wuyou.robot.game.common.BasePlayer;
import pers.wuyou.robot.game.gobang.common.GobangGameEvent;
import pers.wuyou.robot.game.gobang.common.GobangPlayerGameStatus;
import pers.wuyou.robot.game.gobang.entity.GobangPlayer;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.util.GobangNotifyUtil;

import java.util.Map;

/**
 * 玩家加入房间
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerJoin implements GobangGameEvent {

    @Override
    public void call(GobangRoom r, Map<String, Object> data) {
        String groupCode = data.get(Constant.GROUP_CODE).toString();
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        String name = data.get(Constant.NAME).toString();
        GobangRoom room = (GobangRoom) Game.getRoom(groupCode, accountCode, Game.GameType.GOBANG);
        GobangPlayer player = null;
        BasePlayer<?> p = Game.getPlayer(accountCode);
        if (p != null) {
            if (p.getRoom().getGameType() != Game.GameType.GOBANG || !p.isInRoom(groupCode)) {
                // 已在其他房间中
                NotifyUtil.notifyRoom(groupCode, "当前在其他房间[" + p.getRoom().getName() + "](" + p.getRoomId() + ")中, 请先退出当前房间");
                return;
            } else {
                player = (GobangPlayer) p;
            }
        }
        if (room != null && room.getPlayer(accountCode) != null) {
            if (room.isFull()) {
                // 房间已满
                NotifyUtil.notifyRoom(room, "房间已满");
                return;
            }
            if (player != null) {
                if (player.isInRoom(groupCode)) {
                    // 玩家在当前房间中
                    NotifyUtil.notifyRoom(groupCode, "已经在房间里了");
                    return;
                }
                // 玩家在其他房间中
                NotifyUtil.notifyRoom(groupCode, "当前在其他房间[" + player.getRoom().getName() + "](" + player.getRoomId() + ")中, 请先退出当前房间");
                return;
            }
            player = room.addPlayer(new GobangPlayer(accountCode, name, room));
            for (GobangPlayer gobangPlayer : room.getPlayerList()) {
                gobangPlayer.setStatus(GobangPlayerGameStatus.NO_READY);
            }
            GobangNotifyUtil.notifyPlayerJoinSuccess(player);
            // 加入房间成功
            return;
        }
        room = (GobangRoom) Game.addRoom(groupCode, Game.GameType.GOBANG);
        player = room.addPlayer(new GobangPlayer(accountCode, name, room));
        // 创建房间成功
        GobangNotifyUtil.notifyPlayerChooseMode(player);
        player.setStatus(GobangPlayerGameStatus.CHOOSE_MODE);
    }

}

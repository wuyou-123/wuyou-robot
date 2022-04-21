package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.NotifyUtil;
import pers.wuyou.robot.game.common.BasePlayer;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.Map;

/**
 * 玩家加入房间
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerJoin implements LandlordsGameEvent {

    @Override
    public void call(LandlordsRoom r, Map<String, Object> data) {
        String groupCode = data.get(Constant.GROUP_CODE).toString();
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        String name = data.get(Constant.NAME).toString();
        LandlordsRoom room = (LandlordsRoom) Game.getRoom(groupCode, Game.GameType.LANDLORDS);
        LandlordsPlayer player = null;
        BasePlayer<?> p = Game.getPlayer(accountCode);
        if (p != null) {
            if (p.getRoom().getGameType() != Game.GameType.LANDLORDS) {
                // 已在其他房间中
                NotifyUtil.notifyRoom(groupCode, "当前在其他房间[" + p.getRoom().getName() + "](" + p.getRoomId() + ")中, 请先退出当前房间");
                return;
            } else {
                player = (LandlordsPlayer) p;
            }
        }
        if (room != null) {
            if (room.isFull()) {
                // 房间已满
                LandlordsNotifyUtil.notifyRoom(room, "房间已满");
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
            player = room.addPlayer(new LandlordsPlayer(accountCode, name, room));
            LandlordsNotifyUtil.notifyPlayerJoinSuccess(player);
            // 加入房间成功
            return;
        }
        room = (LandlordsRoom) Game.addRoom(groupCode, Game.GameType.LANDLORDS);
        player = room.addPlayer(new LandlordsPlayer(accountCode, name, room));
        // 创建房间成功
        LandlordsNotifyUtil.notifyPlayerCreateSuccess(player);
    }

}

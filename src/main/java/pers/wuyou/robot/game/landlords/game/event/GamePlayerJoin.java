package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.common.Constant;
import pers.wuyou.robot.game.landlords.common.GameEvent;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.util.NotifyUtil;

import java.util.Map;

/**
 * 玩家加入房间
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerJoin implements GameEvent {

    @Override
    public void call(Room r, Map<String, Object> data) {
        String groupCode = data.get(Constant.GROUP_CODE).toString();
        String accountCode = data.get(Constant.ACCOUNT_CODE).toString();
        String name = data.get(Constant.NAME).toString();
        Player player = GameManager.getPlayer(accountCode);
        if (player != null) {
            if (player.isInRoom(groupCode)) {
                // 玩家在当前房间中
                NotifyUtil.notifyRoom(groupCode, "已经在房间里了");
                return;
            }
            // 玩家在其他房间中
            NotifyUtil.notifyRoom(groupCode, "当前在其他房间游戏中, 请先退出当前房间");
            return;
        }
        Room room = GameManager.getRoom(groupCode);
        if (room != null) {
            if (room.getPlayerList().size() == GameManager.MAX_PLAYER_COUNT) {
                // 房间已满
                NotifyUtil.notifyRoom(room, "房间已满");
                return;
            }
            player = GameManager.addPlayer(accountCode, name, room);
            NotifyUtil.notifyPlayerJoinSuccess(player);
            // 加入房间成功
            return;
        }
        player = GameManager.addPlayerAndRoom(accountCode, name, groupCode);
        // 创建房间成功
        NotifyUtil.notifyPlayerCreateSuccess(player);
    }

}

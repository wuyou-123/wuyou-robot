package pers.wuyou.robot.game.gobang.listener;

import cn.hutool.core.map.MapUtil;
import love.forte.simbot.annotation.*;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.util.SenderUtil;
import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.GameEventManager;
import pers.wuyou.robot.game.common.BaseRoom;
import pers.wuyou.robot.game.gobang.common.GobangGameEventCode;
import pers.wuyou.robot.game.gobang.common.GobangPlayerGameStatus;
import pers.wuyou.robot.game.gobang.common.GobangRoomStatus;
import pers.wuyou.robot.game.gobang.common.MessageDispenser;
import pers.wuyou.robot.game.gobang.entity.GobangPlayer;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.exception.GobangException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyou
 */
@Component
@ListenGroup("Gobang")
public class GobangListener {

    @RobotListen(value = GroupMsg.class, isBoot = true)
    @Filter("五子棋")
    public void start(GroupMsg msg, @ContextValue(ContextType.GROUP) String group, @ContextValue(ContextType.QQ) String qq) {
        final GroupMemberInfo memberInfo = RobotCore.getter().getMemberInfo(group, qq);
        Map<String, Object> map = MapUtil.<String, Object>builder()
                .put(Constant.GROUP_CODE, memberInfo.getGroupInfo().getGroupCode())
                .put(Constant.ACCOUNT_CODE, memberInfo.getAccountCode())
                .put(Constant.NAME, memberInfo.getAccountRemarkOrNickname())
                .put(Constant.IGNORE_ROOM, true)
                .put(Constant.GAME_TYPE, Game.GameType.GOBANG)
                .map();
        GameEventManager.callIgnoreRoom(GobangGameEventCode.CODE_ROOM_JOIN, map);

    }

    @RobotListen(value = GroupMsg.class, isBoot = true)
    @OnPrivate
    @Filters(customFilter = "gobang")
    public void gobang(MsgGet msgGet, @ContextValue(ContextType.QQ) String qq, @ContextValue(ContextType.GROUP) String group, @ContextValue(ContextType.MESSAGE) String message) {
        boolean isPrivateMsg = msgGet instanceof PrivateMsg;
        try {
            Map<String, Object> data = new HashMap<>(2);
            data.put(Constant.ACCOUNT_CODE, qq);
            data.put(Constant.IS_PRIVATE_MSG, isPrivateMsg);
            data.put(Constant.MESSAGE, message);
            data.put(Constant.GROUP_CODE, group);
            data.put(Constant.GAME_TYPE, Game.GameType.GOBANG);
            final BaseRoom<?> r = Game.getRoom(group, qq, Game.GameType.GOBANG);
            if (r == null) {
                return;
            }
            GobangPlayer player = (GobangPlayer) r.getPlayer(qq);
            if (player == null) {
                return;
            }
            GobangRoom room = (GobangRoom) Game.getPlayer(qq).getRoom();
            boolean isOtherMsg = true;
            switch (room.getStatus()) {
                case GobangRoomStatus.NO_START:
                case GobangRoomStatus.PLAYER_READY:
                    if (player.getStatus().equals(GobangPlayerGameStatus.CHOOSE_MODE)) {
                        isOtherMsg = MessageDispenser.chooseMode(data);
                        break;
                    }
                    if (player.getStatus().equals(GobangPlayerGameStatus.CHOOSE_FIRST_HAND)) {
                        isOtherMsg = MessageDispenser.chooseFirstHand(data);
                        break;
                    }
                    if (!room.playWithBot()) {
                        isOtherMsg = MessageDispenser.playerReady(data);
                        break;
                    }
                    break;
                case GobangRoomStatus.PLAYING:
                    isOtherMsg = MessageDispenser.chooseChess(data);
                    break;
                case GobangRoomStatus.WAIT_PLAYER_AGAIN:
                    isOtherMsg = MessageDispenser.playerReady(data);
                default:
                    break;
            }
            if (isOtherMsg) {
                MessageDispenser.otherMsg(data);
            }
        } catch (GobangException e) {
            SenderUtil.sendGroupMsg(group, e.getMessage());
        }
    }
}

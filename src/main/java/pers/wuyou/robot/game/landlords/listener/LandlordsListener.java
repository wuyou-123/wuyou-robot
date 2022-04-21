package pers.wuyou.robot.game.landlords.listener;

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
import pers.wuyou.robot.game.landlords.common.LandlordsGameEventCode;
import pers.wuyou.robot.game.landlords.common.MessageDispenser;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.common.LandlordsRoomStatus;
import pers.wuyou.robot.game.landlords.exception.LandlordsException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyou
 */
@Component
@ListenGroup("Landlords")
public class LandlordsListener {

    @RobotListen(value = GroupMsg.class, isBoot = true)
    @Filter("斗地主")
    public void start(GroupMsg msg, @ContextValue(ContextType.GROUP) String group, @ContextValue(ContextType.QQ) String qq) {
        final GroupMemberInfo memberInfo = RobotCore.getter().getMemberInfo(group, qq);
        Map<String, Object> map = MapUtil.<String, Object>builder()
                .put(Constant.GROUP_CODE, memberInfo.getGroupInfo().getGroupCode())
                .put(Constant.ACCOUNT_CODE, memberInfo.getAccountCode())
                .put(Constant.NAME, memberInfo.getAccountRemarkOrNickname())
                .put(Constant.IGNORE_ROOM, true)
                .put(Constant.GAME_TYPE, Game.GameType.LANDLORDS)
                .map();
        GameEventManager.callIgnoreRoom(LandlordsGameEventCode.CODE_ROOM_JOIN, map);

    }

    @RobotListen(value = GroupMsg.class, isBoot = true)
    @Filter(value = "重置斗地主", groups = "696525951")
    public void reset(@ContextValue(ContextType.GROUP) String group) {
        Game.removeRoom(group, Game.GameType.LANDLORDS);
    }

    @RobotListen(value = GroupMsg.class, isBoot = true)
    @OnPrivate
    @Filters(customFilter = "landlords")
    public void landlords(MsgGet msgGet, @ContextValue(ContextType.QQ) String qq, @ContextValue(ContextType.GROUP) String group, @ContextValue(ContextType.MESSAGE) String message) {
        boolean isPrivateMsg = msgGet instanceof PrivateMsg;
        try {
            Map<String, Object> data = new HashMap<>(2);
            data.put(Constant.ACCOUNT_CODE, qq);
            data.put(Constant.IS_PRIVATE_MSG, isPrivateMsg);
            data.put(Constant.MESSAGE, message);
            data.put(Constant.GAME_TYPE, Game.GameType.LANDLORDS);
            final BaseRoom<?> r = Game.getRoom(group, Game.GameType.LANDLORDS);
            if (r == null) {
                return;
            }
            LandlordsPlayer player = (LandlordsPlayer) r.getPlayer(qq);
            if (player == null) {
                return;
            }
            player.setPrivateMessage(isPrivateMsg);
            LandlordsRoom room = (LandlordsRoom) Game.getPlayer(qq).getRoom();
            boolean isOtherMsg = true;
            switch (room.getStatus()) {
                case LandlordsRoomStatus.CALL_LANDLORDS:
                    isOtherMsg = MessageDispenser.callLandlords(data);
                    break;
                case LandlordsRoomStatus.POKER_PLAY:
                    isOtherMsg = MessageDispenser.playerPoker(data);
                    break;
                case LandlordsRoomStatus.NO_START:
                case LandlordsRoomStatus.PLAYER_READY:
                    if (isPrivateMsg) {
                        isOtherMsg = MessageDispenser.playerReady(data);
                    }
                    break;
                default:
                    break;
            }
            if (isOtherMsg) {
                MessageDispenser.otherMsg(data);
            }
        } catch (LandlordsException e) {
            SenderUtil.sendGroupMsg(group, e.getMessage());
        }
    }
}

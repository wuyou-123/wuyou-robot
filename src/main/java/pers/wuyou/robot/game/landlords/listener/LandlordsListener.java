package pers.wuyou.robot.game.landlords.listener;

import cn.hutool.core.map.MapUtil;
import love.forte.simbot.annotation.*;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.annotation.ContextType;
import pers.wuyou.robot.annotation.RobotListen;
import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.common.Constant;
import pers.wuyou.robot.game.landlords.common.GameEventManager;
import pers.wuyou.robot.game.landlords.common.MessageDispenser;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.GameEventCode;
import pers.wuyou.robot.game.landlords.exception.LandLordsException;
import pers.wuyou.robot.util.SenderUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author wuyou
 */
@Component
@ListenGroup("Landlords")
public class LandlordsListener {
    private final Set<String> testGroupSet;

    {
        testGroupSet = new HashSet<>();
        testGroupSet.add("696525951");
        testGroupSet.add("1043409458");
    }

    @RobotListen(GroupMsg.class)
    @Filter("斗地主")
    public void start(@ContextValue(ContextType.GROUP) String group, @ContextValue(ContextType.MEMBER) GroupMemberInfo memberInfo) {
        if (!testGroupSet.contains(group)) {
            return;
        }
        Map<String, Object> map = MapUtil.<String, Object>builder()
                .put(Constant.GROUP_CODE, memberInfo.getGroupInfo().getGroupCode())
                .put(Constant.ACCOUNT_CODE, memberInfo.getAccountCode())
                .put(Constant.NAME, memberInfo.getAccountRemarkOrNickname())
                .put(Constant.IGNORE_ROOM, true)
                .map();
        GameEventManager.callIgnoreRoom(GameEventCode.CODE_ROOM_JOIN, map);

    }

    @RobotListen(GroupMsg.class)
    @Filter(value = "1", groups = "696525951")
    public void reset(@ContextValue(ContextType.GROUP) String group) {
        GameManager.reset(group);
    }

    @RobotListen(PrivateMsg.class)
    @Filter(codes = "1097810498")
    public void add(@ContextValue(ContextType.MESSAGE) String message) {
        String add = "add";
        if (message.startsWith(add)) {
            testGroupSet.add(message.substring(3));
        }
    }

    @RobotListen(PrivateMsg.class)
    @Filter(codes = "1097810498")
    public void remove(@ContextValue(ContextType.MESSAGE) String message) {
        String remove = "remove";
        if (message.startsWith(remove)) {
            testGroupSet.remove(message.substring(6));
        }
    }

    @RobotListen(GroupMsg.class)
    @OnPrivate
    @Filters(customFilter = "landlords")
    public void landlords(MsgGet msgGet, @ContextValue(ContextType.QQ) String qq, @ContextValue(ContextType.GROUP) String group, @ContextValue(ContextType.MESSAGE) String message) {
        boolean isPrivateMsg = msgGet instanceof PrivateMsg;
        try {
            Map<String, Object> data = new HashMap<>(2);
            data.put(Constant.ACCOUNT_CODE, qq);
            data.put(Constant.IS_PRIVATE_MSG, isPrivateMsg);
            Player player = GameManager.getPlayer(qq);
            player.setCurrentMessageIsPrivate(isPrivateMsg);
            Room room = GameManager.getRoomByAccountCode(qq);
            switch (room.getStatus()) {
                case CALL_LANDLORDS:
                    MessageDispenser.callLandlords(message, data);
                    break;
                case POKER_PLAY:
                    MessageDispenser.playerPoker(message, data);
                    break;
                case NO_START:
                case PLAYER_READY:
                    if (isPrivateMsg) {
                        MessageDispenser.playerReady(message, data);
                    }
                    break;
                default:
                    break;
            }
            MessageDispenser.otherMsg(message, data);
        } catch (LandLordsException e) {
            SenderUtil.sendGroupMsg(group, e.getMessage());
        }
    }
}

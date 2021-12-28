package pers.wuyou.robot.game.landlords.filters;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.filter.FilterData;
import love.forte.simbot.filter.ListenerFilter;
import org.jetbrains.annotations.NotNull;
import pers.wuyou.robot.game.landlords.GameManager;
import pers.wuyou.robot.game.landlords.entity.Player;

/**
 * @author wuyou
 */
@Beans("landlords")
public class LandlordsFilter implements ListenerFilter {
    @Override
    public boolean test(@NotNull FilterData data) {
        final MsgGet msgGet = data.getMsgGet();
        final String accountCode = msgGet.getAccountInfo().getAccountCode();
        final Player player = GameManager.getPlayer(accountCode);
        if (player == null) {
            return false;
        }
        if (msgGet instanceof GroupMsg) {
            return player.getRoomId().equals(((GroupMsg) msgGet).getGroupInfo().getGroupCode());
        }
        return true;
    }
}

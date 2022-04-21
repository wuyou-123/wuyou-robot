package pers.wuyou.robot.game.common;

import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.intercept.InterceptionType;
import love.forte.simbot.listener.ListenerInterceptContext;
import love.forte.simbot.listener.ListenerInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.annotation.ContextType;

/**
 * @author wuyou
 */
@Component
public class GameListenerInterceptor implements ListenerInterceptor {

    @NotNull
    @Override
    public InterceptionType intercept(@NotNull ListenerInterceptContext context) {
        if (context.getMsgGet() instanceof PrivateMsg) {
            String accountCode = context.getMsgGet().getAccountInfo().getAccountCode();
            final BasePlayer<?> player = Game.getPlayer(accountCode);
            if (player == null) {
                return InterceptionType.PASS;
            }
            final String roomId = player.getRoomId();
            context.getListenerContext().instant(ContextType.GROUP, roomId);
            context.getListenerContext().instant(ContextType.GROUP_CODE, roomId);
        }
        return InterceptionType.PASS;
    }
}

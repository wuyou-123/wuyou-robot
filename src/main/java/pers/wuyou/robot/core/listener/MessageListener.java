package pers.wuyou.robot.core.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import love.forte.simbot.annotation.ContextValue;
import love.forte.simbot.annotation.ListenGroup;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.annotation.Priority;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MessageRecallEventGet;
import love.forte.simbot.constant.PriorityConstant;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.entity.Message;
import pers.wuyou.robot.core.service.MessageService;

/**
 * 消息监听器,保存聊天记录
 *
 * @author wuyou
 */
@Listener
@ListenGroup("core")
public class MessageListener {
    private final MessageService messageService;

    public MessageListener(MessageService messageService) {
        this.messageService = messageService;
    }

    @RobotListen(MessageGet.class)
    @Priority(PriorityConstant.CORE_FIRST)
    public void saveMessage(MessageGet msgGet, @ContextValue(ContextType.MESSAGE_ENTITY) Message message) {
        messageService.save(message);
    }

    @RobotListen(MessageRecallEventGet.class)
    @Priority(PriorityConstant.CORE_SECOND)
    public void messageRecall(MessageRecallEventGet msgGet) {
        final LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<Message>().eq(Message::getMessageId, msgGet.getId().substring(4));
        final Message message = messageService.getOne(queryWrapper);
        if (message == null) {
            return;
        }
        message.recall(msgGet);
        messageService.updateById(message);
    }

}

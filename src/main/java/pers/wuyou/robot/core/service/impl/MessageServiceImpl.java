package pers.wuyou.robot.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.wuyou.robot.core.entity.Message;
import pers.wuyou.robot.core.mapper.MessageMapper;
import pers.wuyou.robot.core.service.MessageService;

/**
 * @author wuyou
 * @since 2021-08-24
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}

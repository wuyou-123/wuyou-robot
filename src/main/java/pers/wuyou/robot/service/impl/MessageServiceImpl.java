package pers.wuyou.robot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.wuyou.robot.entity.Message;
import pers.wuyou.robot.mapper.MessageMapper;
import pers.wuyou.robot.service.MessageService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-24
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}

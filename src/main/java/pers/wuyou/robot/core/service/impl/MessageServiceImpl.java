package pers.wuyou.robot.core.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.wuyou.robot.core.entity.Message;
import pers.wuyou.robot.core.mapper.MessageMapper;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-24
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IService<Message> {

}

package pers.wuyou.robot.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.wuyou.robot.core.entity.Message;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2021-08-24
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}

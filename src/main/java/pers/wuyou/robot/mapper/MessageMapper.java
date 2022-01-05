package pers.wuyou.robot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.wuyou.robot.entity.Message;

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

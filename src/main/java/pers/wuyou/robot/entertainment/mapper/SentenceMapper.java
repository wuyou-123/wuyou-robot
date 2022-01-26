package pers.wuyou.robot.entertainment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.wuyou.robot.entertainment.entity.Sentence;

/**
 * @author wuyou
 */
@Mapper
public interface SentenceMapper extends BaseMapper<Sentence> {
    /**
     * 获取随机的一条
     * @return 实体对象
     */
    Sentence getRandomOne();
}

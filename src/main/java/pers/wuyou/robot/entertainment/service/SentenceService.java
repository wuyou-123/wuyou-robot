package pers.wuyou.robot.entertainment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.wuyou.robot.entertainment.entity.Sentence;

/**
 * @author wuyou
 */
public interface SentenceService extends IService<Sentence> {
    /**
     * 获取随机的一条
     * @return 实体对象
     */
    Sentence getRandomOne();
}

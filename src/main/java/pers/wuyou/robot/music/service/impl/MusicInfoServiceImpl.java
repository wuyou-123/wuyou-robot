package pers.wuyou.robot.music.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.wuyou.robot.music.entity.MusicInfo;
import pers.wuyou.robot.music.mapper.MusicInfoMapper;
import pers.wuyou.robot.music.service.MusicInfoService;

/**
 * @author wuyou
 */
@Service
public class MusicInfoServiceImpl extends ServiceImpl<MusicInfoMapper, MusicInfo> implements MusicInfoService {
}

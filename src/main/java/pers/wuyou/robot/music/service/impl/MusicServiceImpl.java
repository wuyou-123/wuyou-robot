package pers.wuyou.robot.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.music.entity.MusicInfo;
import pers.wuyou.robot.music.service.BaseMusicService;
import pers.wuyou.robot.music.service.MusicInfoService;
import pers.wuyou.robot.music.service.MusicSearchService;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author wuyou
 */
@Service
@Slf4j
public class MusicServiceImpl extends BaseMusicService {

    public MusicServiceImpl(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
        this.musicPath = RobotCore.PROJECT_PATH + TYPE_NAME + File.separator;
    }

    @Override
    public List<MusicInfo> search(String name) {
        initServices();
        for (SearchService service : SearchService.values()) {
            final List<MusicInfo> musicInfoList = searchMusic(name, service);
            if (musicInfoList != null) {
                return musicInfoList;
            }
            log.info(service.getName() + "搜索返回空");
        }
        return Collections.emptyList();
    }

    @Override
    public List<MusicInfo> search(String name, SearchService service) {
        initServices();
        final List<MusicInfo> list = searchMusic(name, service);
        return list != null ? list : Collections.emptyList();
    }

    @Nullable
    private List<MusicInfo> searchMusic(String name, SearchService service) {
        final MusicSearchService musicSearchService = service.getMusicSearchServiceClass();
        final List<MusicInfo> musicInfoList;
        try {
            musicInfoList = RobotCore.THREAD_POOL.submit(() -> musicSearchService.search(name)).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            return null;
        }
        if (musicInfoList.isEmpty()) {
            return null;
        }
        for (MusicInfo musicInfo : musicInfoList) {
            musicInfo.setType(service);
        }
        RobotCore.THREAD_POOL.execute(() -> {
            final List<MusicInfo> list = musicInfoService.list(new LambdaQueryWrapper<MusicInfo>().in(MusicInfo::getMid, musicInfoList.stream().map(MusicInfo::getMid).collect(Collectors.toList())));
            if (list.size() != musicInfoList.size()) {
                final List<String> collect = list.stream().map(MusicInfo::getMid).collect(Collectors.toList());
                final Map<Boolean, List<MusicInfo>> map = musicInfoList.stream().collect(Collectors.groupingBy(i -> collect.contains(i.getMid())));
                musicInfoService.saveBatch(map.get(Boolean.FALSE));
                musicInfoService.updateBatchById(map.get(Boolean.TRUE));
            }
        });
        return musicInfoList;
    }

    @Override
    public void run(String... args) {
        initServices();
        for (SearchService service : SearchService.values()) {
            RobotCore.THREAD_POOL.execute(() -> {
                final boolean loginResult = service.getMusicSearchServiceClass().login();
                log.info(String.format("%s login %s.", service.getName(), loginResult ? "success" : "fail"));
            });
        }
    }

    private void initServices() {
        if (musicSearchServiceList == null) {
            musicSearchServiceList = new ArrayList<>();
            for (SearchService service : Arrays.stream(SearchService.values()).sorted(Comparator.comparingInt(SearchService::getPriority)).collect(Collectors.toList())) {
                musicSearchServiceList.add(service.getMusicSearchServiceClass());
            }
        }
    }
}

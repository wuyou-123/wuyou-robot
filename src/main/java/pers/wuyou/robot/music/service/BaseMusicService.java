package pers.wuyou.robot.music.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.music.entity.MusicInfo;
import pers.wuyou.robot.music.service.impl.KuWoSearchImpl;
import pers.wuyou.robot.music.service.impl.NetEaseMusicSearchImpl;
import pers.wuyou.robot.music.service.impl.QQMusicSearchImpl;

import java.util.List;

/**
 * @author wuyou
 */
public abstract class BaseMusicService implements CommandLineRunner {
    @Setter
    @Getter
    protected String musicPath;
    protected MusicInfoService musicInfoService;
    @Getter
    protected List<MusicSearchService> musicSearchServiceList;
    public static final String TYPE_NAME = "music";

    /**
     * 搜索音乐
     *
     * @param name 音乐名
     * @return 返回的音乐列表
     */
    public abstract List<MusicInfo> search(String name);

    /**
     * 搜索音乐
     *
     * @param name    音乐名
     * @param service 搜索引擎
     * @return 返回的音乐列表
     */
    public abstract List<MusicInfo> search(String name, SearchService service);

    @Getter
    public enum SearchService {
        /**
         * qq音乐
         */
        QQ(QQMusicSearchImpl.class, "qqMusic", "QQ音乐", 0),
        /**
         * 网易云音乐
         */
        NET_EASE(NetEaseMusicSearchImpl.class, "neteaseCloudMusic", "网易云音乐", 1),
        /**
         * 网易云音乐
         */
        KU_WO(KuWoSearchImpl.class, "kuWoMusic", "酷我音乐", 2);

        private final MusicSearchService musicSearchServiceClass;
        @Getter
        private final String type;
        @Getter
        private final String name;
        @Getter
        private final Integer priority;

        SearchService(Class<? extends MusicSearchService> musicSearchServiceClass, String type, String name, Integer priority) {
            this.musicSearchServiceClass = RobotCore.getApplicationContext().getBean(musicSearchServiceClass);
            this.type = type;
            this.name = name;
            this.priority = priority;
        }
    }
}

package pers.wuyou.robot.music.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import love.forte.simbot.annotation.ContextValue;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.FilterValue;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.filter.MatchType;
import love.forte.simbot.listener.ListenerContext;
import love.forte.simbot.listener.ScopeContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.util.CatUtil;
import pers.wuyou.robot.core.util.FileUtil;
import pers.wuyou.robot.core.util.SenderUtil;
import pers.wuyou.robot.music.entity.MusicInfo;
import pers.wuyou.robot.music.service.BaseMusicService;
import pers.wuyou.robot.music.service.MusicInfoService;
import pers.wuyou.robot.music.service.MusicSearchService;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author wuyou
 */
@Component
public class MusicListener {

    private static final String NET_EASE_PREFIX = "网易";
    private static final String QQ_PREFIX = "qq";
    private final BaseMusicService musicSearchService;
    private final MusicInfoService musicInfoService;
    @Value("${robot.host}")
    private String host;

    public MusicListener(BaseMusicService musicSearchService, MusicInfoService musicInfoService) {
        this.musicSearchService = musicSearchService;
        this.musicInfoService = musicInfoService;
    }

    @RobotListen(value = MessageGet.class, isBoot = true)
    @Filter(value = "^(网易.*|[Q,q]{2}.*|)(点歌|搜歌){{name}}$", matchType = MatchType.REGEX_FIND)
    public void music(MessageGet msgGet,
                      @ContextValue(ContextType.QQ) String qq,
                      @ContextValue(ContextType.MESSAGE) String message,
                      @FilterValue("name") String name,
                      ListenerContext listenerContext) {
        List<MusicInfo> musicInfoList;
        // 根据前缀搜索歌曲
        if (message.toLowerCase(Locale.ROOT).startsWith(NET_EASE_PREFIX)) {
            musicInfoList = musicSearchService.search(name, BaseMusicService.SearchService.NET_EASE);
        } else if (message.toLowerCase(Locale.ROOT).startsWith(QQ_PREFIX)) {
            musicInfoList = musicSearchService.search(name, BaseMusicService.SearchService.QQ);
        } else {
            musicInfoList = musicSearchService.search(name);
        }
        if (musicInfoList.isEmpty()) {
            SenderUtil.sendMsg(msgGet, "搜索失败~");
            getContext(listenerContext).remove(getKey(qq));
            return;
        }
        // 构建发送内容
        final StringBuilder stringBuilder = new StringBuilder(
                String.format("\"%s\"的搜索结果, 来源: %s%n", name, musicInfoList.get(0).getType().getName())
        );
        for (int i = 0; i < musicInfoList.size(); i++) {
            stringBuilder.append(String.format("%s. %s%n   %s - %s%n", i + 1, musicInfoList.get(i).getTitle(), musicInfoList.get(i).getArtist(), musicInfoList.get(i).getAlbum()));
        }
        stringBuilder.append("发送序号听歌\n如果你想要下载的话,也可以发送\"下载+序号\"来下载歌曲");
        // 存入上下文
        final ScopeContext context = getContext(listenerContext);
        context.set(getKey(qq), musicInfoList);
        SenderUtil.sendMsg(msgGet, stringBuilder.toString());
    }

    @RobotListen(value = MessageGet.class, isBoot = true)
    @Filter(value = "^下载{{number,\\d+}}$", matchType = MatchType.REGEX_FIND)
    public void download(MessageGet msgGet, @ContextValue(ContextType.QQ) String qq, @FilterValue("number") int number, ListenerContext listenerContext) {
        MusicInfo musicInfo = getMusicInfo(qq, number, listenerContext);
        if (musicInfo == null) {
            return;
        }
        if (musicInfo.isPayPlay()) {
            SenderUtil.sendMsg(msgGet, "你要下载的歌为付费播放歌曲, 正在通过其他渠道搜索歌曲~");
            final List<MusicInfo> infoList = musicSearchService.search(musicInfo.getTitle(), BaseMusicService.SearchService.KU_WO);
            musicInfo = infoList.stream().filter(MusicInfo::isPayPlay).collect(Collectors.toList()).get(0);
        }
        final MusicSearchService service = musicInfo.getType().getMusicSearchServiceClass();
        final MusicInfo one = musicInfoService.getOne(new LambdaQueryWrapper<MusicInfo>().eq(MusicInfo::getMid, musicInfo.getMid()));
        one.setMusicUrl(musicInfo.getMusicUrl());
        one.setType(musicInfo.getType());
        musicInfo = one;
        if (musicInfo.getFileName() != null && FileUtil.exist(BaseMusicService.TYPE_NAME + File.separator + musicInfo.getFileName())) {
            musicInfo.setFileName(musicInfo.getFileName());
        } else {
            final String fileName = service.download(musicInfo);
            if (fileName != null && !fileName.isEmpty()) {
                musicInfo.setFileName(fileName);
                musicInfoService.update(musicInfo, new LambdaQueryWrapper<MusicInfo>().eq(MusicInfo::getMid, musicInfo.getMid()));
            }
        }
        if (musicInfo.getFileName() == null) {
            SenderUtil.sendMsg(msgGet, "获取下载链接失败,换一个吧~");
        } else {
            SenderUtil.sendMsg(msgGet, host + "music/" + musicInfo.getMid());
            getContext(listenerContext).remove(getKey(qq));
        }
    }

    @RobotListen(value = MessageGet.class, isBoot = true)
    @Filter(value = "^(播放|){{number,\\d+}}$", matchType = MatchType.REGEX_FIND)
    public void play(MessageGet msgGet, @ContextValue(ContextType.QQ) String qq, @FilterValue("number") int number, ListenerContext listenerContext) {
        MusicInfo musicInfo = getMusicInfo(qq, number, listenerContext);
        if (musicInfo == null) {
            return;
        }
        final BaseMusicService.SearchService musicInfoType = musicInfo.getType();
        if (musicInfo.isPayPlay()) {
            SenderUtil.sendMsg(msgGet, "你点的歌为付费播放歌曲, 正在通过其他渠道搜索歌曲~");
            final List<MusicInfo> infoList = musicSearchService.search(musicInfo.getTitle(), BaseMusicService.SearchService.KU_WO);
            musicInfo = infoList.stream().filter(MusicInfo::isPayPlay).collect(Collectors.toList()).get(0);
        }
        if (musicInfo.getPreviewUrl() == null || musicInfo.getPreviewUrl().isEmpty()) {
            musicInfo.setPreviewUrl(musicInfo.getType().getMusicSearchServiceClass().getPreview(musicInfo));
        }
        musicInfo.setType(musicInfoType);
        SenderUtil.sendMsg(msgGet, CatUtil.getMusic(musicInfo));
        getContext(listenerContext).remove(getKey(qq));
        musicInfoService.update(musicInfo, new LambdaQueryWrapper<MusicInfo>().eq(MusicInfo::getMid, musicInfo.getMid()));
    }

    private MusicInfo getMusicInfo(String qq, int number, ListenerContext listenerContext) {

        final ScopeContext context = getContext(listenerContext);
        final Object o = context.get(getKey(qq));
        if (o instanceof List) {
            @SuppressWarnings("unchecked") final List<MusicInfo> musicInfoList = (List<MusicInfo>) o;
            if (musicInfoList.size() >= number && number > 0) {
                return musicInfoList.get(number - 1);
            }
        }
        return null;
    }

    private ScopeContext getContext(ListenerContext listenerContext) {
        final ScopeContext context = listenerContext.getContext(ListenerContext.Scope.GLOBAL);
        assert context != null;
        return context;
    }

    private String getKey(String qq) {
        return "music-" + qq;
    }
}

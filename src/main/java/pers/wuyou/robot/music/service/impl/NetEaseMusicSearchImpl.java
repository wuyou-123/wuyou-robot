package pers.wuyou.robot.music.service.impl;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import pers.wuyou.robot.core.util.HttpUtil;
import pers.wuyou.robot.music.config.MusicProperties;
import pers.wuyou.robot.music.entity.MusicInfo;
import pers.wuyou.robot.music.service.BaseMusicService;
import pers.wuyou.robot.music.service.MusicInfoService;
import pers.wuyou.robot.music.service.MusicSearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wuyou
 */
@Service("NetEaseMusicSearchImpl")
@Slf4j
public class NetEaseMusicSearchImpl implements MusicSearchService {
    private static final String LOGIN_URL = "login/cellphone?phone=%s&password=%s";
    private static final String LOGIN_REFRESH_URL = "login/refresh";
    private static final String SEARCH_URL = "cloudsearch?keywords=%s&limit=10";
    private static final String MUSIC_PLAY_URL = "song/url?id=%s";
    private static final String MUSIC_DOWNLOAD_URL = "song/download/url?id=%s&br=%s";
    private static final String MUSIC_JUMP_URL = "https://music.163.com/#/song?id=%s";
    private static final Integer[] BR_ARRAY = {1411000, 999000, 320000, 128000};
    private static final Map<String, String> NET_EASE_MUSIC_COOKIE = new HashMap<>();

    private static final int SUCCESS_CODE = 200;
    private final String uin;
    private final String pwd;
    private final String serverHost;
    private final BaseMusicService baseMusicService;
    private final MusicInfoService musicInfoService;

    public NetEaseMusicSearchImpl(MusicProperties musicProperties, BaseMusicService baseMusicService, MusicInfoService musicInfoService) {
        this.uin = musicProperties.getNetEase().getAccount();
        this.pwd = musicProperties.getNetEase().getPassword();
        this.serverHost = musicProperties.getNetEase().getServerHost();
        this.baseMusicService = baseMusicService;
        this.musicInfoService = musicInfoService;
    }

    @Override
    public boolean login() {
        NET_EASE_MUSIC_COOKIE.clear();
        final HttpUtil.RequestEntity requestEntity = HttpUtil.get(serverHost + String.format(LOGIN_URL, uin, pwd));
        NET_EASE_MUSIC_COOKIE.putAll(requestEntity.getCookies());
        final JSONObject json = requestEntity.getJSONResponse();
        final Integer code = json.getInteger("code");
        if (code == SUCCESS_CODE) {
            return true;
        }
        log.info("NetEase login fail.");
        log.info(json.toJSONString());
        return false;
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 1;
    }

    @NotNull
    @Override
    public String getType() {
        return BaseMusicService.SearchService.NET_EASE.getType();
    }

    @Override
    public List<MusicInfo> search(String name) {
        final JSONObject json = get(serverHost + String.format(SEARCH_URL, URLUtil.encode(name.trim()))).getJSONResponse();
        final JSONObject result = json.getJSONObject("result");
        final JSONArray jsonArray = result.getJSONArray("songs");
        final List<MusicInfo> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            final String mid = jsonObject.getString("id");
            final String title = jsonObject.getString("name");
            final JSONObject al = jsonObject.getJSONObject("al");
            final String album = al.getString("name");
            final String previewUrl = al.getString("picUrl");
            final JSONArray artistsJson = jsonObject.getJSONArray("ar");
            final List<String> artistList = artistsJson.stream().map(item -> {
                JSONObject object = (JSONObject) item;
                return object.getString("name");
            }).collect(Collectors.toList());
            final String artists = String.join("&", artistList);
            final JSONObject music = get(serverHost + String.format(MUSIC_PLAY_URL, mid)).getJSONResponse();
            final String musicUrl = music.getJSONArray("data").getJSONObject(0).getString("url");
            final String jumpUrl = String.format(MUSIC_JUMP_URL, mid);
            list.add(MusicInfo.builder()
                    .mid(mid)
                    .artist(artists)
                    .album(album)
                    .previewUrl(previewUrl)
                    .title(title)
                    .jumpUrl(jumpUrl)
                    .musicUrl(musicUrl)
                    .build());
        }
        return list;
    }

    @Override
    public String getPreview(MusicInfo musicInfo) {
        final String response = get("https://music.163.com/song?id=" + musicInfo.getMid()).getResponse();
        final String prefix = "f-fl\">\n<img src=\"";
        return response.substring(response.indexOf(prefix) + prefix.length(), response.indexOf("\" class=\"j-img\""));
    }

    @Override
    public void download(final MusicInfo musicInfo) {
        for (Integer br : BR_ARRAY) {
            final String url = serverHost + String.format(MUSIC_DOWNLOAD_URL, musicInfo.getMid(), br);
            final JSONObject json = get(url).getJSONResponse();
            String downloadUrl = json.getJSONObject("data").getString("url");
            if (downloadUrl == null) {
                continue;
            }
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("."));
            final boolean downloadSuccess = HttpUtil.downloadFile(downloadUrl, baseMusicService.getMusicPath() + fileName);
            if (downloadSuccess) {
                musicInfo.setFileName(fileName);
                musicInfoService.update(musicInfo, new LambdaQueryWrapper<MusicInfo>().eq(MusicInfo::getMid, musicInfo.getMid()));
            }
            return;
        }
    }

    private HttpUtil.RequestEntity get(String url) {
        final JSONObject json = HttpUtil.get(serverHost + LOGIN_REFRESH_URL, null, NET_EASE_MUSIC_COOKIE).getJSONResponse();
        if (json == null) {
            login();
        }
        return HttpUtil.get(url, null, NET_EASE_MUSIC_COOKIE);
    }
}

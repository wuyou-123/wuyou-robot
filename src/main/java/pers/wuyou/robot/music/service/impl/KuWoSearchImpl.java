package pers.wuyou.robot.music.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import pers.wuyou.robot.core.util.HttpUtil;
import pers.wuyou.robot.music.entity.MusicInfo;
import pers.wuyou.robot.music.service.BaseMusicService;
import pers.wuyou.robot.music.service.MusicSearchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 酷我音乐实现
 *
 * @author wuyou
 */
@Service("KuWoSearchImpl")
@Configuration
@Slf4j
public class KuWoSearchImpl implements MusicSearchService {
    private static final String GET_TOKEN_URL = "https://www.kuwo.cn/search/list?key=%s";
    private static final String SEARCH_URL = "https://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key=%s&pn=1&rn=10&httpsStatus=1";
    private static final String MUSIC_PLAY_URL = "http://antiserver.kuwo.cn/anti.s?rid=%s&response=res&format=mp3%%7Caac&type=convert_url&br=128kmp3&agent=iPhone&callback=getlink&jpcallback=getlink.mp3";
    private static final String MUSIC_DOWNLOAD_URL = "http://antiserver.kuwo.cn/anti.s?rid=%s&response=res&format=mp3%%7Caac&type=convert_url&br=320kmp3&agent=iPhone&callback=getlink&jpcallback=getlink.mp3";
    private static final String MUSIC_JUMP_URL = "https://www.kuwo.cn/play_detail/%s";
    private static final Map<String, String> COOKIE = new HashMap<>();
    private final BaseMusicService baseMusicService;

    public KuWoSearchImpl(BaseMusicService baseMusicService) {
        this.baseMusicService = baseMusicService;
    }

    @Override
    public boolean login() {
        return true;
    }

    @Override
    public List<MusicInfo> search(String name) {
        COOKIE.clear();
        final String tokenUrl = String.format(GET_TOKEN_URL, name.trim());
        final HttpUtil.RequestEntity requestEntity = HttpUtil.get(tokenUrl);
        COOKIE.putAll(requestEntity.getCookies());
        final String searchUrl = String.format(SEARCH_URL, name.trim());
        final Map<String, String> headers = new HashMap<>(4);
        headers.put("csrf", COOKIE.get("kw_token"));
        headers.put(HttpHeaders.REFERER, tokenUrl);
        final JSONObject jsonResponse = HttpUtil.get(searchUrl, null, COOKIE, headers).getJSONResponse();
        final Integer successCode = 200;
        final Integer code = jsonResponse.getInteger("code");
        List<MusicInfo> list = new ArrayList<>();
        if (!code.equals(successCode)) {
            return list;
        }
        final JSONArray jsonArray = jsonResponse.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String mid = jsonObject.getString("musicrid");
            String title = jsonObject.getString("name");
            String artist = jsonObject.getString("artist");
            String previewUrl = jsonObject.getString("albumpic");
            String album = jsonObject.getString("album");
            boolean payPlay = "1111".equals(jsonObject.getJSONObject("payInfo").getString("play"));
            String musicUrl = String.format(MUSIC_PLAY_URL, mid);
            String jumpUrl = String.format(MUSIC_JUMP_URL, mid.substring(6));
            list.add(MusicInfo.builder()
                    .mid(mid)
                    .artist(artist)
                    .album(album)
                    .title(title)
                    .previewUrl(previewUrl)
                    .jumpUrl(jumpUrl)
                    .musicUrl(musicUrl)
                    .payPlay(payPlay)
                    .build());
        }
        return list;
    }

    @Override
    public String getPreview(MusicInfo musicInfo) {
        return musicInfo.getPreviewUrl();
    }

    @Override
    public String download(MusicInfo musicInfo) {
        final String downloadUrl = String.format(MUSIC_DOWNLOAD_URL, musicInfo.getMid());
        String fileName = musicInfo.getMid() + ".mp3";
        final boolean downloadSuccess = HttpUtil.downloadFile(downloadUrl, baseMusicService.getMusicPath() + fileName);
        return downloadSuccess ? fileName : null;
    }
}

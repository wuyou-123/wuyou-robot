package pers.wuyou.robot.music.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.exception.ResourceNotFoundException;
import pers.wuyou.robot.core.util.*;
import pers.wuyou.robot.music.config.MusicProperties;
import pers.wuyou.robot.music.entity.MusicInfo;
import pers.wuyou.robot.music.service.BaseMusicService;
import pers.wuyou.robot.music.service.MusicInfoService;
import pers.wuyou.robot.music.service.MusicSearchService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wuyou
 */
@Service("QQMusicSearchImpl")
@Configuration
@EnableConfigurationProperties(MusicProperties.class)
@Slf4j
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class QQMusicSearchImpl implements MusicSearchService {
    private static final String SEARCH_URL = "{\"req\":{\"module\":\"music.search.SearchCgiService\",\"method\":\"DoSearchForQQMusicDesktop\",\"param\":{\"search_type\":0,\"query\":\"%s\",\"page_num\":1,\"num_per_page\":10}},\"comm\":{\"uin\":0,\"format\":\"json\",\"cv\":0}}";
    private static final String CHECK = "https://ssl.ptlogin2.qq.com/check?regmaster=&pt_tea=2&pt_vcode=1&uin=%s&appid=716027609&js_ver=21122814&js_type=1&login_sig=u1cFxLxCIZyhQiuufGpUqedhK9g9VlQWIXW1ybpCg-G0-q9wd0mdzw3R9vNHFz2S&u1=https://graph.qq.com/oauth2.0/login_jump&r=0.004892586794276843&pt_uistyle=40";
    private static final String MUSIC_U_FCG = "https://u.y.qq.com/cgi-bin/musicu.fcg";
    private static final String MUSIC_JSON = "{\"req\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"g\",\"songmid\":[\"%s\"],\"songtype\":[0],\"uin\":\"0\",\"loginflag\":1,\"platform\":\"20\"}},\"comm\":{\"uin\":0,\"format\":\"json\",\"ct\":24,\"cv\":0}}";
    private static final String MUSIC_JSON2 = "{\"req\":{\"module\":\"QQConnectLogin.LoginServer\",\"method\":\"QQLogin\",\"param\":{\"code\":\"%s\"}},\"comm\":{\"g_tk\":%s,\"platform\":\"yqq\",\"ct\":24,\"cv\":0}}";
    private static final String MUSIC_DOWNLOAD_JSON = "{\"req\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"g\",\"filename\":[\"%s\"],\"songmid\":[\"%s\"],\"platform\":\"20\"}},\"comm\":{\"uin\":0,\"format\":\"json\",\"cv\":0}}";
    private static final String MUSIC_PLAY_URL = "https://dl.stream.qqmusic.qq.com/%s";
    private static final String MUSIC_JUMP_URL = "https://y.qq.com/n/ryqq/songDetail/%s";
    private static final Map<String, String> QQ_MUSIC_COOKIE = new HashMap<>();
    private static final String TYPE_NAME = "music";
    private static final String JS_FILE_NAME = "getQQMusicAuth.js";
    private static final String PY_FILE_NAME = "getQQMusicAuth.py";
    private static final String JS_FILE_PATH;
    private static final String PY_FILE_PATH;
    private static final String NOW_TIME = "now_time";

    static {
        try {
            InputStream in = QQMusicSearchImpl.class.getClassLoader().getResourceAsStream(TYPE_NAME + File.separator + JS_FILE_NAME);
            InputStream in2 = QQMusicSearchImpl.class.getClassLoader().getResourceAsStream(TYPE_NAME + File.separator + PY_FILE_NAME);
            if (in == null || in2 == null) {
                throw new ResourceNotFoundException();
            }
            JS_FILE_PATH = FileUtil.saveTempFile(in, JS_FILE_NAME, TYPE_NAME);
            PY_FILE_PATH = FileUtil.saveTempFile(in2, PY_FILE_NAME, TYPE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
    }

    private final String uin;
    private final String pwd;
    private final BaseMusicService baseMusicService;
    private final MusicInfoService musicInfoService;
    private boolean isWaitScan;

    public QQMusicSearchImpl(MusicProperties musicProperties, BaseMusicService baseMusicService, MusicInfoService musicInfoService) {
        this.uin = musicProperties.getTencent().getAccount();
        this.pwd = musicProperties.getTencent().getPassword();
        this.baseMusicService = baseMusicService;
        this.musicInfoService = musicInfoService;
    }

    private static String getGtk() {
        String sKey = QQ_MUSIC_COOKIE.get("p_skey");
        int hash = 5381;
        for (int i = 0, len = sKey.length(); i < len; ++i) {
            hash += (hash << 5) + sKey.charAt(i);
        }
        return String.valueOf(hash & 0x7fffffff);
    }

    private static String[] getResultArray(String response) {
        String result = response.replace("'", "");
        return result.substring(response.indexOf("(") + 1, result.length() - 1).split(",");
    }

    /**
     * 初始化cookie
     */
    private static void getScanCookies() {
        String url = "https://xui.ptlogin2.qq.com/cgi-bin/xlogin?appid=716027609&target=self&style=40&s_url=https://y.qq.com/";
        HttpUtil.RequestEntity requestEntity = HttpUtil.get(url);
        QQ_MUSIC_COOKIE.putAll(requestEntity.getCookies());
    }

    /**
     * 获取二维码
     *
     * @return 二维码图片路径
     */
    private static String getLoginQrCode() {
        String now = System.currentTimeMillis() + "";
        getScanCookies();
        String url1 = String.format("https://ssl.ptlogin2.qq.com/ptqrshow?appid=716027609&e=2&l=M&s=3&d=72&v=4&t=%s&pt_3rd_aid=0", now);
        HttpUtil.RequestEntity requestEntity = HttpUtil.get(url1);
        QQ_MUSIC_COOKIE.putAll(requestEntity.getCookies());
        QQ_MUSIC_COOKIE.put("key", now);

        byte[] bytes = requestEntity.getEntity();
        try (FileOutputStream fileOutputStream = new FileOutputStream(RobotCore.TEMP_PATH + now)) {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return RobotCore.TEMP_PATH + now;
    }

    /**
     * 获取登录状态
     *
     * @return 返回的登录实体
     */
    private static HttpUtil.RequestEntity getLoginState() {
        Map<String, String> map = QQ_MUSIC_COOKIE;
        String urlCheckTimeout =
                "https://ssl.ptlogin2.qq.com/ptqrlogin?u1=https://y.qq.com/&ptqrtoken=" + getPtqrtoken(map.get("qrsig"))
                        + "&ptredirect=0&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=0-0-" + map.get("key")
                        + "&js_ver=10233&js_type=1&login_sig=" + map.get("pt_login_sig") + "&pt_uistyle=40&aid=716027609&";
        return HttpUtil.get(urlCheckTimeout, null, map);
    }

    /**
     * 计算qrsig
     *
     * @return 计算后的结果
     */
    private static int getPtqrtoken(String qrsig) {
        int e = 0;
        int n = qrsig.length();
        for (int j = 0; j < n; j++) {
            e = e + (e << 5);
            e = e + qrsig.toCharArray()[j];
            e = 2147483647 & e;
        }
        return e;
    }

    @NotNull
    @Override
    public Integer getPriority() {
        return 0;
    }

    @NotNull
    @Override
    public String getType() {
        return BaseMusicService.SearchService.QQ.getType();
    }

    @Override
    public List<MusicInfo> search(String name) {
        final Map<String, String> data = new HashMap<>(2);
        data.put("data", String.format(SEARCH_URL, name.trim()));
        JSONObject json = HttpUtil.get(MUSIC_U_FCG, data, QQ_MUSIC_COOKIE).getJSONResponse();
        JSONArray jsonArray = json.getJSONObject("req").getJSONObject("data").getJSONObject("body").getJSONObject("song").getJSONArray("list");
        List<MusicInfo> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String mid = jsonObject.getString("mid");
            String title = jsonObject.getString("name");
            String subtitle = jsonObject.getString("subtitle");
            String album = jsonObject.getJSONObject("album").getString("name");
            JSONArray artistsJson = jsonObject.getJSONArray("singer");
            List<String> artistList = artistsJson.stream().map(item -> {
                JSONObject object = (JSONObject) item;
                return object.getString("name");
            }).collect(Collectors.toList());
            String artists = String.join("&", artistList);
            final Map<String, String> data1 = new HashMap<>(2);
            data1.put("data", String.format(MUSIC_JSON, mid));
            final String purl = getPurl(data1);
            if (purl.isEmpty() && cookieIsExpires()) {
                log.warn("cookie已过期, 重新获取cookie");
                final boolean qqAuthSuccess = login();
                if (qqAuthSuccess) {
                    return search(name);
                } else {
                    log.warn("登录失败,返回空集合");
                    return Collections.emptyList();
                }
            }
            String musicUrl = String.format(MUSIC_PLAY_URL, purl);
            String jumpUrl = String.format(MUSIC_JUMP_URL, mid);
            list.add(MusicInfo.builder()
                    .mid(mid)
                    .artist(artists)
                    .album(album)
                    .title(title)
                    .subtitle(subtitle)
                    .jumpUrl(jumpUrl)
                    .musicUrl(musicUrl)
                    .build());
        }
        return list;
    }

    @Override
    public String getPreview(MusicInfo musicInfo) {
        return String.format("https:%s", HttpUtil.getJson(musicInfo.getJumpUrl(), "__INITIAL_DATA__").getJSONObject("detail").getString("picurl"));
    }

    private String getPurl(Map<String, String> data) {
        return HttpUtil.get(MUSIC_U_FCG, data, QQ_MUSIC_COOKIE).getJSONResponse().getJSONObject("req").getJSONObject("data").getJSONArray("midurlinfo").getJSONObject(0).getString("purl");
    }

    @Override
    public void download(final MusicInfo musicInfo) {
        final String musicUrl = musicInfo.getMusicUrl();
        final String name = musicUrl.substring(musicUrl.indexOf(Br.M4A.getPrefix()) + Br.M4A.getPrefix().length(), musicUrl.indexOf(Br.M4A.getEnd()));
        final Map<String, String> data = new HashMap<>(2);
        for (int i = 0; i < Br.values().length; i++) {
            Br br = Br.values()[i];
            String fileName = br.getPrefix() + name + br.getEnd();
            data.put("data", String.format(MUSIC_DOWNLOAD_JSON, fileName, musicInfo.getMid()));
            String purl = getPurl(data);
            if (purl.isEmpty()) {
                continue;
            }
            String downloadUrl = String.format(MUSIC_PLAY_URL, purl);
            final boolean downloadSuccess = HttpUtil.downloadFile(downloadUrl, baseMusicService.getMusicPath() + fileName);
            if (downloadSuccess) {
                musicInfo.setFileName(fileName);
                musicInfoService.update(musicInfo, new LambdaQueryWrapper<MusicInfo>().eq(MusicInfo::getMid, musicInfo.getMid()));
            }
            return;
        }
    }

    private boolean cookieIsExpires() {
        if (QQ_MUSIC_COOKIE.get(NOW_TIME) == null) {
            return true;
        }
        final long time = (Long.parseLong(QQ_MUSIC_COOKIE.get(NOW_TIME)));
        return System.currentTimeMillis() - time > TimeUnit.HOURS.toMillis(10);
    }

    @Override
    public boolean login() {
        QQ_MUSIC_COOKIE.clear();
        final HttpUtil.RequestEntity requestEntity = HttpUtil.get(String.format(CHECK, uin));
        final String response = requestEntity.getResponse();
        QQ_MUSIC_COOKIE.putAll(requestEntity.getCookies());
        final String[] resultArray = getResultArray(response);
        final List<String> list = CommandUtil.exec("node", JS_FILE_PATH, uin, pwd, resultArray[1], resultArray[3], resultArray[5], resultArray[6]);
        if (list.isEmpty()) {
            log.warn("js执行失败,请检查是否安装了node环境");
            return false;
        }
        log.info("js执行成功.");
        final String url = list.get(0);
        final HttpUtil.RequestEntity requestEntity1 = HttpUtil.get(url);
        final String response1 = requestEntity1.getResponse();
        QQ_MUSIC_COOKIE.putAll(requestEntity1.getCookies());
        final String[] resultArray1 = getResultArray(response1);
        log.info(Arrays.toString(resultArray1));
        // 登录成功返回code
        final String successCode = "0";
        // 扫描登录返回code
        final String scanCode = "10005";
        final String retry = "7";
        final String resultCode = resultArray1[0];
        switch (resultCode) {
            case successCode:
                // 登录成功
                return getCookies(resultArray1[2]);
            case scanCode:
                return scanLogin();
            case retry:
                return login();
            default:
                log.warn("qq music login fail!");
                return false;
        }
    }

    private boolean getCookies(String s) {
        final Map<String, String> cookies = HttpUtil.get(s).getCookies();
        QQ_MUSIC_COOKIE.putAll(cookies);
        final List<String> list1 = CommandUtil.exec("python", PY_FILE_PATH, QQ_MUSIC_COOKIE.get("p_uin"), QQ_MUSIC_COOKIE.get("p_skey"), QQ_MUSIC_COOKIE.get("pt_oauth_token"));
        if (list1.isEmpty()) {
            log.warn("python执行失败,请检查是否安装了python环境");
            return false;
        }
        log.info("python执行成功.");
        final String url2 = list1.get(0);
        final String code = url2.substring(url2.indexOf("code=") + 5);
        if (!code.isEmpty()) {
            String json = String.format(MUSIC_JSON2, code, getGtk());
            final HttpUtil.RequestEntity requestEntity2 = HttpUtil.post(MUSIC_U_FCG, json, QQ_MUSIC_COOKIE);
            final Map<String, String> cookies1 = requestEntity2.getCookies();
            if (cookies.isEmpty()) {
                log.warn("qq music login fail, Cookie is empty!");
                return false;
            }
            QQ_MUSIC_COOKIE.putAll(cookies1);
            QQ_MUSIC_COOKIE.put(NOW_TIME, System.currentTimeMillis() + "");
            return true;
        }
        log.warn("qq music login fail, Code is empty!");
        return false;
    }

    private boolean scanLogin() {
        if (isWaitScan) {
            return false;
        }
        QQ_MUSIC_COOKIE.clear();
        final HttpUtil.RequestEntity requestEntity = HttpUtil.get(String.format(CHECK, uin));
        QQ_MUSIC_COOKIE.putAll(requestEntity.getCookies());
        String path = getLoginQrCode();
        SenderUtil.sendPrivateMsg(RobotCore.getADMINISTRATOR().get(0), CatUtil.getImage(path));
        isWaitScan = true;
        try {
            return RobotCore.THREAD_POOL.submit(() -> {
                while (true) {
                    ThreadUtil.sleep(1000);
                    final String response = getLoginState().getResponse();
                    if (response.contains("ptuiCB('0'")) {
                        final String url = Arrays.stream(response.split("'")).filter(i -> i.contains("http")).collect(Collectors.toList()).get(0);
                        final HttpUtil.RequestEntity requestEntity1 = HttpUtil.get(url);
                        QQ_MUSIC_COOKIE.putAll(requestEntity1.getCookies());
                        QQ_MUSIC_COOKIE.put(NOW_TIME, System.currentTimeMillis() + "");
                        log.info("scan login success.");
                        isWaitScan = false;
                        return true;
                    } else if (response.contains("ptuiCB('65'")) {
                        String imagePath = getLoginQrCode();
                        SenderUtil.sendPrivateMsg(RobotCore.getADMINISTRATOR().get(0), CatUtil.getImage(imagePath));
                    }
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private enum Br {

        FLAC("F00000", "flac"),
        MP3("M80000", "mp3"),
        M4A("C40000", "m4a");
        @Getter
        private final String prefix;
        private final String end;

        Br(String prefix, String end) {
            this.prefix = prefix;
            this.end = end;
        }

        public String getEnd() {
            return "." + end;
        }
    }
}

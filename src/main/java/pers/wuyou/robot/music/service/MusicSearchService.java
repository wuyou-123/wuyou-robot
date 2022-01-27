package pers.wuyou.robot.music.service;

import pers.wuyou.robot.music.entity.MusicInfo;

import java.util.List;

/**
 * @author wuyou
 */
public interface MusicSearchService {
    /**
     * 登录
     *
     * @return 登录成功返回true
     */
    boolean login();

    /**
     * 搜索音乐
     *
     * @param name 音乐名
     * @return 返回的结果
     */
    List<MusicInfo> search(String name);

    /**
     * 获取专辑图片
     *
     * @param musicInfo 音乐信息
     * @return 图片链接
     */
    String getPreview(MusicInfo musicInfo);

    /**
     * 下载音乐
     *
     * @param musicInfo 音乐信息
     * @return 下载后的文件名
     */
    String download(final MusicInfo musicInfo);
}

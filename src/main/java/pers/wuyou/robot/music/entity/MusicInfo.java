package pers.wuyou.robot.music.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pers.wuyou.robot.music.service.BaseMusicService;

import java.io.Serializable;

/**
 * music_info
 *
 * @author wuyou
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 音乐id
     */
    @NotNull
    private String mid;
    /**
     * 标题
     */
    @NotNull
    private String title;
    /**
     * 子标题
     */
    private String subtitle;
    /**
     * 艺术家
     */
    @NotNull
    private String artist;
    /**
     * 专辑
     */
    @NotNull
    private String album;
    /**
     * 封面链接
     */
    private String previewUrl;
    /**
     * 跳转链接
     */
    @NotNull
    private String jumpUrl;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 是否要付费播放
     */
    private boolean payPlay;
    /**
     * 跳转链接
     */
    @NotNull
    @TableField(exist = false)
    private String musicUrl;
    /**
     * 类型
     */
    @TableField(exist = false)
    private BaseMusicService.SearchService type;
}
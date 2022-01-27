package pers.wuyou.robot.music.controller;

import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pers.wuyou.robot.music.entity.MusicInfo;
import pers.wuyou.robot.music.service.BaseMusicService;
import pers.wuyou.robot.music.service.MusicInfoService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author wuyou
 */
@Controller
@RequestMapping("music")
public class MusicController {
    private final MusicInfoService musicInfoService;

    public MusicController(MusicInfoService musicInfoService) {
        this.musicInfoService = musicInfoService;
    }

    @GetMapping("/{mid}")
    public void getMusic(@PathVariable("mid") String mid, HttpServletResponse response, Model model) {
        final MusicInfo musicInfo = musicInfoService.getOne(new LambdaQueryWrapper<MusicInfo>().eq(MusicInfo::getMid, mid));
        if (musicInfo == null) {
            response.setStatus(404);
            return;
        }
        final File file = new File(BaseMusicService.TYPE_NAME + File.separator + musicInfo.getFileName());
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        response.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
        String fileName = String.format("%s - %s%s", musicInfo.getTitle(), musicInfo.getArtist(), musicInfo.getFileName().substring(musicInfo.getFileName().lastIndexOf(".")));
        fileName = URLUtil.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename="+fileName);
        byte[] buff = new byte[1024];
        try (OutputStream outputStream = response.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int read = bis.read(buff);

            while (read != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                read = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //出现异常返回给页面失败的信息
            model.addAttribute("result", "下载失败");
        }
        //成功后返回成功信息
        model.addAttribute("result", "下载成功");
    }
}

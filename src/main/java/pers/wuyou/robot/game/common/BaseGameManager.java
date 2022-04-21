package pers.wuyou.robot.game.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.exception.ResourceNotFoundException;
import pers.wuyou.robot.core.util.CommandUtil;
import pers.wuyou.robot.core.util.FileUtil;

import java.io.File;
import java.net.URL;

/**
 * @author wuyou
 */
public abstract class BaseGameManager {

    /**
     * 检查当前环境是否是jar运行
     *
     * @return 是否是jar运行
     */
    public static boolean isRunningInJar() {
        URL url = BaseGameManager.class.getResource("");
        String protocol = url != null ? url.getProtocol() : null;
        return "jar".equals(protocol);
    }

    /**
     * 获取临时路径
     *
     * @return 当前游戏临时路径
     */
    public String getTempPath() {
        return RobotCore.TEMP_PATH + getGameName() + File.separator;
    }

    /**
     * 获取游戏名
     *
     * @return 游戏名
     */
    public abstract String getGameName();

    /**
     * 检查游戏资源
     *
     * @param resArr 游戏资源数组
     * @return 检查游戏资源是否成功
     */
    public boolean checkRes(String... resArr) {
        try {
            if (BaseGameManager.isRunningInJar()) {
                ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                for (String resPath : resArr) {
                    Resource[] resources = resolver.getResources(resPath);
                    if (resources.length == 0) {
                        throw new ResourceNotFoundException();
                    }
                    for (Resource jpgResource : resources) {
                        FileUtil.saveResourceToTempDirectory(jpgResource, getGameName());
                    }
                }
            } else {
                Resource resource = new ClassPathResource(File.separator + getGameName());
                if (!resource.exists()) {
                    throw new ResourceNotFoundException();
                }
                final File file = resource.getFile();
                final File temp = new File(getTempPath());
                cn.hutool.core.io.FileUtil.copyFilesFromDir(file, temp, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 检查环境
     */
    public static void checkEnv() {
        // 检查当前电脑是否安装了python
        final String errTip = "当前电脑没有python环境,请先安装或配置python环境后再运行";
        final String python = "python";
        final String python3 = "python3";
        if (CommandUtil.checkEnv(python) && CommandUtil.checkEnv(python3)) {
            throw new GameException(errTip);
        }
    }

}

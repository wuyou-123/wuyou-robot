package pers.wuyou.robot.music.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wuyou
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "music")
@Component
public class MusicProperties {
    private Tencent tencent;
    private NetEase netEase;

    public static class Tencent extends Properties {
    }

    @Getter
    @Setter
    public static class NetEase extends Properties {
        private String serverHost;

    }

    @Getter
    @Setter
    @ToString
    static class Properties {
        private String account;
        private String password;
    }

}
package pers.wuyou.robot.core.config;

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
@ToString(exclude = "password")
@ConfigurationProperties(prefix = "ssh")
@Component
public class SshProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private Forward forward;

    @Getter
    @Setter
    @ToString
    public static class Forward {

        private String fromHost;
        private Integer fromPort;
        private String toHost;
        private Integer toPort;

    }

}
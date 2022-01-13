package pers.wuyou.robot.core.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.instrument.classloading.LoadTimeWeaver;

import javax.annotation.PreDestroy;

/**
 * @author wuyou
 */
@Configuration
@EnableConfigurationProperties(SshProperties.class)
@Slf4j
public class SshConfiguration implements LoadTimeWeaverAware {

    private final Session session;

    public SshConfiguration(SshProperties sshProperties) {
        Session s = null;
        try {
            s = new JSch().getSession(sshProperties.getUsername(), sshProperties.getHost(), sshProperties.getPort());
            s.setConfig("StrictHostKeyChecking", "no");
            s.setPassword(sshProperties.getPassword());
            s.connect();
            SshProperties.Forward forward = sshProperties.getForward();
            if (forward != null) {
                s.setPortForwardingL(forward.getFromHost(), forward.getFromPort(), forward.getToHost(), forward.getToPort());
                log.info("Forward database success!  {}:{} -> {}:{}", forward.getFromHost(), forward.getFromPort(), forward.getToHost(), forward.getToPort());
            }
        } catch (JSchException e) {
            log.error("Ssh " + sshProperties.getHost() + " failed.", e);
        }
        this.session = s;
    }

    /**
     * 配置销毁时，断开 SSH 链接
     */
    @PreDestroy
    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    @Override
    public void setLoadTimeWeaver(@NotNull LoadTimeWeaver loadTimeWeaver) {
        // 默认
    }

}

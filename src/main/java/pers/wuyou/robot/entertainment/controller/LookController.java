package pers.wuyou.robot.entertainment.controller;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pers.wuyou.robot.entertainment.listener.LookListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author wuyou
 * @date 2022/2/19 0:23
 */
@Slf4j
@Controller
public class LookController {
    private final LookListener lookListener;

    public LookController(LookListener lookListener) {
        this.lookListener = lookListener;
    }

    @GetMapping("/look")
    public void getPic(HttpServletRequest req, HttpServletResponse resp, String group) throws IOException {
        final Enumeration<String> headerNames = req.getHeaderNames();
        log.debug("header-start ----");
        while (headerNames.hasMoreElements()) {
            final String element = headerNames.nextElement();
            log.debug(element + "---" + req.getHeader(element));
        }
        log.debug("header-ends ----");
        log.debug("来源ip" + req.getRemoteAddr());
        lookListener.addIp(req.getHeader("x-real-ip"), group);
        URL url = new URL("https://acg.toubiec.cn/random.php");
        try (final InputStream inputStream = url.openStream()) {
            resp.setContentType(MediaType.IMAGE_PNG_VALUE);
            IoUtil.copy(inputStream, resp.getOutputStream());
        }
    }
}

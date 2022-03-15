package pers.wuyou.robot.common.util;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.common.Constant;
import pers.wuyou.robot.core.util.HttpUtil;

import java.util.Objects;

/**
 * 天行api工具
 *
 * @author wuyou
 * @date 2022/3/15 18:36
 */
@Component
@Slf4j
public class TianApiTool {
    private static final String ROBOT_API_URL = "https://api.tianapi.com/robot/index?key=%s&uniqueid=%s&question=%s";
    private static final String IP_DETAIL_API_URL = "https://api.tianapi.com/ipquery/index?key=%s&ip=%s";
    private static final Integer SUCCESS_CODE = 200;
    private static final String CODE = Constant.CODE;
    @Value("${robot.tianapi-key}")
    private String tianapiKey;

    public String chatApi(String msg, String uin) {
        log.info(String.format("请求聊天%s-%s", uin, msg));
        final JSONObject response = HttpUtil.get(String.format(ROBOT_API_URL, tianapiKey, uin, URLUtil.encode(msg.trim()))).getJSONResponse();
        if (Objects.equals(response.getInteger(CODE), SUCCESS_CODE)) {
            final String str = response.getJSONArray("newslist").getJSONObject(0).getString("reply").trim();
            log.info(String.format("聊天 %s 返回%s", msg, str));
            return str;
        }
        return "";
    }

    public JSONObject getIpDetailApi(String ip) {
        final JSONObject response = HttpUtil.get(String.format(IP_DETAIL_API_URL, tianapiKey, ip)).getJSONResponse();
        if (Objects.equals(response.getInteger(CODE), SUCCESS_CODE)) {
            return response.getJSONArray("newslist").getJSONObject(0);
        }
        return null;
    }
}

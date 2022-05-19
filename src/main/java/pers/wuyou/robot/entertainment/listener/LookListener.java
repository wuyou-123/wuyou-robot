package pers.wuyou.robot.entertainment.listener;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import love.forte.simbot.annotation.ContextValue;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.api.message.events.GroupMsg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.util.TianApiTool;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.annotation.ContextType;
import pers.wuyou.robot.core.annotation.RobotListen;
import pers.wuyou.robot.core.util.CatUtil;
import pers.wuyou.robot.core.util.SenderUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuyou
 * @date 2022/2/19 0:06
 */
@Component
public class LookListener {
    private static final Map<String, TreeSet<String>> LOOK_MAP = new HashMap<>();
    private static final Map<String, String> IP_MAP = new HashMap<>();
    private static final Integer IPV4_LENGTH = 4;
    @Value("${robot.ip-host}")
    private String host;

    private final TianApiTool tianApiTool;

    public LookListener(TianApiTool tianApiTool) {
        this.tianApiTool = tianApiTool;
    }

    public void addIp(String ip, String group) {
        if (LOOK_MAP.get(group) == null) {
            return;
        }
        LOOK_MAP.get(group).add(ip);
        final String ipDetail = getIpDetail(ip);
        IP_MAP.put(ip, ipDetail);
    }

    @RobotListen(value = GroupMsg.class, isBoot = true)
    @Filter("窥屏检测")
    public void look(@ContextValue(ContextType.GROUP) String group) {
        Map<String, String> map = new HashMap<>(1);

        final String now = System.currentTimeMillis() + "";
        map.put("content", "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><msg serviceID=\"1\" templateID=\"12345\" action=\"web\" brief=\"[分享] 窥屏检测中...\" sourceMsgId=\"0\" url=\"\" flag=\"3\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"2\" advertiser_id=\"0\" aid=\"0\"><picture cover=\"" + String.format("%slook?group=%s&amp;time=%s", host, group, now) + "\" w=\"0\" h=\"0\" /><title size=\"25\" color=\"#000000\">窥屏检测中...</title><summary color=\"#000000\">电脑端窥屏暂时无法检测...</summary></item><source name=\"\" icon=\"\" action=\"\" appid=\"0\" /></msg>");
        final String share = CatUtil.UTILS.toCat("xml", map);
        if (LOOK_MAP.containsKey(group)) {
            return;
        }
        LOOK_MAP.put(group, new TreeSet<>(Collections.singleton(now)));
        SenderUtil.sendGroupMsg(group, share);
        RobotCore.THREAD_POOL.execute(() -> {
            ThreadUtil.sleep(10000);
            final StringBuilder stringBuilder = new StringBuilder("检测结束, 有%s人窥屏\n");
            final List<String> list = LOOK_MAP.get(group).stream().filter(item -> !item.equals(now)).collect(Collectors.toList());
            for (String ip : list) {
                final String ipDetail = IP_MAP.getOrDefault(ip, getIpDetail(ip));
                stringBuilder.append(ipDetail);
            }
            SenderUtil.sendGroupMsg(group, String.format(stringBuilder.substring(0, stringBuilder.length() - 1), LOOK_MAP.get(group).size() - 1));
            LOOK_MAP.remove(group);
        });
    }

    private String getIpDetail(String ip) {
        final JSONObject newslist = tianApiTool.getIpDetailApi(ip);
        if (newslist == null) {
            return "";
        }
        final String province = newslist.getString("province");
        final String city = newslist.getString("city");
        return String.format("%s  %s%n", encryptIp(ip), province.equals(city) ? province : province + "  " + city);
    }

    private String encryptIp(String ip) {
        final String[] split = ip.split("\\.");
        if (split.length == IPV4_LENGTH) {
            return String.join(".", ListUtil.of(split[0], split[1], "xxx", "xxx"));
        }
        return ip;
    }
}

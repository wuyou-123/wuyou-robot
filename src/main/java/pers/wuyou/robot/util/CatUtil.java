package pers.wuyou.robot.util;

import catcode.CatCodeUtil;
import catcode.CodeTemplate;
import catcode.MutableNeko;
import catcode.Neko;
import love.forte.catcode.NekoObjects;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.results.FriendInfo;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.Constant;
import pers.wuyou.robot.common.RobotCore;

import java.util.*;

/**
 * @author wuyou<br>
 * 2020年5月3日
 */
@Component
@SuppressWarnings("unused")
public class CatUtil {
    public static final CatCodeUtil UTILS = CatCodeUtil.getInstance();
    private static final CodeTemplate<Neko> NEKO_TEMPLATE = UTILS.getNekoTemplate();
    private static final String AT = Constant.AT;
    public static Getter GETTER;

    @Autowired
    public CatUtil(BotManager manager) {
        CatUtil.GETTER = manager.getDefaultBot().getSender().GETTER;
    }

    /**
     * 格式化QQ号
     *
     * @param fromGroup 群号
     * @param qq        QQ号
     * @return 格式化之后的文本
     */
    public static String formatQq(String fromGroup, String qq) {
        if (qq.isEmpty()) {
            return qq;
        }
        try {
            String name = GETTER.getMemberInfo(fromGroup, qq).getAccountRemarkOrNickname();
            return String.format("[%s](%s)", qq, name);
        } catch (NoSuchElementException e) {
            return String.format("[%s]", qq);
        }
    }

    /**
     * 艾特某人
     *
     * @param qq 要艾特的QQ号
     * @return 猫猫码字符串
     */
    public static String at(String qq) {
        return NEKO_TEMPLATE.at(qq) + " ";
    }

    /**
     * 是否艾特了bot
     *
     * @param msgget MsgGet
     */
    public static boolean atBot(MsgGet msgget) {
        if (msgget instanceof GroupMsg) {
            return getAts((GroupMsg) msgget).contains(RobotCore.getDefaultBotCode());
        } else {
            return false;
        }
    }

    /**
     * 是否艾特了bot
     *
     * @param msg 消息字符串
     */
    public static boolean atBot(String msg) {
        return getAts(msg).contains(RobotCore.getDefaultBotCode());
    }

    /**
     * 获取艾特bot的猫猫码
     */
    public static String atBot() {
        return CatUtil.at(RobotCore.getDefaultBotCode());
    }

    /**
     * 艾特全体
     */
    public static String atAll() {
        return NekoObjects.getNekoAtAll().toString();
    }

    /**
     * 获取第一个艾特的QQ号
     *
     * @param msg 消息内容
     * @return 获取到的QQ号
     */
    public static String startsWithAt(String msg) {
        List<String> list = UTILS.split(msg);
        Neko code = UTILS.getNeko(list.get(0), AT);
        if (code != null) {
            return code.get("code");
        }
        return "";
    }

    /**
     * 获取所有艾特的QQ号
     */
    public static Set<String> getAts(MessageGet msg) {
        Set<String> set = new HashSet<>();
        for (Neko neko : msg.getMsgContent().getCats(AT)) {
            if (neko.get("code") != null) {
                set.add(neko.get("code"));
            }
        }
        return set;
    }

    /**
     * 获取所有艾特的QQ号
     */
    public static List<String> getAtList(MessageGet msg) {
        List<String> list = new ArrayList<>();
        for (Neko neko : msg.getMsgContent().getCats(Constant.AT)) {
            if (neko.get("code") != null) {
                list.add(neko.get("code"));
            }
        }
        return list;
    }

    /**
     * 获取所有艾特的QQ号
     */
    public static Set<String> getAts(String msg) {
        Set<String> set = new HashSet<>();
        for (Neko neko : UTILS.getNekoList(msg, AT)) {
            if (neko.get("code") != null) {
                set.add(neko.get("code"));
            }
        }
        return set;
    }

    /**
     * 获取所有艾特的QQ号的KQ码
     */
    public static Set<Neko> getAtKqs(String msg) {
        final List<Neko> list = UTILS.getNekoList(msg, AT);
        return new LinkedHashSet<>(list);
    }

    /**
     * 获取第一个艾特的QQ号
     */
    public static String getAt(String msg) {
        final Neko neko = UTILS.getNeko(msg);
        if (neko == null) {
            return null;
        }
        return neko.get("code");
    }

    /**
     * 将消息里的at的Neko转为文本格式的@xxx
     *
     * @param fromGroup 消息的群号,用于获取对应名片
     * @param str       被处理的文本内容
     * @return 处理后的内容
     */
    public static String nekoToAtText(String fromGroup, String str) {
        return nekoToAtText(fromGroup, str, false);
    }

    /**
     * 将消息里的at的Neko转为文本格式的@xxx,并添加透明特殊符号
     *
     * @param fromGroup 消息的群号,用于获取对应名片
     * @param str       被处理的文本内容
     * @param withCode  是否在文本后面添加QQ号
     * @return 处理后的内容
     */
    public static String nekoToAtText(String fromGroup, String str, boolean withCode) {
        final Set<Neko> stringSet = CatUtil.getAtKqs(str);
        for (Neko neko : stringSet) {
            final String code = neko.get("code");
            try {
                if ("true".equals(neko.get("all"))) {
                    str = str.replace(neko, "@全体成员");
                } else {
                    if (code == null) {
                        continue;
                    }
                    final GroupMemberInfo memberInfo = GETTER.getMemberInfo(fromGroup, code);
                    str = str.replace(neko, String.format("@%s\u202D%s", memberInfo.getAccountRemarkOrNickname(), withCode ? String.format("%s\u202C", code) : ""));
                }
            } catch (NoSuchElementException e) {
                if (code == null) {
                    continue;
                }
                final FriendInfo friendInfo = GETTER.getFriendInfo(code);
                str = str.replace(neko, String.format("@%s\u202D%s", friendInfo.getAccountRemarkOrNickname(), withCode ? String.format("%s\u202C", code) : ""));
            }
        }
        return str;
    }

    public static List<Neko> getKq(String msg, String type) {
        return UTILS.getNekoList(msg, type);
    }

    public static List<Neko> getKq(MessageGet msg, String type) {
        return msg.getMsgContent().getCats(type);
    }

    public static Neko getFace(String id) {
        return NEKO_TEMPLATE.face(id);
    }

    /**
     * 获取语音的猫猫码
     *
     * @param path 语音的文件路径
     * @return Neko
     */
    public static Neko getRecord(String path) {
        MutableNeko neko = NEKO_TEMPLATE.record(path).mutable();
        neko.setType("voice");
        return neko.immutable();
    }

    public static Neko getImage(String path) {
        return NEKO_TEMPLATE.image(path);
    }

}

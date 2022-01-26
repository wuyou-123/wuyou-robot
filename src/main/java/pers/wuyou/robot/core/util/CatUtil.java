package pers.wuyou.robot.core.util;

import catcode.CatCodeUtil;
import catcode.CodeTemplate;
import catcode.MutableNeko;
import catcode.Neko;
import love.forte.catcode.NekoObjects;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.sender.Getter;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.music.entity.MusicInfo;

import java.util.*;

/**
 * @author wuyou<br>
 * 2020年5月3日
 */
@SuppressWarnings("unused")
public class CatUtil {
    public static final CatCodeUtil UTILS = CatCodeUtil.getInstance();
    private static final CodeTemplate<Neko> NEKO_TEMPLATE = UTILS.getNekoTemplate();
    private static final String CODE = "code";
    private static Getter getter;

    private CatUtil() {
    }

    public static void setGetter(Getter getter) {
        CatUtil.getter = getter;
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
            String name = getter.getMemberInfo(fromGroup, qq).getAccountRemarkOrNickname();
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
        Neko code = UTILS.getNeko(list.get(0), CatType.AT);
        if (code != null) {
            return code.get(CODE);
        }
        return "";
    }

    /**
     * 获取所有艾特的QQ号
     */
    public static Set<String> getAts(MessageGet msg) {
        Set<String> set = new HashSet<>();
        for (Neko neko : msg.getMsgContent().getCats(CatType.AT)) {
            if (neko.get(CODE) != null) {
                set.add(neko.get(CODE));
            }
        }
        return set;
    }

    /**
     * 获取所有艾特的QQ号
     */
    public static List<String> getAtList(MessageGet msg) {
        List<String> list = new ArrayList<>();
        for (Neko neko : msg.getMsgContent().getCats(CatType.AT)) {
            if (neko.get(CODE) != null) {
                list.add(neko.get(CODE));
            }
        }
        return list;
    }

    /**
     * 获取所有艾特的QQ号
     */
    public static Set<String> getAts(String msg) {
        Set<String> set = new HashSet<>();
        for (Neko neko : UTILS.getNekoList(msg, CatType.AT)) {
            if (neko.get(CODE) != null) {
                set.add(neko.get(CODE));
            }
        }
        return set;
    }

    /**
     * 获取所有艾特的QQ号的KQ码
     */
    public static Set<Neko> getAtKqs(String msg) {
        final List<Neko> list = UTILS.getNekoList(msg, CatType.AT);
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
        return neko.get(CODE);
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
            final String code = neko.get(CODE);
            if ("true".equals(neko.get("all"))) {
                str = str.replace(neko, "@全体成员");
            }
            if (code == null) {
                continue;
            }
            try {
                if ("true".equals(neko.get("all"))) {
                    str = str.replace(neko, "@全体成员");
                } else {
                    final GroupMemberInfo memberInfo = getter.getMemberInfo(fromGroup, code);
                    str = str.replace(neko, String.format("@%s\u202D%s", memberInfo.getAccountRemarkOrNickname(), withCode ? String.format("%s\u202C", code) : ""));
                }
            } catch (NoSuchElementException e) {
                str = str.replace(neko, String.format("@%s", code));
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
     * 获取音乐的猫猫码
     *
     * @param musicInfo 音乐信息
     * @return Neko
     */
    public static Neko getMusic(MusicInfo musicInfo) {
        Map<String, String> map = new HashMap<>(8);
        map.put("content", musicInfo.getArtist());
        map.put("type", musicInfo.getType().getType());
        map.put("musicUrl", musicInfo.getMusicUrl());
        map.put("title", musicInfo.getTitle());
        map.put("pictureUrl", musicInfo.getPreviewUrl());
        map.put("jumpUrl", musicInfo.getJumpUrl());
        map.put("brief", "[分享]" + musicInfo.getTitle());
        return CatUtil.UTILS.toNeko(CatType.MUSIC, map);
    }

    /**
     * 获取语音的猫猫码
     *
     * @param path 语音的文件路径
     * @return Neko
     */
    public static Neko getRecord(String path) {
        MutableNeko neko = NEKO_TEMPLATE.record(path).mutable();
        neko.setType(CatType.VOICE);
        return neko.immutable();
    }

    public static Neko getImage(String path) {
        return NEKO_TEMPLATE.image(path);
    }

    public static class CatType {
        /**
         * at消息
         */
        public static final String AT = "at";
        /**
         * 图片
         */
        public static final String IMAGE = "image";
        /**
         * 表情消息
         */
        public static final String FACE = "face";
        /**
         * 戳一戳
         */
        public static final String POKE = "poke";
        /**
         * 拍一拍
         */
        public static final String NUDGE = "nudge";
        /**
         * 语音消息
         */
        public static final String VOICE = "voice";
        /**
         * 文件
         */
        public static final String FILE = "file";
        /**
         * 链接消息
         */
        public static final String SHARE = "share";
        /**
         * 富文本消息
         */
        public static final String RICH = "rich";
        /**
         * json消息
         */
        public static final String JSON = "json";
        /**
         * xml消息
         */
        public static final String XML = "xml";
        /**
         * 骰子
         */
        public static final String DICE = "dice";
        /**
         * 音乐消息
         */
        public static final String MUSIC = "music";
        /**
         * 回复消息
         */
        public static final String QUOTE = "quote";

        private CatType() {
        }
    }

}

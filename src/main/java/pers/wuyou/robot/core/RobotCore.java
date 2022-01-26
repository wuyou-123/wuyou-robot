package pers.wuyou.robot.core;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.message.results.SimpleGroupInfo;
import love.forte.simbot.api.sender.BotSender;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.api.sender.Setter;
import love.forte.simbot.bot.BotManager;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberLeaveEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.entity.AccountInfo;
import pers.wuyou.robot.core.entity.GroupBootState;
import pers.wuyou.robot.core.service.GroupBootStateService;
import pers.wuyou.robot.core.util.CatUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyou
 */
@Slf4j
@SuppressWarnings("unused")
@Order(1)
@Component
public class RobotCore {
    /**
     * 项目名
     */
    public static final String PROJECT_NAME;
    /**
     * 项目路径
     */
    public static final String PROJECT_PATH;
    /**
     * 临时路径
     */
    public static final String TEMP_PATH;
    /**
     * 线程池
     */
    public static final ExecutorService THREAD_POOL;
    /**
     * 机器人管理员
     */
    @lombok.Getter
    private static final List<String> ADMINISTRATOR = new ArrayList<>(Collections.singletonList("1097810498"));
    /**
     * 缓存群开关
     */
    @lombok.Getter
    private static final Map<String, Boolean> BOOT_MAP = new HashMap<>();
    /**
     * 群成员缓存
     */
    private static final Map<String, AccountInfo> MEMBER_INDEX = new HashMap<>();
    /**
     * 全局BotManager
     */
    private static BotManager botManager;
    /**
     * 全局BotSender
     */
    private static BotSender sender;
    private static RobotCore robotCore;

    static {
        PROJECT_NAME = "wuyou-robot";
        PROJECT_PATH = System.getProperty("user.dir") + File.separator;
        TEMP_PATH = System.getProperty("java.io.tmpdir") + File.separator + RobotCore.PROJECT_NAME + File.separator;
        THREAD_POOL = new ThreadPoolExecutor(50, 50, 200, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50), r -> {
            Thread thread = new Thread(r);
            thread.setName(String.format("newThread%d", thread.getId()));
            return thread;
        });
    }

    /**
     * 上下文
     */
    private final ApplicationContext applicationContext;
    private final GroupBootStateService groupBootStateService;

    public RobotCore(ApplicationContext applicationContext, GroupBootStateService groupBootStateService) {
        this.applicationContext = applicationContext;
        this.groupBootStateService = groupBootStateService;
    }

    public static ApplicationContext getApplicationContext() {
        return robotCore.applicationContext;
    }

    private static void syncGroupMemberIndex(String groupCode) {
        Getter getter = getter();
        syncGroupMemberIndex(groupCode, getter);
    }

    private static void syncGroupMemberIndex(String groupCode, Getter getter) {
        boolean isDebugEnabled = log.isDebugEnabled();
        if (isDebugEnabled) {
            log.debug(String.format("Start synchronization group %s member index.", groupCode));
        }
        for (GroupMemberInfo memberInfo : getter.getGroupMemberList(groupCode)) {
            final String accountCode = memberInfo.getAccountCode();
            addMemberIndex(accountCode, groupCode);
        }
    }

    private static synchronized void addMemberIndex(String code, String group) {
        if (MEMBER_INDEX.get(code) == null) {
            MEMBER_INDEX.put(code, new AccountInfo(code, group));
        } else {
            MEMBER_INDEX.get(code).addGroup(group);
        }
    }

    private static synchronized void removeMemberIndex(String code, String group) {
        AccountInfo accountInfo = MEMBER_INDEX.get(code);
        if (accountInfo != null) {
            accountInfo.getGroupSet().remove(group);
            if (accountInfo.getGroupSet().isEmpty()) {
                MEMBER_INDEX.remove(code);
            }
        }
    }

    public static synchronized AccountInfo getAccountFromMemberIndex(String code) {
        return MEMBER_INDEX.get(code);
    }

    public static Getter getter() {
        return sender.GETTER;
    }

    public static Sender sender() {
        return sender.SENDER;
    }

    public static Setter setter() {
        return sender.SETTER;
    }

    public static Boolean isBotAdministrator(String accountCode) {
        return ADMINISTRATOR.contains(accountCode);
    }

    public static String getDefaultBotCode() {
        return botManager.getDefaultBot().getBotInfo().getAccountCode();
    }

    @PostConstruct
    public void init() {
        setApplicationContext();
        initGroupBootMap();
        initIndex();
    }

    /**
     * 初始化成员索引
     */
    private void initIndex() {
        // 为了避免监听和其他simbot监听冲突, 这里使用注册Mirai的监听
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event -> {
            // 有新成员进群, 添加成员索引
            String accountCode = String.valueOf(event.getMember().getId());
            String groupCode = String.valueOf(event.getGroupId());
            if (getDefaultBotCode().equals(accountCode)) {
                // 同步这个群的所有成员索引
                syncGroupMemberIndex(groupCode);
                return;
            }
            addMemberIndex(accountCode, groupCode);
        });
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberLeaveEvent.class, event -> {
            // 成员离开群, 删除成员索引
            String accountCode = String.valueOf(event.getMember().getId());
            String groupCode = String.valueOf(event.getGroupId());
            if (getDefaultBotCode().equals(accountCode)) {
                // bot离开群,删除这个群的所有索引
                Iterator<Map.Entry<String, AccountInfo>> ite = MEMBER_INDEX.entrySet().iterator();
                while (ite.hasNext()) {
                    Map.Entry<String, AccountInfo> m = ite.next();
                    m.getValue().getGroupSet().remove(groupCode);
                    if (m.getValue().getGroupSet().isEmpty()) {
                        // 如果成员没有任何群关联则删除这个成员缓存
                        ite.remove();
                    }
                }
                return;
            }
            removeMemberIndex(accountCode, groupCode);
        });
    }

    private synchronized void setApplicationContext() {
        robotCore = this;
        botManager = this.applicationContext.getBean(BotManager.class);
        sender = botManager.getDefaultBot().getSender();
        CatUtil.setGetter(sender.GETTER);
        boolean isDebugEnabled = log.isDebugEnabled();
        log.info("Start synchronization group member index.");
        THREAD_POOL.execute(() -> {
            // 初始化成员索引
            Getter getter = getter();
            for (SimpleGroupInfo groupInfo : getter.getGroupList()) {
                syncGroupMemberIndex(groupInfo.getGroupCode(), getter);
                if (isDebugEnabled) {
                    log.debug(String.format("Synchronization group %s member index success.", groupInfo.getGroupCode()));
                }
            }
            log.info("Synchronization group member index success.");
        });
    }

    private void initGroupBootMap() {
        final List<GroupBootState> list = groupBootStateService.list();
        for (GroupBootState groupBootState : list) {
            BOOT_MAP.put(groupBootState.getGroupCode(), groupBootState.getState());
        }
    }
}

package pers.wuyou.robot.game.landlords;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.game.landlords.common.GameEventManager;
import pers.wuyou.robot.game.landlords.entity.Player;
import pers.wuyou.robot.game.landlords.entity.Room;
import pers.wuyou.robot.game.landlords.enums.GameEventCode;
import pers.wuyou.robot.game.landlords.exception.LandLordsException;
import pers.wuyou.robot.game.landlords.exception.PlayerException;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.util.RobotUtil;
import pers.wuyou.robot.util.SenderUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * @author wuyou
 */
@Component
public class GameManager {
    /**
     * 游戏名
     */
    public final static String GAME_NAME = "landlords";
    /**
     * 房间列表
     */
    public static final List<Room> ROOM_LIST = new ArrayList<>();
    /**
     * 玩家列表
     */
    public static final List<Player> PLAYER_LIST = new ArrayList<>();
    /**
     * 最大玩家数量
     */
    public static final int MAX_PLAYER_COUNT = 3;
    /**
     * 是不是用Jar文件运行
     */
    public static final boolean RUNNING_IN_JAR;
    /**
     * 临时路径
     */
    public static final String TEMP_PATH;
    /**
     * 用户携带数据
     */
    private static final Map<String, Map<String, Object>> PLAYER_DATA_MAP = new HashMap<>();

    static {
        // 检查当前电脑是否安装了python
        try {
            String python = "python";
            Process proc = Runtime.getRuntime().exec(new String[]{python, "--version"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            final String s = reader.readLine();
            if (s == null || !s.toLowerCase(Locale.ROOT).contains(python)) {
                throw new LandLordsException("当前电脑没有python环境,请先安装或配置python环境后再运行");
            }
        } catch (Exception e) {
            try {
                String python = "python3";
                Process proc = Runtime.getRuntime().exec(new String[]{python, "--version"});
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                final String s = reader.readLine();
                if (s == null || !s.toLowerCase(Locale.ROOT).contains(python)) {
                    throw new LandLordsException("当前电脑没有python环境,请先安装或配置python环境后再运行");
                }
            } catch (Exception e1) {
                throw new LandLordsException("当前电脑没有python环境,请先安装或配置python环境后再运行");
            }
        }
        URL url = GameManager.class.getResource("");
        String protocol = url != null ? url.getProtocol() : null;
        RUNNING_IN_JAR = "jar".equals(protocol);
        TEMP_PATH = RobotUtil.TEMP_PATH + GAME_NAME + File.separator;
    }

    GameManager() {
        PokerHelper.init();
    }

    public static void reset(String group) {
        ROOM_LIST.clear();
        PLAYER_LIST.clear();
        SenderUtil.sendGroupMsg(group, "重置成功");
    }


    /**
     * 尝试开始游戏
     *
     * @param room 房间
     */
    public static void start(Room room) {
        if (room.canStart()) {
            // 开始游戏
            room.start();
            SenderUtil.sendGroupMsg(room.getId(), "玩家已满, 开始游戏! \n" +
                    "注: 当前为测试阶段, 如果遇到问题或有其他建议请反馈到QQ1097810498");
            GameEventManager.call(GameEventCode.CODE_GAME_START, room);
        }
    }

    /**
     * 添加玩家
     *
     * @param accountCode 玩家id
     * @param name        玩家昵称
     * @param room        添加到的房间
     * @return 添加后的玩家
     */
    public static Player addPlayer(@NotNull String accountCode, @NotNull String name, Room room) {
        Player player = new Player(accountCode, name, room);
        PLAYER_LIST.add(player);
        return player;
    }

    /**
     * 添加玩家并将玩家添加到新房间内
     *
     * @param accountCode 房间内的玩家id
     * @param name        房间内的玩家昵称
     * @param groupCode   房间号
     * @return 添加后的玩家
     */
    public static Player addPlayerAndRoom(@NotNull String accountCode, @NotNull String name, @NotNull String groupCode) {
        Room room = new Room(groupCode);
        Player player = new Player(accountCode, name, room);
        GameManager.ROOM_LIST.add(room);
        GameManager.PLAYER_LIST.add(player);
        return player;
    }

    /**
     * 根据房间号获取房间
     *
     * @param groupCode 房间号
     * @return 获取到的房间
     */
    public static Room getRoom(String groupCode) {
        return ROOM_LIST.stream().filter(item -> item.getId().equals(groupCode)).findFirst().orElse(null);
    }

    /**
     * 根据账号号获取玩家
     *
     * @param accountCode 玩家账号
     * @return 获取到的玩家
     */
    public static Player getPlayer(String accountCode) {
        return PLAYER_LIST.stream().filter(item -> item.getId().equals(accountCode)).findFirst().orElse(null);
    }

    /**
     * 获取玩家携带数据
     *
     * @param player 玩家对象
     * @return 获取到的对象
     */
    public static Object getPlayerData(@NotNull Player player, String key) {
        PLAYER_DATA_MAP.putIfAbsent(player.getId(), new HashMap<>(4));
        return PLAYER_DATA_MAP.get(player.getId()).get(key);
    }

    /**
     * 获取玩家携带数据map
     *
     * @param player 玩家对象
     * @return 获取到的对象
     */
    public static Map<String, Object> getPlayerDataMap(@NotNull Player player) {
        PLAYER_DATA_MAP.putIfAbsent(player.getId(), new HashMap<>(4));
        return PLAYER_DATA_MAP.get(player.getId());
    }

    /**
     * 添加玩家携带数据
     *
     * @param player 玩家对象
     * @param key    添加的key
     * @param value  添加的值
     */
    @SuppressWarnings("unused")
    public static void putPlayerData(@NotNull Player player, String key, Object value) {
        PLAYER_DATA_MAP.putIfAbsent(player.getId(), new HashMap<>(4));
        PLAYER_DATA_MAP.get(player.getId()).put(key, value);
    }

    /**
     * 根据账号获取房间
     *
     * @param accountCode 玩家账号
     * @return 获取到的房间
     */
    public static Room getRoomByAccountCode(String accountCode) {
        final Player player = getPlayer(accountCode);
        if (player == null) {
            throw new PlayerException("玩家不存在");
        }
        return player.getRoom();
    }

    /**
     * 移出玩家
     *
     * @param player 被移出的玩家
     */
    public static void removePlayer(Player player) {
        final Room room = player.getRoom();
        room.playerExit(player);
        PLAYER_LIST.remove(player);
        if (room.getPlayerList().isEmpty()) {
            ROOM_LIST.remove(room);
        }
    }

///    public static void verifyPlayerIsFriend(String accountCode) {
///        final FriendInfo friendInfo = RobotUtil.getter().get(accountCode);
///        System.out.println(friendInfo);
///    }
}

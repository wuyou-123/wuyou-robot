package pers.wuyou.robot.game.landlords.game.event;

import pers.wuyou.robot.game.common.Constant;
import pers.wuyou.robot.game.landlords.common.LandlordsGameEvent;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.LandlordsRoom;
import pers.wuyou.robot.game.landlords.entity.PokerSell;
import pers.wuyou.robot.game.landlords.enums.SellType;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;
import pers.wuyou.robot.game.landlords.util.LandlordsNotifyUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GamePlayerMessageOnRound implements LandlordsGameEvent {

    private static final List<String> CMD_LIST = Arrays.asList("上一页", "下一页");
    private static final int PAGE_SIZE = 10;
    private static final String SELL_TYPE = "sellType";
    final Map<String, SellType[]> sellTypeMap = new HashMap<>();

    public GamePlayerMessageOnRound() {
        sellTypeMap.put("提示", new SellType[0]);
        sellTypeMap.put("顺子", new SellType[]{SellType.SINGLE_STRAIGHT, SellType.DOUBLE_STRAIGHT, SellType.THREE_STRAIGHT, SellType.FOUR_STRAIGHT});
        sellTypeMap.put("单顺子", new SellType[]{SellType.SINGLE_STRAIGHT});
        sellTypeMap.put("连对", new SellType[]{SellType.DOUBLE_STRAIGHT});
        sellTypeMap.put("双顺子", new SellType[]{SellType.DOUBLE_STRAIGHT});
        sellTypeMap.put("三顺子", new SellType[]{SellType.THREE_STRAIGHT});
        sellTypeMap.put("四顺子", new SellType[]{SellType.FOUR_STRAIGHT});
        sellTypeMap.put("三带", new SellType[]{SellType.THREE_ZONES_SINGLE, SellType.THREE_ZONES_DOUBLE});
        sellTypeMap.put("三带一", new SellType[]{SellType.THREE_ZONES_SINGLE});
        sellTypeMap.put("三带二", new SellType[]{SellType.THREE_ZONES_DOUBLE});
        sellTypeMap.put("四带", new SellType[]{SellType.FOUR_ZONES_SINGLE, SellType.FOUR_ZONES_DOUBLE});
        sellTypeMap.put("四带一", new SellType[]{SellType.FOUR_ZONES_SINGLE});
        sellTypeMap.put("四带二", new SellType[]{SellType.FOUR_ZONES_DOUBLE});
        sellTypeMap.put("飞机", new SellType[]{SellType.THREE_STRAIGHT_WITH_SINGLE, SellType.THREE_STRAIGHT_WITH_DOUBLE});
        sellTypeMap.put("炸弹", new SellType[]{SellType.BOMB});
    }

    private List<PokerSell> getNextPagePokerSellList(LandlordsPlayer player) {
        final Map<String, Object> playerDataMap = player.getPlayerDataMap();
        List<PokerSell> list = getList(player);
        int page = (int) playerDataMap.getOrDefault("page", 0);
        if (page * PAGE_SIZE > list.size()) {
            return new ArrayList<>();
        }
        playerDataMap.put("page", page + 1);
        return list.subList(page * PAGE_SIZE, Math.min(list.size(), (page + 1) * PAGE_SIZE));
    }

    private List<PokerSell> getPrePagePokerSellList(LandlordsPlayer player) {
        final Map<String, Object> playerDataMap = player.getPlayerDataMap();
        List<PokerSell> list = getList(player);
        int page = (int) playerDataMap.getOrDefault("page", 0);
        if (page <= 1) {
            return new ArrayList<>();
        }
        playerDataMap.put("page", page - 1);
        return list.subList((page - 2) * PAGE_SIZE, Math.min(list.size(), (page - 1) * PAGE_SIZE));
    }

    private List<PokerSell> getList(LandlordsPlayer player) {
        final Map<String, Object> playerDataMap = player.getPlayerDataMap();
        final List<SellType> sellTypes = Arrays.asList((SellType[]) playerDataMap.get(SELL_TYPE));
        List<PokerSell> sells;
        if (player.equals(player.getRoom().getLastPlayer())) {
            sells = PokerHelper.validSells(null, player.getPokers());
        } else {
            sells = PokerHelper.validSells(player.getRoom().getLastPlayPoker(), player.getPokers());
        }
        // 将每个类型的牌控制在三种以内
        return sells.stream()
                .filter(item -> sellTypes.isEmpty() || sellTypes.contains(item.getSellType()))
                .collect(Collectors.groupingBy(PokerSell::getSellType))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(a -> a.getValue().get(0).getSellType().index()))
                .flatMap(i -> i.getValue().stream().limit(5))
                .collect(Collectors.toList());
    }

    @Override
    public void call(LandlordsRoom room, Map<String, Object> data) {
        LandlordsPlayer player = room.getCurrentPlayer();
        final String message = data.get(Constant.MESSAGE).toString();
        final SellType[] types = sellTypeMap.get(message);
        final Map<String, Object> playerDataMap = player.getPlayerDataMap();
        if (types == null) {
            parseMessage(player, message);
            return;
        }
        final List<SellType> sellTypes = Arrays.asList(types);
        if (sellTypes.isEmpty() || room.getLastPlayPoker() == null || room.getLastPlayer().equals(player) || sellTypes.contains(room.getLastPlayPoker().getSellType())) {
            playerDataMap.put(SELL_TYPE, types);
            playerDataMap.put("page", 0);
            final List<PokerSell> list = getNextPagePokerSellList(player);
            player.setStatus(LandlordsPlayerGameStatus.CHOOSE_TIP);
            playerDataMap.put("list", list);
            LandlordsNotifyUtil.notifyPlayerChoosePokers(player, list);
        } else {
            LandlordsNotifyUtil.notifyPlayerTypePokerInvalid(player);
        }
    }

    private void parseMessage(LandlordsPlayer player, String message) {
        final SellType[] types = sellTypeMap.get(message);
        final Map<String, Object> playerDataMap = player.getPlayerDataMap();
        if (CMD_LIST.contains(message)) {
            if (playerDataMap.get(SELL_TYPE) == null) {
                return;
            }
            List<PokerSell> list;
            if (message.equals(CMD_LIST.get(0))) {
                list = getPrePagePokerSellList(player);
            } else {
                list = getNextPagePokerSellList(player);
            }
            player.setStatus(LandlordsPlayerGameStatus.CHOOSE_TIP);
            if (list.isEmpty()) {
                LandlordsNotifyUtil.notifyPlayer(player, String.format("没有%s了", message));
                LandlordsNotifyUtil.notifyPlayerChoosePokers(player);
                return;
            }
            playerDataMap.put("list", list);
            LandlordsNotifyUtil.notifyPlayerChoosePokers(player, list);
            return;
        }
///        if (player.getStatus().equals(LandlordsPlayerGameStatus.CHOOSE_TIP)) {
///            LandlordsNotifyUtil.notifyPlayer(player, "请输入一个数字.");
///            return;
///        }
        // 发送其他消息
        if (player.isPrivateMessage()) {
            LandlordsNotifyUtil.notifyPlayerSpeak(player, message);
        }
    }
}

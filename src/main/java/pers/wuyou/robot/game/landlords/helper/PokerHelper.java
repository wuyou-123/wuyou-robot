package pers.wuyou.robot.game.landlords.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.core.util.CatUtil;
import pers.wuyou.robot.core.util.CommandUtil;
import pers.wuyou.robot.game.landlords.LandlordsGameManager;
import pers.wuyou.robot.game.landlords.entity.LandlordsPlayer;
import pers.wuyou.robot.game.landlords.entity.Poker;
import pers.wuyou.robot.game.landlords.entity.PokerSell;
import pers.wuyou.robot.game.landlords.enums.PokerLevel;
import pers.wuyou.robot.game.landlords.enums.PokerType;
import pers.wuyou.robot.game.landlords.enums.SellType;
import pers.wuyou.robot.game.landlords.exception.LandlordsException;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author wuyou
 */
@Component
@Slf4j
@SuppressWarnings("AlibabaUndefineMagicConstant")
public class PokerHelper {
    /**
     * 所有扑克牌类型
     */
    private static final List<Poker> BASE_POKERS = new ArrayList<>(54);
    private static final Comparator<Poker> POKER_COMPARATOR = Comparator.comparingInt(o -> o.getLevel().getLevel());
    private static final String SEPARATOR = File.separator;
    public static final LandlordsGameManager GAME_MANAGER = new LandlordsGameManager();

    static {
        PokerLevel[] pokerLevels = PokerLevel.values();
        PokerType[] pokerTypes = PokerType.values();

        for (PokerLevel level : pokerLevels) {
            if (level == PokerLevel.LEVEL_BIG_KING || level == PokerLevel.LEVEL_SMALL_KING) {
                BASE_POKERS.add(new Poker(level, PokerType.BLANK));
                continue;
            }
            for (PokerType type : pokerTypes) {
                if (type == PokerType.BLANK) {
                    continue;
                }
                BASE_POKERS.add(new Poker(level, type));
            }
        }
    }

    public static void sortPoker(List<Poker> pokers) {
        pokers.sort(POKER_COMPARATOR);
    }

    public static List<PokerSell> validSells(PokerSell lastPokerSell, List<Poker> pokers) {
        List<PokerSell> sells = PokerHelper.parsePokerSells(pokers);
        if (lastPokerSell == null) {
            return sells;
        }

        List<PokerSell> validSells = new ArrayList<>();
        for (PokerSell sell : sells) {
            if (sell.getSellType() == lastPokerSell.getSellType() && sell.getScore() > lastPokerSell.getScore() && sell.getSellPokers().size() == lastPokerSell.getSellPokers().size()) {
                validSells.add(sell);
            }

            if (sell.getSellType() == SellType.KING_BOMB) {
                validSells.add(sell);
            }
        }
        if (lastPokerSell.getSellType() != SellType.BOMB) {
            for (PokerSell sell : sells) {
                if (sell.getSellType() == SellType.BOMB && sell.getScore() > lastPokerSell.getScore()) {
                    validSells.add(sell);
                }
            }
        }
        return validSells;
    }

    public static int[] getIndexes(Character[] options, List<Poker> pokers) {
        List<Poker> copyList = new ArrayList<>(pokers);
        int[] indexes = new int[options.length];
        for (int index = 0; index < options.length; index++) {
            char option = options[index];
            boolean isTarget = false;
            for (int pi = 0; pi < copyList.size(); pi++) {
                Poker poker = copyList.get(pi);
                if (poker != null && Arrays.asList(poker.getLevel().getAlias()).contains(option)) {
                    isTarget = true;
                    //Index start from 1, not 0
                    indexes[index] = pi + 1;
                    copyList.set(pi, null);
                    break;
                }
            }
            if (!isTarget) {
                return new int[0];
            }
        }
        Arrays.sort(indexes);
        return indexes;
    }

    public static boolean checkPokerIndex(int[] indexes, List<Poker> pokers) {
        boolean access = true;
        if (indexes == null || indexes.length == 0) {
            access = false;
        } else {
            for (int index : indexes) {
                if (index > pokers.size() || index < 1) {
                    access = false;
                    break;
                }
            }
        }
        return access;
    }

    @SuppressWarnings("all")
    public static PokerSell checkPokerType(List<Poker> pokers) {

        if (pokers == null || pokers.isEmpty()) {
            return new PokerSell(SellType.ILLEGAL, null, -1);
        }
        sortPoker(pokers);

        int[] levelTable = new int[20];
        for (Poker poker : pokers) {
            levelTable[poker.getLevel().getLevel()]++;
        }

        int startIndex = -1;
        int endIndex = -1;
        int count = 0;

        int singleCount = 0;
        int doubleCount = 0;
        int threeCount = 0;
        int threeStartIndex = -1;
        int threeEndIndex = -1;
        int fourCount = 0;
        int fourStartIndex = -1;
        int fourEndIndex = -1;
        for (int index = 0; index < levelTable.length; index++) {
            int value = levelTable[index];
            if (value == 0) {
                continue;
            }
            endIndex = index;
            count++;
            if (startIndex == -1) {
                startIndex = index;
            }
            switch (value) {
                case 1:
                    singleCount++;
                    break;
                case 2:
                    doubleCount++;
                    break;
                case 3:
                    if (threeStartIndex == -1) {
                        threeStartIndex = index;
                    }
                    threeEndIndex = index;
                    threeCount++;
                    break;
                case 4:
                    if (fourStartIndex == -1) {
                        fourStartIndex = index;
                    }
                    fourEndIndex = index;
                    fourCount++;
                    break;
                default:
            }
        }

        if (singleCount == doubleCount && singleCount == threeCount && singleCount == 0 && fourCount == 1) {
            return new PokerSell(SellType.BOMB, pokers, startIndex);
        }

        if (singleCount == 2 && startIndex == PokerLevel.LEVEL_SMALL_KING.getLevel() && endIndex == PokerLevel.LEVEL_BIG_KING.getLevel()) {
            return new PokerSell(SellType.KING_BOMB, pokers, PokerLevel.LEVEL_SMALL_KING.getLevel());
        }

        if (startIndex == endIndex) {
            switch (levelTable[startIndex]) {
                case 1:
                    return new PokerSell(SellType.SINGLE, pokers, startIndex);
                case 2:
                    return new PokerSell(SellType.DOUBLE, pokers, startIndex);
                case 3:
                    return new PokerSell(SellType.THREE, pokers, startIndex);
                default:
            }
        }
        if (endIndex - startIndex == count - 1 && endIndex < PokerLevel.LEVEL_2.getLevel()) {
            if (levelTable[startIndex] == 1 && singleCount > 4 && doubleCount + threeCount + fourCount == 0) {
                return new PokerSell(SellType.SINGLE_STRAIGHT, pokers, endIndex);
            } else if (levelTable[startIndex] == 2 && doubleCount > 2 && singleCount + threeCount + fourCount == 0) {
                return new PokerSell(SellType.DOUBLE_STRAIGHT, pokers, endIndex);
            } else if (levelTable[startIndex] == 3 && threeCount > 1 && doubleCount + singleCount + fourCount == 0) {
                return new PokerSell(SellType.THREE_STRAIGHT, pokers, endIndex);
            } else if (levelTable[startIndex] == 4 && fourCount > 1 && doubleCount + threeCount + singleCount == 0) {
                return new PokerSell(SellType.FOUR_STRAIGHT, pokers, endIndex);
            }
        }

        if (threeCount != 0) {
            if (singleCount != 0 && singleCount == threeCount && doubleCount == 0 && fourCount == 0) {
                if (threeCount == 1) {
                    return new PokerSell(SellType.THREE_ZONES_SINGLE, pokers, threeEndIndex);
                } else {
                    if (threeEndIndex - threeStartIndex + 1 == threeCount && threeEndIndex < PokerLevel.LEVEL_2.getLevel()) {
                        return new PokerSell(SellType.THREE_STRAIGHT_WITH_SINGLE, pokers, threeEndIndex);
                    }
                }
            } else if (doubleCount != 0 && doubleCount == threeCount && singleCount == 0 && fourCount == 0) {
                if (threeCount == 1) {
                    return new PokerSell(SellType.THREE_ZONES_DOUBLE, pokers, threeEndIndex);
                } else {
                    if (threeEndIndex - threeStartIndex + 1 == threeCount && threeEndIndex < PokerLevel.LEVEL_2.getLevel()) {
                        return new PokerSell(SellType.FOUR_STRAIGHT_WITH_DOUBLE, pokers, threeEndIndex);
                    }
                }
            }
        }

        if (fourCount != 0) {
            if (singleCount != 0 && singleCount == fourCount * 2 && doubleCount == 0 && threeCount == 0) {
                if (fourCount == 1) {
                    return new PokerSell(SellType.FOUR_ZONES_SINGLE, pokers, fourEndIndex);
                } else {
                    if (fourEndIndex - fourStartIndex + 1 == fourCount && fourEndIndex < PokerLevel.LEVEL_2.getLevel()) {
                        return new PokerSell(SellType.FOUR_STRAIGHT_WITH_SINGLE, pokers, fourEndIndex);
                    }
                }
            } else if (doubleCount != 0 && doubleCount == fourCount * 2 && singleCount == 0 && threeCount == 0) {
                if (fourCount == 1) {
                    return new PokerSell(SellType.FOUR_ZONES_DOUBLE, pokers, fourEndIndex);
                } else {
                    if (fourEndIndex - fourStartIndex + 1 == fourCount && fourEndIndex < PokerLevel.LEVEL_2.getLevel()) {
                        return new PokerSell(SellType.FOUR_STRAIGHT_WITH_DOUBLE, pokers, fourEndIndex);
                    }
                }
            }
        }
        return new PokerSell(SellType.ILLEGAL, null, -1);
    }

    public static int parseScore(SellType sellType, int level) {
        if (sellType == SellType.BOMB) {
            return level * 4 + 999;
        } else if (sellType == SellType.KING_BOMB) {
            return Integer.MAX_VALUE;
        } else if (sellType == SellType.SINGLE || sellType == SellType.DOUBLE || sellType == SellType.THREE) {
            return level;
        } else if (sellType == SellType.SINGLE_STRAIGHT || sellType == SellType.DOUBLE_STRAIGHT || sellType == SellType.THREE_STRAIGHT || sellType == SellType.FOUR_STRAIGHT) {
            return level;
        } else if (sellType == SellType.THREE_ZONES_SINGLE || sellType == SellType.THREE_STRAIGHT_WITH_SINGLE || sellType == SellType.THREE_ZONES_DOUBLE || sellType == SellType.FOUR_STRAIGHT_WITH_DOUBLE) {
            return level;
        } else if (sellType == SellType.FOUR_ZONES_SINGLE || sellType == SellType.FOUR_STRAIGHT_WITH_SINGLE || sellType == SellType.FOUR_ZONES_DOUBLE) {
            return level;
        }
        return -1;
    }

    public static List<Poker> getPoker(int[] indexes, List<Poker> pokers) {
        List<Poker> resultPokers = new ArrayList<>(indexes.length);
        for (int index : indexes) {
            resultPokers.add(pokers.get(index - 1));
        }
        sortPoker(resultPokers);
        return resultPokers;
    }

    public static List<List<Poker>> distributePoker() {
        Collections.shuffle(BASE_POKERS);
        List<List<Poker>> pokersList = new ArrayList<>();
        List<Poker> pokers1 = new ArrayList<>(17);
        pokers1.addAll(BASE_POKERS.subList(0, 17));
        List<Poker> pokers2 = new ArrayList<>(17);
        pokers2.addAll(BASE_POKERS.subList(17, 34));
        List<Poker> pokers3 = new ArrayList<>(17);
        pokers3.addAll(BASE_POKERS.subList(34, 51));
        List<Poker> pokers4 = new ArrayList<>(3);
        pokers4.addAll(BASE_POKERS.subList(51, 54));
        pokersList.add(pokers1);
        pokersList.add(pokers2);
        pokersList.add(pokers3);
        pokersList.add(pokers4);
        for (List<Poker> pokers : pokersList) {
            sortPoker(pokers);
        }
        return pokersList;
    }

    public static List<PokerSell> parsePokerSells(List<Poker> pokers) {
        final List<PokerSell> pokerSells = new ArrayList<>();
        allSingleOrDouble(pokers, pokerSells);
        shunzi(pokerSells);
        shunziWithArgs(pokerSells);
        kingBoom(pokers, pokerSells);
        return pokerSells;
    }

    private static void kingBoom(final List<Poker> pokers, final List<PokerSell> pokerSells) {
        int size = pokers.size();
        if (size > 1 && pokers.get(size - 1).getLevel() == PokerLevel.LEVEL_BIG_KING && pokers.get(size - 2).getLevel() == PokerLevel.LEVEL_SMALL_KING) {
            pokerSells.add(new PokerSell(SellType.KING_BOMB, new ArrayList<>(Arrays.asList(pokers.get(size - 2), pokers.get(size - 1))), PokerLevel.LEVEL_BIG_KING.getLevel()));
        }
    }

    private static void shunziWithArgs(final List<PokerSell> pokerSells) {
        for (int index = 0; index < pokerSells.size(); index++) {
            PokerSell sell = pokerSells.get(index);
            if (sell.getSellType() == SellType.THREE) {
                parseArgs(pokerSells, sell, 1, SellType.SINGLE, SellType.THREE_ZONES_SINGLE);
                parseArgs(pokerSells, sell, 1, SellType.DOUBLE, SellType.THREE_ZONES_DOUBLE);
            } else if (sell.getSellType() == SellType.BOMB) {
                parseArgs(pokerSells, sell, 2, SellType.SINGLE, SellType.FOUR_ZONES_SINGLE);
                parseArgs(pokerSells, sell, 2, SellType.DOUBLE, SellType.FOUR_ZONES_DOUBLE);
            } else if (sell.getSellType() == SellType.THREE_STRAIGHT) {
                int count = sell.getSellPokers().size() / 3;
                parseArgs(pokerSells, sell, count, SellType.SINGLE, SellType.THREE_STRAIGHT_WITH_SINGLE);
                parseArgs(pokerSells, sell, count, SellType.DOUBLE, SellType.THREE_STRAIGHT_WITH_DOUBLE);
            } else if (sell.getSellType() == SellType.FOUR_STRAIGHT) {
                int count = (sell.getSellPokers().size() / 4) * 2;
                parseArgs(pokerSells, sell, count, SellType.SINGLE, SellType.FOUR_STRAIGHT_WITH_SINGLE);
                parseArgs(pokerSells, sell, count, SellType.DOUBLE, SellType.FOUR_STRAIGHT_WITH_DOUBLE);
            }
        }
    }

    private static void shunzi(final List<PokerSell> pokerSells) {
        parsePokerSellStraight(pokerSells, SellType.SINGLE);
        parsePokerSellStraight(pokerSells, SellType.DOUBLE);
        parsePokerSellStraight(pokerSells, SellType.THREE);
        parsePokerSellStraight(pokerSells, SellType.BOMB);
    }

    private static void allSingleOrDouble(final List<Poker> pokers, final List<PokerSell> pokerSells) {
        int count = 0;
        int lastLevel = -1;
        List<Poker> sellPokers = new ArrayList<>(4);
        for (Poker poker : pokers) {
            int level = poker.getLevel().getLevel();
            if (lastLevel == -1) {
                ++count;
            } else {
                if (level == lastLevel) {
                    ++count;
                } else {
                    count = 1;
                    sellPokers.clear();
                }
            }
            sellPokers.add(poker);
            if (count == 1) {
                pokerSells.add(new PokerSell(SellType.SINGLE, new ArrayList<>(sellPokers), poker.getLevel().getLevel()));
            } else if (count == 2) {
                pokerSells.add(new PokerSell(SellType.DOUBLE, new ArrayList<>(sellPokers), poker.getLevel().getLevel()));
            } else if (count == 3) {
                pokerSells.add(new PokerSell(SellType.THREE, new ArrayList<>(sellPokers), poker.getLevel().getLevel()));
            } else if (count == 4) {
                pokerSells.add(new PokerSell(SellType.BOMB, new ArrayList<>(sellPokers), poker.getLevel().getLevel()));
            }

            lastLevel = level;
        }
    }

    private static void parseArgs(List<PokerSell> pokerSells, PokerSell pokerSell, int deep, SellType sellType, SellType targetSellType) {
        Set<Integer> existLevelSet = new HashSet<>();
        for (Poker p : pokerSell.getSellPokers()) {
            existLevelSet.add(p.getLevel().getLevel());
        }
        parseArgs(existLevelSet, pokerSells, new HashSet<>(), pokerSell, deep, sellType, targetSellType);
    }

    private static void parseArgs(Set<Integer> existLevelSet, List<PokerSell> pokerSells, Set<List<Poker>> pokersList, PokerSell pokerSell, int deep, SellType sellType, SellType targetSellType) {
        if (deep == 0) {
            List<Poker> allPokers = new ArrayList<>(pokerSell.getSellPokers());
            for (List<Poker> ps : pokersList) {
                allPokers.addAll(ps);
            }
            pokerSells.add(new PokerSell(targetSellType, allPokers, pokerSell.getCoreLevel()));
            return;
        }

        for (int index = 0; index < pokerSells.size(); index++) {
            PokerSell subSell = pokerSells.get(index);
            if (subSell.getSellType() == sellType && !existLevelSet.contains(subSell.getCoreLevel())) {
                pokersList.add(subSell.getSellPokers());
                existLevelSet.add(subSell.getCoreLevel());
                parseArgs(existLevelSet, pokerSells, pokersList, pokerSell, deep - 1, sellType, targetSellType);
                existLevelSet.remove(subSell.getCoreLevel());
                pokersList.remove(subSell.getSellPokers());
            }
        }
    }

    @SuppressWarnings("all")
    private static void parsePokerSellStraight(List<PokerSell> pokerSells, SellType sellType) {
        int minLength = -1;
        int width = -1;
        SellType targetSellType = null;
        switch (sellType) {
            case SINGLE:
                minLength = 5;
                width = 1;
                targetSellType = SellType.SINGLE_STRAIGHT;
                break;
            case DOUBLE:
                minLength = 3;
                width = 2;
                targetSellType = SellType.DOUBLE_STRAIGHT;
                break;
            case THREE:
                minLength = 2;
                width = 3;
                targetSellType = SellType.THREE_STRAIGHT;
                break;
            case BOMB:
                minLength = 2;
                width = 4;
                targetSellType = SellType.FOUR_STRAIGHT;
                break;
            default:
                break;
        }

        int increase1 = 0;
        int lastLevel1 = -1;
        List<Poker> sellPokers1 = new ArrayList<>(4);
        for (int index = 0; index < pokerSells.size(); index++) {
            PokerSell sell = pokerSells.get(index);

            if (sell.getSellType() == sellType) {
                int level = sell.getCoreLevel();
                if (lastLevel1 == -1) {
                    ++increase1;
                } else {
                    if (level - 1 == lastLevel1 && level != PokerLevel.LEVEL_2.getLevel()) {
                        ++increase1;
                    } else {
                        if (increase1 >= minLength) {
                            for (int s = 0; s <= increase1 - minLength; s++) {
                                int len = minLength + s;
                                for (int subIndex = 0; subIndex <= increase1 - len; subIndex++) {
                                    List<Poker> pokers = new ArrayList<>(sellPokers1.subList(subIndex * width, (subIndex + len) * width));
                                    pokerSells.add(new PokerSell(targetSellType, pokers, pokers.get(pokers.size() - 1).getLevel().getLevel()));
                                }
                            }
                        }
                        increase1 = 1;
                        sellPokers1.clear();
                    }
                }
                sellPokers1.addAll(sell.getSellPokers());
                lastLevel1 = level;
            }
        }
        if (increase1 >= minLength) {
            for (int s = 0; s <= increase1 - minLength; s++) {
                int len = minLength + s;
                for (int subIndex = 0; subIndex <= increase1 - len; subIndex++) {
                    List<Poker> pokers = new ArrayList<>(sellPokers1.subList(subIndex * width, (subIndex + len) * width));
                    pokerSells.add(new PokerSell(targetSellType, pokers, pokers.get(pokers.size() - 1).getLevel().getLevel()));
                }
            }
        }
        sellPokers1.clear();
    }

    public static String textOnlyNoType(List<Poker> pokers) {
        StringBuilder builder = new StringBuilder();
        if (pokers != null && !pokers.isEmpty()) {
            for (Poker poker : pokers) {
                String name = poker.getLevel().getName();
                builder.append(name).append(" ");
            }
        }
        return builder.toString();
    }

    public static String getPoker(LandlordsPlayer player) {
        return getPoker(player.getPokers());
    }

    @SuppressWarnings("all")
    public static String getPoker(List<Poker> pokers) {
        if (pokers.isEmpty()) {
            return "";
        }
        List<String> pokerList = new ArrayList<>();
        for (Poker poker : pokers) {
            String b;
            String a = poker.getLevel().getName()
                    .replace("A", "1")
                    .replace("小王", "s")
                    .replace("大王", "x");
            switch (poker.getType().toString()) {
                case "SPADE":
                    b = "a";
                    break;
                case "HEART":
                    b = "b";
                    break;
                case "CLUB":
                    b = "c";
                    break;
                case "DIAMOND":
                    b = "d";
                    break;
                default:
                    b = "e";
            }
            pokerList.add((b + a).toLowerCase(Locale.ROOT).replace("10", "0"));
        }
//        if (pokerList.size() == 1) {
//            return CatUtil.getImage(landlordsGameManager.getTempPath() + pokerList.get(0) + ".jpg").toString();
//        }
        char[] sort = new char[]{'x', 's', '2', '1', 'k', 'q', 'j', '0', '9', '8', '7', '6', '5', '4', '3'};
        char[] sort2 = new char[]{'e', 'a', 'b', 'c', 'd'};
        pokerList.sort((a, b) -> {
            int aIndex = -1;
            int bIndex = -1;
            for (int i = 0; i < sort.length; i++) {
                if (a.split("")[1].charAt(0) == (sort[i])) {
                    aIndex = i;
                }
                if (b.split("")[1].charAt(0) == (sort[i])) {
                    bIndex = i;
                }
            }
            if (aIndex == bIndex) {
                for (int i = 0; i < sort2.length; i++) {
                    if (a.split("")[0].charAt(0) == (sort2[i])) {
                        aIndex = i;
                    }
                    if (b.split("")[0].charAt(0) == (sort2[i])) {
                        bIndex = i;
                    }
                }
            }
            return aIndex - bIndex;
        });
        String pokerStr = pokerList.toString().replace(" ", "").replace(",", "_");
        pokerStr = pokerStr.substring(1, pokerStr.length() - 1);
        File pokerDir = new File(GAME_MANAGER.getTempPath() + "poker_comp");
        if (!pokerDir.exists() && !pokerDir.mkdirs()) {
            throw new LandlordsException("Destination '" + pokerDir + "' directory cannot be created");
        }
        File pokerFile = new File(pokerDir + SEPARATOR + pokerStr + ".jpg");
        if (pokerFile.exists()) {
            return CatUtil.getImage(pokerFile.toString()).toString();
        }
        CommandUtil.exec("python", pokerList, GAME_MANAGER.getTempPath() + "generatePoker.py", GAME_MANAGER.getTempPath(), pokerFile.toString());
        return CatUtil.getImage(pokerFile.toString()).toString();
    }

    public static Character[] parsePoker(String message) {
        message = replaceDoubleMessage(message);
        message = replaceKingMessage(message);
        message = message.replace("10", "0");
        String[] msg = message.split(" ");
        boolean access = true;
        List<Character> options = new ArrayList<>();
        for (String str : msg) {
            for (char c : str.toCharArray()) {
                if (c != ' ' && c != '\t') {
                    if (PokerLevel.aliasContains(c)) {
                        access = false;
                        break;
                    } else {
                        options.add(c);
                    }
                }
            }
        }
        return access ? options.toArray(new Character[0]) : null;
    }

    private static String replaceDoubleMessage(String message) {
        String pattern = "^对.$";
        boolean isMatch = Pattern.matches(pattern, message);
        if (!isMatch) {
            return message;
        }
        return String.valueOf(message.charAt(1)) + message.charAt(1);
    }

    private static String replaceKingMessage(String message) {
        return message.replace("大王", "x")
                .replace("小王", "s")
                .replace("王炸", "sx")
                .replace("双王", "sx");
    }
}

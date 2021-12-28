package pers.wuyou.robot.game.landlords.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Poker level
 *
 * @author nico
 */
@Getter
public enum PokerLevel {
    /**
     * 所有的牌
     */
    LEVEL_3(3, "3", new Character[]{'3', '三'}),

    LEVEL_4(4, "4", new Character[]{'4', '四'}),

    LEVEL_5(5, "5", new Character[]{'5', '五'}),

    LEVEL_6(6, "6", new Character[]{'6', '六'}),

    LEVEL_7(7, "7", new Character[]{'7', '七'}),

    LEVEL_8(8, "8", new Character[]{'8', '八'}),

    LEVEL_9(9, "9", new Character[]{'9', '九'}),

    LEVEL_10(10, "10", new Character[]{'T', 't', '0', '十'}),

    LEVEL_J(11, "J", new Character[]{'J', 'j'}),

    LEVEL_Q(12, "Q", new Character[]{'Q', 'q'}),

    LEVEL_K(13, "K", new Character[]{'K', 'k'}),

    LEVEL_A(14, "A", new Character[]{'A', 'a', '1'}),

    LEVEL_2(15, "2", new Character[]{'2', '二'}),

    LEVEL_SMALL_KING(16, "小王", new Character[]{'S', 's'}),

    LEVEL_BIG_KING(17, "大王", new Character[]{'X', 'x'}),
    ;

    private static final Set<Character> ALIAS_SET = new HashSet<>();

    static {
        for (PokerLevel level : PokerLevel.values()) {
            PokerLevel.ALIAS_SET.addAll(Arrays.asList(level.getAlias()));
        }
    }

    private final int level;
    private final String name;
    private final Character[] alias;

    PokerLevel(int level, String name, Character[] alias) {
        this.level = level;
        this.name = name;
        this.alias = alias;
    }

    public static boolean aliasContains(char key) {
        return !ALIAS_SET.contains(key);
    }
}

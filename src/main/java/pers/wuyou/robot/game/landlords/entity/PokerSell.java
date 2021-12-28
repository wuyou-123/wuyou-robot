package pers.wuyou.robot.game.landlords.entity;


import lombok.Getter;
import pers.wuyou.robot.game.landlords.enums.SellType;
import pers.wuyou.robot.game.landlords.helper.PokerHelper;

import java.util.List;

/**
 * @author wuyou
 */
@Getter
public class PokerSell {

    private final int score;

    private final SellType sellType;

    private final List<Poker> sellPokers;

    private final int coreLevel;

    public PokerSell(SellType sellType, List<Poker> sellPokers, int coreLevel) {
        this.score = PokerHelper.parseScore(sellType, coreLevel);
        this.sellType = sellType;
        this.sellPokers = sellPokers;
        this.coreLevel = coreLevel;
    }

    @Override
    public String toString() {
        return String.format("%s\t| %d\t|%s", sellType, score, sellPokers);
    }

    public boolean match(PokerSell that) {
        return sellType == that.sellType && sellPokers.size() == that.sellPokers.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PokerSell pokerSell = (PokerSell) o;

        if (score != pokerSell.score) {
            return false;
        }
        if (coreLevel != pokerSell.coreLevel) {
            return false;
        }
        if (sellType != pokerSell.sellType) {
            return false;
        }
        return sellPokers.equals(pokerSell.sellPokers);
    }

    @Override
    public int hashCode() {
        int result = score;
        result = 31 * result + sellType.hashCode();
        result = 31 * result + sellPokers.hashCode();
        result = 31 * result + coreLevel;
        return result;
    }
}

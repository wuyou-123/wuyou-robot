package pers.wuyou.robot.game.gobang.common;

import lombok.extern.slf4j.Slf4j;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.entity.Step;
import pers.wuyou.robot.game.gobang.util.MathUtil;

import java.util.List;

/**
 * @author wuyou
 * @date 2022/3/31 20:33
 */
@Slf4j
@SuppressWarnings("AlibabaUndefineMagicConstant")
public class Opening {
    /*
     * 一个简单的开局库，用花月+浦月必胜开局
     */

    /**
     * -2-
     * -1-
     * ---
     */
    private Step huayue(GobangRoom room) {
        log.info("使用花月开局");
        List<Step> s = room.getAllSteps();
        if (MathUtil.pointEqual(s.get(1), new int[]{6, 7})) {
            if (s.size() == 2) {
                return new Step(6, 8);
            }
        }
        if (MathUtil.pointEqual(s.get(1), new int[]{7, 6})) {
            if (s.size() == 2) {
                return new Step(6, 6);
            }
        }
        if (MathUtil.pointEqual(s.get(1), new int[]{8, 7})) {
            if (s.size() == 2) {
                return new Step(8, 6);
            }
        }
        if (MathUtil.pointEqual(s.get(1), new int[]{7, 8})) {
            if (s.size() == 2) {
                return new Step(8, 8);
            }
        }
        return new Step(7, 7);
    }

    private Step puyue(GobangRoom room) {
        log.info("使用浦月开局");
        List<Step> s = room.getAllSteps();
        if (MathUtil.pointEqual(s.get(1), new int[]{6, 6})) {
            if (s.size() == 2) {
                return new Step(6, 8);
            }
        }
        if (MathUtil.pointEqual(s.get(1), new int[]{8, 6})) {
            if (s.size() == 2) {
                return new Step(6, 6);
            }
        }
        if (MathUtil.pointEqual(s.get(1), new int[]{8, 8})) {
            if (s.size() == 2) {
                return new Step(8, 6);
            }
        }
        if (MathUtil.pointEqual(s.get(1), new int[]{6, 8})) {
            if (s.size() == 2) {
                return new Step(8, 8);
            }
        }
        return new Step(7, 7);
    }

    public Step match(GobangRoom room) {
        List<Step> s = room.getAllSteps();
        if (room.getBoard()[s.get(0).getX()][s.get(0).getY()] != 1) {
            return null;
        }
        if (s.size() > 2) {
            return null;
        }
        if (MathUtil.containPoint(new int[][]{new int[]{6, 7}, new int[]{7, 6}, new int[]{8, 7}, new int[]{7, 8}}, s.get(1))) {
            return huayue(room);
        } else if (MathUtil.containPoint(new int[][]{new int[]{6, 6}, new int[]{8, 8}, new int[]{8, 6}, new int[]{6, 8}}, s.get(1))) {
            return puyue(room);
        }
        return null;
    }

}

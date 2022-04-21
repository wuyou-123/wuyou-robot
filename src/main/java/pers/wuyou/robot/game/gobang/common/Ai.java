package pers.wuyou.robot.game.gobang.common;

import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.entity.Role;
import pers.wuyou.robot.game.gobang.entity.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyou
 */
public class Ai {
    private final Opening opening = new Opening();
    private final Negamax negamax;
    private final GobangRoom room;

    public Ai(GobangRoom room) {
        this.room = room;
        this.negamax = new Negamax(room);
    }

    public Open26.Moon start(boolean first) {
        if (first) {
            room.init();
            List<Open26.Moon> names = new ArrayList<>(Open26.MOON_LIST);
            return names.get((RobotCore.getRANDOM().nextInt(Open26.MOON_LIST.size())));
        } else {
            room.init();
            return null;
        }
    }

    /**
     * 下棋
     *
     * @return 选择的位置
     */
    public Step begin() {
        Step p = null;
        if (room.getAllSteps().size() > 1) {
            p = opening.match(room);
        }
        if (p == null) {
            // 如果是人机模式则计算另一个role
            if (room.playWithBot()) {
                p = negamax.deepAll(Role.reverse(room.getCurrentPlayer().getRole()));
            } else {
                p = negamax.deepAll(room.getCurrentPlayer().getRole());
            }
        }
        if (room.playWithBot()) {
            room.put(p, Role.reverse(room.getCurrentPlayer().getRole()));
        } else {
            room.put(p, room.getCurrentPlayer().getRole());
        }
        return p;
    }

    /**
     * 下子并计算
     */
    public Step turn(Step step) {
        room.put(step, step.getRole());
        return this.begin();
    }

    /**
     * 悔棋
     */
    @SuppressWarnings("unused")
    public void backward() {
        room.backward();
    }

    /**
     * 悔棋
     */
    @SuppressWarnings("unused")
    public void forward() {
        room.forward();
    }
}

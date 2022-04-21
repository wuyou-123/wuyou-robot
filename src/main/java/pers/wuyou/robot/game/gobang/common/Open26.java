package pers.wuyou.robot.game.gobang.common;

import lombok.Getter;
import pers.wuyou.robot.game.gobang.entity.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyou
 * @date 2022/3/31 19:58
 */
public class Open26 {
    private Open26() {
    }

    public static final List<Moon> MOON_LIST = new ArrayList<>();

    static {
        MOON_LIST.add(new Moon("默认", 7, 7));
        MOON_LIST.add(new Moon("疏月", 5, 5));
        MOON_LIST.add(new Moon("溪月", 5, 6));
        MOON_LIST.add(new Moon("寒月", 5, 7));
        MOON_LIST.add(new Moon("残月", 6, 5));
        MOON_LIST.add(new Moon("花月", 6, 6));
        MOON_LIST.add(new Moon("金月", 7, 5));
        MOON_LIST.add(new Moon("雨月", 7, 6));
        MOON_LIST.add(new Moon("新月", 8, 5));
        MOON_LIST.add(new Moon("丘月", 8, 6));
        MOON_LIST.add(new Moon("松月", 8, 7));
        MOON_LIST.add(new Moon("游月", 9, 5));
        MOON_LIST.add(new Moon("山月", 9, 6));
        MOON_LIST.add(new Moon("瑞月", 9, 7));
        MOON_LIST.add(new Moon("流月", 5, 5));
        MOON_LIST.add(new Moon("水月", 5, 6));
        MOON_LIST.add(new Moon("恒月", 5, 7));
        MOON_LIST.add(new Moon("峡月", 5, 8));
        MOON_LIST.add(new Moon("长月", 5, 9));
        MOON_LIST.add(new Moon("岚月", 6, 5));
        MOON_LIST.add(new Moon("浦月", 6, 6));
        MOON_LIST.add(new Moon("云月", 6, 7));
        MOON_LIST.add(new Moon("明月", 7, 5));
        MOON_LIST.add(new Moon("银月", 7, 6));
        MOON_LIST.add(new Moon("名月", 8, 5));
        MOON_LIST.add(new Moon("斜月", 8, 6));
        MOON_LIST.add(new Moon("慧月", 9, 5));
    }

    @Getter
    public static class Moon {
        private final String name;
        private final int[][] board = initBoard();
        private final Step step;

        public Moon(String name, int index1, int index2) {
            this.name = name;
            this.board[index1][index2] = 1;
            this.step = new Step(index1, index2);
        }

        private int[][] initBoard() {
            return new int[][]{
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15],
                    new int[15]
            };
        }
    }

}

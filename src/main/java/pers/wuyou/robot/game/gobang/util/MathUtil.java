package pers.wuyou.robot.game.gobang.util;


import pers.wuyou.robot.game.gobang.entity.Step;

/**
 * @author wuyou
 * @date 2022/3/31 15:10
 */
public class MathUtil {

    private static final double THRESHOLD = 1.15;

    public static boolean equal(double a, double b) {
        if (b == 0) {
            b = 0.01;
        }
        return b >= 0 ? ((a >= b / THRESHOLD) && (a <= b * THRESHOLD))
                : ((a >= b * THRESHOLD) && (a <= b / THRESHOLD));
    }

    public static boolean greatThan(double a, double b) {
        // 注意处理b为0的情况，通过加一个0.1 做简单的处理
        return b >= 0 ? (a >= (b + 0.1) * THRESHOLD) : (a >= (b + 0.1) / THRESHOLD);
    }

    public static boolean greatOrEqualThan(int a, int b) {
        return equal(a, b) || greatThan(a, b);
    }

    public static boolean littleThan(double a, double b) {
        return b >= 0 ? (a <= (b - 0.1) / THRESHOLD) : (a <= (b - 0.1) * THRESHOLD);
    }

    public static boolean littleOrEqualThan(double a, double b) {
        return equal(a, b) || littleThan(a, b);
    }

    public static boolean containPoint(int[][] arrays, Step p) {
        for (int[] a : arrays) {
            if (a[0] == p.getX() && a[1] == p.getY()) {
                return true;
            }
        }
        return false;
    }

    public static boolean pointEqual(Step a, int[] b) {
        return a.getX() == b[0] && a.getY() == b[1];
    }

}

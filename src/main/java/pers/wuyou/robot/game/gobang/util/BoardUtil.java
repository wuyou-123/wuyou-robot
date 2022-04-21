package pers.wuyou.robot.game.gobang.util;

/**
 * @author wuyou
 * @date 2022/3/31 15:46
 */
public class BoardUtil {
    public static int[][] create(int w, int h) {
        int[][] r = new int[w][h];
        for (int i = 0; i < w; i++) {
            int[] row = new int[h];
            for (int j = 0; j < h; j++) {
                row[j] = 0;
            }
            r[i] = row;
        }
        return r;
    }
}

package pers.wuyou.robot.game.gobang.common;

import lombok.var;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.entity.Role;
import pers.wuyou.robot.game.gobang.entity.Score;

/**
 * 启发式评价函数
 * 这个是专门给某一个位置打分的，不是给整个棋盘打分的
 * 并且是只给某一个角色打分
 *
 * @author wuyou
 * @date 2022/3/31 15:24
 */
public class EvaluatePoint {
    private static int empty = 0, count = 0, block = 0, secondCount = 0;

    private EvaluatePoint() {
    }

    public static void reset() {
        count = 1;
        block = 0;
        empty = -1;
        secondCount = 0;
    }

    /**
     * 表示在当前位置下一个棋子后的分数
     * 为了性能考虑，增加了一个dir参数，如果没有传入则默认计算所有四个方向，如果传入值，则只计算其中一个方向的值
     */
    @SuppressWarnings({"AlibabaMethodTooLong", "AlibabaUndefineMagicConstant", "AlibabaAvoidComplexCondition"})
    public static int scorePoint(GobangRoom room, int px, int py, int role, Integer dir) {
        var board = room.getBoard();
        int result = 0;
        var len = board.length;
        if (dir == null || dir == 0) {
            reset();
            for (var i = py + 1; true; i++) {
                if (i >= len) {
                    block++;
                    break;
                }
                var t = board[px][i];
                if (t == Role.EMPTY) {
                    if (empty == -1 && i < len - 1 && board[px][i + 1] == role) {
                        empty = count;
                        continue;
                    } else {
                        break;
                    }
                }
                if (t == role) {
                    count++;
                } else {
                    block++;
                    break;
                }
            }


            for (var i = py - 1; true; i--) {
                if (i < 0) {
                    block++;
                    break;
                }
                var t = board[px][i];
                if (t == Role.EMPTY) {
                    if (empty == -1 && i > 0 && board[px][i - 1] == role) {
                        //注意这里是0，因为是从右往左走的
                        empty = 0;
                        continue;
                    } else {
                        break;
                    }
                }
                if (t == role) {
                    secondCount++;
                    //注意这里，如果左边又多了己方棋子，那么empty的位置就变大了
                    if (empty != -1) {
                        empty++;
                    }
                } else {
                    block++;
                    break;
                }
            }

            count += secondCount;

            room.getScoreCache()[role][0][px][py] = countToScore(count, block, empty);
        }
        result += room.getScoreCache()[role][0][px][py];

        if (dir == null || dir == 1) {

            // |
            reset();

            for (var i = px + 1; true; i++) {
                if (i >= len) {
                    block++;
                    break;
                }
                var t = board[i][py];
                if (t == Role.EMPTY) {
                    if (empty == -1 && i < len - 1 && board[i + 1][py] == role) {
                        empty = count;
                        continue;
                    } else {
                        break;
                    }
                }
                if (t == role) {
                    count++;
                } else {
                    block++;
                    break;
                }
            }

            for (var i = px - 1; true; i--) {
                if (i < 0) {
                    block++;
                    break;
                }
                var t = board[i][py];
                if (t == Role.EMPTY) {
                    if (empty == -1 && i > 0 && board[i - 1][py] == role) {
                        empty = 0;
                        continue;
                    } else {
                        break;
                    }
                }
                if (t == role) {
                    secondCount++;
                    //注意这里，如果左边又多了己方棋子，那么empty的位置就变大了
                    if (empty != -1) {
                        empty++;
                    }
                } else {
                    block++;
                    break;
                }
            }

            count += secondCount;

            room.getScoreCache()[role][1][px][py] = countToScore(count, block, empty);
        }
        result += room.getScoreCache()[role][1][px][py];


        // \
        if (dir == null || dir == 2) {
            reset();

            for (var i = 1; true; i++) {
                int x = px + i, y = py + i;
                if (x >= len || y >= len) {
                    block++;
                    break;
                }
                var t = board[x][y];
                if (t == Role.EMPTY) {
                    if (empty == -1 && (x < len - 1 && y < len - 1) && board[x + 1][y + 1] == role) {
                        empty = count;
                        continue;
                    } else {
                        break;
                    }
                }
                if (t == role) {
                    count++;
                } else {
                    block++;
                    break;
                }
            }

            for (var i = 1; true; i++) {
                int x = px - i, y = py - i;
                if (x < 0 || y < 0) {
                    block++;
                    break;
                }
                var t = board[x][y];
                if (t == Role.EMPTY) {
                    if (empty == -1 && (x > 0 && y > 0) && board[x - 1][y - 1] == role) {
                        empty = 0;
                        continue;
                    } else {
                        break;
                    }
                }
                if (t == role) {
                    secondCount++;
                    if (empty != -1) {
                        empty++;  //注意这里，如果左边又多了己方棋子，那么empty的位置就变大了
                    }
                } else {
                    block++;
                    break;
                }
            }

            count += secondCount;

            room.getScoreCache()[role][2][px][py] = countToScore(count, block, empty);
        }
        result += room.getScoreCache()[role][2][px][py];


        // /
        if (dir == null || dir == 3) {
            reset();

            for (var i = 1; true; i++) {
                int x = px + i, y = py - i;
                if (x < 0 || y < 0 || x >= len || y >= len) {
                    block++;
                    break;
                }
                var t = board[x][y];
                if (t == Role.EMPTY) {
                    if (empty == -1 && (x < len - 1 && y > 0) && board[x + 1][y - 1] == role) {
                        empty = count;
                        continue;
                    } else {
                        break;
                    }
                }
                if (t == role) {
                    count++;
                } else {
                    block++;
                    break;
                }
            }

            for (var i = 1; true; i++) {
                int x = px - i, y = py + i;
                if (x < 0 || y < 0 || x >= len || y >= len) {
                    block++;
                    break;
                }
                var t = board[x][y];
                if (t == Role.EMPTY) {
                    if (empty == -1 && (x > 0 && y < len - 1) && board[x - 1][y + 1] == role) {
                        empty = 0;
                        continue;
                    } else {
                        break;
                    }
                }
                if (t == role) {
                    secondCount++;
                    if (empty != -1) {
                        empty++;  //注意这里，如果左边又多了己方棋子，那么empty的位置就变大了
                    }
                } else {
                    block++;
                    break;
                }
            }

            count += secondCount;

            room.getScoreCache()[role][3][px][py] = countToScore(count, block, empty);
        }
        result += room.getScoreCache()[role][3][px][py];

        return result;
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    private static int countToScore(int count, int block, int empty) {

        //没有空位
        if (empty <= 0) {
            if (count >= 5) {
                return Score.FIVE;
            }
            if (block == 0) {
                switch (count) {
                    case 1:
                        return Score.ONE;
                    case 2:
                        return Score.TWO;
                    case 3:
                        return Score.THREE;
                    case 4:
                        return Score.FOUR;
                    default:
                }
            }

            if (block == 1) {
                switch (count) {
                    case 1:
                        return Score.BLOCKED_ONE;
                    case 2:
                        return Score.BLOCKED_TWO;
                    case 3:
                        return Score.BLOCKED_THREE;
                    case 4:
                        return Score.BLOCKED_FOUR;
                    default:
                }
            }

        } else if (empty == 1 || empty == count - 1) {
            //第1个是空位
            if (count >= 6) {
                return Score.FIVE;
            }
            if (block == 0) {
                switch (count) {
                    case 2:
                        return Score.TWO / 2;
                    case 3:
                        return Score.THREE;
                    case 4:
                        return Score.BLOCKED_FOUR;
                    case 5:
                        return Score.FOUR;
                    default:
                }
            }

            if (block == 1) {
                switch (count) {
                    case 2:
                        return Score.BLOCKED_TWO;
                    case 3:
                        return Score.BLOCKED_THREE;
                    case 4:
                    case 5:
                        return Score.BLOCKED_FOUR;
                    default:
                }
            }
        } else if (empty == 2 || empty == count - 2) {
            //第二个是空位
            if (count >= 7) {
                return Score.FIVE;
            }
            if (block == 0) {
                switch (count) {
                    case 3:
                        return Score.THREE;
                    case 4:
                    case 5:
                        return Score.BLOCKED_FOUR;
                    case 6:
                        return Score.FOUR;
                    default:
                }
            }

            if (block == 1) {
                switch (count) {
                    case 3:
                        return Score.BLOCKED_THREE;
                    case 4:
                    case 5:
                        return Score.BLOCKED_FOUR;
                    case 6:
                        return Score.FOUR;
                    default:
                }
            }

            if (block == 2) {
                switch (count) {
                    case 4:
                    case 5:
                    case 6:
                        return Score.BLOCKED_FOUR;
                    default:
                }
            }
        } else if (empty == 3 || empty == count - 3) {
            if (count >= 8) {
                return Score.FIVE;
            }
            if (block == 0) {
                switch (count) {
                    case 4:
                    case 5:
                        return Score.THREE;
                    case 6:
                        return Score.BLOCKED_FOUR;
                    case 7:
                        return Score.FOUR;
                    default:
                }
            }

            if (block == 1) {
                switch (count) {
                    case 4:
                    case 5:
                    case 6:
                        return Score.BLOCKED_FOUR;
                    case 7:
                        return Score.FOUR;
                    default:
                }

            } else if (empty == 4 || empty == count - 4) {

                if (block == 2) {
                    if (count == 7) {
                        return Score.BLOCKED_FOUR;
                    }
                }
            } else if (empty == 5 || empty == count - 5) {
                return Score.FIVE;
            }

            return 0;
        }
        return 0;
    }
}

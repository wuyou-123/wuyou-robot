package pers.wuyou.robot.game.gobang.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pers.wuyou.robot.common.util.Encryption;
import pers.wuyou.robot.core.util.CatUtil;
import pers.wuyou.robot.core.util.CommandUtil;
import pers.wuyou.robot.game.common.Game;
import pers.wuyou.robot.game.common.BaseRoom;
import pers.wuyou.robot.game.gobang.GobangGameManager;
import pers.wuyou.robot.game.gobang.common.Ai;
import pers.wuyou.robot.game.gobang.common.EvaluatePoint;
import pers.wuyou.robot.game.gobang.common.Zobrist;
import pers.wuyou.robot.game.gobang.enums.RoomMode;
import pers.wuyou.robot.game.gobang.util.BoardUtil;
import pers.wuyou.robot.game.gobang.util.GobangNotifyUtil;
import pers.wuyou.robot.game.landlords.common.LandlordsPlayerGameStatus;

import java.io.File;
import java.util.*;

/**
 * @author wuyou
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GobangRoom extends BaseRoom<GobangPlayer> {
    private int[][] board = null;
    private int score = 0;
    private int step = -1;
    private int lastScore = 0;
    private List<Step> steps = new ArrayList<>();
    private Ai ai = new Ai(this);
    private RoomMode mode;
    private Step lastStep;
    int count;
    int count2;
    int total = 0;
    private Zobrist zobrist = new Zobrist();
    private int[][] comScore;
    private int[][] humScore;
    private Object evaluateCache;
    private List<Step> currentSteps = new ArrayList<>();
    private List<Step> allSteps = new ArrayList<>();
    private List<Step> stepsTail = new ArrayList<>();
    private int[][][][] scoreCache;
    private int comMaxScore;
    private int humMaxScore;
    private String boardImage;

    public GobangRoom(@NotNull String id) {
        super(id, "五子棋", 2, Game.GameType.GOBANG, new GobangGameManager());
    }

    public boolean canStart() {
        return playerList.stream().filter(item -> Objects.equals(item.getStatus(), LandlordsPlayerGameStatus.READY)).count() == GobangGameManager.MAX_PLAYER_COUNT;
    }

    public boolean playWithBot() {
        return mode == RoomMode.PLAY_WITH_BOT;
    }

    public void start() {
        if (playWithBot()) {
            playerList.get(0).setPre(playerList.get(0));
            playerList.get(0).setNext(playerList.get(0));
        } else {
            for (int i = 0; i < playerList.size(); i++) {
                GobangPlayer player = playerList.get(i);
                player.setPre(playerList.get(i + 1 == maxPlayerCount ? 0 : i + 1));
                player.setNext(playerList.get(i - 1 == -1 ? maxPlayerCount - 1 : i - 1));
            }
        }
    }

    public void addStep(Step step) {
        allSteps.add(step);
    }

    public String getBoardImage() {
        String boardStr = Arrays.deepToString(this.board);
        String md5 = Encryption.encrypByMd5(boardStr);
        boardImage = gameManager.getTempPath() + md5 + ".jpg";
        CommandUtil.exec("python", gameManager.getTempPath() + "generateBoard.py", gameManager.getTempPath() + "board.jpg", boardImage, boardStr, lastStep == null ? null : lastStep.toString());
        return CatUtil.getImage(boardImage).toString();
    }

    public void sendBoard() {
        String boardStr = Arrays.deepToString(this.board);
        String md5 = Encryption.encrypByMd5(boardStr);
        boardImage = gameManager.getTempPath() + md5 + ".jpg";
        String imgCat = CatUtil.getImage(boardImage).toString();
        if (!new File(boardImage).exists()) {
            imgCat = getBoardImage();
        }
        if (playWithBot()) {
            GobangNotifyUtil.notifyPlayer(playerList.get(0), imgCat);
        } else {
            GobangNotifyUtil.notifyRoom(this, imgCat);
        }
    }

    public void init() {
        zobrist.init();
        allSteps.clear();
        int size = 15;
        this.count = 0;
        if (board != null) {
            size = board.length;
            for (int[] ints : board) {
                this.count += java.util.Arrays.stream(ints).filter(d -> d > 0).count();
            }
        } else {
            board = new int[15][15];
            for (int i = 0; i < size; i++) {
                int[] row = new int[size];
                for (int j = 0; j < size; j++) {
                    row[j] = 0;
                }
                board[i] = row;
            }
        }

        // 存储双方得分
        this.comScore = BoardUtil.create(size, size);
        this.humScore = BoardUtil.create(size, size);

        // scoreCache[role][dir][row][column]
        this.scoreCache = new int[][][][]{
                new int[size][size][size],
                new int[][][]{
                        BoardUtil.create(size, size),
                        BoardUtil.create(size, size),
                        BoardUtil.create(size, size),
                        BoardUtil.create(size, size)
                },
                new int[][][]{
                        BoardUtil.create(size, size),
                        BoardUtil.create(size, size),
                        BoardUtil.create(size, size),
                        BoardUtil.create(size, size)}
        };

        this.initScore();

    }

    /**
     * 冲四的分其实肯定比活三高，但是如果这样的话容易形成盲目冲四的问题，所以如果发现电脑有无意义的冲四，则将分数降低到和活三一样
     * 而对于冲四 活三这种杀棋，则将分数提高。
     */
    public int fixScore(int type) {
        if (type < Score.FOUR && type >= Score.BLOCKED_FOUR) {

            if (type < Score.BLOCKED_FOUR + Score.THREE) {
                //单独冲四，意义不大
                return Score.THREE;
            } else if (type < Score.BLOCKED_FOUR * 2) {
                //冲四活三，比双三分高，相当于自己形成活四
                return Score.FOUR;
            } else {
                //双冲四 比活四分数也高
                return Score.FOUR * 2;
            }
        }
        return type;
    }

    public boolean starTo(Step point, List<Step> points) {
        if (points == null || points.size() == 0) {
            return true;
        }
        for (Step b : points) {
            // 距离必须在5步以内
            if ((Math.abs(point.getX() - b.getX()) > 4 || Math.abs(point.getY() - b.getY()) > 4)) {
                return true;
            }
            // 必须在米子方向上
            //noinspection AlibabaAvoidNegationOperator
            if (!(point.getX() == b.getX() || point.getY() == b.getY() || (Math.abs(point.getX() - b.getX()) == Math.abs(point.getY() - b.getY())))) {
                return true;
            }
        }
        return false;
    }

    public void initScore() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                // 空位，对双方都打分
                if (board[i][j] == Role.EMPTY) {
                    if (this.hasNeighbor(i, j, 2, 2)) {
                        //必须是有邻居的才行
                        int cs = EvaluatePoint.scorePoint(this, i, j, Role.BLACK, 0);
                        int hs = EvaluatePoint.scorePoint(this, i, j, Role.WHITE, 0);
                        this.comScore[i][j] = cs;
                        this.humScore[i][j] = hs;
                    }

                } else if (board[i][j] == Role.BLACK) {
                    // 对电脑打分，玩家此位置分数为0
                    this.comScore[i][j] = EvaluatePoint.scorePoint(this, i, j, Role.BLACK, 0);
                    this.humScore[i][j] = 0;
                } else if (board[i][j] == Role.WHITE) {
                    // 对玩家打分，电脑位置分数为0
                    this.humScore[i][j] = EvaluatePoint.scorePoint(this, i, j, Role.WHITE, 0);
                    this.comScore[i][j] = 0;
                }
            }
        }
    }

    /**
     * 只更新一个点附近的分数
     * 参见 evaluate point 中的代码，为了优化性能，在更新分数的时候可以指定只更新某一个方向的分数
     */
    public void updateScore(Step p) {
        int radius = 4;
        int len = board.length;

        // 无论是不是空位 都需要更新
        // -
        for (int i = -radius; i <= radius; i++) {
            int x = p.getX(), y = p.getY() + i;
            if (y < 0) {
                continue;
            }
            if (y >= len) {
                break;
            }
            update(x, y, 0);
        }

        // |
        for (int i = -radius; i <= radius; i++) {
            int x = p.getX() + i, y = p.getY();
            if (x < 0) {
                continue;
            }
            if (x >= len) {
                break;
            }
            update(x, y, 1);
        }

        // \
        for (int i = -radius; i <= radius; i++) {
            int x = p.getX() + i, y = p.getY() + i;
            if (x < 0 || y < 0) {
                continue;
            }
            if (x >= len || y >= len) {
                break;
            }
            update(x, y, 2);
        }

        // /
        for (int i = -radius; i <= radius; i++) {
            int x = p.getX() + i, y = p.getY() - i;
            if (x < 0 || y < 0) {
                continue;
            }
            if (x >= len || y >= len) {
                continue;
            }
            update(x, y, 3);
        }


    }

    public void update(int x, int y, int dir) {
        int role = board[x][y];
        if (role != Role.reverse(Role.BLACK)) {
            int cs = EvaluatePoint.scorePoint(this, x, y, Role.BLACK, dir);
            this.comScore[x][y] = cs;
        } else {
            this.comScore[x][y] = 0;
        }
        if (role != Role.reverse(Role.WHITE)) {
            int hs = EvaluatePoint.scorePoint(this, x, y, Role.WHITE, dir);
            this.humScore[x][y] = hs;
        } else {
            this.humScore[x][y] = 0;
        }

    }

    /**
     * 下子
     */
    public void put(Step p, int role) {
        p.setRole(role);
        board[p.getX()][p.getY()] = role;
        this.zobrist.go(p.getX(), p.getY(), role);
        this.updateScore(p);
        this.allSteps.add(p);
        this.currentSteps.add(p);
        this.stepsTail = new ArrayList<>();
        this.count++;
    }

    /**
     * 移除棋子
     */
    public void remove(Step p) {
        int r = board[p.getX()][p.getY()];
        this.zobrist.go(p.getX(), p.getY(), r);
        board[p.getX()][p.getY()] = Role.EMPTY;
        this.updateScore(p);
        this.allSteps.remove(this.allSteps.size() - 1);
        this.currentSteps.remove(this.currentSteps.size() - 1);
        this.count--;
    }

    /**
     * 悔棋
     */
    public void backward() {
        if (this.allSteps.size() < 2) {
            return;
        }
        int i = 0;
        while (i < 2) {
            Step s = this.allSteps.get(this.allSteps.size() - 1);
            this.remove(s);
            this.stepsTail.add(s);
            i++;
        }
    }

    /**
     * 前进
     */
    public void forward() {
        if (this.stepsTail.size() < 2) {
            return;
        }
        int i = 0;
        while (i < 2) {
            Step s = this.stepsTail.remove(this.stepsTail.size() - 1);
            this.put(s, s.getRole());
            i++;
        }
    }


    /**
     * 棋面估分
     * 这里只算当前分，而不是在空位下一步之后的分
     */
    public int evaluate(int role) {

        //这里加了缓存，但是并没有提升速度
        // if(Config.cache && this.evaluateCache[this.zobrist.code]) return this.evaluateCache[this.zobrist.code]

        // 这里都是用正整数初始化的，所以初始值是0
        this.comMaxScore = 0;
        this.humMaxScore = 0;

        //遍历出最高分，开销不大
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == Role.BLACK) {
                    this.comMaxScore += fixScore(this.comScore[i][j]);
                } else if (board[i][j] == Role.WHITE) {
                    this.humMaxScore += fixScore(this.humScore[i][j]);
                }
            }
        }
        // 有冲四延伸了，不需要专门处理冲四活三
        // 不过这里做了这一步，可以减少电脑胡乱冲四的毛病
        //this.comMaxScore = fixScore(this.comMaxScore)
        //this.humMaxScore = fixScore(this.humMaxScore)
        // if (Config.cache) this.evaluateCache[this.zobrist.code] = result

        return (role == Role.BLACK ? 1 : -1) * (this.comMaxScore - this.humMaxScore);

    }

    //启发函数
    /*
     * 变量starBread的用途是用来进行米子计算
     * 所谓米子计算，只是，如果第一步尝试了一个位置A，那么接下来尝试的位置有两种情况：
     * 1: 大于等于活三的位置
     * 2: 在A的米子位置上
     * 注意只有对小于活三的棋才进行starSpread优化
     */

    /*
     * gen 函数的排序是非常重要的，因为好的排序能极大提升AB剪枝的效率。
     * 而对结果的排序，是要根据role来的
     */

    @SuppressWarnings({"DuplicatedCode", "AlibabaMethodTooLong", "AlibabaAvoidComplexCondition"})
    public List<Step> gen(int role, boolean onlyThrees, boolean starSpread) {
        if (mode == RoomMode.PLAY_WITH_HUMAN) {
            return Collections.singletonList(lastStep);
        }
        if (this.count <= 0) {
            return new ArrayList<Step>() {{
                add(new Step(7, 7));
            }};
        }

        List<Step> fives = new ArrayList<>();
        List<Step> comfours = new ArrayList<>();
        List<Step> humfours = new ArrayList<>();
        List<Step> comblockedfours = new ArrayList<>();
        List<Step> humblockedfours = new ArrayList<>();
        List<Step> comtwothrees = new ArrayList<>();
        List<Step> humtwothrees = new ArrayList<>();
        List<Step> comthrees = new ArrayList<>();
        List<Step> humthrees = new ArrayList<>();
        List<Step> comtwos = new ArrayList<>();
        List<Step> humtwos = new ArrayList<>();
        List<Step> neighbors = new ArrayList<>();

        int reverseRole = Role.reverse(role);
        // 找到双方的最后进攻点
        List<Step> attackPoints = new ArrayList<>(); // 进攻点
        List<Step> defendPoints = new ArrayList<>(); // 防守点


        // 默认情况下 我们遍历整个棋盘。但是在开启star模式下，我们遍历的范围就会小很多
        // 只需要遍历以两个点为中心正方形。
        // 注意除非专门处理重叠区域，否则不要把两个正方形分开算，因为一般情况下这两个正方形会有相当大的重叠面积，别重复计算了
        if (starSpread) {

            int i = this.currentSteps.size() - 1;
            while (i >= 0) {
                Step p = this.currentSteps.get(i);
                if (reverseRole == Role.BLACK && p.getScoreBot() >= Score.THREE || reverseRole == Role.WHITE && p.getScoreHuman() >= Score.THREE) {
                    defendPoints.add(p);
                    break;
                }
                i -= 2;
            }

            i = this.currentSteps.size() - 2;
            while (i >= 0) {
                Step p = this.currentSteps.get(i);
                if (role == Role.BLACK && p.getScoreBot() >= Score.THREE
                        || role == Role.WHITE && p.getScoreHuman() >= Score.THREE) {
                    attackPoints.add(p);
                    break;
                }
                i -= 2;
            }

            if (attackPoints.size() == 0) {
                attackPoints.add(this.currentSteps.get(0).getRole() == role ? this.currentSteps.get(0) : this.currentSteps.get(1));
            }
            if (defendPoints.size() == 0) {
                defendPoints.add(this.currentSteps.get(0).getRole() == reverseRole ? this.currentSteps.get(0) : this.currentSteps.get(1));
            }
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == Role.EMPTY) {

                    if (this.allSteps.size() < 6) {
                        if (!this.hasNeighbor(i, j, 1, 1)) {
                            continue;
                        }
                    } else if (!this.hasNeighbor(i, j, 2, 2)) {
                        continue;
                    }

                    int scoreHum = this.humScore[i][j];
                    int scoreCom = this.comScore[i][j];
                    int maxScore = Math.max(scoreCom, scoreHum);

                    if (onlyThrees && maxScore < Score.THREE) {
                        continue;
                    }

                    Step p = new Step(i, j);
                    p.setScoreHuman(scoreHum);
                    p.setScoreBot(scoreCom);
                    p.setScore(maxScore);
                    p.setRole(role);

                    total++;
                    /* 双星延伸，以提升性能
                     * 思路：每次下的子，只可能是自己进攻，或者防守对面（也就是对面进攻点）
                     * 我们假定任何时候，绝大多数情况下进攻的路线都可以按次序连城一条折线，那么每次每一个子，一定都是在上一个己方棋子的八个方向之一。
                     * 因为既可能自己进攻，也可能防守对面，所以是最后两个子的米子方向上
                     * 那么极少数情况，进攻路线无法连成一条折线呢?很简单，我们对前双方两步不作star限制就好，这样可以 兼容一条折线中间伸出一段的情况
                     */
                    if (starSpread) {
                        if (maxScore < Score.FOUR && (maxScore < Score.BLOCKED_FOUR || starTo(this.currentSteps.get(this.currentSteps.size() - 1), new ArrayList<>()))) {
                            if (starTo(p, attackPoints) && starTo(p, defendPoints)) {
                                count2++;
                                continue;
                            }
                        }
                    }

                    if (scoreCom >= Score.FIVE) {//先看电脑能不能连成5
                        fives.add(p);
                    } else if (scoreHum >= Score.FIVE) {//再看玩家能不能连成5
                        //别急着返回，因为遍历还没完成，说不定电脑自己能成五。
                        fives.add(p);
                    } else if (scoreCom >= Score.FOUR) {
                        comfours.add(p);
                    } else if (scoreHum >= Score.FOUR) {
                        humfours.add(p);
                    } else if (scoreCom >= Score.BLOCKED_FOUR) {
                        comblockedfours.add(p);
                    } else if (scoreHum >= Score.BLOCKED_FOUR) {
                        humblockedfours.add(p);
                    } else if (scoreCom >= 2 * Score.THREE) {
                        //能成双三也行
                        comtwothrees.add(p);
                    } else if (scoreHum >= 2 * Score.THREE) {
                        humtwothrees.add(p);
                    } else if (scoreCom >= Score.THREE) {
                        comthrees.add(p);
                    } else if (scoreHum >= Score.THREE) {
                        humthrees.add(p);
                    } else if (scoreCom >= Score.TWO) {
                        comtwos.add(0, p);
                    } else if (scoreHum >= Score.TWO) {
                        humtwos.add(0, p);
                    } else {
                        neighbors.add(p);
                    }
                }
            }
        }

        //如果成五，是必杀棋，直接返回
        if (fives.size() > 0) {
            return fives;
        }

        // 自己能活四，则直接活四，不考虑冲四
        if (role == Role.BLACK && comfours.size() > 0) {
            return comfours;
        }
        if (role == Role.WHITE && humfours.size() > 0) {
            return humfours;
        }

        // 对面有活四冲四，自己冲四都没，则只考虑对面活四 （此时对面冲四就不用考虑了)

        if (role == Role.BLACK && humfours.size() > 0 && comblockedfours.size() == 0) {
            return humfours;
        }
        if (role == Role.WHITE && comfours.size() > 0 && humblockedfours.size() == 0) {
            return comfours;
        }

        // 对面有活四自己有冲四，则都考虑下
        List<Step> fours = role == Role.BLACK ? new ArrayList<Step>(comfours) {{
            addAll(humfours);
        }} : new ArrayList<Step>(humfours) {{
            addAll(comfours);
        }};
        List<Step> blockedfours = role == Role.BLACK ? new ArrayList<Step>(comblockedfours) {{
            addAll(humblockedfours);
        }} : new ArrayList<Step>(humblockedfours) {{
            addAll(comblockedfours);
        }};
        if (fours.size() > 0) {
            return new ArrayList<Step>(fours) {{
                addAll(blockedfours);
            }};
        }

        List<Step> result = new ArrayList<>();
        if (role == Role.BLACK) {
            result = new ArrayList<Step>(comtwothrees) {{
                addAll(humtwothrees);
                addAll(comblockedfours);
                addAll(humblockedfours);
                addAll(comthrees);
                addAll(humthrees);
            }};
        }
        if (role == Role.WHITE) {
            result = new ArrayList<Step>(humtwothrees) {{
                addAll(comtwothrees);
                addAll(humblockedfours);
                addAll(comblockedfours);
                addAll(humthrees);
                addAll(comthrees);
            }};
        }

        // result.sort(function(a, b) { return b.score - a.score })

        //双三很特殊，因为能形成双三的不一定比一个活三强
        if (comtwothrees.size() > 0 || humtwothrees.size() > 0) {
            return result;
        }


        // 只返回大于等于活三的棋
        if (onlyThrees) {
            return result;
        }


        final List<Step> twos;
        if (role == Role.BLACK) {
            twos = new ArrayList<Step>(comtwos) {{
                addAll(humtwos);
            }};
        } else {
            twos = new ArrayList<Step>(humtwos) {{
                addAll(comtwos);
            }};
        }

        twos.sort((a, b) -> b.getScore() - a.getScore());
        result = new ArrayList<Step>(result) {{
            addAll(twos.size() > 0 ? twos : neighbors);
        }};

        //这种分数低的，就不用全部计算了
        if (result.size() > 20) {
            return result.subList(0, 20);
        }

        return result;
    }

    private boolean hasNeighbor(int x, int y, int distance, int count) {
        int len = board.length;
        int startX = x - distance;
        int endX = x + distance;
        int startY = y - distance;
        int endY = y + distance;
        for (int i = startX; i <= endX; i++) {
            if (i < 0 || i >= len) {
                continue;
            }
            for (int j = startY; j <= endY; j++) {
                if (j < 0 || j >= len) {
                    continue;
                }
                if (i == x && j == y) {
                    continue;
                }
                if (board[i][j] != Role.EMPTY) {
                    count--;
                    if (count <= 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

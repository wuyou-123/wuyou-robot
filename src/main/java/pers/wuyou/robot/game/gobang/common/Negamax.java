package pers.wuyou.robot.game.gobang.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import pers.wuyou.robot.game.gobang.entity.GobangRoom;
import pers.wuyou.robot.game.gobang.entity.Role;
import pers.wuyou.robot.game.gobang.entity.Score;
import pers.wuyou.robot.game.gobang.entity.Step;
import pers.wuyou.robot.game.gobang.util.MathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wuyou
 * @date 2022/3/31 18:29
 */
@Slf4j
public class Negamax {
    private final GobangRoom room;
    private static final int MAX = Score.FIVE * 10;
    private static final int MIN = -1 * MAX;
    /**
     * 每次思考的节点数
     */
    int count = 0;
    /**
     * AB剪枝次数
     */
    int abCut;
    /**
     * zobrist缓存节点数
     */
    int cacheCount = 0;
    /**
     * zobrist缓存命中数量
     */
    int cacheGet = 0;

    private final Map<Integer, Step> Cache = new HashMap<>();

    long start;

    public Negamax(GobangRoom room) {
        this.room = room;
    }

    /**
     * max min search
     * white is max, black is min
     */
    private int negamax(List<Step> candidates, int role, int deep) {
        int alpha = MIN;
        count = 0;
        abCut = 0;
        room.setCurrentSteps(new ArrayList<>());

        for (Step p : candidates) {
            room.put(p, role);
            List<Step> steps = new ArrayList<>();
            steps.add(p);
            Step v = r(deep - 1, -MAX, -alpha, Role.reverse(role), 1, steps, 0);
            v.setScore(v.getScore() * -1);
            alpha = Math.max(alpha, v.getScore());
            room.remove(p);
            p.setV(v);

            // 超时判定
            if (System.currentTimeMillis() - start > 100 * 1000) {
                log.warn("timeout...");
                break; // 超时，退出循环
            }
        }
        log.info("迭代完成,deep=" + deep);
        return alpha;
    }

    private Step r(int deep, int alpha, int beta, int role, int step, List<Step> steps, int spread) {
        Step c = Cache.get(room.getZobrist().getCode());
        if (c != null) {
            // 如果缓存中的结果搜索深度不比当前小，则结果完全可用
            if (c.getDeep() >= deep) {
                cacheGet++;
                // 记得clone，因为这个分数会在搜索过程中被修改，会使缓存中的值不正确
                Step result = new Step();
                BeanUtils.copyProperties(c, result, Step.class);
                result.setStep(step + c.getStep());
                return result;
            }
        }

        int evaluate = room.evaluate(role);
        Step leaf = new Step();
        leaf.setScore(evaluate);
        leaf.setStep(step);
        leaf.setSteps(steps);

        count++;
        // 搜索到底 或者已经胜利
        // 注意这里是小于0，而不是1，因为本次直接返回结果并没有下一步棋
        if (deep <= 0 || MathUtil.greatOrEqualThan(evaluate, Score.FIVE) || MathUtil.littleOrEqualThan(evaluate, -Score.FIVE)) {
            return leaf;
        }
        Step best = new Step();
        best.setScore(MIN);
        best.setStep(step);
        best.setSteps(steps);
        // 双方个下两个子之后，开启star spread 模式
        List<Step> points = room.gen(role, room.getCount() > 10 ? step > 1 : step > 3, step > 1);

        if (points.size() == 0) {
            return leaf;
        }

        for (Step p : points) {
            room.put(p, role);
            int deep1 = deep - 1;
            int spread1 = spread;
            if (spread1 < 1) {
                // 冲四延伸
                //noinspection AlibabaAvoidComplexCondition
                if ((role == Role.BLACK && p.getScoreHuman() >= Score.FIVE) || (role == Role.WHITE && p.getScoreBot() >= Score.FIVE)) {
                    // deep1 = deep+1
                    deep1 += 2;
                    spread1++;
                }
                // 单步延伸策略：双三延伸
                //if ( (role == Role.com && p.getScore()Com >= Score.THREE * 2) || (role == Role.hum && p.getScore()Hum >= Score.THREE*2)) {
                //  deep1 = deep
                //  spread1 ++
                //}
            }

            List<Step> steps1 = new ArrayList<>(steps);
            steps1.add(p);
            Step v = r(deep1, -beta, -alpha, Role.reverse(role), step + 1, steps1, spread1);
            v.setScore(v.getScore() * -1);
            room.remove(p);


            // 注意，这里决定了剪枝时使用的值必须比MAX小
            if (v.getScore() > best.getScore()) {
                best = v;
            }
            alpha = Math.max(best.getScore(), alpha);
            //AB 剪枝
            // 这里不要直接返回原来的值，因为这样上一层会以为就是这个分，实际上这个节点直接剪掉就好了，根本不用考虑，也就是直接给一个很大的值让他被减掉
            // 这样会导致一些差不多的节点都被剪掉，但是没关系，不影响棋力
            // 一定要注意，这里必须是 greatThan 即 明显大于，而不是 greatOrEqualThan 不然会出现很多差不多的有用分支被剪掉，会出现致命错误
            if (MathUtil.greatOrEqualThan(v.getScore(), beta)) {
                abCut++;
                // 被剪枝的，直接用一个极大值来记录，但是注意必须比MAX小
                v.setScore(MAX - 1);
                // 剪枝标记
                v.setCut(true);
                // cache(deep, v) // 别缓存被剪枝的，而且，这个返回到上层之后，也注意都不要缓存
                return v;
            }
        }

        cache(deep, best);
        return best;
    }

    private void cache(int deep, Step score) {
        if (score.isCut()) {
            return; // 被剪枝的不要缓存哦，因为分数是一个极值
        }
        // 记得clone，因为score在搜索的时候可能会被改的，这里要clone一个新的
        Step obj = new Step();
        BeanUtils.copyProperties(score, obj, Step.class);
        obj.setDeep(deep);
        Cache.put(room.getZobrist().getCode(), obj);
        cacheCount++;
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    private Step deeping(List<Step> candidates, int role, int deep) {

        start = System.currentTimeMillis();
        Cache.clear(); // 每次开始迭代的时候清空缓存。这里缓存的主要目的是在每一次的时候加快搜索，而不是长期存储。事实证明这样的清空方式对搜索速度的影响非常小（小于10%)

        int bestScore;
        for (int i = 2; i <= deep; i += 2) {
            bestScore = negamax(candidates, role, i);
            //// 每次迭代剔除必败点，直到没有必败点或者只剩最后一个点
            //// 实际上，由于必败点几乎都会被AB剪枝剪掉，因此这段代码几乎不会生效
            //var newCandidates = candidates.filter(function (d) {
            //  return !d.abcut
            //})
            //candidates = newCandidates.length ? newCandidates : [candidates[0]] // 必败了，随便走走

            if (MathUtil.greatOrEqualThan(bestScore, Score.FIVE)) {
                break; // 能赢了
            }
            // 下面这样做，会导致上一层的分数，在这一层导致自己被剪枝的bug，因为我们的判断条件是 >=， 上次层搜到的分数，在更深一层搜索的时候，会因为满足 >= 的条件而把自己剪枝掉
            // if (math.littleThan(bestScore, T.THREE * 2)) bestScore = MIN // 如果能找到双三以上的棋，则保留bestScore做剪枝，否则直接设置为最小值
        }

        // 美化一下
        candidates = candidates.stream().map((d) -> {
            Step r = new Step(d.getX(), d.getY());
            r.setScore(d.getV().getScore());
            r.setStep(d.getV().getStep());
            r.setSteps(d.getV().getSteps());
            r.setRole(d.getRole());
            return r;
        }).collect(Collectors.toList());

        // 排序
        // 经过测试，这个如果放在上面的for循环中（就是每次迭代都排序），反而由于迭代深度太浅，排序不好反而会降低搜索速度。
        candidates.sort((a, b) -> {
            if (MathUtil.equal(a.getScore(), b.getScore())) {
                // 大于零是优势，尽快获胜，因此取步数短的
                // 小于0是劣势，尽量拖延，因此取步数长的
                if (a.getScore() >= 0) {
                    if (a.getStep() != b.getStep()) {
                        return a.getStep() - b.getStep();
                    } else {
                        return b.getScore() - a.getScore(); // 否则 选取当前分最高的（直接评分)
                    }
                } else {
                    if (a.getStep() != b.getStep()) {
                        return b.getStep() - a.getStep();
                    } else {
                        return b.getScore() - a.getScore(); // 否则 选取当前分最高的（直接评分)
                    }
                }
            } else {
                return (b.getScore() - a.getScore());
            }
        });

        Step result = candidates.get(0);
        room.setLastStep(result);
        double time = (System.currentTimeMillis() - start) / 1000.0;
        int min = result.getSteps().stream().mapToInt(Step::getScore).min().orElse(0);
        log.info("选择节点：" + result + ", 分数:" + result.getScore() + ", 步数:" + result.getStep() + ", 最小值：" + min);
        log.info("搜索节点数:" + count + ",AB剪枝次数:" + abCut);
        // 注意，减掉的节点数实际远远不止 ABcut 个，因为减掉的节点的子节点都没算进去。实际 4W个节点的时候，剪掉了大概 16W个节点
        log.info("当前统计：" + count + "个节点, 耗时:" + time + "s, NPS:" + count / time + "N/S");
        return result;
    }

    public Step deepAll(int role) {
        List<Step> candidates = room.gen(role, false, false);
        return deeping(candidates, role, room.playWithBot() ? 10 : 2);

    }
}

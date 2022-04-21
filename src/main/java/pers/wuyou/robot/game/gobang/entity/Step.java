package pers.wuyou.robot.game.gobang.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyou
 */
@Data
public class Step {
    private int x;
    private int y;
    private int role;
    private int scoreHuman;
    private int scoreBot;
    private int maxScore;
    private int score;
    private int step;
    private int deep;
    private boolean cut;
    private Step v = null;
    private List<Step> steps = new ArrayList<>();

    public Step() {
    }

    public Step(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Step(int x, int y, int role) {
        this.x = x;
        this.y = y;
        this.role = role;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

}

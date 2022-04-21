package pers.wuyou.robot.game.gobang.common;

import lombok.Data;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.game.gobang.entity.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyou
 * @date 2022/3/31 15:37
 */
@Data
public class Zobrist {

    private List<Integer> com = new ArrayList<>();
    private List<Integer> hum = new ArrayList<>();
    private int size = 15;
    private int code;

    public void init() {
        this.com.clear();
        this.hum.clear();
        for (int i = 0; i < this.size * this.size; i++) {
            this.com.add(this.rand());
            this.hum.add(this.rand());
        }
        this.code = this.rand();
    }

    private int rand() {
        return RobotCore.getRANDOM().nextInt(1000000000);
    }

    public void go(int x, int y, int role) {
        int index = this.size * x + y;
        this.code ^= (role == Role.BLACK ? this.com.get(index) : this.hum.get(index));
    }

}

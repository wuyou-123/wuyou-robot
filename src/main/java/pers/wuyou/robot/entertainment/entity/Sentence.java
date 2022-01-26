package pers.wuyou.robot.entertainment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 戳一戳发送的内容
 * sentence
 *
 * @author wuyou
 */
@Data
@NoArgsConstructor
public class Sentence implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String text;

    public Sentence(String text) {
        this.text = text;
    }
}
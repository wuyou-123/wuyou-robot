package pers.wuyou.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 群开关机状态
 *
 * @author wuyou
 * @since 2021-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupBootState implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 群号
     */
    private String groupCode;

    /**
     * 开关机状态
     */
    private Boolean state;


}

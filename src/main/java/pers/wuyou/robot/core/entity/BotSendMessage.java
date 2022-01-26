package pers.wuyou.robot.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * bot发送的消息实体类
 *
 * @author wuyou
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BotSendMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * flag
     */
    private String flag;
    /**
     * 消息内容
     */
    @NotNull
    @TableField("message")
    private String content;
    /**
     * 发送消息的bot账号
     */
    @NotNull
    private String botCode;
    /**
     * 目标账号,群号或者QQ号
     */
    @NotNull
    private String targetCode;
    /**
     * 是否已发送
     */
    private Boolean isSent;
    /**
     * 发送类型,群或者私聊
     */
    @NotNull
    private String sendType;
    /**
     * 发送时间
     */
    private Date sendTime;
}
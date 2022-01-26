package pers.wuyou.robot.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MessageRecallEventGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息实体类
 *
 * @author wuyou
 */
@Data
@NoArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 消息的id
     */
    @NotNull
    private String messageId;

    /**
     * 发送消息的人的QQ号
     */
    @NotNull
    private String accountCode;

    /**
     * 发送消息的群号
     */
    private String groupCode;

    /**
     * 消息的flag
     */
    @NotNull
    private String flag;

    /**
     * 消息类型
     */
    @NotNull
    private Type type;

    /**
     * 消息文本(不包含猫猫码)
     */
    @NotNull
    private String messageText;

    /**
     * 消息内容
     */
    @NotNull
    @TableField("message")
    private String content;

    /**
     * 发送时间
     */
    @NotNull
    private Date sendTime;

    /**
     * 是否已撤回
     */
    @NotNull
    private Boolean isRecall;

    /**
     * 撤回的时间
     */
    private Date recallTime;

    /**
     * 撤回消息账号
     */
    private String recallAccountCode;

    public Message(MessageGet msgGet) {
        this.content = msgGet.getMsg();
        this.messageText = msgGet.getText();
        this.flag = msgGet.getFlag().getFlag().getId();
        this.accountCode = msgGet.getAccountInfo().getAccountCode();
        this.isRecall = false;
        this.sendTime = new Date(msgGet.getTime());
        this.messageId = msgGet.getId();
        if (msgGet instanceof GroupMsg) {
            this.groupCode = ((GroupMsg) msgGet).getGroupInfo().getGroupCode();
            this.type = Type.valueOf(((GroupMsg) msgGet).getGroupMsgType().name());
            return;
        }

        if (msgGet instanceof PrivateMsg) {
            this.type = Type.valueOf(((PrivateMsg) msgGet).getPrivateMsgType().name());
            return;
        }
        throw new IllegalArgumentException();
    }

    public void recall(MessageRecallEventGet msgGet) {
        if (msgGet.getOperatorInfo() != null) {
            this.recallAccountCode = msgGet.getOperatorInfo().getAccountCode();
        }
        this.isRecall = true;
        this.recallTime = new Date(msgGet.getRecallTime());
    }

    @SuppressWarnings("unused")
    enum Type {
        /**
         * 好友消息
         */
        FRIEND,
        /**
         * 来自群的临时会话
         */
        GROUP_TEMP,
        /**
         * 自己
         */
        SELF,
        /**
         * 其他
         */
        OTHER,
        /**
         * 普通消息
         */
        NORMAL,
        /**
         * 匿名消息
         */
        ANON,
        /**
         * 系统消息
         */
        SYS

    }

}
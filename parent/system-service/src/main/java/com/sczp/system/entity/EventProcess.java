package com.sczp.system.entity;

import com.sczp.system.moudl.EventStatus;
import com.sczp.system.moudl.EventType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name="Event_Process")
@DynamicInsert    /*插入时只插入非null属性，其他取数据库默认值*/
@DynamicUpdate
public class EventProcess extends BaseEntity{

    @NotNull
    @Column(unique=true)
    @ApiModelProperty("全局唯一ID, 例如UUID。")
    private String eventID;

    @NotNull
    @ApiModelProperty("事件状态, 枚举类型，现在只有两个状态: 待发布(NEW), 已发布(PUBLISHED)")
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @NotNull
    @ApiModelProperty("事件内容，这里我们会将事件内容转成json存到这个字段里。")
    private String payload;

    @NotNull
    @ApiModelProperty("事件类型, 枚举类型，每个事件都会有一个类型, 比如我们之前提到的创建用户USER_CREATED就是一个事件类型。")
    @Enumerated(EnumType.STRING)
    private EventType eventType;
}

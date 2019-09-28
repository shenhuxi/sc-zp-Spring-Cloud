package com.sczp.order.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 用户
 * @author shixh
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name="S_Order")
@DynamicInsert    /*插入时只插入非null属性，其他取数据库默认值*/
@DynamicUpdate
public class Order extends BaseEntity{

    @NotNull
    @Column(unique=true)
    @ApiModelProperty("订单编号")
    private String orderCode;

    @NotNull
    @ApiModelProperty("商品编号")
    private String serialNumber;

    @NotNull
    @ApiModelProperty("客户id")
    private Long userId;

    @NotNull
    @ApiModelProperty("物品消费的金额")
    private BigDecimal price;

}

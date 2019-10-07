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
 * @author zengpeng
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name="S_Product")
@DynamicInsert    /*插入时只插入非null属性，其他取数据库默认值*/
@DynamicUpdate
public class Product extends BaseEntity{
    @NotNull
    @ApiModelProperty("名称")
    private String name;

    @NotNull
    @ApiModelProperty("物品金额")
    private BigDecimal price;

    @NotNull
    @ApiModelProperty("库存")
    private Long stocks;

    @NotNull
    @ApiModelProperty("商品编号")
    @Column(unique=true)
    private String serialNumber;

}

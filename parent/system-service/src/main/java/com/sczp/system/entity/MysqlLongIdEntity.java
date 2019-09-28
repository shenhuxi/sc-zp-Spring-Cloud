package com.sczp.system.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * 描述: Mysql数据库的主键生成定义:系统生成自增长整数型数据作为主键
 * 作者: qinzhw
 * 创建时间: 2018/7/16 15:38
 */
@MappedSuperclass
public class MysqlLongIdEntity extends IdEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "ID, 新增时为空,更新时必填", allowEmptyValue=true)
	@Column(columnDefinition = "bigint comment 'ID主键'")
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}

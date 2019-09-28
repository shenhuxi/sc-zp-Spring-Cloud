package com.sczp.order.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * 描述: 实体类的基础公共属性，中间表类、一次性数据等只需要继承MysqlLongIdEntity即可
 * 由于基类，所以字段名取短一点
 * 作者: qinzhw
 * 创建时间: 2018/7/16 15:45
 */
@MappedSuperclass
public class BaseEntity extends MysqlLongIdEntity implements Serializable{

	private static final long serialVersionUID = -4498233384948128317L;

	@ApiModelProperty(value = "创建时间", allowEmptyValue=true)
	@Temporal(TemporalType.TIMESTAMP)
	@org.hibernate.annotations.CreationTimestamp
	@Column(columnDefinition = "timestamp not null comment '创建时间'")
	private Date ct;

	@ApiModelProperty(value = "创建人ID", allowEmptyValue=true)
	@Column(columnDefinition = "bigint comment '创建人ID'")
	private Long cp;

	@ApiModelProperty(value = "创建人名", allowEmptyValue=true)
	@Column(columnDefinition = "varchar(100) comment '创建人名'")
	private String cpName;

	@ApiModelProperty(value = "修改时间", allowEmptyValue=true)
	@Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.UpdateTimestamp
	@Column(columnDefinition = "timestamp default CURRENT_TIMESTAMP comment '修改时间'")
	private Date et;

	@ApiModelProperty(value = "修改人ID", allowEmptyValue=true)
	@Column(columnDefinition = "bigint comment '修改人ID'")
	private Long ep;

	@ApiModelProperty(value = "修改人名", allowEmptyValue=true)
	@Column(columnDefinition = "varchar(100) comment '修改人名'")
	private String epName;

	@ApiModelProperty(value = "删除标志(0-正常，1-删除)", allowEmptyValue=true)
	@Column(columnDefinition = "int(2) default 0 comment '删除标志(0-正常，1-删除)'")
	private int delFlag;


	public Date getCt() {
		return ct;
	}

	public void setCt(Date ct) {
		this.ct = ct;
	}

	public Long getCp() {
		return cp;
	}

	public void setCp(Long cp) {
		this.cp = cp;
	}

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	public Date getEt() {
		return et;
	}

	public void setEt(Date et) {
		this.et = et;
	}

	public Long getEp() {
		return ep;
	}

	public void setEp(Long ep) {
		this.ep = ep;
	}

	public String getEpName() {
		return epName;
	}

	public void setEpName(String epName) {
		this.epName = epName;
	}

	public int getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}
}

package com.sczp.mould.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name="S_ROLE")
@DynamicInsert    /*插入时只插入非null属性，其他取数据库默认值*/
@DynamicUpdate
public class Role extends BaseEntity  implements GrantedAuthority {

    @Column(nullable=false)
    private String name;
    
    @Override
    public String getAuthority() {
        return name;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
    
}
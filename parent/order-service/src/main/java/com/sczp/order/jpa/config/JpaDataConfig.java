package com.sczp.order.jpa.config;

import com.sczp.order.jpa.repository.MyRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


@Configuration
@EnableJpaRepositories(basePackages = {"com.sczp.**.repository"},
                            repositoryFactoryBeanClass = MyRepositoryFactoryBean.class)
@EnableSpringDataWebSupport
public class JpaDataConfig {  
  
}  
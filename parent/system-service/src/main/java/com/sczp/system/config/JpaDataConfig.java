package com.sczp.system.config;

import com.sczp.system.repository.MyRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


@Configuration
@EnableJpaRepositories(basePackages = {"com.sczp.system.repository"},
                            repositoryFactoryBeanClass = MyRepositoryFactoryBean.class)
@EnableSpringDataWebSupport
public class JpaDataConfig {  
  
}  
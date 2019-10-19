package com.sczp.common.aspect;

import com.sczp.common.util.synchronized_util.SimpleDateFormatUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class config {

    @Bean
    SimpleDateFormatUtil simpleDateFormatUtil(){
        return new SimpleDateFormatUtil();
    }
}

package com.qindaorong.edu.config;

import com.qindaorong.edu.framework.RpcRegistry;
import com.qindaorong.edu.framework.RpcServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.qindaorong.edu.service")
public class SpringConfig {

    @Bean(name="rpcRegistry")
    public RpcRegistry rpcRegistry(){
        return new RpcRegistry(8080);
    }
}

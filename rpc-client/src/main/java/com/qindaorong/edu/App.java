package com.qindaorong.edu;

import com.qindaorong.edu.api.IHelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {

        ApplicationContext context=new
                AnnotationConfigApplicationContext(SpringConfig.class);
        RpcProxyClient rpcProxyClient=context.getBean(RpcProxyClient.class);

        IHelloService iHelloService=rpcProxyClient.clientProxy
                (IHelloService.class,"v2.0");

        System.out.println(iHelloService.sayHello(1.0));
    }
}

package com.qindaorong.edu;

import com.qindaorong.edu.discovery.IServiceDiscovery;
import com.qindaorong.edu.discovery.ServiceDiscoveryWithZk;
import com.qindaorong.edu.remote.RpcRequest;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RemoteInvocationHandler implements InvocationHandler {

    private String version;

    IServiceDiscovery serviceDiscovery = new ServiceDiscoveryWithZk();

    public RemoteInvocationHandler(String version) {
        this.version = version;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //请求数据的包装
        RpcRequest rpcRequest=new RpcRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParamTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);
        rpcRequest.setVersion(version);
        String serviceName=rpcRequest.getClassName();
        if(!StringUtils.isEmpty(version)){
            serviceName=serviceName+"-"+version;
        }

        String serverAddress = serviceDiscovery.discovery(serviceName);

        RpcNetTransport netTransport=new RpcNetTransport(serverAddress);
        Object result=netTransport.send(rpcRequest);
        return result;
    }
}

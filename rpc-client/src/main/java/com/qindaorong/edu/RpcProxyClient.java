package com.qindaorong.edu;

import java.lang.reflect.Proxy;

public class RpcProxyClient {

    public <T> T clientProxy(final Class<T> interfaceCls, String version){

        return (T) Proxy.newProxyInstance(interfaceCls.getClassLoader(),
                new Class<?>[]{interfaceCls},new RemoteInvocationHandler(version));
    }
}

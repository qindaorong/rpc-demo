package com.qindaorong.edu.discovery;

public interface IServiceDiscovery {

    /**
     * 根据服务名称返回服务地址
     * @param serviceName
     * @return
     */
    String  discovery (String serviceName);
}

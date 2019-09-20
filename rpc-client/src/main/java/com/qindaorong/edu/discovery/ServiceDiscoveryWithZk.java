package com.qindaorong.edu.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public class ServiceDiscoveryWithZk implements IServiceDiscovery {

    CuratorFramework curatorFramework =null;

    List<String> serviceRepos=new ArrayList<>(); //服务地址的本地缓存

    public static String CONNECTION_STR="192.168.100.16:2181,192.168.100.17:2181,192.168.100.18:2181";

    {
        //初始化zookeeper的连接， 会话超时时间是5s，衰减重试
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(CONNECTION_STR).sessionTimeoutMs(5000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).
                namespace("registry")
                .build();
        curatorFramework.start();
    }

    @Override
    public String discovery(String serviceName) {
        String servicePath="/"+serviceName;
        try {

            //获得注册中心中的所有地址
            if(serviceRepos.isEmpty()){
                serviceRepos =curatorFramework.getChildren().forPath(servicePath);
                registryWatch(servicePath);
            }

            //负载均衡
            LoadBalanceStrategy loadBalance= new  RandomLoadBalance();
            return loadBalance.selectOne(serviceRepos);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void registryWatch(String servicePath) throws Exception {
        PathChildrenCache nodeCache = new PathChildrenCache(curatorFramework,servicePath,true);
        PathChildrenCacheListener listener = (curatorFramework1, pathChildrenCacheEvent)->{
            System.out.println("客户端收到节点变更的事件");
            serviceRepos=curatorFramework1.getChildren().forPath(servicePath);// 再次更新本地的缓存地址
        };
        nodeCache.getListenable().addListener(listener);
        nodeCache.start();
    }
}

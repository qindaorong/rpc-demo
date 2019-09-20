package com.qindaorong.edu.framework;

import com.qindaorong.edu.annotations.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.net.URL;
import io.netty.channel.socket.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServer {

    private static final String ROOT_PACKAGE_NAME="com.qindaorong.edu.service";

    private List<String> classNames = new ArrayList<String>();

    public ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<String,Object>();

    private int port;

    public RpcServer(int port) {
        this.port = port;
        doScannerClass(ROOT_PACKAGE_NAME);
        //doRegister();
    }


    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).
                    childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().
                                    addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null))).
                                    addLast(new ObjectEncoder()).
                                    addLast(new ProcessorHandler(registryMap));
                        }
                    });
            serverBootstrap.bind(port).sync();
            System.out.println("GP RPC Registry start listen at " + port );
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }


    private void doScannerClass(String packageName){
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            //如果是一个文件夹，继续递归
            if(file.isDirectory()){
                doScannerClass(packageName + "." + file.getName());
            }else{
                String className=packageName + "." + file.getName().replace(".class", "").trim();

                try {
                    Class<?> clazz = Class.forName(className);

                    if(clazz.isAnnotationPresent(RpcService.class)){
                        RpcService rpcService = clazz.getAnnotation(RpcService.class);
                        Class<?> i = clazz.getInterfaces()[0];
                        registryMap.put(i.getName()+"-"+rpcService.version(), clazz.newInstance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void doRegister(){
        if(classNames.size() == 0){ return; }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(), clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

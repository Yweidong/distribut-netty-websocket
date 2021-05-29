package com.thought.monkey.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.java.Log;
import com.thought.monkey.handler.protobuf.RemoteNotificationHandler;
import com.thought.monkey.server.distributed.NettyWorker;
import com.thought.monkey.server.distributed.NodeRouter;
import com.thought.monkey.util.FutureTaskScheduler;

import java.util.Scanner;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 */
@Log
public class NettyWebSocketServer {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);//在本地模拟多端口实现伪分布式
        String next = scanner.next();

        new NettyWebSocketServer().run("127.0.0.1",Integer.valueOf(next));
    }

    private void run(String host,int port) {
        NioEventLoopGroup boss = new NioEventLoopGroup();

        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {

            serverBootstrap.group(boss,worker)
                            .channel(NioServerSocketChannel.class)
                            .option(ChannelOption.SO_BACKLOG,1024)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel channel) throws Exception {
                                    ChannelPipeline pipeline = channel.pipeline();

                                    //定义分发器
                                    pipeline.addLast(new ProtocolDispatcher());

                                }
                            });

            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();


            //将服务节点注册到zk上
            NettyWorker.getInstance().setWsNode(host, port);

            FutureTaskScheduler.add(()->{
                log.info("=====开始注册====");

                /**
                 *
                 * 启动节点
                 * */

                NettyWorker.getInstance().init();

                /***
                 *启动节点的管理
                 */
                NodeRouter.getInstance().init();

            });



            channelFuture.channel().closeFuture().sync();




        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }
}

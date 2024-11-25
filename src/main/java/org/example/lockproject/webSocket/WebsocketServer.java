package org.example.lockproject.webSocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


public class WebsocketServer {

    private int port;

    public WebsocketServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//使用NioSocketChannel 作為服務器的通道實現
                    .option(ChannelOption.SO_BACKLOG, 128)//設置線程隊列得到連接數
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//設置保持活動連接狀態
                    .handler(new LoggingHandler(LogLevel.INFO)) // bossGroup 設置日誌
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            //基於http協議 使用http 編碼 解碼器
                            ch.pipeline().addLast(new HttpServerCodec());

                            //以塊方式寫 添加ChunkedWriteHandler處理器
                            ch.pipeline().addLast(new ChunkedWriteHandler());

                            /*
                            * http數據在傳出過程中是分段 HttpObjectAggregator 就是可以將多個段聚合
                            * 當瀏覽器發送大量數據時 就會發出多次http請求
                            */
                            ch.pipeline().addLast(new HttpObjectAggregator(8192));

                            /*
                             * 對印websocket 他的數據是以 偵(frame) 型式傳遞
                             * 可以看到WebSocketFrom 下面有六個子類
                             * 瀏覽器請求時 ws://Localhost:8088/nginx_ticket 表示請求的URI
                             * webSocketServerProtocolHandler 核心功能是將 http 協議升級為 ws 協議 保持長連接
                             *
                             */
                            ch.pipeline().addLast(new WebSocketServerProtocolHandler("/nginx_ticket"));

                            // IdleStateHandler Netty 處理心跳超時事件
                            pipeline.addLast(new IdleStateHandler(60, 30, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new WebsocketServerHandler());

                            pipeline.addLast();

                            //業務處理
                            pipeline.addLast(new WebsocketServerHandler());
                        }
                    });

            System.out.println("ws服務端 啟動...");
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();


        }

    }

    public static void main(String[] args) throws Exception {
        new WebsocketServer(8088).run();

    }

}



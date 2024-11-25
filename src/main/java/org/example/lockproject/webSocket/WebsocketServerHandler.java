package org.example.lockproject.webSocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.time.LocalDateTime;

public class WebsocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        System.out.println("ws服務端收到消息:"+msg.text());

        //回復消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服務器時間:"+ LocalDateTime.now()+msg.text()));


    }

    /**
     * 連接建立 一旦連接 第一個被執行
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //id 表示唯一的值 LongText是唯一的 ShortText不是唯一
        System.out.println(ctx.channel().id().asLongText());
        System.out.println(ctx.channel().id().asShortText());

    }

    /**
     * ChannelHandler 被從 ChannelPipeline 中移除時調用
     * 瀏覽器關閉時觸發
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客戶端關閉..");
        System.out.println(ctx.channel().id().asLongText());
        System.out.println(ctx.channel().id().asShortText());
    }

    /**
     * 處理異常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 心跳檢測 搭配 IdleStateHandler
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.READER_IDLE) {
                // 如果讀超時，可以主動關閉連接或發送心跳
                System.out.println("讀超時，關閉連接");
                ctx.close();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                // 如果寫超時，發送心跳消息
                System.out.println("寫超時，發送心跳");
                ctx.channel().writeAndFlush(new TextWebSocketFrame("ping"));
            } else if (event.state() == IdleState.ALL_IDLE) {
                // 如果讀寫都空閒，可以選擇主動關閉連接
                System.out.println("讀寫空閒，關閉連接");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}

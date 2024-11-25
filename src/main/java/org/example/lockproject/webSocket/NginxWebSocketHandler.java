package org.example.lockproject.webSocket;

import jakarta.annotation.PreDestroy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class NginxWebSocketHandler implements WebSocketHandler {

    private final RabbitTemplate rabbitTemplate;

    //可以用來做重連
//    private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<WebSocketSession, Long> sessionLastHeartbeat = new ConcurrentHashMap<>();

    public NginxWebSocketHandler(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 當連接建立時，記錄心跳時間
        sessionLastHeartbeat.put(session, System.currentTimeMillis());
        sendHeartbeat(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Object payload = message.getPayload();
        if (payload instanceof String) {
            String text = (String) payload;

            if ("ping".equalsIgnoreCase(text)) {
                sessionLastHeartbeat.put(session, System.currentTimeMillis());
                session.sendMessage(new TextMessage("pong"));
            } else {
                rabbitTemplate.convertAndSend("myExchange", "myRoutingKey", text);
                session.sendMessage(new TextMessage("Message sent to RabbitMQ"));
            }
        } else {
            session.sendMessage(new TextMessage("Unsupported payload type"));
        }
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        // 清理關閉的連接
        sessionLastHeartbeat.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        if (exception instanceof IOException) {
            System.err.println("Network error on session " + session.getId() + ": " + exception.getMessage());
        } else {
            System.err.println("Unexpected error on session " + session.getId() + ": " + exception.getMessage());
        }
        sessionLastHeartbeat.remove(session);
        try {
            session.close();
        } catch (IOException e) {
            System.err.println("Error closing session " + session.getId() + ": " + e.getMessage());
        }
    }

    // 定期檢查心跳
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private void sendHeartbeat(WebSocketSession session) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("ping"));

                    // 檢查心跳是否超時
                    Long lastHeartbeat = sessionLastHeartbeat.get(session);
                    if (lastHeartbeat != null && System.currentTimeMillis() - lastHeartbeat > 60000) {
                        session.close();
                        sessionLastHeartbeat.remove(session);
                    }
                } else {
                    sessionLastHeartbeat.remove(session);
                    throw new IllegalStateException("Session is closed");
                }
            } catch (Exception e) {
                // 錯誤處理
                sessionLastHeartbeat.remove(session);
            }
        }, 0, 30, TimeUnit.SECONDS); // 每30秒執行一次
    }

    //在 WebSocket 組件生命周期結束時（比如 Spring Context 銷毀時）關閉 scheduler
    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}

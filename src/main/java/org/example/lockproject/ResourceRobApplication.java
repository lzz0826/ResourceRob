package org.example.lockproject;

import org.example.lockproject.webSocket.WebsocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

@SpringBootApplication
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class ResourceRobApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ResourceRobApplication.class, args);

        new WebsocketServer(8088).run();

        //TODO nginx連MQ斷線重連 JAVA連Redis Listener斷線重連
        //TODO 存mysql 目前測試count錯 但是 mq吐java收到編號到0(待查測試的count前端沒計到而已還是後段也是 需要驗證比數 nginx比數 跟資料庫)  rabbitmq_pool.lua 線程池目前沒法用
        System.out.println("Hello World");
    }

}

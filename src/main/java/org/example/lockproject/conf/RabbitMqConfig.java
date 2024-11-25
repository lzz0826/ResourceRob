package org.example.lockproject.conf;

import org.example.lockproject.mq.enums.NginxQueueEnums;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {


    //----------------------- 單存RabbitMq -------------------


    /**
     * A區
     */
    public static final String QUEUEA_NAME = "queueA";

    public static final String QUEUEB_NAME = "queueB";
    @Bean
    public Queue queueA() {
        return new Queue(QUEUEA_NAME,true);
    }

    /**
     * B區
     */
    @Bean
    public Queue queueB() {
        return new Queue(QUEUEB_NAME,true);
    }


    //----------------------- Nginx + RabbitMq -----------------

    /**
     * nginxQ 包含(A.B區) 通過這後在JAVA在區分存DB (減少 MQ對Nginx STOMP 連接負擔)
     */

    @Bean
    public Queue nginxQ() {
        return new Queue(NginxQueueEnums.NginxQ.name,true);
    }

    /**
     * nginxQA區
     */

    @Bean
    public Queue nginxQa() {
        return new Queue(NginxQueueEnums.NginxQA.name,true);
    }

    /**
     * nginxQB區
     */
    @Bean
    public Queue nginxQB() {
        return new Queue(NginxQueueEnums.NginxQB.name,true);
    }


}

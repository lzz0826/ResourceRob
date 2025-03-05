package org.example.lockproject.conf;

import org.example.lockproject.mq.enums.NginxExchangeEnums;
import org.example.lockproject.mq.enums.NginxQueueEnums;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {



    //----------------------- Nginx + RabbitMq -----------------

    /**
     * nginxQ 包含(A.B區) 通過這後在JAVA在區分存DB (減少 MQ對Nginx STOMP 連接負擔)
     */
    @Bean
    public Queue nginxQ() {
        return QueueBuilder.durable(NginxQueueEnums.nginxQ.name())
                .withArgument("x-dead-letter-exchange", NginxExchangeEnums.deadExchange.name()) // 指定死信交換機
                .withArgument("x-dead-letter-routing-key", NginxExchangeEnums.deadExchange.routingKey)   // 指定死信隊列的 routing key
                .build();
    }

    @Bean
    public Exchange ticketExchange() {
        return ExchangeBuilder.directExchange(NginxExchangeEnums.ticketExchange.name()).durable(true).build();
    }

    @Bean
    public Binding nginxQueueToTicketExchange() {
        return BindingBuilder.bind(nginxQ())
                .to(ticketExchange())
                .with(NginxExchangeEnums.ticketExchange.routingKey)
                .noargs();
    }

    /**
     * nginxQA區
     */

    @Bean
    public Queue nginxQa() {
        return new Queue(NginxQueueEnums.nginxQA.name(),true);
    }

    /**
     * nginxQB區
     */
    @Bean
    public Queue nginxQB() {
        return new Queue(NginxQueueEnums.nginxQB.name(),true);
    }


    //死信交換機
    @Bean
    public Exchange dlxExchange() {
        return ExchangeBuilder.directExchange(NginxExchangeEnums.deadExchange.name()).durable(true).build();
    }

    //死信隊列
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(NginxQueueEnums.nginxQDlx.name()).build();
    }


    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with(NginxExchangeEnums.deadExchange.routingKey)
                .noargs();
    }


    //----------------------- 單純RabbitMq -------------------

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


}

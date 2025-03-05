package org.example.lockproject.mq.enums;

import io.micrometer.common.util.StringUtils;

public enum NginxExchangeEnums {

    //死信隊列交換機
    deadExchange("dlx-key"),

    ticketExchange("ticket-key"),

    ;





    public final String routingKey;


    NginxExchangeEnums(String routingKey){
        this.routingKey = routingKey;
    }

    public static NginxExchangeEnums parse(String name) {
        if(!StringUtils.isBlank(name)){
            for(NginxExchangeEnums info : values()){
                if(info.name().equals(name)){
                    return info;
                }
            }
        }
        return null;
    }



}

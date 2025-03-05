package org.example.lockproject.mq.enums;

import io.micrometer.common.util.StringUtils;

public enum NginxExchangeEnums {
    
    //死信隊列交換機
    NginxQ("deadExchange","dlx-key");

    public final String name;

    public final String key;


    NginxExchangeEnums(String name, String key){
        this.name = name;
        this.key = key;
    }

    public static NginxExchangeEnums parse(String name) {
        if(!StringUtils.isBlank(name)){
            for(NginxExchangeEnums info : values()){
                if(info.name.equals(name)){
                    return info;
                }
            }
        }
        return null;
    }




}

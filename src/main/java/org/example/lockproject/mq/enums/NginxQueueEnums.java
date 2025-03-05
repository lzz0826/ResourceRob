package org.example.lockproject.mq.enums;

import io.micrometer.common.util.StringUtils;

public enum NginxQueueEnums {


    //共用 通道 包含 QA QB
    nginxQ(),

    //死信隊列
    nginxQDlx(),

    //區域A
    nginxQA(),


    //區域A
    nginxQB();



    NginxQueueEnums(){
    }

    public static NginxQueueEnums parse(String name) {
        if(!StringUtils.isBlank(name)){
            for(NginxQueueEnums info : values()){
                if(info.name().equals(name)){
                    return info;
                }
            }
        }
        return null;
    }




}

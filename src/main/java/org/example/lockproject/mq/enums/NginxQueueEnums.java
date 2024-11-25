package org.example.lockproject.mq.enums;

import io.micrometer.common.util.StringUtils;

public enum NginxQueueEnums {


    //共用 通道 包含 QA QB
    NginxQ("nginxQ","nginxQ"),

    //區域A
    NginxQA("nginxQA","nginxQA"),


    //區域A
    NginxQB("nginxQB","nginxQB");

    public final String name;
    public final String area;

    NginxQueueEnums(String name, String area){
        this.name = name;
        this.area = area;
    }

    public static NginxQueueEnums parse(String name) {
        if(!StringUtils.isBlank(name)){
            for(NginxQueueEnums info : values()){
                if(info.name.equals(name)){
                    return info;
                }
            }
        }
        return null;
    }




}

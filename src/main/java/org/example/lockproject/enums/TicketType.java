package org.example.lockproject.enums;

import org.apache.commons.lang3.StringUtils;

public enum TicketType {

    TICKET_CREATE(0),
    TICKET_PAYING(1),
    TICKET_IS_PAY(2),
    TICKET_NOT_PAY(3),
    TICKET_END(4),
    TICKET_OTHER(9),
    ;


    public final int tpye;
    TicketType(int tpye){
        this.tpye = tpye;
    }


    public static TicketType parse(int type) {
        for (TicketType info : values()) {
            if (info.tpye == type) {
                return info;
            }
        }
        return null;
    }
}


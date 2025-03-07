package org.example.lockproject.enums;

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



}


package org.example.lockproject.enums;

public enum TicketTokenEnums {

    TICKET_TOKEN(60L);

    //超時時間
    public final Long expireTime;
    TicketTokenEnums(Long expireTime){
        this.expireTime = expireTime;
    }
}

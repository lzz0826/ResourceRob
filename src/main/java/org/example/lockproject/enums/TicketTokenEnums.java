package org.example.lockproject.enums;

public enum TicketBookingEnums {


    SOME_TICKET("lockKey","SOME_TICKET_KEY",5000);

    //用來做全局鎖
    public final String lockKey;

    //剩餘張數
    public final String ticketKey;

    //超時時間
    public final int expireTime;
    TicketBookingEnums(String lockKey , String ticketKey, int expireTime){
        this.lockKey = lockKey;
        this.ticketKey = ticketKey;
        this.expireTime = expireTime;

    }





    }

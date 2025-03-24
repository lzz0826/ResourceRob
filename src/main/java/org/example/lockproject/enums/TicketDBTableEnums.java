package org.example.lockproject.enums;

public enum TicketDBTableEnums {

    NGINX_QA("ticket_nginxQA"),
    NGINX_QB("ticket_nginxQB");

    private final String name;

    TicketDBTableEnums(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // MyBatis 使用 ${} 會有sql注入風險
    // 用來驗證外部傳入的 tableName，確保它是 Enum 內的值
    public static String validateTableName(String tableName) {
        for (TicketDBTableEnums validTable : TicketDBTableEnums.values()) {
            if (validTable.getName().equals(tableName)) {
                return tableName; // 傳入的值符合 Enum，回傳安全的表名
            }
        }
        throw new IllegalArgumentException("非法的表名：" + tableName);
    }
}

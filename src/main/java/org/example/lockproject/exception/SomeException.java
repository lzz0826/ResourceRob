package org.example.lockproject.exception;

public class SomeException extends Exception {

    // 可選：定義錯誤碼或其他有用的訊息
    private int errorCode;

    // 可選：錯誤訊息
    private String details;

    // 構造函數
    public SomeException(String message) {
        super(message);  // 父類別的構造函數，設置異常訊息
    }

    // 構造函數，包含錯誤碼
    public SomeException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    // 可選：構造函數，帶有異常原因
    public SomeException(String message, Throwable cause) {
        super(message, cause);
    }

    // 可選：構造函數，帶有異常原因和錯誤碼
    public SomeException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    // 可選：獲取錯誤碼
    public int getErrorCode() {
        return errorCode;
    }

    // 可選：獲取詳細訊息
    public String getDetails() {
        return details;
    }

    // 可選：設置錯誤碼
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    // 可選：設置詳細訊息
    public void setDetails(String details) {
        this.details = details;
    }
}

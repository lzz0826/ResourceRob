package org.example.lockproject.common;

import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;

public enum StatusCode {

 /**
 * 系統
 */
  Success(0,"成功"),

  SystemError(-1,"失敗"),

  MissingParameter(10,"缺少必要參數"),

  ErrorParameter(11,"參數錯誤"),

  AddFail(12,"新增失敗"),

  NeedPage(13,"需要頁碼"),
  NeedPageSize(14,"需要頁碼大小"),

  NotAllowedNullStr(15,"不允許空的字串"),

  BindExceptionError(16,"數據綁定錯誤,參數型別錯誤"),


  QueuesError(17,"Mq對列錯誤"),

  ;

  public final int code;

  public final String msg;

    // 在使用JSON.parseObject 避免抱錯 添加靜態方法來根據 code 進行匹配
    @JSONCreator
    public static StatusCode fromCode(@JSONField(name = "statusCode") int code) {
        for (StatusCode e : StatusCode.values()) {
            if (e.code == code) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid StatusCode code: " + code);
    }


  StatusCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static StatusCode getByCode(int code) {
    for (StatusCode e : StatusCode.values()) {
      if (e.code == code) {
        return e;
      }
    }

    return null;
  }





}

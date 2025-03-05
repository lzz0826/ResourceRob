package org.example.lockproject.advice;

import lombok.extern.log4j.Log4j2;
import org.example.lockproject.common.BaseResp;
import org.example.lockproject.common.StatusCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodArgumentNotValidExceptionHandler {

  /**
   * MethodArgumentNotValidException
   * 參數驗證捕獲
   */
  @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
  public BaseResp<?> handleException(MethodArgumentNotValidException ex){
    Object[] detailMessageArguments = ex.getDetailMessageArguments();
    return BaseResp.fail(detailMessageArguments[1],StatusCode.MissingParameter);
  }

}

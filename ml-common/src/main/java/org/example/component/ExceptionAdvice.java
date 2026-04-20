package org.example.component;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.ServiceException;
import org.example.result.ResultCode;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.example.result.Result;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-19 11:57
 */
@Slf4j
@RestControllerAdvice(basePackages = {"org.example"})
public class ExceptionAdvice {

    @ExceptionHandler(ServiceException.class)
    public Object serviceException(ServiceException e) {
        String coderMessage = e.getMessage();
        log.error("业务层异常: " + coderMessage);
        return new Result<>(e.getResultCode(), coderMessage);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object hibernateValidatorException(BindException e) {
        // 获取BindingResult
        BindingResult bindingResult = e.getBindingResult();
        // 获取BindingResult中所有属性错误信息集合中的第一个属性错误
        FieldError firstFieldError = bindingResult.getFieldErrors().get(0);
        // 异常信息 : "xxx实例的xxx属性校验失败: xxx异常信息"
        String coderMessage = String.format("%s实例的%s属性校验失败: %s",
                firstFieldError.getObjectName(),
                firstFieldError.getField(),
                firstFieldError.getDefaultMessage());
        // 记录日志
        log.error("控制层异常：" + coderMessage);
        // 响应
        return new Result<>(ResultCode.ILLEGAL_PARAM, firstFieldError.getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e) {
        String coderMessage = e.getMessage();
        log.error("其他异常: " + coderMessage);
        return new Result<>(ResultCode.SERVER_ERROR, coderMessage);
    }
}


package com.newland.bd.ms.learning.exp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常的处理器, 基于springmvc的情况
 */
@Lazy
@ControllerAdvice
public class SpringExceptionMapper {
    private static final Logger logger = LoggerFactory.getLogger(SpringExceptionMapper.class);

    /**
     * springmvc时, 错误的统一处理.
     *
     * @param e 捕获到的异常信息, 包括unchecked与checked
     * @return 错误访问的结构, 系统统一规定
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String handleError(Exception e) {
        return e.getMessage();
    }
}
package com.baidu.shop.global;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.status.HTTPStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

//@RestControllerAdvice
//@Slf4j
public class GlobalException {

//    @ExceptionHandler(value = Exception.class)
//    public Result<JSONObject>  testException(Exception e){
//        log.error("code: {} ,message: {}", HTTPStatus.ERROR,e.getMessage());
//        return new Result<JSONObject>(HTTPStatus.ERROR,e.getMessage(),null);
//    }
//
//    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
//    public Map<String,Object> MethodArgumentNotValidException(Exception e){
//        Map<String,Object> map = new HashMap<>();
//        map.put("code",HTTPStatus.PARAMS_VALIDATE_ERROR);
//
//        List<String> list = new ArrayList<>();
//        /// BindException
//        if (e instanceof BindException){
//            List<FieldError> fieldErrors = ((BindException) e).getFieldErrors();
//            if (fieldErrors != null) {
//                fieldErrors.stream().forEach(fieldError ->{
//                    list.add("Field -->"+fieldError.getField()+" : "+fieldError.getDefaultMessage());
//                    log.error("Field -->"+fieldError.getField()+" : "+fieldError.getDefaultMessage());
//                });
//            }
//            /// MethodArgumentNotValidException
//        }else if(e instanceof MethodArgumentNotValidException){
//            List<FieldError> fieldErrors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
//            if (fieldErrors != null) {
//                fieldErrors.stream().forEach(fieldError ->{
//                    list.add("Field -->"+fieldError.getField()+" : "+fieldError.getDefaultMessage());
//                    log.error("Field -->"+fieldError.getField()+" : "+fieldError.getDefaultMessage());
//                });
//            }
//            /// ValidationException 的子类异常ConstraintViolationExceptio
//        }else if(e instanceof ConstraintViolationException){
//            /*
//             * ConstraintViolationException的e.getMessage()形如
//             *     {方法名}.{参数名}: {message}
//             *  这里只需要取后面的message即可
//             */
//            String msg = e.getMessage();
//            if (msg != null) {
//                int lastIndex = msg.lastIndexOf(':');
//                if (lastIndex >= 0) {
//                   list.add(msg.substring(lastIndex + 1).trim());
//                }
//            }
//            // ValidationException 的其它子类异常
//        }else {
//            list.add("处理参数时异常");
//        }
//
//        String msg = list.parallelStream().collect(Collectors.joining(","));
//        map.put("message", msg);
//        return map;
//    }
}

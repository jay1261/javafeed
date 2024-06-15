package com.sparta.javafeed.aop;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j(topic = "Request Info")
public class RequestInfoAop {
    @Pointcut("execution(* com.sparta.javafeed.controller.*.*(..))")
    private void AllController(){}

    @Before("AllController()")
    public void logRequestInfo(JoinPoint joinPoint){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        log.info("Request URI: " + request.getRequestURI());
        log.info("Http Method: " + request.getMethod());
    }

}

package my.sample.web;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RefuseRepeatSubmitValidator {

    @Pointcut("@annotation(my.sample.web.RefuseRepeatSubmit)")
    public void refusePointCut() {}

    @Around("refusePointCut()")
    public void refuseAround(ProceedingJoinPoint point) throws Throwable {
        point.proceed();
        System.out.println("Execution this AOP method!!!");
    }
}

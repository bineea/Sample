package my.sample.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefuseRepeatSubmit {

    //认定为重复请求的时间间隔，单位根据缓存设置确定
    int time() default 60;
}
package top.jianx.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jianx
 * @since 1.0
 *
 * Spring包扫描注解
 */
@Target(ElementType.TYPE)//修饰元素
@Retention(RetentionPolicy.RUNTIME)//运行时有效
public @interface ComponentScan {
    String value() default "";
}

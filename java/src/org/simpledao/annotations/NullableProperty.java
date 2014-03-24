package org.simpledao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Jun 22, 2011
 * Time: 9:18:16 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NullableProperty
{
    boolean value() default true;
    String nullValue() default "";
}

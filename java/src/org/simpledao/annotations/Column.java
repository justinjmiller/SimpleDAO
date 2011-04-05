package org.simpledao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Feb 24, 2011
 * Time: 12:26:51 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Column
{
    String value() default "";
    boolean nullable() default false;
    String defaultValue() default "-1";
    boolean updateKey() default false;
    SortOrder sortOrder() default SortOrder.UNDEFINED;
}

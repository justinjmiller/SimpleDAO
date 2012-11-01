package org.simpledao.annotations;

import org.simpledao.SortOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Jun 22, 2011
 * Time: 9:14:01 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OrderedColumn
{
    SortOrder sortOrder() default SortOrder.ASCENDING;
    int orderPosition() default 1;
}

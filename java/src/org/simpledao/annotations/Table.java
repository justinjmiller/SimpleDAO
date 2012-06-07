package org.simpledao.annotations;


import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Feb 24, 2011
 * Time: 12:26:23 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Table
{
    String value() ;
}

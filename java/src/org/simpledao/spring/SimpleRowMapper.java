package org.simpledao.spring;

import org.simpledao.ColumnDefinition;
import org.simpledao.ReflectionUtils;
import org.simpledao.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Feb 28, 2011
 * Time: 10:18:43 AM
 */
public class SimpleRowMapper<T>
{
    private Class<T> beanType;

    public SimpleRowMapper(Class<T> clazz)
    {
        //this.beanType =((Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

        this.beanType = clazz;
        System.out.println("test");
        
    }
    public T mapRow(ResultSet resultSet, int i) throws SQLException
    {
        //Class<?> type = ((Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        try
        {
        T newObject = beanType.newInstance();
          Map<String, ColumnDefinition> propMap = ReflectionUtils.getBeanPropertyDBColumnMap(newObject);
            System.out.println(propMap.size());
//        T newObject = (T)type.getClass().newInstance();
            return newObject;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        //Utils.getBeanPropertyMap(T..class.newInstance());
        //his.clazz = Class.forName(getClass().getGenericSuperclass().toString().split("[<>]")[1]);
        /*ParameterizedType pt = (ParameterizedType)bean.getClass().getGenericSuperclass();
                T newBean = (T)((Class)pt.getActualTypeArguments()[0]).newInstance();*/
        return null;
    }


}

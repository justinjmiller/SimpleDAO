package org.simpledao;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.locale.LocaleBeanUtils;
import org.simpledao.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Jun 15, 2011
 * Time: 4:36:10 PM
 */
public class ReflectionUtils
{
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    public static BeanDescriptor describeBean( Object bean)
    {
        if ( bean instanceof SimpleBean)
            return ((SimpleBean)bean).describe();
        else
        {
            PropertyDescriptor descriptors[] = PropertyUtils.getPropertyDescriptors( bean );
            for (PropertyDescriptor prop : descriptors)
            {
                if ( prop.getPropertyType() == BeanDescriptor.class )
                {
                    try
                    {
                        return (BeanDescriptor)PropertyUtils.getProperty(bean,prop.getName());
                    }
                    catch (Exception e)
                    {
                        log.error("Unable to get property '" + prop.getName() + "'", e);
                    }
                }
            }

        }
        BeanDescriptor descriptor = new BeanDescriptor();
        descriptor.setTable( inferBeanDBTableName(bean));
        descriptor.setUpdateKeys( inferBeanDBUpdateKeys( bean));
        descriptor.setPropertyMap( getBeanPropertyDBColumnMap(bean));
        descriptor.setOrderedColumns( getBeanDBOrderBy(bean));
        return descriptor;
    }

    public static Map<String,ColumnDefinition> getBeanPropertyDBColumnMap(Object bean)
    {
        Map<String,ColumnDefinition> props = new HashMap<String,ColumnDefinition>();
        PropertyDescriptor descriptors[] = PropertyUtils.getPropertyDescriptors( bean );
        for (PropertyDescriptor descriptor : descriptors)
        {
            String property = descriptor.getName();
            if (!"class".equals(property) && descriptor.getReadMethod().getAnnotation(ExcludedProperty.class) == null)
            {
                ColumnDefinition column = new ColumnDefinition(Utils.getPropertyDBName(property));

                Column ca = descriptor.getReadMethod().getAnnotation(Column.class);
                NullableProperty np = descriptor.getReadMethod().getAnnotation(NullableProperty.class);
                OrderedColumn oc = descriptor.getReadMethod().getAnnotation(OrderedColumn.class);
                UpdateKeyColumn ukc = descriptor.getReadMethod().getAnnotation(UpdateKeyColumn.class);


                if ( ca != null )
                {
                    if (!"".equals(ca.value()))
                        column.setName( ca.value());
                    column.setNullable( ca.nullable());
                    column.setUpdateKey( ca.updateKey());
                    if ( ca.sortOrder() != SortOrder.UNDEFINED)
                    {
                        column.setSortOrder( ca.sortOrder());
                        column.setOrderByPosition( ca.orderByPosition());
                    }
                }
                if (ukc != null )
                    column.setUpdateKey(true);
                if ( np != null )
                        column.setNullable( np.value());
                if ( oc != null )
                {
                    column.setSortOrder( oc.sortOrder());
                    column.setOrderByPosition( oc.orderPosition());
                }
/*                if ( ca != null && !"".equals(ca.value()))
                {
                    props.put(property, ((Column)ca).value());
                }
                else
                {
                    props.put(property, Utils.getPropertyDBName(property));
    //                if (!"class".equals(property) && property.indexOf("DB") < 0)
                }*/
                props.put(property,column);
            }
        }

        //Map props = BeanUtils.describe(this);
        return props;
    }

    /**
     * Tries to determine the database table name via two methods:
     * 1. A @Table type annotation on the class (e.g., @Table("LIBRARY_BOOKS")
     *  *Note this annotation is marked inheritable so will be picked up
     *  by subclasses
     * 2. Converting the Class name to a table name via camel case rules
     *  (e.g., a class named LibraryBooks will become LIRARY_BOOKS)
     * **Inner classes that don't have an annotation in the class heirarchy
     *   will be queried for their superclass and this method will recurse 
     * @param bean - the object to inspect
     * @return a database table name
     */
    public static String inferBeanDBTableName( Object bean )
    {
        Annotation ta = bean.getClass().getAnnotation(Table.class);
        if ( ta != null && ta instanceof Table && !"".equals(((Table)ta).value()))
        {
            return ((Table)ta).value();
        }
        else if ( bean.getClass().getName().contains("$"))
        {
            // most likely an anonymous inner class, behave apprropriately
            try
            {
                return inferBeanDBTableName( bean.getClass().getSuperclass().newInstance() );
            }
            catch (Exception e)
            {
                log.error("infer table name - unable to instantiate super class. " + e.getMessage(), e);
                throw new RuntimeException("infer table name - unable to instantiate super class of inner class",e);
            }
        }
        else
        {
            return Utils.getPropertyDBName( bean.getClass().getName().replaceAll("\\w+\\.","").replaceAll("Bean",""));
        }
    }

    public static String[] inferBeanDBUpdateKeys( Object bean )
    {
        List<String> keys = new ArrayList<String>();
        String guessedKey = null;
        PropertyDescriptor descriptors[] = PropertyUtils.getPropertyDescriptors( bean );
        for (PropertyDescriptor descriptor : descriptors)
        {
            String property = descriptor.getName();
            Column ca = descriptor.getReadMethod().getAnnotation(Column.class);
            UpdateKeyColumn ua = descriptor.getReadMethod().getAnnotation(UpdateKeyColumn.class);
            if ( (ca != null  && ca.updateKey()) || ua != null)
            {
                String columnName = Utils.getPropertyDBName(property); 
                if ( ca != null && !"".equals(ca.value()))
                {
                    columnName = ca.value();
                }
                keys.add(columnName);
            }
            else
            {
                if  ("id".equals(property) || property.toUpperCase().equals(bean.getClass().getName().replaceAll("\\w+\\.","").replaceAll("Bean","").toUpperCase() +  "ID"))
                {
                    guessedKey = Utils.getPropertyDBName(property);
                }
            }
        }
        if ( keys.size() == 0 && guessedKey != null && !"".equals(guessedKey))
        {
            keys.add(guessedKey);
        }
        return keys.toArray(new String[keys.size()]);
    }

    public static HashMap<Integer, SortedColumn> getBeanDBOrderBy( Object bean )
    {
        HashMap<Integer, SortedColumn> sorts = new HashMap<Integer,SortedColumn>();
        PropertyDescriptor descriptors[] = PropertyUtils.getPropertyDescriptors( bean );
        for (PropertyDescriptor descriptor : descriptors)
        {
            String property = descriptor.getName();
            Column ca = descriptor.getReadMethod().getAnnotation(Column.class);
            OrderedColumn oca = descriptor.getReadMethod().getAnnotation(OrderedColumn.class);
            if ( (ca != null && ca.sortOrder() != SortOrder.UNDEFINED) || (oca!= null && oca.sortOrder() != SortOrder.UNDEFINED))
            {
                SortedColumn column = new SortedColumn();
                column.setName(Utils.getPropertyDBName(property));
                int position = 0;
                if ( ca != null )
                {
                    if ( !"".equals(ca.value())) column.setName( ca.value());
                    if ( ca.sortOrder() != SortOrder.UNDEFINED ) column.setSortOrder( ca.sortOrder());
                    position =  ca.orderByPosition();
                }
                if ( oca != null && oca.sortOrder() != SortOrder.UNDEFINED)
                {
                    column.setSortOrder(oca.sortOrder());
                    position =oca.orderPosition();
                }
                if ( position == sorts.size() )
                    throw new RuntimeException("Cannot have multiple properties with the same sort position");
                if ( position == 0 ) position = sorts.size() + 1;
                sorts.put(position, column);
            }
        }
        return sorts;
    }

    /**
     * Set all the properties in the bean based on a map of properties passed in
     * @param bean the bean to reflect upon
     * @param  props  HashMap of properties to use when populating
     */
    public static void populateBean( Object bean, HashMap props )
    {
        if ( log.isDebugEnabled() ) { log.debug("populate - begin");}

        for (Object o : props.keySet())
        {
            String propName = (String) o;
            if ( log.isDebugEnabled() ) { log.debug("populate - property '" + propName + "'");}

            if (propName == null)
            {
                continue;
            }
            Object value = props.get(propName);
            // turn date into displayable date

/*
            for ( Method method : this.getClass().getMethods() )
            {
                String propMethod = "set" + propName.substring(0,1).toUpperCase() + propName.substring(1);
                if ( method.getName().equals( propMethod ))
                {
                    Class clazz = method.getParameterTypes()[0];
                    if ( clazz.getName().equals("java.lang.String"))
                    {
                        try
                        {
                            method.invoke(this, value );
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                    }
                    else if ( clazz.getName().equals("java.util.Date"))
                    {

                    }
                    else
                    {

                    }

*/
/*
                    for ( Class clazz : method.getParameterTypes() )
                    {

                    }
*/
/*
                }
            }
*/

            if (propName.matches(".*[dD]ate$") && value != null && value instanceof String )
            {
                if ( log.isDebugEnabled() ) { log.debug("populate - property '" + propName + "' is a string and has date in the name, format it");}
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
                //sdf.setLenient(false);
                try
                {
                    Date dt = sdf.parse(value.toString());
                    sdf.applyPattern("MM/dd/yyyy");
                    //java.sql.Date newDt = new java.sql.Date( dt.getTime());
                    value = sdf.format(dt);
                    //value = dt.toString();
                }
                catch (ParseException e)
                {
                    log.error("populate - unable to format the date. " + e.getMessage(), e );
                }
            }

            try
            {

                if ( value instanceof java.sql.Timestamp || value instanceof java.sql.Date || value instanceof java.sql.Time)
                {
                    if ( log.isDebugEnabled() ) { log.debug("populate - set the date property '" + propName + "'");}

                    PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(bean, propName);
                    if (descriptor.getPropertyType().equals(java.util.Date.class) )
                    {
                        if ( log.isDebugEnabled() ) { log.debug("populate - property '" + propName + "' expects date");}
                        LocaleBeanUtils.setProperty(bean, propName, value);
                    }
                    else if ( descriptor.getPropertyType().equals(java.lang.String.class))
                    {
                        if ( log.isDebugEnabled() ) { log.debug("populate - property '" + propName + "' expects string");}
                        BeanUtils.setProperty(bean, propName, value);
                    }

                }
                else
                {
                    if ( log.isDebugEnabled() ) { log.debug("populate - set the property '" + propName + "'");}
                    BeanUtils.setProperty(bean, propName, value);
                }
            }
            catch (Exception e)
            {
                log.error("populate - unable to set the property. " + e.getMessage(),e );
                // do nothing, property not found or set
            }
        }
    }


}

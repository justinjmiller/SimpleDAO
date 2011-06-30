package org.simpledao;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpledao.annotations.ExcludedProperty;

import java.util.HashMap;
import java.util.Map;
import java.beans.PropertyDescriptor;

/**
 * <p>SimpleBean is an abstract class intended to be extended by
 * all beans used by the {@link SimpleDAO1 SimpleDAO} framework.  It includes public
 * methods for populating the bean from the database and determining
 * specific database properties at runtime (e.g. database table name)</p>
 * <p/>
 * User: jumiller
 * Date: Apr 27, 2006
 * Time: 2:28:55 PM
 * </p>
 *
 * @author Justin Miller
 * @version 1.0
 * @see SimpleDAO1
 */
public abstract class SimpleBean
{
    private static final Log log = LogFactory.getLog( SimpleBean.class );

    /**
     * The table used in the statements sent to the database.  uses class name by default.
     * Can be overridden at the bean level.
     */
    protected String dbTableName ;

    protected String[] dbPrimaryKey;

    protected Map<Integer, SortedColumn> dbOrderBy = null;

    protected Map<String, ColumnDefinition> dbColumnMap = null;

//    private HashMap<String,String> dbPrimaryKey;


    /**
     * get the database table name this bean maps to.  most likely just the bean name
     * @return string database table name
     */
    @ExcludedProperty
    public String getDBTableName()
    {
        if ( dbTableName == null || "".equals( dbTableName ) )
        {
            //dbTableName = Utils.getPropertyDBName(getClass().getName().substring( getClass().getName().lastIndexOf(".") + 1));
            //dbTableName = Utils.getPropertyDBName( getClass().getName().replaceAll("\\w+\\.","").replaceAll("Bean",""));
            dbTableName = ReflectionUtils.inferBeanDBTableName(this);
            return  dbTableName;
        }
        else
        {
            return dbTableName;
        }
    }

    public void setDBTableName(String dbTableName)
    {
        this.dbTableName = dbTableName;
    }

    @ExcludedProperty
    public String[] getDBPrimaryKey()
    {
        if ( dbPrimaryKey == null  )
        {
            /*dbPrimaryKey = new String[1];
            if ( dbTableName == null || "".equals( dbTableName ) )
            {
                dbPrimaryKey[0] = Utils.getPropertyDBName(getClass().getName().replaceAll("\\w+\\.","").replaceAll("Bean","")) + "_ID";
            }
            else
            {
                dbPrimaryKey[0] = dbTableName + "_ID";
            } */
            dbPrimaryKey = ReflectionUtils.inferBeanDBUpdateKeys(this);
            return dbPrimaryKey;
        }
        else
        {
            return dbPrimaryKey;
        }
    }

/*

    public HashMap<String,String> getDBPrimaryKey()
    {
        if ( dbPrimaryKey == null )
        {
            dbPrimaryKey = new HashMap<String,String>();

            String keyString = getDBTableName() + "_ID";

            if ( dbTableName == null || "".equals( dbTableName ) )
            {
                keyString = Utils.getPropertyDBName(getClass().getName().replaceAll("\\w+\\.","").replaceAll("Bean","")) + "_ID";
            }
            else
            {
                keyString = dbTableName + "_ID";
            }

            dbPrimaryKey.put( keyString, Utils.getCamelCaseColumnName( keyString ) );

            return dbPrimaryKey;
        }
        else
        {
            return dbPrimaryKey;
        }
    }
    public void setDBPrimaryKey( HashMap<String,String> dbPrimaryKey )
    {
        this.dbPrimaryKey = dbPrimaryKey;
    }
*/

    public void setDBPrimaryKey( String dbPrimaryKey )
    {
/*
        if ( this.dbPrimaryKey == null )
        {
            this.dbPrimaryKey = new HashMap<String,String>();
        }
        this.dbPrimaryKey.put( dbPrimaryKey, Utils.getCamelCaseColumnName( dbPrimaryKey ) );
*/
        this.dbPrimaryKey = new String[] { dbPrimaryKey };
    }

    public void setDBPrimaryKey( String[] dbPrimaryKey )
    {
        this.dbPrimaryKey = dbPrimaryKey;
    }

    @ExcludedProperty
    public Map<Integer, SortedColumn> getDBOrderBy()
    {
        return dbOrderBy;
    }

    public void setDBOrderBy(Map<Integer, SortedColumn> dbOrderBy)
    {
        this.dbOrderBy = dbOrderBy;
    }

    @ExcludedProperty
    public Map<String, ColumnDefinition> getDBColumnMap()
    {
        if ( dbColumnMap == null )
        {
            dbColumnMap = ReflectionUtils.getBeanPropertyDBColumnMap(this);
            dbColumnMap.remove("DBTableName");
            dbColumnMap.remove("DBPrimaryKey");
            dbColumnMap.remove("DBOrderBy");
            dbColumnMap.remove("DBColumnMap");
        }
        return dbColumnMap;
    }

    public void setDBColumnMap(Map<String, ColumnDefinition> dbColumnMap)
    {
        this.dbColumnMap = dbColumnMap;
    }

    /**
     * Retrieve a map of the available accessors in the bean.
     * @return Map
     */
    //public Map<String,String> describe()
    public BeanDescriptor describe()
    {
        /*Map<String,String> props = new HashMap<String,String>();
        PropertyDescriptor descriptors[] = PropertyUtils.getPropertyDescriptors( this );
        for (PropertyDescriptor descriptor : descriptors)
        {
            String property = descriptor.getName();
            if (!"class".equals(property) && property.indexOf("DB") < 0)
            {
                props.put(property, Utils.getPropertyDBName(property));
            }
        } */
        return new BeanDescriptor(getDBTableName(),getDBPrimaryKey(),getDBOrderBy(),getDBColumnMap());
        //Map props = BeanUtils.describe(this);
        //return props;
    }

	public Map<String, Object> describeWithValues()
	{
		Map<String, Object> props = new HashMap<String, Object>();
		PropertyDescriptor descriptors[] = PropertyUtils.getPropertyDescriptors( this );
		for (PropertyDescriptor descriptor : descriptors)
		{
			String property = descriptor.getName();
			if (!"class".equals(property) && property.indexOf("DB") < 0)
			{
                try
                {
                    props.put(property, PropertyUtils.getProperty( this, property));
                }
                catch (Exception e)
                {
                    log.error(e);
                }
            }
		}
		return props;
	}

	/**
     * Set all the properties in the bean based on a map of properties passed in
     * @param  props  HashMap of properties to use when populating
     * @deprecated
     */
    public void populate( HashMap props )
    {
        if ( log.isDebugEnabled() ) { log.debug("populate - begin");}
        ReflectionUtils.populateBean(this, props);

        /*
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
                    e.printStackTrace();
                }
            }

            try
            {

                if ( value instanceof java.sql.Timestamp || value instanceof java.sql.Date || value instanceof java.sql.Time)
                {
                    if ( log.isDebugEnabled() ) { log.debug("populate - set the date property '" + propName + "'");}

                    PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(this, propName);
                    if (descriptor.getPropertyType().equals(java.util.Date.class) )
                    {
                        if ( log.isDebugEnabled() ) { log.debug("populate - property '" + propName + "' expects date");}
                        LocaleBeanUtils.setProperty(this, propName, value);
                    }
                    else if ( descriptor.getPropertyType().equals(java.lang.String.class))
                    {
                        if ( log.isDebugEnabled() ) { log.debug("populate - property '" + propName + "' expects string");}
                        BeanUtils.setProperty(this, propName, value);
                    }

                }
                else
                {
                    if ( log.isDebugEnabled() ) { log.debug("populate - set the property '" + propName + "'");}
                    BeanUtils.setProperty(this, propName, value);
                }
            }
            catch (Exception e)
            {
                log.error("populate - unable to set the property. " + e.getMessage(),e );
                // do nothing, property not found or set
            }
        } */
    }

    /**
     * Set null all the public properties in the bean
     * @throws Exception please just catch this exception
     */
    public void reset()
    {
        Map props = null;
        try
        {
            props = BeanUtils.describe( this );
        }
        catch (Exception e)
        {
            log.error(e);
        }
        for (Object o : props.keySet())
        {
            String propName = (String) o;
            try
            {
                BeanUtils.setProperty(this, propName, null);
            }
            catch (Exception e)
            {
                log.error(e);
            }
        }
    }

}

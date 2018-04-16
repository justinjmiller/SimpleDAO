package org.simpledao;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Feb 24, 2011
 * Time: 6:54:09 AM
 */
//todo: insert byte array blob
//todo: handle clob in insert
public class SimpleDAO<T>
{
    private static final Logger log = LoggerFactory.getLogger( SimpleDAO.class );
    private static final Logger sqlLog = LoggerFactory.getLogger("SQL");

    public void simpleInsert( T bean ) throws SQLException
    {
        SimpleDBConnection dbc = new SimpleDBConnection();
        Connection con = null;
        try
        {
            con = dbc.getDBConnection();
            simpleInsert( con, bean, getBeanDescriptor(bean) );
        }
        finally
        {
            dbc.closeDBConnection(con);
        }
    }

    /**
     * Insert data into the database based on columns introspected from the bean
     * @param con Connection object used to communicate with the database (JDBC)
     * @param bean SimpleBean derived class that has the approprate getters/setters
     * @throws SQLException catch-all
     * @see SimpleBean
     */
    public void simpleInsert( Connection con, T bean ) throws SQLException
    {
        simpleInsert(con, bean, getBeanDescriptor(bean));
    }


    /**
     * Creates and executes a SQL INSERT statement against the passed in Connection object.
     * The columns to be inserted along with their values are ascertained from the passed in
     * SimpleBean derived class and the associated Map of bean proeprties.
     * @param con Connection object used to communicate with the database (JDBC)
     * @param bean SimpleBean derived class that has the approprate getters/setters
     * @param description A description of the bean
     * @throws SQLException catch-all
     * @see SimpleBean
     */
    public void simpleInsert( Connection con, T bean, BeanDescriptor description ) throws SQLException
    {
            //todo: refactor this back
            PreparedStatement ps = buildInsertStatement(bean, description, con);
            ps.executeUpdate();
            ps.close();
    }

    public T simpleSelect( T criteria ) throws SQLException
    {

        SimpleDBConnection dbc = new SimpleDBConnection();
        Connection con = null;
        try
        {
            con = dbc.getDBConnection();
            return simpleSelect( con, criteria);
        }
        finally
        {
            dbc.closeDBConnection( con );
        }
    }

    public T simpleSelect( Connection con, T criteria) throws SQLException
    {
        if ( log.isDebugEnabled() ) { log.debug("Get the beans properties");}
        return simpleSelect(con, criteria, getBeanDescriptor(criteria));
    }


    public T simpleSelect( Connection con, T criteria, BeanDescriptor descriptor ) throws SQLException
	{
		if ( log.isDebugEnabled() ) { log.debug("call simpleSelectList and get the first bean");}

		ArrayList<T> beanList = simpleSelectList(con, criteria, descriptor);
		if ( beanList.size() > 0 )
		{
			return beanList.get(0);
		}
		else
		{
			return null;
		}
	}

    public ArrayList<T> simpleSelectList( T criteria ) throws SQLException
    {
        SimpleDBConnection dbc = new SimpleDBConnection();
        Connection con = null;
        try
        {
            con = dbc.getDBConnection();
            return simpleSelectList( con, criteria);
        }
        finally
        {
            dbc.closeDBConnection(con);
        }
    }

    public ArrayList<T> simpleSelectList( Connection con, T criteria) throws SQLException
    {
        return simpleSelectList( con, criteria, getBeanDescriptor(criteria));
    }

    public ArrayList<T> simpleSelectList( Connection con, T bean, BeanDescriptor descriptor ) throws SQLException
    {
        ArrayList<T> beanList = new ArrayList<T>();
        Map<String,String> columnPropertyMap = Utils.getColumnPropertyMap( descriptor.getPropertyMap());

        PreparedStatement ps = buildSelectStatement( bean, descriptor, con );
        ResultSet rs = ps.executeQuery();

        ResultSetMetaData metaData = rs.getMetaData();

        int columnCount = metaData.getColumnCount();

        while ( rs.next() )
        {
            HashMap<String,Object> props = new HashMap<String,Object>();
            for ( int i = 1; i <= columnCount ; i++)
            {
                if ( columnPropertyMap.containsKey(metaData.getColumnName((i)).toUpperCase()))
                {
                    if ( metaData.getColumnType(i) == Types.BLOB || metaData.getColumnTypeName(i).equalsIgnoreCase("bytea") )
                    {
                        if ( log.isDebugEnabled() ) { log.debug("simpleSelectList - column # '" + i + "' is a BLOB");}

                        Blob blob = rs.getBlob( metaData.getColumnName(i).toUpperCase() );

                        if ( blob != null )
                        {
                            if (log.isDebugEnabled())
                            {
                                log.debug("simpleSelectList - column # '" + i + "' BLOB is not null, write it to bean");
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                            BufferedInputStream bis = new BufferedInputStream(blob.getBinaryStream());

                            byte[] buffer = new byte[1024];
                            int curByte;
                            try
                            {
                                while ((curByte = bis.read(buffer, 0, buffer.length)) != -1)
                                {
                                    baos.write(buffer, 0, curByte);
                                }
                            } catch (IOException e)
                            {
                                log.error("Unable to write BLOB", e);
                                throw new RuntimeException("Unable to read the blob from the database", e);
                            }
                            props.put( Utils.getCamelCaseColumnName( metaData.getColumnName(i) ), baos.toByteArray() );
                        }
                    }
                    else if  ( metaData.getColumnType(i) == Types.CLOB || metaData.getColumnTypeName(i).equalsIgnoreCase("text") )
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("simpleSelectList - write CLOB to bean'");
                        }
                        props.put(columnPropertyMap.get(metaData.getColumnName(i).toUpperCase()), rs.getString(i));

                    }
                    else if ( metaData.getColumnType(i) == Types.DATE || metaData.getColumnTypeName(i).equalsIgnoreCase("date") )
                    {
                        if ( log.isDebugEnabled() ) { log.debug("simpleSelectList - column # '" + i + "' is a DATE");}
                        props.put( columnPropertyMap.get( metaData.getColumnName(i).toUpperCase()), rs.getTimestamp(i) );
                    }
                    else if ( metaData.getColumnType(i) == Types.TIME || metaData.getColumnTypeName(i).equalsIgnoreCase("time") )
                    {
                        if ( log.isDebugEnabled() ) { log.debug("simpleSelectList - column # '" + i + "' is a TIME");}
                        props.put( columnPropertyMap.get( metaData.getColumnName(i).toUpperCase()), rs.getTime(i) );
                    }
                    else if ( metaData.getColumnType(i) == Types.TIMESTAMP || metaData.getColumnTypeName(i).equalsIgnoreCase("timestamp"))
                    {
                        if ( log.isDebugEnabled() ) { log.debug("simpleSelectList - column # '" + i + "' is a TIMESTAMP");}
                        props.put( columnPropertyMap.get( metaData.getColumnName(i).toUpperCase()), rs.getTimestamp(i) );
                    }
                    else
                    {
                        if ( log.isDebugEnabled() ) { log.debug("simpleSelectList - column # '" + i + "' is not special");}
                        props.put( columnPropertyMap.get( metaData.getColumnName(i).toUpperCase()), rs.getString(i) );
                    }
                }

            }

            // create the return bean
            //Type pt = bean.getClass().getGenericSuperclass();
            //T newBean = (T)((Class)pt).newInstance();
            //T newBean = (T)((Class)((ParameterizedType)pt).getActualTypeArguments()[0]).newInstance();
            T newBean;
            try
            {
                newBean = (T)bean.getClass().newInstance();
            }
            catch (Exception e)
            {
                log.error("Unable to create new bean", e);
                throw new RuntimeException("Unable to instantiate the new Object",e);
            }

            ReflectionUtils.populateBean(newBean,props);
            beanList.add( newBean );
        }
        ps.close();

        return beanList;
    }

    public void simpleUpdate( T bean ) throws SQLException
    {
        SimpleDBConnection dbc = new SimpleDBConnection();
        Connection con = null;
        try
        {
            con = dbc.getDBConnection();
            simpleUpdate( con, bean, getBeanDescriptor(bean) );
        }
        finally
        {
            dbc.closeDBConnection(con);
        }
    }

    /**
     * Introspects the passed SimpleBean for columns that need to be updated.  The
     * resulting Map of columns is then passed to the simpleUpdate method that
     * accepts a Map parameter.
     *
     * @param con Connection object used to communicate with the database (JDBC) and run the UPDATE
     * @param bean  SimpleBean derived class with getters and setters that holds the values to update.
     * This method introspects the passed SimpleBean for columns to update
     * @throws SQLException catch-all
     * @see SimpleBean
     */
    public void simpleUpdate( Connection con, T bean ) throws SQLException
    {
        simpleUpdate(con, bean, getBeanDescriptor(bean));
    }

    /**
     * Creates and runs a SQL UPDATE statement against the passed database connection object.
     * The columns to be updated along with their values are ascertained from the passed in
     * SimpleBean derived class and Map of columns.
     *
     * @param con Connection object used to communicate with the database (JDBC) and run the UPDATE
     * @param bean  SimpleBean derived class with getters and setters that holds the values to update
     * @param description A Map of SimpleBean properties that will be used as columns to update
     * @throws SQLException catch-all
     * @see SimpleBean
     */
    public void simpleUpdate( Connection con, T bean,BeanDescriptor description ) throws SQLException
    {
        ArrayList<BoundVariable> bindVariables = new ArrayList<BoundVariable>();

            //todo: refactor this back
            PreparedStatement ps = buildUpdateStatement(bean, description, con);
            ps.executeUpdate();
            ps.close();
    }

    public void simpleDelete( T bean ) throws SQLException
    {
        SimpleDBConnection dbc = new SimpleDBConnection();
        Connection con = null;
        try
        {
            con = dbc.getDBConnection();
            simpleDelete( con, bean, getBeanDescriptor(bean) );
        }
        finally
        {
            dbc.closeDBConnection(con);
        }
    }

    /**
     *
     * @param con Connection object used to run the DELETE statement against
     * @param bean SimpleBean derived class used to determine the table name and WHERE clause
     * @throws SQLException catch-all
     * @see SimpleBean
     */
    public void simpleDelete( Connection con, T bean ) throws SQLException
    {
        simpleDelete(con, bean, getBeanDescriptor(bean));
    }


    /**
     * Creates and executes a SQL DELETE statement against the Connection parameter.
     * The table name is determined from the SimpleBean derived class and the WHERE clause
     * is asertained from the Map of SimpleBean properties.
     *
     * @param con Connection object used to run the DELETE statement against
     * @param bean SimpleBean derived class used to determine the table name and WHERE clause
     * @param description Map of SimpleBean properties used to determine WHERE clause
     * @throws SQLException catch-all
     */
    public void simpleDelete( Connection con, T bean, BeanDescriptor description ) throws SQLException
    {
            //todo: refactor this back
            PreparedStatement ps = buildDeleteStatement(bean, description, con);
            ps.executeUpdate();
            ps.close();
    }

    //-----------------------PRIVATE METHODS---------------------------------

    //todo: handle BeanDescriptor SQL Statement
    private PreparedStatement buildInsertStatement(T bean, BeanDescriptor description, Connection con ) throws SQLException
    {
        ArrayList<BoundVariable> bindVariables = new ArrayList<BoundVariable>();
        StringBuilder sql = new StringBuilder("INSERT INTO " );
        StringBuilder valuesSQL = new StringBuilder(" ) VALUES ( ");
        int propCount = 0;

        sql.append( description.getTable() );
        sql.append( " ( " );

        for (String property : description.getPropertyMap().keySet())
        {
            String column = description.getPropertyMap().get(property).getName();
            // if the database column is not already specified (unlikely), then determine it
            if (column == null || "".equals(column))
            {
                column = Utils.getPropertyDBName(property);
            }

            PropertyDescriptor pd;
            Object value;
            try
            {
                pd = PropertyUtils.getPropertyDescriptor( bean, property);
                value = PropertyUtils.getProperty ( bean, property);
            }
            catch (Exception e)
            {
                log.error("Unable to find bean property named '" + property + "'",e);
                throw new RuntimeException("Unable to get the bean property named '" + property + "'",e);
            }


            Class type = pd.getPropertyType();

            //todo: replace this with ReflectionUtils.isPropertyNull()
            if (value == null ||
			  (type == Integer.class || "int".equals(type.getName())) && ((Integer) value < 0) ||
			  ( type == Double.class || "double".equals( type.getName() ) ) && ((Double) value < 0.0d))
            {
                continue;
            }

            if (propCount > 0)
            {
                sql.append(", ");
                valuesSQL.append(", ");
            }
            sql.append(column);
            valuesSQL.append("?");

            propCount++;
            bindVariables.add(new BoundVariable(propCount, column, type, value));
        }
        sql.append( valuesSQL );
        sql.append( " )");

        if ( sqlLog.isDebugEnabled() ) { sqlLog.debug("buildInsertStatement SQL:" + sql); }

        return Utils.prepareStatement(con, sql.toString(), bindVariables);
    }

    private PreparedStatement buildSelectStatement( T bean, BeanDescriptor descriptor, Connection con) throws SQLException
    {
        String sql;

        ArrayList<BoundVariable> bindVariables = new ArrayList<BoundVariable>();

        if ( descriptor.getTable().toUpperCase().contains("SELECT ") &&
                descriptor.getTable().toUpperCase().contains("FROM "))
        {
            sql = bindStaticSQLStatement(bean, descriptor, bindVariables);
        }
        else
        {

            StringBuilder selectSQL = new StringBuilder( "SELECT ");
            StringBuilder whereSQL = new StringBuilder(" FROM " );
            StringBuilder orderSQL = new StringBuilder("");

            whereSQL.append( descriptor.getTable() );

            int colCount = 0;
            int whereCount = 0;

            for (Map.Entry<String,ColumnDefinition> ent: descriptor.getPropertyMap().entrySet())
            {
                String property = ent.getKey();
                String column = ent.getValue().getName();

                if ( log.isDebugEnabled()) { log.debug("buildSelectStatement - get property '" + property + "' for column '" + column + "'");}
                
                PropertyDescriptor pd;
                Object value;
                try
                {
                    pd = PropertyUtils.getPropertyDescriptor( bean, property );
                    value = PropertyUtils.getProperty ( bean, property );
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Unable to get the property '" + property + "'",e);
                }
                Class type = pd.getPropertyType();

                if ( colCount > 0 )
                {
                    selectSQL.append(", " );
                }
                selectSQL.append( column );
                colCount++;


                if ( ent.getValue().isNullable()  &&  Utils.isPropertyNull(type, value, ent.getValue().getNullValue()))
                {
                    whereSQL.append(whereCount > 0 || whereSQL.toString().contains("WHERE") ? " AND " : " WHERE " )
                        .append(column).append( " IS NULL ");
                }
                else if ( !Utils.isPropertyNull( type, value ) )
                {
                    whereSQL.append(whereCount > 0 || whereSQL.toString().contains("WHERE") ? " AND " : " WHERE " )
                        .append(column).append( value.toString().contains("%") ? " LIKE ? " : " = ? ");
                    whereCount ++;
                    bindVariables.add( new BoundVariable( whereCount, column, type, value));
                }

            }

            Map<Integer, SortedColumn> sorts = descriptor.getOrderedColumns();
            if ( sorts != null && sorts.size() > 0 )
            {
                //todo: this might need to look at the property<->column map to make sure this column is included
                for ( int i = 1; i <= sorts.size(); i ++ )
                {
                    SortedColumn sc = sorts.get(i);
                    if ( i == 1 )
                        orderSQL.append(" ORDER BY ");
                    else
                        orderSQL.append(", ");
                    orderSQL.append( sc.getName() );
                    if ( sc.getSortOrder() == SortOrder.DESCENDING)
                        orderSQL.append(" DESC");
                }
            }
            selectSQL.append ( whereSQL );
            selectSQL.append( orderSQL );
            sql = selectSQL.toString();
        }
        if ( sqlLog.isDebugEnabled() ) { sqlLog.debug("buildSelectStatement SQL:" + sql); }

        return Utils.prepareStatement(con, sql, bindVariables);
    }

    private PreparedStatement buildUpdateStatement( T bean, BeanDescriptor descriptor, Connection con) throws SQLException
    {
        String sql;
        ArrayList<BoundVariable> bindVariables = new ArrayList<BoundVariable>() ;

        if ( descriptor.getTable().toUpperCase().contains("UPDATE "))
        {
            sql = bindStaticSQLStatement(bean, descriptor, bindVariables);
       }
        else
        {
            bindVariables = new ArrayList<BoundVariable>();
            StringBuilder updateSQL = new StringBuilder("UPDATE ");
            StringBuilder whereSQL = new StringBuilder(" WHERE ");
            ArrayList<BoundVariable> keyBindVariables = new ArrayList<BoundVariable>();

            int columnCount = 0;
            int keyCount = 0;

            updateSQL.append(descriptor.getTable());

            updateSQL.append(" SET ");

            String[] keys = descriptor.getUpdateKeys();

            for (String property : descriptor.getPropertyMap().keySet())
            {
                ColumnDefinition def = descriptor.getPropertyMap().get(property);
                String column = def.getName();
                if (column == null || "".equals(column))
                {
                    column = Utils.getPropertyDBName(property);
                }

                PropertyDescriptor pd;
                Object value;
                try
                {
                    pd = PropertyUtils.getPropertyDescriptor(bean, property);
                    value = PropertyUtils.getProperty(bean, property);
                } catch (Exception e)
                {
                    throw new RuntimeException("Unable to get the property '" + property + "'", e);
                }

                if (isColumnAKey(keys, column))
                {
                    if (Utils.isPropertyNull(pd.getPropertyType(), value))
                    {
                        throw new SimpleDAOException("Key may not have a null/0 value;");
                    }
                    if (keyCount > 0)
                    {
                        whereSQL.append(" AND ");
                    }
                    whereSQL.append(column);
                    whereSQL.append(" = ?");

                    //todo: this count can go away, just use the size of the key BB list
                    keyCount++;
                    keyBindVariables.add(new BoundVariable(keyCount, column, pd.getPropertyType(), value));
                }
                else
                {
                    Class type = pd.getPropertyType();
                    StringBuilder col = new StringBuilder();
                    if (columnCount > 0)
                    {
                        col.append(", ");
                    }
                    col.append(column);
                    col.append(" = ");

                    if (Utils.isPropertyNull(type, value))
                    {
                        if (def.isNullable())
                        {
                            log.debug("set {0} to NULL", column);
                            col.append("?");
                            columnCount++;
                            bindVariables.add(new BoundVariable(columnCount, column, type, null));
                        }
                        else
                        {
                            continue;
                        }
                    }
                    else
                    {
                        col.append("?");
                        columnCount++;
                        bindVariables.add(new BoundVariable(columnCount, column, type, value));
                    }
                    updateSQL.append(col);
                }
            }

            // add the keys to the bind variable list
            for (BoundVariable bv : keyBindVariables)
            {
                bindVariables.add(new BoundVariable(columnCount + bv.getPosition(), bv.getName(), bv.getType(), bv.getValue()));
            }

            updateSQL.append(whereSQL);
            sql = updateSQL.toString();
        }

        if ( sqlLog.isDebugEnabled() ) { sqlLog.debug("buildUpdateStatement SQL:" + sql); }
        return Utils.prepareStatement(con, sql, bindVariables);
    }

    private PreparedStatement buildDeleteStatement( T bean, BeanDescriptor description,Connection con ) throws SQLException
    {
        ArrayList<BoundVariable> bindVariables = new ArrayList<BoundVariable>();
        StringBuilder sql = new StringBuilder( "DELETE FROM ");

        sql.append( description.getTable() );
        sql.append( " WHERE " );

        int colCount = 0;
        for (String property : description.getPropertyMap().keySet())
        {
            String column = description.getPropertyMap().get( property ).getName();

            PropertyDescriptor pd;
            Object value;
            try
            {
                pd = PropertyUtils.getPropertyDescriptor( bean, property );
                value = PropertyUtils.getProperty ( bean, property );
            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to get the property '" + property + "'",e);
            }
            Class type = pd.getPropertyType();

            if ( !Utils.isPropertyNull( type, value ) )
            {

                if ( colCount > 0 )
                {
                    sql.append(" AND ");
                }
                sql.append( column );
                sql.append( " = ? " );
                colCount ++;
                bindVariables.add( new BoundVariable( colCount, column, type, value ));
            }
        }
        if ( sqlLog.isDebugEnabled() ) { sqlLog.debug("buildDeleteStatement SQL:" + sql); }
        return Utils.prepareStatement(con, sql.toString(), bindVariables);
    }

    private BeanDescriptor getBeanDescriptor( T bean )
    {
        if ( bean instanceof SimpleBean)
            return ((SimpleBean)bean).describe();
        else
            return ReflectionUtils.describeBean(bean);
    }

    private boolean isColumnAKey( String[] keys, String column)
    {
        for ( String key : keys )
        {
            if ( key.equalsIgnoreCase( column ))
            {
				if ( log.isDebugEnabled()) { log.debug("Key column found: " + column) ;}
				return true;
            }
        }
        return false;
    }

    private String bindStaticSQLStatement(T bean, BeanDescriptor descriptor, ArrayList<BoundVariable>  bindVariables)
    {
        String sql = descriptor.getTable();
        String loopsql = sql;
        int paramCount = 0;
        while (loopsql.contains("@"))
        {
            String param;
            int paramStart = loopsql.indexOf("@") + 1;
            int paramEnd = loopsql.indexOf(" ", paramStart);
            if (paramEnd > paramStart)
            {
                param = loopsql.substring(paramStart, paramEnd);
            }
            else
            {
                param = loopsql.substring(paramStart);
            }
            PropertyDescriptor pd;
            Object value;
            try
            {
                pd = PropertyUtils.getPropertyDescriptor(bean, param);
                value = PropertyUtils.getProperty(bean, param);
            } catch (Exception e)
            {
                log.error("Unable to get bean property named '" + param + "'", e);
                throw new RuntimeException("Unable to get the bean property named '" + param + "'", e);
            }
            paramCount++;
            bindVariables.add(new BoundVariable(paramCount, descriptor.getPropertyMap().get(param).getName(), pd.getPropertyType(), value));
            sql = sql.replace("@" + param, "?");
            loopsql = loopsql.substring(loopsql.indexOf("@" + param) + param.length());
        }
        return sql;
    }
}

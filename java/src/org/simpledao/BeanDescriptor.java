package org.simpledao;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Jun 22, 2011
 * Time: 2:41:33 PM
 */
public class BeanDescriptor
{
    private String table;
    private String[] updateKeys;
    private Map<Integer, SortedColumn> orderedColumns;
    private Map<String,ColumnDefinition> propertyMap;

    public BeanDescriptor()
    {
    }

    public BeanDescriptor(String table, String[] updateKeys, Map<Integer, SortedColumn> orderedColumns, Map<String, ColumnDefinition> propertyMap)
    {
        this.table = table;
        this.updateKeys = updateKeys;
        this.orderedColumns = orderedColumns;
        this.propertyMap = propertyMap;
    }

    public String getTable()
    {
        return table;
    }

    public void setTable(String table)
    {
        this.table = table;
    }

    public String[] getUpdateKeys()
    {
        return updateKeys;
    }

    public void setUpdateKeys(String[] updateKeys)
    {
        this.updateKeys = updateKeys;
    }

    public Map<Integer, SortedColumn> getOrderedColumns()
    {
        return orderedColumns;
    }

    public void setOrderedColumns(Map<Integer, SortedColumn> orderedColumns)
    {
        this.orderedColumns = orderedColumns;
    }

    public Map<String, ColumnDefinition> getPropertyMap()
    {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, ColumnDefinition> propertyMap)
    {
        this.propertyMap = propertyMap;
    }


}


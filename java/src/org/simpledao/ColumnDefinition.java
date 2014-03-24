package org.simpledao;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Jun 23, 2011
 * Time: 6:57:10 AM
 */
public class ColumnDefinition
{
    private String name;
    private boolean updateKey;
    private boolean nullable;
    private String nullValue;
    private SortOrder sortOrder;
    private int orderByPosition;
    private Object value;

    public ColumnDefinition()
    {
    }

    public ColumnDefinition(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isUpdateKey()
    {
        return updateKey;
    }

    public void setUpdateKey(boolean updateKey)
    {
        this.updateKey = updateKey;
    }

    public boolean isNullable()
    {
        return nullable;
    }

    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    public String getNullValue() {
        return nullValue;
    }

    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    public SortOrder getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public int getOrderByPosition()
    {
        return orderByPosition;
    }

    public void setOrderByPosition(int orderByPosition)
    {
        this.orderByPosition = orderByPosition;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
}

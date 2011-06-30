package org.simpledao;

/**
 * Bean that represents a bound variable when calling a prepared statement.
 * <p>
 * User: jumiller
 * Date: Mar 16, 2006
 * Time: 2:13:01 PM
 * </p>
 * @author Justin Miller
 * @version 1.0
 */
public class BoundVariable
{
    private int position;
    private String name;
    private Class type;
    private Object value;

    public BoundVariable() {}

    public BoundVariable(int position, String name, Class type, Object value)
    {
        this.position = position;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Class getType()
    {
        return type;
    }

    public void setType(Class type)
    {
        this.type = type;
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

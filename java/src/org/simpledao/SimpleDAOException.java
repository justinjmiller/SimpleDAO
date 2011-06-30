package org.simpledao;

/**
 * <p>A simple exception used by the SimpleDAO framework 
 * User: jumiller
 * Date: Mar 24, 2006
 * Time: 2:32:42 PM
 * @author Justin Miller
 * @version 1.0
 */
public class SimpleDAOException extends RuntimeException
{
    public SimpleDAOException()
    {
        super();
    }

    public SimpleDAOException(String message)
    {
        super(message);
    }

    public SimpleDAOException(Throwable cause)
    {
        super(cause);
    }

    public SimpleDAOException(String message, Throwable cause)
    {
        super(message, cause);
    }
}

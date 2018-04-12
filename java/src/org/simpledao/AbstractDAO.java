package org.simpledao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jumiller
 * Date: Apr 5, 2011
 * Time: 3:34:19 PM
 */
public abstract class AbstractDAO<T>
{
    private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);

    protected SimpleDAO<T> dao = new SimpleDAO<T>();

    protected abstract Connection getConnection() throws SQLException;

    protected void closeConnection( Connection con)
    {
        try
        {
            if (con != null )
            {
                log.debug("close the database connection");
                con.close();
            }
        }
        catch ( SQLException ex)
        {
            log.error(ex.getMessage());
        }
    }

    public List<T> getList(T criteria)  throws SQLException
    {
//        if ( log.isDebugEnabled()) { log.debug("GetList('" + criteria.getClass().getName() + "') - begin");}
        log.debug("GetList('{}' - begin", criteria.getClass().getName() );
        Connection con = null;
        try
        {
            con = getConnection();
            return dao.simpleSelectList(con, criteria);
        }
        catch ( SQLException ex)
        {
            log.error(ex.getMessage());
            throw ex;
        }
        finally
        {
            closeConnection(con);
            if ( log.isDebugEnabled()) { log.debug("GetList('" + criteria.getClass().getName() + "') - end");}
        }
    }

    public T get(T criteria) throws SQLException
    {
        if ( log.isDebugEnabled()) { log.debug("Get('" + criteria.getClass().getName() + "') - begin");}
        Connection con = null;
        try
        {
            con = getConnection();
            return dao.simpleSelect(con, criteria);
        }
        catch (SQLException ex)
        {
            log.error(ex.getMessage());
            throw ex;
        }
        finally
        {
            closeConnection(con);
            if ( log.isDebugEnabled()) { log.debug("Get('" + criteria.getClass().getName() + "') - end");}
        }
    }

    public void insert(T criteria) throws SQLException
    {
        if ( log.isDebugEnabled()) { log.debug("Insert('" + criteria.getClass().getName() + "') - begin");}
        Connection con = null;
        try
        {
            con = getConnection();
            dao.simpleInsert(con, criteria);
        }
        catch ( SQLException ex)
        {
            log.error(ex.getMessage());
            throw ex;
        }
        finally
        {
            closeConnection(con);
            if ( log.isDebugEnabled()) { log.debug("Insert('" + criteria.getClass().getName() + "') - end");}
        }

    }

    public void update(T criteria) throws SQLException
    {
        if ( log.isDebugEnabled()) { log.debug("Update('" + criteria.getClass().getName() + "') - begin");}
        Connection con = null;
        try
        {
            con = getConnection();
            dao.simpleUpdate(con, criteria);
        }
        catch ( SQLException ex)
        {
            log.error(ex.getMessage());
            throw ex;
        }
        finally
        {
            closeConnection(con);
            if ( log.isDebugEnabled()) { log.debug("Update('" + criteria.getClass().getName() + "') - end");}
        }
    }

    public void delete(T criteria) throws SQLException
    {
        if ( log.isDebugEnabled()) { log.debug("GetList('" + criteria.getClass().getName() + "') - begin");}
        Connection con = null;
        try
        {
            con = getConnection();
            dao.simpleDelete(con, criteria);
        }
        catch ( SQLException ex)
        {
            log.error(ex.getMessage());
            throw ex;
        }
        finally
        {
            closeConnection(con);
            if ( log.isDebugEnabled()) { log.debug("Delete('" + criteria.getClass().getName() + "') - end");}
        }
    }

}

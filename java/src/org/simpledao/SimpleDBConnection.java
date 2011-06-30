package org.simpledao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * <p/>
 * User: jumiller
 * Date: Mar 1, 2007
 * Time: 9:04:23 AM
 * </p>
 */
public class SimpleDBConnection
{
    //*****************************************************Instance Variables
    private static Log log = LogFactory.getLog( SimpleDBConnection.class ) ;

    //private String JNDI_DSNAME_PREFIX = "java:comp/env/jdbc/";
    private static String DEFAULT_PROP_FILE = "database.properties";

    private String databaseDriver;
    private String databaseURL;
    private String databaseUser;
    private String databasePassword;
    private boolean usingDatabasePool = false;
    private String propFile;
    private String jndiDSName;
    private Type type;

    public enum Type { PROP_FILE , JNDI_NAME }

    //*****************************************************Constructors
    public SimpleDBConnection()
    {
        type = Type.PROP_FILE;
        propFile = DEFAULT_PROP_FILE;
        readPropFile();
    }

    public SimpleDBConnection( Type type,String namespace )
    {
        if ( log.isDebugEnabled() )
        {
           log.debug("new SimpleDBConnection with type '" + type + "' and namespace '" + namespace + "'");
        }

        this.type = type;
        switch ( type )
        {
            case PROP_FILE:
                this.propFile = namespace;
                readPropFile();
                break;
            case JNDI_NAME:
                this.jndiDSName = namespace;
        }
    }

    //*****************************************************Getters/Setters

    public String getDatabaseDriver()
    {
        return databaseDriver;
    }

    public void setDatabaseDriver(String databaseDriver)
    {
        this.databaseDriver = databaseDriver;
    }

    public String getDatabaseURL()
    {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL)
    {
        this.databaseURL = databaseURL;
    }

    public String getDatabaseUser()
    {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser)
    {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePassword()
    {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword)
    {
        this.databasePassword = databasePassword;
    }

    public boolean isUsingDatabasePool()
    {
        return usingDatabasePool;
    }

    public void setUsingDatabasePool(boolean usingDatabasePool)
    {
        this.usingDatabasePool = usingDatabasePool;
    }

    public String getPropFile()
    {
        return propFile;
    }

    public void setPropFile(String propFile)
    {
        this.propFile = propFile;
        readPropFile();
    }

    public String getJndiDSName()
    {
        return jndiDSName;
    }

    public void setJndiDSName(String jndiDSName)
    {
        this.jndiDSName = jndiDSName;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    //*****************************************************Public Functions

    /**
     *
     * @return
     * @throws java.sql.SQLException
     */
    public Connection getDBConnection() throws SQLException
    {
        if ( log.isDebugEnabled() )
        {
           log.debug("getDBConnection()");
        }

        if ( jndiDSName == null || "".equals( jndiDSName ) )
        {
            if ( usingDatabasePool )
            {
                return getPooledDBConnection();
            }
            else
            {
                return getSingleDBConnection();
            }
        }
        else
        {
            return getJNDIDBConnection();
        }
    }

    private Connection getJNDIDBConnection() throws SQLException
    {
        //jndiDSName = ( jndiDSName == null || "".equals( jndiDSName ) ) ? JNDI_DSNAME_PREFIX + appName + "DB" : jndiDSName;
        if ( "".equals(jndiDSName ) || jndiDSName == null)
        {
            throw new RuntimeException("A JNDI Datasource name must be specified to use the JNDI Connection");
        }

        if ( log.isDebugEnabled() )
        {
           log.debug("getJNDIDBConnection() jndiDSName: " + jndiDSName);
        }

        DataSource ds;
        try
        {

            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup( jndiDSName );
        }
        catch (NamingException ne)
        {
            log.error(ne);
            throw new RuntimeException("The JNDI Datasource name specified '" + jndiDSName + "' was not found");
        }

        return ds.getConnection();

    }

    private void readPropFile()
    {
        Properties props = new Properties();
        try
        {
            props.load( Thread.currentThread().getContextClassLoader().getResourceAsStream( propFile ) );
        }
        catch (Exception e)
        {
            if ( propFile.equals( DEFAULT_PROP_FILE ) )
            {
                // no default prop file found, return
                return;
            }
            else
            {
                log.error(e);
                throw new RuntimeException("Unable to load the properties file '" + propFile + "'");
            }
        }
        databaseURL = props.getProperty( "url" );
        databaseUser = props.getProperty( "user" );
        databasePassword = props.getProperty( "password" );
        databaseDriver = props.getProperty( "driver" );
        jndiDSName = props.getProperty("jndiDSName");
        String useDatabasePool = props.getProperty("usePool");
        usingDatabasePool = "true".equalsIgnoreCase(useDatabasePool) || "yes".equalsIgnoreCase(useDatabasePool);

    }

    private Connection getPooledDBConnection() throws SQLException
    {
        if (log.isDebugEnabled() ) { log.debug("get a pooled database connection"); }
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(databaseURL);
        ds.setUsername(databaseUser);
        ds.setPassword(databasePassword);
        ds.setInitialSize( 10 );
        ds.setMaxIdle( 5 );
        ds.setDriverClassName( databaseDriver );
        return ds.getConnection();
    }

    private Connection getSingleDBConnection() throws SQLException
    {
        if (log.isDebugEnabled() ) { log.debug("get a non-pooled database connection"); }
        try
        {
            Class.forName( databaseDriver);
        }
        catch (ClassNotFoundException e)
        {
            log.error(e);
            throw new RuntimeException("Unable to load the database driver specified '" + databaseDriver + "'");
        }

        return DriverManager.getConnection( databaseURL, databaseUser, databasePassword);
    }

    public void closeDBConnection(Connection con)
    {
        try
        {
            con.close();
        }
        catch (Exception e) {/**/}
    }

}

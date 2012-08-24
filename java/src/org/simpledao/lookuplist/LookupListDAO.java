package org.simpledao.lookuplist;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * <p/>
 * User: jumiller
 * Date: Aug 23, 2006
 * Time: 12:38:47 PM
 * </p>
 */
public class LookupListDAO
{
    // ----------------------------------------------------- Instance Variables
    protected static Logger log = LoggerFactory.getLogger(LookupListDAO.class);
    protected static Logger sqlLog = LoggerFactory.getLogger("SQL");

    private static final String SELECT_LIST_SQL =
            "SELECT #TABLE#_ID AS ID, DESCRIPTION " +
            "FROM #TABLE# #WHERE# ORDER BY DESCRIPTION";

    private LookupListDAO() {}

    private static LookupListDAO lookupList = new LookupListDAO();

    // ----------------------------------------------------- Public Methods

    public static LookupListDAO getInstance()
    {
        return lookupList;
    }

    public ArrayList<LookupListBean> getLookupList(Connection con, String tableName) throws Exception
    {
        ArrayList<LookupListBean> list = new ArrayList<LookupListBean>();
        try
        {
            String sql ;
            Statement stmnt = con.createStatement();

            sql = SELECT_LIST_SQL.replaceAll( "#TABLE#", tableName ).replaceAll( "#WHERE#", ""  );
            if ( sqlLog.isDebugEnabled() )
            { sqlLog.debug("getLookupList SQL: " + sql ); }

            ResultSet rs = stmnt.executeQuery( sql );

            while (rs.next())
            {
                if ( log.isDebugEnabled() )
                { log.debug("getLookupList - add List Item: " + rs.getString("DESCRIPTION") ); }
                list.add( new LookupListBean( rs.getInt( "ID" ),
                                          rs.getString( "DESCRIPTION" )  ) );
            }
        }
        catch (SQLException e)
        {
            log.error("getLookupList: " + e.getMessage());
            throw new Exception("An error occurred while getting the Lookup List '" + tableName + "'",e);
        }

        return list;
    }

    public ArrayList<LookupListBean> getLookupList(Connection con , String tableName, String criteria) throws Exception
    {
        ArrayList<LookupListBean> list = new ArrayList<LookupListBean>();
        String sql ;
        try
        {

            Statement stmnt = con.createStatement();

            sql = SELECT_LIST_SQL.replaceAll( "#TABLE#", tableName ).replaceAll( "#WHERE#", " WHERE " + criteria );

            if ( sqlLog.isDebugEnabled() )
            { sqlLog.debug("getLookupList SQL: " + sql ); }

            ResultSet rs = stmnt.executeQuery( sql );

            while (rs.next())
            {
                if ( log.isDebugEnabled() )
                { log.debug("getLookupList - add List Item: " + rs.getString("DESCRIPTION") ); }
                list.add( new LookupListBean( rs.getInt( "ID" ),
                                          rs.getString( "DESCRIPTION" )  ) );
            }
        }
        catch (SQLException e)
        {
            log.error("getLookupList: " + e.getMessage());
            throw new Exception("An error occurred while getting the Lookup List '" + tableName + "'",e);
        }

        return list;
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Data;
using System.Configuration;
using System.Reflection;
using SimpleDAO.Attributes;
using System.Data.OleDb;
using PropDict = System.Collections.Generic.Dictionary<string, string>;
using OrderByList = System.Collections.Generic.SortedList<int, SimpleDAO.SortedColumn>;

namespace SimpleDAO
{

    public class SimpleDAO<E> where E: class //SimpleObject //where E:class  //where E: SimpleObject
    {
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private Dictionary<string, PropertyInfo> cachedProps;       

        public int NullIntValue { get; set; }

        public string NullStringValue { get; set; }

        public DateTime NullDateTimeValue { get; set; }

        public char WildcardChar { get; set; }

        public string ConnectionString { get; set; }

        public SimpleDAO()
        {
            NullIntValue = 0;
            NullStringValue = null;
            NullDateTimeValue = DateTime.MinValue;
            WildcardChar = '%';
        }

        protected virtual IDbConnection getDBConnection()
        {
            // check the type and act appropriately
            string connectionString;
            try
            {
                connectionString = ConfigurationManager.ConnectionStrings["DB"].ConnectionString;
                
            }
            catch (Exception ex)
            {
                log.Info(ex);
                connectionString = ConnectionString;
            }
            return new OleDbConnection(connectionString);
        }

        public E SimpleSelect(E obj)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                return SimpleSelect(con, obj);
            }
        }

        public E SimpleSelect(IDbConnection con, E obj)
        {
            PropDict props =  new Dictionary<string,string>();
            if (obj is SimpleObject)
            {
                SimpleObject so = obj as SimpleObject;
                props = so.Describe();
            }
            else
                props = ReflectionUtils.GetObjectPropertyMap(obj);

            return SimpleSelect(con, obj, props);
        }


        public E SimpleSelect(IDbConnection con, E obj, PropDict props)
        {
            //SimpleObject newObj = null;
            /*
            IDbCommand cmd = buildSelectDbCommand(obj, props, con);
            IDataReader reader = cmd.ExecuteReader();
            if (reader.Read())
            {
                int columns = reader.FieldCount;
                // loop through the columns
                Dictionary<string,object> dbData = new Dictionary<string,object>();
                for (int i = 0; i < columns; i++)
                {
                    string columnName = reader.GetName(i);
                    string propName = Utils.GetPropertyName(columnName);
                    Object columnValue = reader.GetValue(i);
                    dbData.Add(propName, columnValue);
                }
                //SimpleObject newObj = (SimpleObject)AppDomain.CurrentDomain.CreateInstanceAndUnwrap(obj.GetType().Assembly.GetName().FullName, obj.GetType().FullName);
                //newObj = Activator.CreateInstance(Type.GetType(obj.GetType().Name));
                newObj = Activator.CreateInstance(obj.GetType().Assembly.GetName().FullName, obj.GetType().FullName).Unwrap() as SimpleObject;
                newObj.Populate(dbData);
            }

            reader.Close();
            cmd.Dispose();
            */

            if (log.IsInfoEnabled) { log.Info("SimpleSelect on '" + obj.GetType().FullName + "'"); }

            // null OrderedList as we are only expecting a single result
            // todo: this could be modified to order by the key
            List<E> objList = SimpleSelectList(con, obj, props, null);
            if (objList.Count > 0)
            {
                if (log.IsDebugEnabled) { log.Debug("SimpleSelect - return the populated object"); }

                return objList[0];
            }
            else
            {
                return null; // default(E); // null;
            }
            //return (E)newObj;
        }

        public List<E> SimpleSelectList(E obj)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                return SimpleSelectList(con, obj);
            }
        }

        public List<E> SimpleSelectList( IDbConnection con, E obj)
        {
            PropDict props = new Dictionary<string, string>();
            if (obj is SimpleObject)
            {
                SimpleObject so = obj as SimpleObject;
                props = so.Describe();
            }
            else
                props = ReflectionUtils.GetObjectPropertyMap(obj);
            return SimpleSelectList(con, obj, props);
        }

        public List<E> SimpleSelectList(IDbConnection con, E obj, PropDict props)
        {
            return SimpleSelectList(con, obj, props, getDBOrderBy(obj));
        }
        public List<E> SimpleSelectList(IDbConnection con, E obj, PropDict props, OrderByList orderByList)
        {
            if (log.IsInfoEnabled) { log.Info("SimpleSelectList on '" + obj.GetType().FullName + "'"); }

            List<E> objList = new List<E>();

            IDbCommand cmd = buildSelectCommand(obj, props, con,orderByList);

            if (log.IsDebugEnabled) { log.Debug("SimpleSelectList - ExecuteReader with sql: " + cmd.CommandText); }
            
            IDataReader reader = cmd.ExecuteReader();
            while (reader.Read())
            {
                
                int columns = reader.FieldCount;


                // loop through the columns
                Dictionary<string, object> dbData = new Dictionary<string, object>();
                for (int i = 0; i < columns; i++)
                {
                    if (!reader.GetValue(i).Equals(DBNull.Value))
                    {
                        string columnName = reader.GetName(i);
                        //string propName = Utils.GetPropertyName(columnName);

                        string propName = Utils.GetPropertyName(columnName, props);
                        if (string.IsNullOrEmpty(propName))
                        {
                            propName = Utils.GetPropertyName(columnName);
                            if (  log.IsInfoEnabled)
                                log.Info("no property was found in the list to populate for column named '" + columnName + "', will attempt with property name '" + propName + "'" );
                        }
                        Object columnValue = reader.GetValue(i);
                        dbData.Add(propName, columnValue);
                    }
                }
                E newObj = Activator.CreateInstance(obj.GetType().Assembly.GetName().FullName, obj.GetType().FullName).Unwrap() as E;// as SimpleObject;
                //SimpleObject newObj = (SimpleObject)AppDomain.CurrentDomain.CreateInstanceAndUnwrap(obj.GetType().Assembly.GetName().FullName, obj.GetType().FullName);
                //newObj = Activator.CreateInstance(Type.GetType(obj.GetType().Name));
                ReflectionUtils.PopulateObject(newObj, dbData);
                objList.Add(newObj);
            }

            reader.Close();
            cmd.Dispose();
            return objList;
        }

        public E SimpleInsert(E obj)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                return SimpleInsert(con, obj);
            }
        }

        public E SimpleInsert(IDbConnection con, E obj)
        {
            PropDict props = new Dictionary<string, string>();
            if (obj is SimpleObject)
            {
                SimpleObject so = obj as SimpleObject;
                props = so.Describe();
            }
            else
                props = ReflectionUtils.GetObjectPropertyMap(obj);
            return SimpleInsert(con, obj, props);
        }

        public E SimpleInsert(IDbConnection con, E obj, PropDict props)
        {
            if (log.IsInfoEnabled) { log.Info("SimpleInsert with data from '" + obj.GetType().FullName + "'"); }
            //SimpleObject newObj = null;
            E newObj = default(E);// null;

            IDbCommand cmd = con.CreateCommand();
            StringBuilder tableSQL = new StringBuilder("INSERT INTO ");
            tableSQL.Append(getDBTable(obj)).Append(" (");
            StringBuilder valueSQL = new StringBuilder(" ) VALUES ( ");
            int colCount = 0;

            foreach (KeyValuePair<string, string> prop in props)
            {
                string property = prop.Key;
                string column = prop.Value == null ? Utils.GetDBColumnName(property) : prop.Value;

                PropertyInfo pInfo = obj.GetType().GetProperty(property);
                object propValue = pInfo.GetValue(obj, null);
                if ( !isPropertyNull(pInfo,propValue) )
                {
                    if (colCount > 0)
                    {
                        tableSQL.Append(", ");
                        valueSQL.Append(", ");
                    }
                    tableSQL.Append(column);
                    colCount++;
                    valueSQL.Append("@").Append(property);
                    Utils.AddPropParamToCmd(cmd, property, propValue);
                }
            }
            tableSQL.Append(valueSQL).Append(")");
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = tableSQL.ToString();

            if (log.IsDebugEnabled) { log.Debug("SimpleInsert - execute query: " + tableSQL.ToString()); }
            //cmd.ExecuteNonQuery();
            IDataReader reader =  cmd.ExecuteReader();
            while ( reader.Read() )
            {
                int columns = reader.FieldCount;
                Dictionary<string, object> dbData = new Dictionary<string, object>();
                for (int i = 0; i < columns; i++)
                {
                    if (!reader.GetValue(i).Equals(DBNull.Value))
                    {
                        string columnName = reader.GetName(i);
                        string propName = Utils.GetPropertyName(columnName, props);
                        Object columnValue = reader.GetValue(i);
                        dbData.Add(propName, columnValue);
                    }
                }
                newObj = Activator.CreateInstance(obj.GetType().Assembly.GetName().FullName, obj.GetType().FullName).Unwrap() as E; // as SimpleObject;
                //newObj.Populate(dbData);
                ReflectionUtils.PopulateObject(newObj, dbData);
            }
            reader.Close();
            cmd.Dispose();
            return newObj;
        }

        public void SimpleUpdate(E obj)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                SimpleUpdate(con, obj);
            }
        }

        public void SimpleUpdate(IDbConnection con, E obj)
        {
            PropDict props = new Dictionary<string, string>();
            if (obj is SimpleObject)
            {
                SimpleObject so = obj as SimpleObject;
                props = so.Describe();
            }
            else
                props = ReflectionUtils.GetObjectPropertyMap(obj);
            SimpleUpdate(con, obj, props);
        }

        public void SimpleUpdate(IDbConnection con, E obj, PropDict props)
        {
            SimpleUpdate(con, obj, props, getDBUpdateKey(obj));
        }

        public void SimpleUpdate(IDbConnection con, E obj, string[] keys)
        {
            PropDict props = new Dictionary<string, string>();
            if (obj is SimpleObject)
            {
                SimpleObject so = obj as SimpleObject;
                props = so.Describe();
            }
            else
                props = ReflectionUtils.GetObjectPropertyMap(obj);
            SimpleUpdate(con, obj, props, keys);
        }

        public void SimpleUpdate(IDbConnection con, E obj, PropDict props, string[] keys)
        {
            if (log.IsInfoEnabled) { log.Info("SimpleUpdate with data from '" + obj.GetType().FullName + "'"); }
            
            Dictionary<string, Object> whereProps = new Dictionary<string, object>();

            IDbCommand cmd = con.CreateCommand();

            StringBuilder updateSQL = new StringBuilder("UPDATE ");
            updateSQL.Append(getDBTable(obj)).Append(" SET ");
            StringBuilder whereSQL = new StringBuilder("");
            int colCount = 0;
            int whereCount = 0;
            foreach (KeyValuePair<string, string> prop in props)
            {
                string property = prop.Key;
                string column = prop.Value == null || prop.Value.Equals(string.Empty) ?
                    Utils.GetDBColumnName(property) : 
                    prop.Value;

                PropertyInfo pInfo = obj.GetType().GetProperty(property);
                Object propValue = pInfo.GetValue(obj, null);

                // see if this is in the key list
                if (isColumnKey(getDBUpdateKey(obj), column))
                {
                    // do we need to check for null?
                    //TODO check for null and throw an exception
                    if (whereCount == 0)
                    {
                        whereSQL.Append(" WHERE ");
                    }
                    else
                    {
                        whereSQL.Append(" AND ");
                    }
                    whereCount++;
                    whereSQL.Append(column);
                    if (propValue.ToString().Contains(WildcardChar))
                    {
                        whereSQL.Append(" LIKE @");
                    }
                    else
                    {
                        whereSQL.Append(" = @");
                    }
                    whereSQL.Append(property);
                    whereProps.Add(property, propValue);
                    //Utils.AddPropParamToCmd(cmd, property, propValue);

                }
                else // column is not in the key list, add to update
                {
                    if (!isPropertyNull(pInfo, propValue))
                    {
                        if (colCount > 0)
                        {
                            updateSQL.Append(", ");
                        }
                        updateSQL.Append(column).Append("=@").Append(property);
                        colCount++;
                        Utils.AddPropParamToCmd(cmd, property, propValue);
                    }
                }
            }

            updateSQL.Append(whereSQL);
            foreach (var whereProp in whereProps)
            {
                Utils.AddPropParamToCmd(cmd, whereProp.Key, whereProp.Value);
            }
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = updateSQL.ToString();

            if (log.IsDebugEnabled) { log.Debug("SimpleUpdate - execute query: " + updateSQL.ToString()); }
            
            int rowsUpdated = cmd.ExecuteNonQuery();
            cmd.Dispose();

        }

        public void SimpleDelete(E obj)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                SimpleDelete(con, obj);
            }
        }

        public void SimpleDelete(IDbConnection con, E obj)
        {
            PropDict props = new Dictionary<string, string>();
            if (obj is SimpleObject)
            {
                SimpleObject so = obj as SimpleObject;
                props = so.Describe();
            }
            else
                props = ReflectionUtils.GetObjectPropertyMap(obj);
            SimpleDelete(con, obj, props);
        }

        public void SimpleDelete(IDbConnection con, E obj, PropDict props)
        {
            if (log.IsInfoEnabled) { log.Info("SimpleDelete with data from '" + obj.GetType().FullName + "'"); }

            IDbCommand cmd = con.CreateCommand();
            StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.Append(getDBTable(obj)).Append(" WHERE ");
            int colCount = 0;

            foreach (KeyValuePair<string, string> prop in props)
            {
                string property = prop.Key;
                string column = prop.Value == null ? Utils.GetDBColumnName(property) : prop.Value;

                PropertyInfo pInfo = obj.GetType().GetProperty(property);
                Object propValue = pInfo.GetValue(obj, null);
                if ( !isPropertyNull(pInfo, propValue) )
                {
                    if (colCount > 0)
                    {
                        sql.Append(" AND ");
                    }
                    sql.Append(column);
                    if (propValue.ToString().Contains(WildcardChar))
                    {
                        sql.Append(" LIKE @");
                    }
                    else
                    {
                        sql.Append(" = @");
                    }
                    sql.Append(property);
                    colCount++;
                    Utils.AddPropParamToCmd(cmd, property, propValue);
                }

            }
            cmd.CommandType = CommandType.Text;
            cmd.CommandText = sql.ToString();

            if (log.IsDebugEnabled) { log.Debug("SimpleDelete - execute query: " + sql.ToString()); }
            
            int rowsDeleted = cmd.ExecuteNonQuery();
        }

        private IDbCommand buildSelectCommand(E obj, PropDict props, IDbConnection con, OrderByList orderedColumns )
        {
            IDbCommand cmd = con.CreateCommand();
            cmd.CommandType = CommandType.Text;
            //Regex sqlRegEx = new Regex("SELECT.+FROM");
            String dbTable = getDBTable(obj);
            if (dbTable.ToUpper().Contains("SELECT ") && dbTable.ToUpper().Contains("FROM "))
            {
                string sql = dbTable;
                // bind the variables
                //todo: handle ORACLE or ?
                while (  sql.Contains("@") )
                {
                    int paramStart = sql.IndexOf("@") + 1;
                    int paramEnd = sql.IndexOf(" ", paramStart);
                    string param;
                    if (paramEnd > paramStart)
                    {
                        param = sql.Substring(paramStart, paramEnd - paramStart);
                    }
                    else
                    {
                        param = sql.Substring(paramStart, sql.Length - paramStart);
                    }
                    PropertyInfo pInfo = obj.GetType().GetProperty(param);
                    if ( pInfo != null )
                        Utils.AddPropParamToCmd(cmd, param, pInfo.GetValue(obj, null));
                    sql = sql.Substring(sql.IndexOf( '@' + param) + param.Length + 1);
                    // figure out the parameter going backwards
                    /*
                    int charindex = sql.IndexOf("?") - 1;
                    do
                    {
                        if (" ".Equals(sql[charindex]))
                        {
                            string temp = sql.Substring(charindex, sql.IndexOf("?") - charindex);
                            paramFound = true;
                        }
                        else
                        {
                            charindex--;
                        }
                    }
                    while (paramFound == false);
                    sql = sql.Substring(sql.IndexOf("?")+ 1);
                    */
                }
                cmd.CommandText = dbTable;

            }
            else
            {
                StringBuilder sb = new StringBuilder("SELECT ");
                StringBuilder wb = null;
                StringBuilder ob = null;

                int colCount = 0;
                foreach (KeyValuePair<string, string> prop in props)
                {
                    string property = prop.Key;
                    //string column = prop.Value == null ? Utils.GetDBColumnName(property) : prop.Value
                    /*
                    if (string.IsNullOrEmpty(prop.Value))
                    {
                        //props.Remove(property);
                        props[property]. = Utils.GetDBColumnName(property);
                    }

                    string column = prop.Value;*/
                    string column = prop.Value ?? Utils.GetDBColumnName(property);

                    if (log.IsDebugEnabled) { log.Debug("buildSelectDbCommand - get property '" + property + "' for column '" + column + "'"); }

                    PropertyInfo pInfo = obj.GetType().GetProperty(property);
                    if (colCount > 0)
                    {
                        sb.Append(", ");
                    }
                    sb.Append(column);
                    colCount++;

                    Object propValue = pInfo.GetValue(obj, null);
                    if (!isPropertyNull(pInfo,propValue) || isPropertyNullable(pInfo))
                    {
                        if (wb == null)
                        {
                            wb = new StringBuilder(" WHERE ");
                        }
                        else
                        {
                            wb.Append(" AND ");
                        }
                        if (propValue == null)
                        {
                            wb.Append(column).Append(" IS NULL");
                        }
                        else
                        {
                            wb.Append(column);
                            if (propValue.ToString().Contains(WildcardChar))
                            {
                                wb.Append(" LIKE @");
                            }
                            else
                            {
                                wb.Append(" = @");
                            }
                            wb.Append(property);
                            Utils.AddPropParamToCmd(cmd, property, propValue);
                        }
                    }
                    /*
                    if (obj.DBOrderBy != null && obj.DBOrderBy.ContainsKey(prop.Key) )
                    {
                        if (ob == null)
                        {
                            ob = new StringBuilder(" ORDER BY ");
                        }
                        else
                        {
                            ob.Append(", ");
                        }
                        ob.Append(column);
                        if ( !obj.DBOrderBy[prop.Key] )
                        {
                            ob.Append(" DESC");
                        }

                    }
                    */

                }

                // deal with order by
                if (orderedColumns != null && orderedColumns.Count > 0)
                {
                    //ob = new StringBuilder(" ORDER BY ");
                    foreach (KeyValuePair<int, SortedColumn> orderBy in orderedColumns)
                    {
                        // see if the property has an associated column in props
                        if (props.ContainsValue(orderBy.Value.Name.ToUpper()))
                        {
                            if (ob == null)
                            {
                                ob = new StringBuilder(" ORDER BY ");
                            }
                            else
                            {
                                ob.Append(", ");
                            }
                            ob.Append(orderBy.Value.Name);
                            if (orderBy.Value.Order == SortOrder.Descending)
                            {
                                ob.Append(" DESC");
                            }
                        }
                    }
                }
                sb.Append(" FROM ").Append(dbTable).Append(wb).Append(ob);
                cmd.CommandText = sb.ToString();
            }
            return cmd;
        }

        //private bool isValueNull(object value, string propTypeName)
        private bool isPropertyNull(PropertyInfo pInfo, object value)
        {
            string propTypeName = pInfo.PropertyType.Name;
            string nullValue = null;
            object[] attributes = pInfo.GetCustomAttributes(typeof(ColumnAttribute), false);
            if (attributes.Length > 0)
            {
                ColumnAttribute colAttr = attributes[0] as ColumnAttribute;
                nullValue = colAttr.NullValue;
            }

            //todo: use the nullValue attribute

            //Type propType = value.GetType();
            //string propTypeName = propType.Name;
            if ( propTypeName.Equals("Int32"))
            {
                if ((int)value == NullIntValue )
                    return true;
            }
            else if (propTypeName.Equals("String"))
            {
                if ((string)value == NullStringValue)
                    return true;
            }
            else if (propTypeName.Equals("DateTime"))
            {
                if ((DateTime)value == NullDateTimeValue)
                    return true;
            }
            else if (propTypeName.Equals("Single"))
            {
                if ((Single)value == NullIntValue)
                    return true;
            }
            else if (propTypeName.Equals("Double"))
            {
                if ((Double)value == NullIntValue)
                    return true;
            }
            else if (propTypeName.Equals("Nullable`1"))
            {
                return value == null;
            }
            return false;
        }

        private bool isColumnKey(string[] columns, string column)
        {
            foreach (string col in columns)
            {
                if (col.ToUpper().Equals(column.ToUpper()))
                    return true;
                
            }
            return false;
        }


        private String getDBTable(E temp)
        {
            if (temp is SimpleObject)
                return (temp as SimpleObject).DBTable;
            else
            {
                return ReflectionUtils.InferDBTable(temp);
            }
        }

        private OrderByList getDBOrderBy(E temp)
        {
            if (temp is SimpleObject)
                return (temp as SimpleObject).DBOrderBy;
            else
            {
                return ReflectionUtils.GetDBOrderBy(temp);
            }
        }

        private string[] getDBUpdateKey(E temp)
        {
            if (temp is SimpleObject)
                return (temp as SimpleObject).DBUpdateKey;
            else
                return ReflectionUtils.InferDBUpdateKeys(temp);

        }

        private bool isPropertyNullable(PropertyInfo prop)
        {
            object[] attributes = prop.GetCustomAttributes(typeof(ColumnAttribute), false);
            if (attributes.Length > 0)
            {
                ColumnAttribute colAttr = attributes[0] as ColumnAttribute;
                return colAttr.Nullable;
            }
            else
            {
                return false;
            }
        }

    }
}

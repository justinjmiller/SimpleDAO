using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;
using SimpleDAO.Attributes;

namespace SimpleDAO
{
    public class ReflectionUtils
    {
        public static Dictionary<string, string> GetObjectPropertyMap(Object obj)
        {
            Dictionary<string, string> propMap = new Dictionary<string, string>();
            PropertyInfo[] props = obj.GetType().GetProperties();
            foreach (PropertyInfo prop in props)
            {
                object[] attributes = prop.GetCustomAttributes(typeof(ColumnAttribute), false);
                if (attributes.Length > 0)
                {
                    ColumnAttribute colAttr = attributes[0] as ColumnAttribute;
                    propMap.Add(prop.Name, colAttr.Name);
                }
                else
                {
                    if (prop.CanRead && prop.CanWrite && !Utils.IsCollection(prop.PropertyType))
                    {
                        propMap.Add(prop.Name, Utils.GetDBColumnName(prop.Name));
                    }
                }
            }
            return propMap;
        }


        public static String InferDBTable(Object temp)
        {
            object[] attrs = temp.GetType().GetCustomAttributes(typeof(TableAttribute), false);
            if (attrs.Length > 0)
            {
                return (attrs[0] as TableAttribute).Name;
            }
            else
            {
                return Utils.GetDBColumnName(temp.GetType().Name);
            }
        }

        public static string[] InferDBUpdateKeys(Object obj)
        {
            List<string> keys = new List<string>();
            string guessedKey = null;

            PropertyInfo[] props = obj.GetType().GetProperties();
            foreach (PropertyInfo prop in props)
            {
                object[] attributes = prop.GetCustomAttributes(typeof(ColumnAttribute), false);
                if (attributes.Length > 0)
                {
                    ColumnAttribute colAttr = attributes[0] as ColumnAttribute;
                    if (colAttr.UpdateKey)
                        keys.Add(colAttr.Name ?? Utils.GetDBColumnName(prop.Name));
                }
                if ("Id".Equals(prop.Name) || prop.Name.Equals(obj.GetType().Name + "Id"))
                {
                    guessedKey = Utils.GetDBColumnName(prop.Name);
                }

            }
            if (keys.Count == 0 && !string.IsNullOrEmpty(guessedKey))
            {
                // no column attributes found with the UpdateKey value.  Take a guess
                keys.Add(guessedKey);
            }
            return keys.ToArray();
        }

        public static SortedList<int, SortedColumn> GetDBOrderBy(Object obj)
        {
            SortedList<int, SortedColumn> columns = new SortedList<int, SortedColumn>();
            //Dictionary<string, SortOrder> columns = new Dictionary<string, SortOrder>();
            PropertyInfo[] props = obj.GetType().GetProperties();
            foreach (PropertyInfo prop in props)
            {
                object[] attributes = prop.GetCustomAttributes(typeof(ColumnAttribute), false);
                if (attributes.Length > 0)
                {
                    ColumnAttribute colAttr = attributes[0] as ColumnAttribute;
                    if (colAttr.OrderByPosition > 0 && colAttr.OrderBy > 0)
                        //columns.Add(colAttr.Name ?? GetDBColumnName(prop.Name), colAttr.OrderBy);
                        columns[colAttr.OrderByPosition] = new SortedColumn(colAttr.Name ?? Utils.GetDBColumnName(prop.Name), colAttr.OrderBy);
                    //columns.Add(colAttr.OrderByPosition, new SortedColumn(colAttr.Name ?? GetDBColumnName(prop.Name), colAttr.OrderBy));
                }
            }
            return columns;
        }

        public static void PopulateObject(Object obj, Dictionary<string, object> props)
        {
            PropertyInfo[] propList = obj.GetType().GetProperties();
            foreach (KeyValuePair<string, object> prop in props)
            {
                string propName = prop.Key;
                object propValue = prop.Value;
                //todo: possibly use this PropertyInfo propInfo = GetType().GetProperty(propName, propValue.GetType());
                PropertyInfo propInfo = obj.GetType().GetProperty(propName);
                if (propInfo != null && propInfo.CanWrite)
                {
                    MethodInfo method = propInfo.GetSetMethod();
                    if (propValue.GetType() != propInfo.PropertyType)
                    {
                        propValue = Utils.ChangeType(propValue, propInfo.PropertyType);
                    }
                    method.Invoke(obj, new object[] { propValue });
                }


            }
        }
    }
}

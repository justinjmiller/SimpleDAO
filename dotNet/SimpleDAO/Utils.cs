using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using System.ComponentModel;
using System.Reflection;
using SimpleDAO.Attributes;

namespace SimpleDAO
{
    public class Utils
    {
        public static string GetDBColumnName(string property)
        {
            StringBuilder dbColumnName = new StringBuilder();
            char[] chars = property.ToCharArray();
            for (int i = 0; i < property.Length; i++)
            {
                if ( Char.IsUpper(chars[i]) && i > 1)
                {
                    dbColumnName.Append("_");
                    dbColumnName.Append(chars[i]);
                }
                else
                {
                    dbColumnName.Append(chars[i]);
                }
            }
            return dbColumnName.ToString().ToUpper();
        }

        public static string GetPropertyName(string dbColumn)
        {
            StringBuilder propertyName = new StringBuilder();

            if (dbColumn.IndexOf("_") > 0)
            {
                dbColumn = dbColumn.ToLower();
                String[] parts = dbColumn.Split("_".ToCharArray());

                for (int i = 0; i < parts.Length; i++)
                {
//                    if (i == 0)
//                    {
//                        propertyName.Append(parts[i].ToLower());
//                    }
//                    else
                    {
                        propertyName.Append(parts[i].Substring(0, 1).ToUpper());
                        propertyName.Append (parts[i].Substring(1).ToLower());
                    }
                }
            }
            else
            {
                propertyName.Append(dbColumn.Substring(0, 1).ToUpper());
                propertyName.Append(dbColumn.Substring(1).ToLower());
//                propertyName.Append(dbColumn.ToLower());
            }

            return propertyName.ToString();
        }

        public static void AddPropParamToCmd(IDbCommand cmd, string propName, object propValue, char ParameterIndicator)
        {
            IDataParameter param = cmd.CreateParameter();
            param.ParameterName = ParameterIndicator + propName;
            param.Direction = ParameterDirection.Input;
            if (propValue == null)
            {
                param.Value = DBNull.Value;
            }
            else
            {
                Type propType = propValue.GetType();
                string propTypeName = propType.Name;
                switch (propTypeName)
                {
                    case "Int32":
                        param.DbType = DbType.Int32;
                        if (propValue == null)
                            param.Value = null;
                        else
                            param.Value = (int)propValue;
                        break;
                    case "String":
                        param.DbType = DbType.String;
                        param.Value = propValue == null ? null : (string)propValue;
                        break;
                    case "DateTime":
                        param.DbType = DbType.DateTime;
                        if (propValue == null)
                            param.Value = null;
                        else
                            param.Value = (DateTime)propValue;
                        break;
                    case "Single":
                        param.DbType = DbType.Single;
                        if (propValue == null)
                            param.Value = null;
                        else
                            param.Value = (Single)propValue;
                        break;
                    default:
                        if (propValue == null)
                            param.Value = null;
                        else
                            param.Value = propValue.ToString();
                        break;


                }

            }
            cmd.Parameters.Add(param);
        }

        public static string GetPropertyName(string column, Dictionary<string, string> props)
        {
            return (from p in props
                    where p.Value != null && p.Value.Equals(column, StringComparison.InvariantCultureIgnoreCase)
                    select p).FirstOrDefault().Key;
            /*
            foreach (KeyValuePair<string,string> prop in props)
            {
                if ( prop.Value.Equals(column,StringComparison.CurrentCultureIgnoreCase))
                {
                    return prop.Key;
                }
            }
            return null;*/
        }

        public static object ChangeType(object value, Type conversionType)
        {
            if (conversionType == null)
            {
                throw new ArgumentNullException("conversionType");
            } 

            // If it's not a nullable type, just pass through the parameters to Convert.ChangeType

            if (conversionType.IsGenericType &&
              conversionType.GetGenericTypeDefinition().Equals(typeof(Nullable<>)))
            {
                // It's a nullable type, so instead of calling Convert.ChangeType directly which would throw a
                // InvalidCastException (per http://weblogs.asp.net/pjohnson/archive/2006/02/07/437631.aspx),
                // determine what the underlying type is
                // If it's null, it won't convert to the underlying type, but that's fine since nulls don't really
                // have a type--so just return null
                // Note: We only do this check if we're converting to a nullable type, since doing it outside
                // would diverge from Convert.ChangeType's behavior, which throws an InvalidCastException if
                // value is null and conversionType is a value type.
                if (value == null)
                {
                    return null;
                } // end if

                // It's a nullable type, and not null, so that means it can be converted to its underlying type,
                // so overwrite the passed-in conversion type with this underlying type
                NullableConverter nullableConverter = new NullableConverter(conversionType);
                conversionType = nullableConverter.UnderlyingType;
            } // end if

            // Now that we've guaranteed conversionType is something Convert.ChangeType can handle (i.e. not a
            // nullable type), pass the call on to Convert.ChangeType
            return Convert.ChangeType(value, conversionType);
        }

        public static bool IsCollection(Type type)
        {
            return typeof(System.Collections.ICollection).IsAssignableFrom(type)
                || typeof(ICollection<>).IsAssignableFrom(type);
        }


     
    }
}

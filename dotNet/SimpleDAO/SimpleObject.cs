using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Data;
using SimpleDAO.Attributes;

namespace SimpleDAO
{
    [Serializable]
    public abstract class SimpleObject
    {
        protected string _DBTable;
        protected string[] _DBUpdateKey;

        public virtual Dictionary<string, string> Describe()
        {
            Dictionary<string, string> propList = new Dictionary<string, string>();
            PropertyInfo[] props = this.GetType().GetProperties();
            foreach (PropertyInfo prop in props)
            {
                object[] attributes = prop.GetCustomAttributes(typeof(ColumnAttribute), false);
                if (attributes.Length > 0)
                {
                    ColumnAttribute colAttr = attributes[0] as ColumnAttribute;
                    propList.Add(prop.Name, colAttr.Name);
                }
                else
                {
                    if (prop.CanRead && prop.CanWrite && !prop.Name.StartsWith("DB") && !Utils.IsCollection(prop.PropertyType))
                    {
                        propList.Add(prop.Name, Utils.GetDBColumnName(prop.Name));
                    }
                }
            }
            return propList;
        }

        public virtual void Populate(Dictionary<string, object> props)
        {
            PropertyInfo[] propList = GetType().GetProperties();
            foreach (KeyValuePair<string,object> prop in props)
            {
                string propName = prop.Key;
                object propValue = prop.Value;
                //todo: possibly use this PropertyInfo propInfo = GetType().GetProperty(propName, propValue.GetType());
                PropertyInfo propInfo = GetType().GetProperty(propName);
                    if (propInfo != null && propInfo.CanWrite)
                {
                    MethodInfo method = propInfo.GetSetMethod();
                    if (propValue.GetType() != propInfo.PropertyType)
                    {
                        //propValue = Convert.ChangeType(propValue, propInfo.PropertyType);
                        propValue = Utils.ChangeType(propValue, propInfo.PropertyType);
                    }
                    method.Invoke(this,new object[]{propValue});
                }
                /*
                Type propType = propValue.GetType();
                string propTypeName = propType.Name;
                switch (propTypeName)
                {
                    case "Int32":
                        break;
                }
                */
                
                
            }
        }
        
        public virtual string DBTable
        {
            get 
            {
                if (string.IsNullOrEmpty(_DBTable))
                {
                    //_DBTable = Utils.GetDBColumnName( this.GetType().Name.Replace( "Object","") );
                    _DBTable = ReflectionUtils.InferDBTable(this);
                }
                return _DBTable;
            }
            set { _DBTable = value; }
            
        }

        public virtual string[] DBUpdateKey
        {
            get
            {
                if (_DBUpdateKey == null || String.Empty.Equals(_DBUpdateKey))
                {
                    /*
                    _DBUpdateKey = new string[1];
                    if (string.IsNullOrEmpty(_DBTable))
                    {
                        _DBUpdateKey[0] = Utils.GetDBColumnName(this.GetType().Name.Replace("Object", "")) + "_ID";
                    }
                    else
                    {
                        _DBUpdateKey[0] = _DBTable + "_ID";
                    }
                     */
                    _DBUpdateKey = ReflectionUtils.InferDBUpdateKeys(this);
                    return _DBUpdateKey;
                }
                else
                {
                    return _DBUpdateKey;
                }
            }
            set 
            {
                Dictionary<string, string> props = Describe();
                foreach (var column in value)
                {
                    if (!props.ContainsValue(column))
                    {
                        throw new ArgumentException("column '" + column + "' is not defined and cannot be used as an update key");
                    }
                }
                _DBUpdateKey = value; 
            }
        }

        public virtual SortedList<int,SortedColumn> DBOrderBy { get; set; }

        public virtual void GetObject<E>(IDbConnection con) where E: SimpleObject
        {
            SimpleDAO<E> dao = new SimpleDAO<E>();
            List<E> list = dao.SimpleSelectList(con, (E)this);
            if (list.Count > 0)
            {
            }
        }

        public virtual List<E> GetList<E>(IDbConnection con)  where E: SimpleObject
        {
            SimpleDAO<E> dao = new SimpleDAO<E>();
            return dao.SimpleSelectList(con, (E)this);
        }

        public virtual SimpleObject Insert(IDbConnection con)
        {
            SimpleDAO<SimpleObject> dao = new SimpleDAO<SimpleObject>();
            return dao.SimpleInsert(con, this);
        }

        public virtual void Update(IDbConnection con)
        {
            SimpleDAO<SimpleObject> dao = new SimpleDAO<SimpleObject>();
            dao.SimpleUpdate(con, this);
        }

        public virtual void Delete(IDbConnection con)
        {
            SimpleDAO<SimpleObject> dao = new SimpleDAO<SimpleObject>();
            dao.SimpleDelete(con, this);
        }

    }
}

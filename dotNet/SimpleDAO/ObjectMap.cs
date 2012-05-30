using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SimpleDAO
{
    class ObjectMap
    {
        public string DBTable { get; set; }
        public List<PropertyMap> PropertyList { get; set; }
        public SortedList<int,SortedColumn> OrderList { get; set; }
    }

    class PropertyMap
    {
        public string PropertyName { get; set; }
        public string ColumnName { get; set; }
        public bool UpdateKey { get; set; }
        public bool Nullable { get; set; }
        public object NullValue { get; set; }
    }
}

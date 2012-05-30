using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SimpleDAO
{
    public class SimpleLookupObject: SimpleObject
    {
        public int Id { get; set; }
        public string Name { get; set; }

        public override string[]  DBUpdateKey 
        { 
            get 
            {
                if (_DBUpdateKey == null || String.Empty.Equals(_DBUpdateKey))
                {
                    _DBUpdateKey = new string[] { "ID" };
                }
                return _DBUpdateKey;
            } 
        }

        public SimpleLookupObject()
        {
            SortedList<int, SortedColumn> orderBy = new SortedList<int, SortedColumn>();
            orderBy.Add(1,new SortedColumn("Name", SortOrder.Ascending));
            DBOrderBy = orderBy;
            
        }

        public SimpleLookupObject( int id, string name) :this()
        {
            Id = id;
            Name = name;
        }

    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SimpleDAO
{
    public class SortedColumn
    {
        public string Name { get; set; }
        public SortOrder Order { get; set; }

        public SortedColumn(string name, SortOrder order)
        {
            Name = name;
            Order = order;
        }
    }
}

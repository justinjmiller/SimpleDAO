using System;

namespace SimpleDAO.Attributes
{
    public class OrderByAttribute : Attribute
    {
        public SortOrder SortOrder { get; set; }
        public OrderByAttribute(SortOrder sortOrder)
        {
            SortOrder = sortOrder;
        }
    }
}

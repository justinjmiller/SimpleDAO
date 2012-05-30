using System;

namespace SimpleDAO.Attributes
{
    [AttributeUsage(AttributeTargets.Property,AllowMultiple=true,Inherited=true)]
    public class ColumnAttribute : Attribute
    {
        public string Name { get; set; }
        public SortOrder OrderBy { get; set; }
        public bool Nullable { get; set; }
        public bool UpdateKey { get; set; }
        public int OrderByPosition { get; set; }
        public string NullValue { get; set; }

        public ColumnAttribute(string name)
        {
            Name = name;
        }
    }
}

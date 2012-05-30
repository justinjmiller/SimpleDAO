using System;

namespace SimpleDAO.Attributes
{
    [AttributeUsage(AttributeTargets.Class,Inherited=true,AllowMultiple=false)]
    public class TableAttribute : Attribute
    {
        public string Name { get; set; }
        public TableAttribute( string name)
        {
            Name = name;
        }
    }
}

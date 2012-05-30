using System;

namespace SimpleDAO.Attributes
{
    [AttributeUsage(AttributeTargets.Property,AllowMultiple=true,Inherited=true)]
    public class PrimaryKeyAttribute: Attribute
    {
    }
}

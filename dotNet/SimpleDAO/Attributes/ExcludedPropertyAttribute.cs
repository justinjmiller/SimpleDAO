using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SimpleDAO.Attributes
{
    [AttributeUsage(AttributeTargets.Property, AllowMultiple = true, Inherited = true)]
    public class ExcludedPropertyAttribute : Attribute
    {
    }
}

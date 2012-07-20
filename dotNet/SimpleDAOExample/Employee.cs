using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleDAO;
using SimpleDAO.Attributes;

namespace SimpleDAOExample
{
    public class Employee : SimpleObject
    {
        public Employee()
        {

        }

        public int Id { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public int Status { get; set; }
        public string StatusName { get; set; }
    }
}

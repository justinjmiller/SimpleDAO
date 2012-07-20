using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleDAO.Attributes;
using SimpleDAO;

namespace SimpleDAOTest
{
    [Table("EMPLOYEE")]
    class MockSimpleAttrributedObject  : SimpleObject
    {
        public int Id { get; set; }

        public string CommonName { get; set; }

        public int Status { get; set; }

        public String FullName { get; set; }

        public DateTime CreateDate { get; set; }

        public MockSimpleAttrributedObject(int id)
        {
            Id = id;
        }

    }
}

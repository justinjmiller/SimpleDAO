using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleDAO.Attributes;
using SimpleDAO;

namespace SimpleDAOTest
{
    [TableAttribute("MOCK_SIMPLE")]
    class MockSimpleObject : SimpleObject
    {
        public MockSimpleObject()
        {

        }

        public int Id { get; set; }

        public string CommonName { get; set; }

        public int? Status { get; set; }

        public String FullName { get; set; }

        public DateTime? CreateDate { get; set; }

        public MockSimpleObject(int id)
        {
            Id = id;
        }

    }
}

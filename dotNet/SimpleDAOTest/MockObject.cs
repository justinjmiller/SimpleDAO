using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleDAO.Attributes;
using SimpleDAO;

namespace SimpleDAOTest
{
    [Table("MOCK_SIMPLE")]
    class MockObject
    {
        [Column("ID", UpdateKey=true)]
        public int Id { get; set; }

        public string CommonName { get; set; }

        public int? Status { get; set; }

        public String FullName { get; set; }

        [Column("CREATE_DATE",OrderByPosition=3,OrderBy=SortOrder.Ascending)]
        public DateTime CreateDate { get; set; }

    }

    [Table("EMPLOYEE")]
    class Employee : MockObject
    {
        [Column("FIRST_NAME", OrderBy = SortOrder.Ascending, OrderByPosition = 2)]
        public string FirstName { get; set; }

        [Column("LAST_NAME", OrderBy = SortOrder.Ascending, OrderByPosition = 1)]
        public string LastName { get; set; }
    }
}

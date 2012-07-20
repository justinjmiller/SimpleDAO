using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Data;
using System.Data.SqlClient;
using System.Data.OleDb;
using SimpleDAO;

namespace SimpleDAOTest
{
    /// <summary>
    /// Summary description for UnitTest1
    /// </summary>
    [TestClass]
    public class UnitTest1
    {
        public UnitTest1()
        {
            //
            // TODO: Add constructor logic here
            //
        }

        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }

        #region Additional test attributes
        //
        // You can use the following additional attributes as you write your tests:
        //
        // Use ClassInitialize to run code before running the first test in the class
        // [ClassInitialize()]
        // public static void MyClassInitialize(TestContext testContext) { }
        //
        // Use ClassCleanup to run code after all tests in a class have run
        // [ClassCleanup()]
        // public static void MyClassCleanup() { }
        //
        // Use TestInitialize to run code before running each test 
        // [TestInitialize()]
        // public void MyTestInitialize() { }
        //
        // Use TestCleanup to run code after each test has run
        // [TestCleanup()]
        // public void MyTestCleanup() { }
        //
        #endregion

        [TestMethod]
        public void TestSimpleSelect()
        {
            MockSimpleObject obj = new MockSimpleObject();
            Dictionary<string, string> props = new Dictionary<string, string>();
            props.Add("Id", null);
            props.Add("CommonName", null);
            props.Add("FullName", "full_name");
            obj.CommonName = null;
            obj.Id = 5;

            //obj.FullName = string.Empty;
            //SqlConnection con = new SqlConnection("Data Source=(local);Initial Catalog=JPAS;User ID=jpas_user;Password=jpas2007");
            OleDbConnection con = new OleDbConnection(@"Provider=Microsoft.Jet.OLEDB.4.0;Data Source=C:\dev\SimpleDAO\SimpleDAOTest\SimpleDAO.mdb");
            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            SimpleObject newObj = dao.SimpleSelect(con, obj,props);
            con.Close();
        }

        [TestMethod]
        public void TestSimpleSelectList()
        {
            MockSimpleObject obj = new MockSimpleObject();
            //obj.DBOrderBy = new Dictionary<string, bool>();
            //obj.DBOrderBy.Add("Status", true);
            obj.Status = 1;
            //obj.CommonName = "cn10%";
            //SqlConnection con = new SqlConnection("Data Source=(local);Initial Catalog=JPAS;User ID=jpas_user;Password=jpas2007");
            OleDbConnection con = new OleDbConnection(@"Provider=Microsoft.Jet.OLEDB.4.0;Data Source=C:\dev\SimpleDAO\SimpleDAOTest\SimpleDAO.mdb");

            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            dao.NullStringValue = null;
            dao.NullIntValue = 0;
            List<MockSimpleObject> list = dao.SimpleSelectList(con, obj);
            con.Close();
            Assert.IsTrue(list.Count == 3);
        }

        [TestMethod]
        public void TestSimpleInsert()
        {
            MockSimpleObject obj = new MockSimpleObject();
            obj.CommonName = "testInsert";
            obj.CreateDate = DateTime.Today;
            obj.FullName = "Test Insert";
            obj.Id = 99;
            obj.Status = 4;
            //SqlConnection con = new SqlConnection(@"Data Source=(local);Initial Catalog=JPAS;User ID=jpas_user;Password=jpas2007");
            OleDbConnection con = new OleDbConnection(@"Provider=Microsoft.Jet.OLEDB.4.0;Data Source=C:\dev\SimpleDAO\SimpleDAOTest\SimpleDAO.mdb");
            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            dao.SimpleInsert(con, obj);
            con.Close();
        }

        [TestMethod]
        public void TestSimpleDelete()
        {
            MockSimpleObject obj = new MockSimpleObject();
            obj.Id = 99;
//            SqlConnection con = new SqlConnection("Data Source=(local);Initial Catalog=JPAS;User ID=jpas_user;Password=jpas2007");
            OleDbConnection con = new OleDbConnection(@"Provider=Microsoft.Jet.OLEDB.4.0;Data Source=C:\dev\SimpleDAO\SimpleDAOTest\SimpleDAO.mdb");
            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            dao.NullStringValue = null;
            dao.NullIntValue = 0;
            dao.SimpleDelete(con, obj);
            con.Close();
        }

        [TestMethod]
        public void TestSimpleUpdate()
        {
            MockSimpleObject obj = new MockSimpleObject();
            string[] key = new string[] { "ID" };
            obj.DBUpdateKey = key;
            obj.Id = 99;
            obj.CommonName = "object1";
            obj.CreateDate = DateTime.Now;
            obj.FullName = "Object One";
            obj.Status = 5;
//            SqlConnection con = new SqlConnection("Data Source=(local);Initial Catalog=JPAS;User ID=jpas_user;Password=jpas2007");
            OleDbConnection con = new OleDbConnection(@"Provider=Microsoft.Jet.OLEDB.4.0;Data Source=C:\dev\SimpleDAO\SimpleDAOTest\SimpleDAO.mdb");
            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            dao.SimpleUpdate(con, obj);
            con.Close();
        }

    }
}

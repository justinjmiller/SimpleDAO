using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Data;
using System.Data.SqlClient;
using System.Data.OleDb;
using System.Data.OracleClient;

using SimpleDAO;

namespace SimpleDAOTest
{
    /// <summary>
    /// Summary description for UnitTest1
    /// </summary>
    [TestClass]
    public class SimpleDAOTest
    {
        private static readonly string connectString = @"Provider=Microsoft.Jet.OLEDB.4.0;Data Source=..\..\..\SimpleDaoTest\Data\SimpleDAO.mdb";
        public SimpleDAOTest()
        {
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
            MockSimpleObject criteria = new MockSimpleObject();
            criteria.Id = 5;
            OleDbConnection con = new OleDbConnection(connectString);
            //OracleConnection con = new OracleConnection(connectString);
            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>(':');

            MockSimpleObject newObj = dao.SimpleSelect(con, criteria);
            Assert.AreEqual(newObj.Id, 5);
            newObj = null;

            Dictionary<string, string> props = new Dictionary<string, string>();
            props.Add("Id", null);
            props.Add("CommonName", null);
            props.Add("FullName", "FULL_NAME");
            newObj = dao.SimpleSelect(con, criteria, props);
            con.Close();
            Assert.AreEqual(newObj.Id, 5);

        }

        [TestMethod]
        public void TestAttributeSimpleSelect()
        {
            Employee criteria = new Employee();
            criteria.Id = 5;
            OleDbConnection con = new OleDbConnection(connectString);
            con.Open();
            SimpleDAO<MockObject> dao = new SimpleDAO<MockObject>();
            MockObject returned = dao.SimpleSelect(con, criteria);
            Assert.AreEqual(returned.Id, criteria.Id);


        }

        [TestMethod]
        public void TestSimpleSelectWithSQL()
        {
            MockSimpleObject criteria = new MockSimpleObject();
            criteria.Id = 5;
            criteria.DBTable = "SELECT * FROM MOCK_SIMPLE WHERE ID= @Id AND COMMON_NAME=@CommonName AND FULL_NAME =@FullName";
            //obj.DBTable = "SELECT * FROM SOME_TABLE WHERE ID= ? AND COMMON_NAME=? AND FULL_NAME =? AND COL3=?";
            OleDbConnection con = new OleDbConnection(connectString);
            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            MockSimpleObject newObj = dao.SimpleSelect(con, criteria);
            Assert.AreEqual(criteria.Id, newObj.Id);
            newObj = null;

            Dictionary<string, string> props = new Dictionary<string, string>();
            props.Add("Id", null);
            props.Add("CommonName", null);
            props.Add("FullName", "full_name");
            newObj = dao.SimpleSelect(con, criteria, props);
            con.Close();
            Assert.AreEqual(newObj.Id, 5);
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
            OleDbConnection con = new OleDbConnection(connectString);

            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            dao.NullStringValue = null;
            dao.NullIntValue = 0;
            List<MockSimpleObject> list = dao.SimpleSelectList(con, obj);
            con.Close();
            Assert.IsTrue(list.Count == 3);
        }

        [TestMethod, Ignore]
        public void TestSimpleInsert()
        {
            MockSimpleObject obj = new MockSimpleObject();
            obj.CommonName = "testInsert";
            obj.CreateDate = DateTime.Today;
            obj.FullName = "Test Insert";
            obj.Id = 99;
            obj.Status = 4;
            OleDbConnection con = new OleDbConnection(connectString);
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
            /*obj.CommonName = "testInsert";
            obj.CreateDate = DateTime.Today;
            obj.FullName = "Test Insert";
            obj.Status = 4;*/
            //SqlConnection con = new SqlConnection(connectString);
            OleDbConnection con = new OleDbConnection(connectString);
            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            //dao.NullStringValue = null;
            //dao.NullIntValue = 0;
            //obj.CommonName = "testInsert";
            dao.SimpleDelete(con, obj);
            con.Close();
        }

        [TestMethod]
        public void TestSimpleUpdate()
        {
            MockSimpleObject obj = new MockSimpleObject();
            string[] key = new string[] { "ID" };
            //obj.DBUpdateKey = key;
            obj.Id = 99;
            obj.CommonName = "object1";
            obj.CreateDate = DateTime.Today;
            obj.FullName = "Object One";
            obj.Status = 1;
            OleDbConnection con = new OleDbConnection(connectString);
            con.Open();
            SimpleDAO.SimpleDAO<MockSimpleObject> dao = new SimpleDAO.SimpleDAO<MockSimpleObject>();
            Dictionary<string, string> props = new Dictionary<string, string>();
            props.Add("Id", "ID");
            props.Add("CommonName", "COMMON_NAME");
            props.Add("FullName", "FULL_NAME");
            props.Add("Status", "STATUS");
            dao.SimpleUpdate(con, obj); //, props);
            con.Close();
            Assert.Inconclusive("nothing to assert");
        }



    }
}

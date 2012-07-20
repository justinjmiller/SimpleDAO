using SimpleDAO;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System;

namespace SimpleDAOTest
{
    
    
    /// <summary>
    ///This is a test class for UtilsTest and is intended
    ///to contain all UtilsTest Unit Tests
    ///</summary>
    [TestClass()]
    public class UtilsTest
    {


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
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //[TestInitialize()]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion


        /// <summary>
        ///A test for InferDBTable
        ///</summary>
        [TestMethod()]
        public void InferDBTableTest()
        {
            MockObject mo = new MockObject();
            string expected = "MOCK_SIMPLE";
            string actual = ReflectionUtils.InferDBTable(mo);
            Assert.AreEqual(expected, actual);
            Employee temp = new Employee();
            expected = "EMPLOYEE";
            actual = ReflectionUtils.InferDBTable(temp);
            Assert.AreEqual(expected, actual);
        }

        /// <summary>
        ///A test for GetPropertyName
        ///</summary>
        [TestMethod()]
        public void GetSinglePropertyNameTest()
        {
            string dbColumn = "FIRST_NAME"; 
            string expected = "FirstName"; 
            string actual;
            actual = Utils.GetPropertyName(dbColumn);
            Assert.AreEqual(expected, actual);
        }

        /// <summary>
        ///A test for GetDBColumnName
        ///</summary>
        [TestMethod()]
        public void GetDBColumnNameTest()
        {
            string property = "firstName"; 
            string expected = "FIRST_NAME"; 
            string actual;
            actual = Utils.GetDBColumnName(property);
            Assert.AreEqual(expected, actual);
        }

        /// <summary>
        ///A test for ChangeType
        ///</summary>
        [TestMethod()]
        public void ChangeTypeTest()
        {
            object value = null; // TODO: Initialize to an appropriate value
            Type conversionType = null; // TODO: Initialize to an appropriate value
            object expected = null; // TODO: Initialize to an appropriate value
            object actual;
            actual = Utils.ChangeType(value, conversionType);
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for GetDBOrderBy
        ///</summary>
        [TestMethod()]
        public void GetDBOrderByTest()
        {
            Employee obj = new Employee();
            SortedList<int, SortedColumn> actual = ReflectionUtils.GetDBOrderBy(obj);
            Assert.AreEqual(3, actual.Count );
        }

        /// <summary>
        ///A test for GetDBUpdateKeys
        ///</summary>
        [TestMethod()]
        public void GetDBUpdateKeysTest()
        {
            MockSimpleObject obj = new MockSimpleObject();
            string[] expected = new string[] { "MOCK_SIMPLE_ID" };
            string[] actual = ReflectionUtils.InferDBUpdateKeys(obj);
            Assert.AreEqual(expected[0], actual[0]);
        }

        /// <summary>
        ///A test for GetObjectPropertyMap
        ///</summary>
        [TestMethod()]
        public void GetObjectPropertyMapTest()
        {
            object obj = null; // TODO: Initialize to an appropriate value
            Dictionary<string, string> expected = null; // TODO: Initialize to an appropriate value
            Dictionary<string, string> actual;
            actual = ReflectionUtils.GetObjectPropertyMap(obj);
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for GetPropertyName
        ///</summary>
        [TestMethod()]
        public void GetPropertyNameTest()
        {
            string column = "CUSTOMER_FIRST_NAME";
            Dictionary<string, string> props = new Dictionary<string, string>();
            props.Add("FirstName", "CUSTOMER_FIRST_NAME");
            string expected = "FirstName";
            string actual;
            actual = Utils.GetPropertyName(column, props);
            Assert.AreEqual(expected, actual);

            props.Remove("FirstName");
            props.Add("FirstName", "FIRST_NAME");
            actual = Utils.GetPropertyName(column, props);
            Assert.AreNotEqual(expected, actual);
        }

        /// <summary>
        ///A test for IsCollection
        ///</summary>
        [TestMethod()]
        public void IsCollectionTest()
        {
            Type type = null; // TODO: Initialize to an appropriate value
            bool expected = false; // TODO: Initialize to an appropriate value
            bool actual;
            actual = Utils.IsCollection(type);
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for PopulateObject
        ///</summary>
        [TestMethod()]
        public void PopulateObjectTest()
        {
            object obj = null; // TODO: Initialize to an appropriate value
            Dictionary<string, object> props = null; // TODO: Initialize to an appropriate value
            ReflectionUtils.PopulateObject(obj, props);
            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }
    }
}

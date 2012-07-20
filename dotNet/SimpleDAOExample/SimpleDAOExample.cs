using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Data.OleDb;
using SimpleDAO;

namespace SimpleDAOExample
{
    public partial class SimpleDAOExample : Form
    {
        private static readonly string connectString = @"Provider=Microsoft.Jet.OLEDB.4.0;Data Source=..\..\..\Data\SimpleDAOExample.mdb";
        public SimpleDAOExample()
        {
            InitializeComponent();
        }

        private void SimpleDAOExample_Load(object sender, EventArgs e)
        {
            bindData();
        }

        private void bindDataGrid()
        {
            using (OleDbConnection con = new OleDbConnection(connectString))
            {
                con.Open();
                Employee emp = new Employee();
                emp.DBTable = "vw_Employee";
                SortedList<int, SortedColumn> sorts = new SortedList<int, SortedColumn>();
                sorts.Add(1,new SortedColumn("Last_Name", SimpleDAO.SortOrder.Ascending));
                sorts.Add(2,new SortedColumn("First_Name", SimpleDAO.SortOrder.Ascending));   
                emp.DBOrderBy = sorts;
                Dictionary<string,string> props= emp.Describe();
                SimpleDAO<Employee> sdao = new SimpleDAO<Employee>();
                List<Employee> list = (List<Employee>)sdao.SimpleSelectList(con, emp);
                dgData.DataSource = list;
            }
        }

        private void btnRefresh_Click(object sender, EventArgs e)
        {
            bindDataGrid();
        }

        private void label2_Click(object sender, EventArgs e)
        {

        }

        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }

        public void bindData()
        {
            using (OleDbConnection con = new OleDbConnection(connectString))
            {
                con.Open();
                SimpleLookupObject lookup = new SimpleLookupObject();
                lookup.DBTable = "status";
                SimpleDAO<SimpleLookupObject> sdaolookup = new SimpleDAO<SimpleLookupObject>();
                List<SimpleLookupObject> lookuplist = sdaolookup.SimpleSelectList(con, lookup);
                cbStatus.DisplayMember = "Name";
                cbStatus.ValueMember = "Id";
                cbStatus.DataSource = lookuplist;
                
                Employee emp = new Employee();
                emp.DBTable = "vw_Employee";
                SortedList<int, SortedColumn> sorts = new SortedList<int, SortedColumn>();
                sorts.Add(1, new SortedColumn("Last_Name", SimpleDAO.SortOrder.Ascending));
                sorts.Add(2, new SortedColumn("First_Name", SimpleDAO.SortOrder.Ascending));
                emp.DBOrderBy = sorts;
                Dictionary<string, string> props = emp.Describe();
                SimpleDAO<Employee> sdaoemp = new SimpleDAO<Employee>();
                List<Employee> list = (List<Employee>)sdaoemp.SimpleSelectList(con, emp);
                dgData.DataSource = list;
            }

        }

        private void btnAdd_Click(object sender, EventArgs e)
        {
            Employee emp = new Employee();
            Dictionary<string, string> props = emp.Describe();
            props.Remove("StatusName");

            emp.FirstName = tbFirst.Text;
            emp.LastName = tbLast.Text;
            emp.Status =(int) cbStatus.SelectedValue;
            SimpleDAO<Employee> sdao = new SimpleDAO<Employee>();
            using (OleDbConnection con = new OleDbConnection(connectString))
            {
                con.Open();
                sdao.SimpleInsert(con, emp);
            }

            bindDataGrid();

        }
    }
}

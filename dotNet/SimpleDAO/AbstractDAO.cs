using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;

namespace SimpleDAO
{
    public abstract class AbstractDAO<E> where E: class
    {
        protected SimpleDAO<E> dao = new SimpleDAO<E>();

        protected abstract IDbConnection getDBConnection();
        public virtual List<E> GetList(E criteria)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                return dao.SimpleSelectList(con , criteria);
            }
        }

        public virtual E Get(E criteria)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                return dao.SimpleSelect(con, criteria);
            }
        }

        public virtual void Delete(E criteria)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                dao.SimpleDelete(con, criteria);
            }
        }

        public virtual void Update(E criteria)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                dao.SimpleUpdate(con, criteria);
            }
        }

        public virtual E Insert(E criteria)
        {
            using (IDbConnection con = getDBConnection())
            {
                con.Open();
                return dao.SimpleInsert(con, criteria);
            }
        }

    }
}

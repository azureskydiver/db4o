using System;
using System.Collections.Generic;
using System.Text;
using Db4objects.Db4o.Query;
using Db4objects.Db4o;
using System.IO;
using OManager.BusinessLayer.Login;
using OManager.BusinessLayer.QueryManager;
using OManager.DataLayer.Connection;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.Connection
{
    class FetchSaveRecentConnections
    {
        IObjectContainer dbrecentConn = null;

        RecentQueries m_recentQueries = null;

        public RecentQueries Recentconn
        {
            get { return m_recentQueries; }
            set { m_recentQueries = value; }
        }
        public FetchSaveRecentConnections()
        {
           // dbrecentConn = Db4oClient.RecentConn;            
        }
        
        public FetchSaveRecentConnections(RecentQueries recentconn):this()
        {
            m_recentQueries = recentconn;
            
        }
        public bool SaveRecentConnection()
        {
            try
            {
                
                m_recentQueries.Timestamp = DateTime.Now;
                RecentQueries r = new RecentQueries(m_recentQueries.ConnParam);
                RecentQueries temprc = r.ChkIfRecentConnIsInDb();
                if (temprc != null)
                {
                    temprc.Timestamp = DateTime.Now;
                    temprc.QueryList = m_recentQueries.QueryList;

                }
                else
                    temprc = m_recentQueries;
                dbrecentConn = Db4oClient.RecentConn;
                dbrecentConn.Set(temprc);               
                dbrecentConn.Commit();
              //  dbrecentConn.Close();
                dbrecentConn = null;
                Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
            }

            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }

            return true;
        }

       
        public List<RecentQueries> GetRecentQueries()
        {
            string RecentConnFileName;
            List<RecentQueries> recConnections = null;
            try
            {
 #if DEBUG
                
                if (!Directory.Exists(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar+ "db4objects"+ Path.DirectorySeparatorChar + "ObjectManagerEnterprise"))
                {
                    Directory.CreateDirectory(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + 
                        Path.DirectorySeparatorChar + "db4objects" + Path.DirectorySeparatorChar + "ObjectManagerEnterprise");
                }
#endif
                RecentConnFileName = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects" + Path.DirectorySeparatorChar + "ObjectManagerEnterprise" + Path.DirectorySeparatorChar + "ObjectManagerPlus.yap";
                Db4oClient.RecentConnFile = RecentConnFileName;
                dbrecentConn = Db4oClient.RecentConn;
                IQuery query = dbrecentConn.Query();
                query.Constrain(typeof(RecentQueries));
                IObjectSet os = query.Execute();
                
                recConnections = new List<RecentQueries>();
                while (os.HasNext())
                {
                    recConnections.Add((RecentQueries)os.Next());
                }
                dbrecentConn = null;
                Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                dbrecentConn = null;
                Db4oClient.CloseRecentConnectionFile(Db4oClient.
                    RecentConn);
                return null;
            }
            return recConnections;
        }

    }
}

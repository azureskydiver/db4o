using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using System;
using System.Collections; 
using System.Collections.Generic;
using System.Text;
using OManager.BusinessLayer.QueryManager;
using OManager.DataLayer.Connection;

using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.BusinessLayer.Login
{

    //Comments
    //check for some data structures which give uniqueness of the queries.
    public class RecentQueries
    {
        #region Declaration
        DateTime m_timestamp;
        ConnParams m_connParam;
        List<OMQuery> m_queryList;
        [Transient]
        IObjectContainer container = null;
       
        #endregion



        public RecentQueries(ConnParams connParam)
        {
            m_queryList = new List<OMQuery>();
            this.m_connParam = connParam;
          //  container = Db4oClient.RecentConn;
           
        }

        public List<OMQuery> QueryList
        {
            get { return m_queryList; }
            set { m_queryList = value; }
        }

        public ConnParams ConnParam
        {
            get { return m_connParam; }
            set { m_connParam = value; }
        }

        public DateTime Timestamp
        {
            get { return m_timestamp; }
            set { m_timestamp = value; }
        }


        //public RecentQueries PopulateParameters(DateTime date)
        //{
        //    this.Timestamp = date;
        //    this.ConnParam = m_connParam;
        //    return this;
        //}



        public void AddQueryToList(OMQuery query)
        {
            try
            {
                container = Db4oClient.RecentConn;
                if (QueryList != null)
                {
                    CompareQueryTimestamps comp = new CompareQueryTimestamps();
                    
                    List<OMQuery> qList = FetchAllQueries();
                  //  qList.Sort(comp);
                  

                    if (qList.Count >= 20)
                    {
                        bool check = false;
                        foreach (OMQuery qry in qList)
                        {
                            if (qry.QueryString == query.QueryString)
                            {
                                //check if the query string is among 5
                                check = true;
                            }
                        }
                        //if it is not among five then remove the last item from the list
                        if (check == false)
                        {
                            OMQuery q = qList[qList.Count - 1];
                            qList.RemoveAt(qList.Count - 1);
                            foreach (OMQuery qry1 in this.QueryList)
                            {
                                if (q.QueryString.Equals(qry1.QueryString))
                                {
                                    this.QueryList.Remove(qry1);
                                    break;
                                }
                            }
                            container.Delete(q);
                            container.Commit();
                        }
                    }

                    List<OMQuery> qListForClass;
                    qListForClass = FetchQueriesForAClass(query.BaseClass);
                    qListForClass.Sort(comp);
                    if (qListForClass.Count >= 5)
                    {
                        bool check = false;
                        foreach (OMQuery qry in qListForClass)
                        {
                            if (qry.QueryString == query.QueryString)
                            {
                                //check if the query string is among 5
                                check = true;
                            }
                        }
                        //if it is not among five then remove the last item from the list
                        if (check == false)
                        {
                            OMQuery q = qListForClass[qListForClass.Count - 1];
                            qListForClass.RemoveAt(qListForClass.Count - 1);
                            foreach (OMQuery qry1 in this.QueryList)
                            {
                                if (q.QueryString.Equals(qry1.QueryString))
                                {
                                    this.QueryList.Remove(qry1);
                                    break;
                                }
                            }
                            container.Delete(q);
                            container.Commit();
                        }
                    }

                   


                    // recent queries should always be 5 for a class therefore its checked 
                    //against qListForClass.
                    foreach (OMQuery q in qListForClass)
                    {
                        if (q != null)
                        {
                            if (q.QueryString != null)
                            {
                                if (q.QueryString.Equals(query.QueryString))
                                {
                                    //if query string is same as already in the list then remove from the list
                                    //so that same string can be added again with updated timestamp

                                    foreach (OMQuery qry1 in this.QueryList)
                                    {
                                        if (q.QueryString.Equals(qry1.QueryString))
                                        {
                                            this.QueryList.Remove(qry1);
                                            break;
                                        }
                                    }
                                                               
                                   
                                }
                            }
                        }
                    }

                    //add query with latest timestamp.
                    this.QueryList.Add(query);
                    RecentQueries temprc = this.ChkIfRecentConnIsInDb();
                    if (temprc != null)
                    {
                        temprc.Timestamp = DateTime.Now;
                        temprc.QueryList = this.QueryList;

                    }
                    else
                        temprc = this;

                    container.Ext().Set(temprc, 5);
                    container.Commit();
                    container = null;
                    Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);


                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                container = null;
                Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
            }
        }

        private List<OMQuery> FetchAllQueries()
        {
            container = Db4oClient.RecentConn;
            IQuery query = container.Query();
            query.Constrain(typeof(RecentQueries));
            IObjectSet os = query.Execute();

            List<RecentQueries> recConnections = new List<RecentQueries>();
            while (os.HasNext())
            {
                recConnections.Add((RecentQueries)os.Next());
            }
            List<OMQuery> QryList = new List<OMQuery>();
            foreach (RecentQueries recCon in recConnections)
            {
                foreach (OMQuery q in recCon.QueryList)
                {
                    if(q!=null)
                    QryList.Add(q);
                }
            }

            CompareQueryTimestamps comp = new CompareQueryTimestamps();
            QryList.Sort(comp);
            return QryList;

        }

        public List<OMQuery> FetchQueriesForAClass(string className)
        {
           
            List<OMQuery> qList = new List<OMQuery>();           
            IObjectSet objSet = null;
            try
            {
                container = Db4oClient.RecentConn;
                IQuery query = container.Query();
                query.Constrain(typeof(RecentQueries));
                query.Descend("m_connParam").Descend("m_connection").Constrain(m_connParam.Connection);    
                objSet = query.Execute();
                if (objSet != null)
                {
                    RecentQueries recentQueries = (RecentQueries)objSet.Next();
                    foreach (OMQuery q in recentQueries.QueryList)
                    {
                        if (q != null && q.BaseClass.Equals(className))
                            qList.Add(q);
                    }

                    CompareQueryTimestamps comp = new CompareQueryTimestamps();
                    qList.Sort(comp);
                }
                else
                    return null; 
               // container = null;
                //Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                container = null;
                Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
                
            }
            return qList;
        }

       

        public RecentQueries ChkIfRecentConnIsInDb()
        {
            RecentQueries recConnection = null;
           
            IObjectSet objSet = null;
            try
            {
                container = Db4oClient.RecentConn;
                IQuery qry = container.Query();
                qry.Constrain(typeof(RecentQueries));
                if (m_connParam.Host == null)
                {
                    qry.Descend("m_connParam").Descend("m_connection").Constrain(m_connParam.Connection);
                    objSet = qry.Execute();
                }
                else
                {
                    qry.Descend("m_connParam").Descend("m_host").Constrain(m_connParam.Host);
                    qry.Descend("m_connParam").Descend("m_port").Constrain(m_connParam.Port);
                    qry.Descend("m_connParam").Descend("m_userName").Constrain(m_connParam.UserName);
                    qry.Descend("m_connParam").Descend("m_passWord").Constrain(m_connParam.PassWord);
                    objSet = qry.Execute();

                    
                }
                if (objSet.Count > 0)
                {
                     recConnection = (RecentQueries)objSet.Next();
                }
             
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }
            return recConnection;
            
        }

        public void deleteRecentQueriesForAConnection()
        {
            List<OMQuery> qList = new List<OMQuery>();           
            IObjectSet objSet = null;
            try
            {
                container = Db4oClient.RecentConn;
                IQuery query = container.Query();
                query.Constrain(typeof(RecentQueries));
                query.Descend("m_connParam").Descend("m_connection").Constrain(m_connParam.Connection);
                objSet = query.Execute();
                if (objSet != null)
                {
                    RecentQueries recQueries = (RecentQueries)objSet.Next();
                   
                    for (int i = 0; i < recQueries.QueryList.Count; i++)
                    {

                        OMQuery q = recQueries.m_queryList[0]; 
                        if (q != null)
                        {
                           // m_queryList.Remove(q); 
                            container.Delete(q);
                            
                        }
                    }
                    recQueries.m_queryList.Clear();   
                    container.Ext().Set(recQueries, 5);   
                    container.Commit();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

        }
    }

    public class CompareTimestamps : IComparer<RecentQueries>
    {
        public int Compare(RecentQueries con1, RecentQueries con2)
        {
            
                return con2.Timestamp.CompareTo(con1.Timestamp);
        }
    }
}

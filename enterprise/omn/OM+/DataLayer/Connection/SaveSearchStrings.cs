using System;
using System.Collections.Generic;
using OManager.BusinessLayer.Login;
using System.Text;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OManager.DataLayer.Connection
{
    

    public class GroupofSearchStrings
    {

        ConnParams m_connParam;

        public ConnParams ConnParam
        {
            get { return m_connParam; }
            set { m_connParam = value; }
        }
        List<SeachString> m_SearchStringList;
        [Transient]
        IObjectContainer container = null;

        public List<SeachString> SearchStringList
        {
            get { return m_SearchStringList; }
            
        }

        public GroupofSearchStrings(ConnParams connParam)
        {           
            m_connParam = connParam;
            m_SearchStringList = new List<SeachString>(); 
        }

        internal void AddSearchStringToList(SeachString strToAdd)
        {
            try
            {
                container = Db4oClient.RecentConn;
                if (m_SearchStringList != null)
                {
                    GroupofSearchStrings groupSearchString = FetchAllSearchStringsForAConnection();

                    if (groupSearchString == null)
                    {
                        groupSearchString = new GroupofSearchStrings(m_connParam);                       
                        List<SeachString> l=new List<SeachString>();
                        l.Add(strToAdd);
                        groupSearchString.m_SearchStringList = l;
                        container.Set(groupSearchString);
                        container.Commit();
                        return; 
                    }

                    List<SeachString> searchStringForConnection = groupSearchString.m_SearchStringList;

                    if (searchStringForConnection.Count >= 20)
                    {
                        bool check = false;
                        SeachString temp=null;
                        foreach (SeachString str in searchStringForConnection)
                        {
                            if (str.SearchString.Equals(strToAdd.SearchString))
                            {
                                temp = str;
                                check = true;
                                break;
                            }
                        }
                        if (check == false)
                        {
                            temp = searchStringForConnection[searchStringForConnection.Count - 1];
                            searchStringForConnection.Remove(temp);
                            strToAdd.Timestamp = DateTime.Now;
                            searchStringForConnection.Add(strToAdd);

                        }
                        else
                        {
                            searchStringForConnection.Remove(temp);
                            strToAdd.Timestamp = DateTime.Now;
                            searchStringForConnection.Add(strToAdd);

                        }
                        
                        container.Delete(temp);  
                        CompareSearchStringTimestamps cmp = new CompareSearchStringTimestamps();
                        searchStringForConnection.Sort(cmp);
                        groupSearchString.m_SearchStringList = searchStringForConnection;
                        container.Ext().Set(groupSearchString, 5);
                        container.Commit();

                    }

                    else
                    {
                        bool checkstr = false;
                        SeachString temp=null;
                        foreach (SeachString str in searchStringForConnection)
                        {
                            if (str.SearchString.Equals(strToAdd.SearchString))
                            {
                                temp = str;
                                checkstr = true;
                                break;
                            }
                        }
                        if (checkstr == false)
                        {                            
                            searchStringForConnection.Add(strToAdd);

                        }
                        else
                        {
                            searchStringForConnection.Remove(temp);
                            strToAdd.Timestamp = DateTime.Now;
                            searchStringForConnection.Add(strToAdd);
                            container.Delete(temp);
                        }
                        
                        
                        CompareSearchStringTimestamps cmp = new CompareSearchStringTimestamps();
                        searchStringForConnection.Sort(cmp);
                        groupSearchString.m_SearchStringList = searchStringForConnection;
                        container.Ext().Set(groupSearchString, 5);
                        container.Commit();

                    }


                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                container = null;
                
            }
        }

        private  GroupofSearchStrings FetchAllSearchStringsForAConnection()
        {
            GroupofSearchStrings grpSearchStrings = null;
            try
            {
                container = Db4oClient.RecentConn;
                IQuery query = container.Query();
                query.Constrain(typeof(GroupofSearchStrings));
                query.Descend("m_connParam").Descend("m_connection").Constrain(m_connParam.Connection);
                IObjectSet objSet = query.Execute();
                if (objSet.Count != 0)
                {
                    grpSearchStrings = (GroupofSearchStrings)objSet.Next();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

            return grpSearchStrings;
        }

        internal List<string> ReturnStringList()
        {
            List<string> stringList = null;
            try
            {

                GroupofSearchStrings groupSearchList = FetchAllSearchStringsForAConnection();
                if (groupSearchList != null)
                {
                    List<SeachString> searchStringList = groupSearchList.m_SearchStringList;
                    stringList = new List<string>();
                    foreach (SeachString str in searchStringList)
                    {
                        stringList.Add(str.SearchString);
                    }
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }

            return stringList;
        }
        internal void RemovesSearchStringsForAConnection()
        {

            try
            {
                GroupofSearchStrings grpSearchString = FetchAllSearchStringsForAConnection();
                if (grpSearchString != null)
                {
                 
                    for (int i = 0; i < grpSearchString.m_SearchStringList.Count; i++)
                    {
                        SeachString sString = grpSearchString.m_SearchStringList[i];
                        //foreach (SeachString sString in grpSearchString.m_SearchStringList)

                        if (sString != null)
                        {
                            //grpSearchString.m_SearchStringList.Remove(sString);
                            container.Delete(sString);
                        }

                    }
                    grpSearchString.m_SearchStringList.Clear(); 
                    container.Ext().Set(grpSearchString, 5);  
                    container.Commit();
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
            }


        }
    }
    public class CompareSearchStringTimestamps : IComparer<SeachString>
    {
        public int Compare(SeachString s1, SeachString s2)
        {

            return s2.Timestamp.CompareTo(s1.Timestamp);
        }
    }
}

/* Copyright (C) 2009 Versant Inc.   http://www.db4o.com */
using System;
using System.Collections.Generic;
using Db4objects.Db4o.Query;
using Db4objects.Db4o;
using System.IO;
using OManager.BusinessLayer.Login;
using OManager.BusinessLayer.UIHelper;
using OManager.DataLayer.Connection;
using OME.Logging.Common;

namespace OManager.BusinessLayer.Config
{
    public class Config
    {
        public ISearchPath AssemblySearchPath
        {
            get
            {
                if (_searchPath == null)
                {
                    _searchPath = LoadSearchPath();

                }
                return _searchPath;
            }
            set { _searchPath = value; }
        }

        public static Config Instance
        {
            get { return _config; }
        }

        public string DbPath
        {
            get { return _path; }
            set { _path = value; }

        }

        public bool SaveRecentConnection(RecentQueries recentQueries)
        {
            try
            {
                recentQueries.Timestamp = DateTime.Now;
                RecentQueries temprc = Db4oClient.CurrentRecentConnection.ChkIfRecentConnIsInDb();
                if (temprc != null)
                {
                    temprc.Timestamp = DateTime.Now;
                    temprc.QueryList = recentQueries.QueryList;
                    temprc.ConnParam.ConnectionReadOnly = recentQueries.ConnParam.ConnectionReadOnly;
                }
                else
                {
                    temprc = recentQueries;
                    temprc.TimeOfCreation = Sharpen.Runtime.CurrentTimeMillis();
                }
                IObjectContainer dbrecentConn = Db4oClient.OMNConnection;
                dbrecentConn.Store(temprc);
                dbrecentConn.Commit();
                Db4oClient.CloseRecentConnectionFile();
            }

            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }

            return true;
        }


        public List<RecentQueries> GetRecentQueries(bool remote)
        {
            List<RecentQueries> recConnections = null;
            try
            {
#if DEBUG
                CreateOMNDirectory();
#endif
                IObjectContainer dbrecentConn = Db4oClient.OMNConnection;
                IQuery query = dbrecentConn.Query();
                query.Constrain(typeof (RecentQueries));
                if (remote)
                {
                    query.Descend("m_connParam").Descend("m_host").Constrain(null).Not();
                    query.Descend("m_connParam").Descend("m_port").Constrain(0).Not();
                }
                else
                {
                    query.Descend("m_connParam").Descend("m_host").Constrain(null);
                    query.Descend("m_connParam").Descend("m_port").Constrain(0);
                }
                IObjectSet os = query.Execute();

                if (os.Count > 0)
                {
                    recConnections = new List<RecentQueries>();
                    while (os.HasNext())
                    {
                        recConnections.Add((RecentQueries) os.Next());
                    }
                }
                Db4oClient.CloseRecentConnectionFile();
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                Db4oClient.CloseRecentConnectionFile();
                return null;
            }
            return recConnections;

        }

        private void CreateOMNDirectory()
        {
            string omnConfigFolderPath = Path.GetDirectoryName(OMNConfigDatabasePath());
            if (!Directory.Exists(omnConfigFolderPath))
            {
                Directory.CreateDirectory(omnConfigFolderPath);
            }
        }

        public void SaveAssemblySearchPath(string path)
        {
            _path = path;
            PathContainer pathContainer = PathContainerFor(Db4oClient.OMNConnection);
            Db4oClient.OMNConnection.Delete(pathContainer.SearchPath);
            pathContainer.SearchPath = AssemblySearchPath;
            pathContainer.ConnParam = OMEInteraction.GetCurrentRecentConnection().ConnParam;
            // pathContainer.ConnParam = GetConnparams(Db4oClient.OMNConnection); 
            Db4oClient.OMNConnection.Store(pathContainer);
            Db4oClient.CloseRecentConnectionFile();
        }
        

        public static string OMNConfigDatabasePath()
        {
            string applicationDataPath = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
            return Path.Combine(applicationDataPath,
                                Path.Combine("db4objects",
                                             Path.Combine("ObjectManagerEnterprise", "ObjectManagerPlus.yap")));
        }

        private ISearchPath LoadSearchPath()
        {
            ISearchPath searchPath = PathContainerFor(Db4oClient.OMNConnection).SearchPath;
            Db4oClient.CloseRecentConnectionFile();
            return searchPath;
        }

        private PathContainer PathContainerFor(IObjectContainer database)
        {

            IQuery query = database.Query();
            query.Constrain(typeof (PathContainer));
            query.Descend("_connParam").Descend("m_connection").Constrain(_path).Contains() ;

            IObjectSet paths = query.Execute();
            return paths.Count == 0 ? new PathContainer(new SearchPathImpl()) : (PathContainer) paths[0];
        }

        private Config()
        {
        }

        private ISearchPath _searchPath;
        private static readonly Config _config = new Config();
        private string _path;

    }



}

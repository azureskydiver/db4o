/* Copyright (C) 2009 db4objects Inc.   http://www.db4o.com */
using System;
using System.Collections.Generic;
using System.Reflection;
using Db4objects.Db4o.Query;
using Db4objects.Db4o;
using System.IO;
using OManager.BusinessLayer.Login;
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
				if (null == _searchPath)
				{
					_searchPath = LoadSearchPath();
				}

				return _searchPath;
			}
		}

		public static Config Instance
		{
			get { return _config; }
		}

		public bool SaveRecentConnection(RecentQueries recentQueries)
		{
			try
			{
				recentQueries.Timestamp = DateTime.Now;
				RecentQueries r = new RecentQueries(recentQueries.ConnParam);
				RecentQueries temprc = r.ChkIfRecentConnIsInDb();
				if (temprc != null)
				{
					temprc.Timestamp = DateTime.Now;
					temprc.QueryList = recentQueries.QueryList;
				}
				else
					temprc = recentQueries;

				IObjectContainer dbrecentConn = Db4oClient.RecentConn;
				dbrecentConn.Store(temprc);
				dbrecentConn.Commit();
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
			List<RecentQueries> recConnections;
			try
			{
#if DEBUG
				string omnConfigFolderPath = Path.GetDirectoryName(OMNConfigDatabasePath());
				if (!Directory.Exists(omnConfigFolderPath))
				{
					Directory.CreateDirectory(omnConfigFolderPath);
				}
#endif
				Db4oClient.RecentConnFile = OMNConfigDatabasePath();
				IObjectContainer dbrecentConn = Db4oClient.RecentConn;
				IQuery query = dbrecentConn.Query();
				query.Constrain(typeof(RecentQueries));
				IObjectSet os = query.Execute();

				recConnections = new List<RecentQueries>();
				while (os.HasNext())
				{
					recConnections.Add((RecentQueries)os.Next());
				}
				Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
				Db4oClient.CloseRecentConnectionFile(Db4oClient.RecentConn);
				return null;
			}
			return recConnections;
		}

		public void SaveAssemblySearchPath()
		{
			using(IObjectContainer configDatabase = Db4oClient.OpenConfigDatabase(OMNConfigDatabasePath()))
			{
				PathContainer pathContainer = PathContainerFor(configDatabase);
				configDatabase.Delete(pathContainer.SearchPath);
				pathContainer.SearchPath = AssemblySearchPath;

				configDatabase.Store(pathContainer);
			}
		}

		public static string OMNConfigDatabasePath()
		{
			string applicationDataPath = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
			return Path.Combine(applicationDataPath, Path.Combine("db4objects", Path.Combine("ObjectManagerEnterprise", "ObjectManagerPlus.yap")));
		}
		
		private static ISearchPath LoadSearchPath()
		{
			using (IObjectContainer configDatabase = Db4oClient.OpenConfigDatabase(OMNConfigDatabasePath()))
			{
				return PathContainerFor(configDatabase).SearchPath;
			}
		}

		private static PathContainer PathContainerFor(IObjectContainer database)
		{
			IList<PathContainer> paths = database.Query<PathContainer>();
			return paths.Count == 0 ? new PathContainer(new SearchPathImpl()) : paths[0];
		}

		private Config()
		{
		}
		
		private ISearchPath _searchPath;
		private static readonly Config _config = new Config();
	}
}

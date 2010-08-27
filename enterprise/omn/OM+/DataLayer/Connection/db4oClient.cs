using System;
using System.Configuration;
using Db4objects.Db4o;
using Db4objects.Db4o.CS;
using Db4objects.Db4o.CS.Config;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using OManager.BusinessLayer.Login;
using System.IO;
using OManager.DataLayer.Reflection;
using OME.Logging.Common;

namespace OManager.DataLayer.Connection
{
	public class Db4oClient
	{
		private static TypeResolver typeResolver;
		private static IObjectContainer objContainer;
		private static IObjectContainer userConfigDatabase;
		public static ConnParams conn;
		public static string RecentConnFile;
		public static string exceptionConnection = "";
		public static bool boolExceptionForRecentConn;
		private static bool isConnected;
		public static TypeResolver TypeResolver
		{
			get
			{
				return typeResolver;
			}
		}

		private static RecentQueries currentRecentConnection;
		public static RecentQueries CurrentRecentConnection
		{
			get
			{
				return currentRecentConnection;
			}
			set
			{
				currentRecentConnection = value;
			}
		}
       
		public static bool IsConnected
		{
			get
			{
				return isConnected ;


			}
			set
			{
				isConnected=value  ;
			}
		}

		/// <summary>
		/// Static property which either returns a new object container for a specific logon identity or returns the object container already 
		/// allocated to the logon identity.
		/// </summary>
		public static IObjectContainer Client
		{
			get
			{
				exceptionConnection = "";
				try
				{
					if (objContainer == null)
					{
						// Prior to opening the objectContainer set all required Db4o configurations.
						// Db4oConfiguration.SetConfiguration();
						if (conn != null)
						{
							// Retrieve an objectContainer for this client. 
							if (conn.Host != null)
							{
								IClientConfiguration config = Db4oClientServer.NewClientConfiguration();
								ConfigureCommon(config.Common);

								objContainer = Db4oClientServer.OpenClient(config, conn.Host, conn.Port, conn.UserName, conn.PassWord);
								typeResolver = new TypeResolver(objContainer.Ext().Reflector());
							}
							else
							{
                                if (File.Exists(conn.Connection))
                                {
									IEmbeddedConfiguration config = Db4oEmbedded.NewConfiguration();
									ConfigureCommon(config.Common);

									objContainer = Db4oEmbedded.OpenFile(config,conn.Connection);
                                    typeResolver = new TypeResolver(objContainer.Ext().Reflector());
                                }
                                else
                                {
                                    exceptionConnection = "File does not exist!";
                                }
							}
							isConnected = true;
						}
					}
				}
				catch (InvalidPasswordException)
				{
					exceptionConnection = "Incorrect Credentials. Please enter again.";
				}
				catch (DatabaseFileLockedException)
				{
					exceptionConnection = "Database is locked and is used by another application.";
				}
				catch (IncompatibleFileFormatException ex)
				{
					exceptionConnection = ex.Message;
				}
				catch (System.Net.Sockets.SocketException)
				{
					exceptionConnection = "No connection could be made because the target machine actively refused it.";
				}
				catch (InvalidCastException)
				{
					exceptionConnection = "Java Database is not supproted.";
				}
				catch (Exception oEx)
				{
					exceptionConnection = oEx.Message;
				}

				return objContainer;
			}

		}

		protected static void ConfigureCommon(ICommonConfiguration config)
		{
			config.Queries.EvaluationMode(QueryEvaluationMode.Lazy);
			config.ActivationDepth = 1;
			config.AllowVersionUpdates = true ;
			config.Add(new JavaSupport());
		}
		
		public static IObjectContainer RecentConn
		{
			get
			{
				try
				{
					RecentConnFile = GetOMNConfigdbPath();
					if (userConfigDatabase == null && RecentConnFile != null)
					{
						Db4oFactory.Configure().UpdateDepth(int.MaxValue);
						Db4oFactory.Configure().ActivationDepth(int.MaxValue);
						Db4oFactory.Configure().LockDatabaseFile(false);
						userConfigDatabase = Db4oFactory.OpenFile(RecentConnFile);
					}
				}
				catch (Exception oEx)
				{
					LoggingHelper.HandleException(oEx);
					boolExceptionForRecentConn = true;
				}

				return userConfigDatabase;
			}
		}


		/// <summary>
		/// Static property which closes the corresponding object container for the current logon identity.
		/// </summary>
		public static void CloseConnection()
		{
			try
			{
				if (objContainer != null)
				{
					objContainer.Close();
					objContainer = null;
					isConnected = false;
				}
				conn = null;

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		public static void CloseRecentConnectionFile(IObjectContainer objectContainer)
		{
			objectContainer = RecentConn;
			try
			{
				if (objectContainer != null)
				{
					objectContainer.Close();
					objectContainer = null;
					userConfigDatabase = null;
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);

			}
		}

		public static IObjectContainer OpenConfigDatabase(string path)
		{
			Db4oFactory.Configure().UpdateDepth(int.MaxValue);
			Db4oFactory.Configure().ActivationDepth(int.MaxValue);
			Db4oFactory.Configure().LockDatabaseFile(false);

			return Db4oFactory.OpenFile(path);
		}

		private static string GetOMNConfigdbPath()
		{
			string applicationDataPath = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
			return Path.Combine(applicationDataPath, Path.Combine("db4objects", Path.Combine("ObjectManagerEnterprise", "ObjectManagerPlus.yap")));
		}
	}
}

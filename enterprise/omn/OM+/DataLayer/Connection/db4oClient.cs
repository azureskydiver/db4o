using System;
using System.Configuration;
using Db4objects.Db4o;
using Db4objects.Db4o.CS;
using Db4objects.Db4o.CS.Config;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.Events;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Internal;
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
		public static string omnConnection;
		public static string exceptionConnection = "";
		public static bool boolExceptionForRecentConn;
		private static bool isConnected;
		private static IEmbeddedConfiguration embeddedConfig;
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
        public static bool IsClient
        {
            get { return ((IInternalObjectContainer) objContainer).IsClient; }
           
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
						if (conn != null)
						{
							if (conn.Host != null)
							{
								objContainer=ConnectClient();
							}
							else
							{
							   if (File.Exists(conn.Connection))
                                {
									objContainer=ConnectEmbedded();
                                }
                                else
                                {
                                    exceptionConnection = "File does not exist!";
                                }
							}
							if (objContainer != null)
							{
								typeResolver = new TypeResolver(objContainer.Ext().Reflector());
								isConnected = true;
							}
						}
					}
				}
				catch (InvalidPasswordException)
				{
					exceptionConnection = "Incorrect Credentials. Please enter again.";
					embeddedConfig = null;
				}
				catch (DatabaseFileLockedException)
				{
					exceptionConnection = "Database is locked and is used by another application.";
					embeddedConfig = null;
				}
				catch (IncompatibleFileFormatException ex)
				{
					exceptionConnection = ex.Message;
					embeddedConfig = null;
				}
				catch (System.Net.Sockets.SocketException)
				{
					exceptionConnection = "No connection could be made because the target machine actively refused it.";
					embeddedConfig = null;
				}
				catch (InvalidCastException)
				{
					exceptionConnection = "Java Database is not supproted.";
					embeddedConfig = null;
				}
				catch (Exception oEx)
				{
                    
					exceptionConnection = oEx.Message;
					embeddedConfig = null;
				}

				return objContainer;
			}
		}

		private static IObjectContainer ConnectEmbedded()
		{
			if (embeddedConfig == null)
				embeddedConfig = Db4oEmbedded.NewConfiguration();
			ConfigureCommon(embeddedConfig.Common);
			embeddedConfig.File.ReadOnly = conn.ConnectionReadOnly;
			return Db4oEmbedded.OpenFile(embeddedConfig, conn.Connection);
		}

		private static IObjectContainer  ConnectClient()
		{
			IClientConfiguration config = Db4oClientServer.NewClientConfiguration();
			ConfigureCommon(config.Common);
			return  Db4oClientServer.OpenClient(config, conn.Host, conn.Port, conn.UserName, conn.PassWord) ;
			
		}
		

		protected static void ConfigureCommon(ICommonConfiguration config)
		{
			config.Queries.EvaluationMode(QueryEvaluationMode.Lazy);
			config.ActivationDepth = 1;
			config.AllowVersionUpdates = true ;
		}
		
		public static IObjectContainer OMNConnection
		{
			get
			{
				try
				{
					omnConnection = GetOMNConfigdbPath();
					if (userConfigDatabase == null && omnConnection != null)
					{
						IEmbeddedConfiguration config = Db4oEmbedded.NewConfiguration();
						config.Common.Diagnostic.AddListener(new DiagnosticToTrace());
						config.Common.UpdateDepth = int.MaxValue;
						config.Common.ActivationDepth = int.MaxValue;
						userConfigDatabase = Db4oEmbedded.OpenFile(config,omnConnection);
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
					conn = null;
				}
				embeddedConfig = null;
				

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		public static void CloseRecentConnectionFile()
		{
			
			try
			{
				if (OMNConnection != null)
				{
					OMNConnection.Close();
					userConfigDatabase = null;
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);

			}
		}

		private static string GetOMNConfigdbPath()
		{
			string applicationDataPath = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
			return Path.Combine(applicationDataPath, Path.Combine("db4objects", Path.Combine("ObjectManagerEnterprise", "ObjectManagerPlus.yap")));
		}
	}
}

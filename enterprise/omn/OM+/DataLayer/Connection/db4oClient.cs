using System;
using Db4objects.Db4o;
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

		/// <summary>
		/// Static property which either returns a new object container for a specific logon identity or returns the object container already 
		/// allocated to the logon identity.
		/// </summary>
		public static IObjectContainer Client
		{
			get
			{
				exceptionConnection = "";
				Db4oFactory.Configure().Queries().EvaluationMode(QueryEvaluationMode.Lazy);
				Db4oFactory.Configure().ActivationDepth(1);
				Db4oFactory.Configure().AllowVersionUpdates(true);
				Db4oFactory.Configure().BlockSize(8);

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
								objContainer = Db4oFactory.OpenClient(conn.Host, conn.Port, conn.UserName, conn.PassWord);
								typeResolver = new TypeResolver(objContainer.Ext().Reflector());
							}
							else
							{
								if (File.Exists(conn.Connection))
								{
									objContainer = Db4oFactory.OpenFile(conn.Connection);
									typeResolver = new TypeResolver(objContainer.Ext().Reflector());
								}
								else
								{
									exceptionConnection = "File does not exist!";
								}
							}
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

		public static IObjectContainer RecentConn
		{
			get
			{
				try
				{
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
	}
}

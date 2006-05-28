/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Diagnostics;

using Db4oTools;

using j4o.lang;
using j4o.io;

using com.db4o;
using com.db4o.ext;
using com.db4o.foundation;
using com.db4o.query;

namespace com.db4o.test  {
	
	public class Tester : AllTests 
	{
		private static ObjectServer objectServer;

		private static ExtObjectContainer oc;

		static internal AllTests currentRunner;

		static internal bool clientServer = true;

		static internal bool runServer = true;

		static internal int errorCount = 0;

		static internal int assertionCount = 0;

		static internal int run;

		public static bool COMPARE_INTERNAL_OK = false;

		public static bool CanCheckFileSize() 
		{
			if (currentRunner != null) 
			{
				return !clientServer || !currentRunner.REMOTE_SERVER;
			}
			return false;
		}

		private static Type ClassOf(Object obj)
		{
			if(obj == null)
			{
				return null;
			}
			if(obj is Type)
			{
				return (Type)obj;
			}
			if(obj is Class)
			{
				return ((Class)obj).GetNetType();
			}
			return obj.GetType();
		}

		public static void CloseAll()
		{
			Tester.Close();
			if (Tester.IsClientServer())
			{
				Tester.Server().Close();
			}
		}
      
		public static void Close() 
		{
			if (null != oc) 
			{
				while (!oc.Close()) 
				{
				}
				oc = null;
			}
		}
      
		public static void Commit() 
		{
			oc.Commit();
		}
		
		public static void CommitSync(ExtObjectContainer client1, ExtObjectContainer client2) 
		{
			client1.SetSemaphore("sem", 0);
			client1.Commit();
			client1.ReleaseSemaphore("sem");
			Tester.Ensure(client2.SetSemaphore("sem", 5000));
			client2.ReleaseSemaphore("sem");
		}
		

		public static ObjectServer CurrentServer()
		{
			if(clientServer && runServer)
			{
				return objectServer;
			}
			return null;
		}

		public static void Delete() 
		{
			new File(FILE_SOLO).Delete();
			new File(FILE_SERVER).Delete();
		}
      
		public static void Delete(Object obj) 
		{
			ObjectContainer().Delete(obj);
		}
      
		public static void DeleteAllInstances(Object obj) 
		{
			try 
			{
				Query q = ObjectContainer().Query();
				q.Constrain(ClassOf(obj));
				ObjectSet set1 = q.Execute();
				while (set1.HasNext()) 
				{
					ObjectContainer().Delete(set1.Next());
				}
			}  
			catch (Exception e) 
			{
				Error(e);
			}
		}
      
		public static void End() 
		{
			Close();
			if (objectServer != null) 
			{
				Thread.Sleep(1000);
				objectServer.Close();
				objectServer = null;
			}
		}
      
		public static bool Ensure(bool condition) 
		{
			return Ensure(string.Empty, condition);
		}

		public static bool Ensure(string message, bool condition) 
		{
			assertionCount++;
			if (!condition) 
			{
				Error(message);
				return false;
			}
			return true;
		}
		
		public static bool EnsureEquals(object expected, object actual)
		{
			return EnsureEquals(expected, actual, null);
		}

		public static bool EnsureEquals(object expected, object actual, string message)
		{
			bool eq = true;
			if(expected == null)
			{
				eq = (actual == null);
			}
			else
			{
				eq = expected.Equals(actual);
			}
			string text = message == null
			              	? string.Format("'{0}' != '{1}'", expected, actual)
			              	: string.Format("{0} - '{1}' != '{2}'", message, expected, actual);
			return Ensure(text, eq);
		}
      
		public static void EnsureOccurrences(Object obj, int count) 
		{
			int occ = Occurrences(obj);
			Ensure(occ == count);
			if(occ != count)
			{
				Console.WriteLine("Expected: " + count + " Found: " + occ);
			}
            
		}

		public static void RunIfTestMethod(j4o.lang.reflect.Method method, object target)
		{
			string name = method.GetName();
			if (name.StartsWith("test") || name.StartsWith("Test")) 
			{
				try 
				{
					method.Invoke(target, null);
				}  
				catch (Exception e) 
				{
					Tester.Error(e);     
				}
			}
		}
      
		public static void Error() 
		{
			Error(string.Empty);
		}

		public static void Error(string message) 
		{
			errorCount++;
			Console.WriteLine("!!! TEST CASE FAILED !!! [" + message + "]" + StackTrace());
		}

		public static string StackTrace()
		{
#if CF_1_0 || CF_2_0
			return "";
#else
			return new StackTrace(true).ToString();
#endif
		}

		public static void Error(Exception error)
		{   
#if CF_1_0
			Console.WriteLine("!!! TEST CASE FAILED !!! [" + error.Message + "]");
#else
			errorCount++;
			error = GetRootCause(error);
			Console.WriteLine("!!! TEST CASE FAILED !!! [" + error.Message + "]" + error.StackTrace);

#endif
		}

		private static Exception GetRootCause(Exception error)
		{
			return null != error.GetBaseException()
				? error.GetBaseException()
				: error;
		}

		public static int FileLength() 
		{
			String fileName1 = clientServer ? FILE_SERVER : FILE_SOLO;
			try 
			{
				return (int)new File(fileName1).Length();
			}  
			catch (System.IO.IOException e) 
			{
				j4o.lang.JavaSystem.PrintStackTrace(e);
			}
			return 0;
		}
      
		public static void ForEach(Object obj, Visitor4 vis) 
		{
			ObjectContainer con1 = ObjectContainer();
			con1.Deactivate(obj, Int32.MaxValue);
			ObjectSet set1 = oc.Get(obj);
			while (set1.HasNext()) 
			{
				vis.Visit(set1.Next());
			}
		}
      
		public static Object GetOne(Object obj) 
		{
			Query q = oc.Query();
			q.Constrain(ClassOf(obj));
			ObjectSet set = q.Execute();
			if (set.Size() != 1) 
			{
				Error();
			}
			return set.Next();
		}

		public static bool IsClientServer()
		{
			return CurrentServer() != null;
		}
      
		public static void Log(Query q) 
		{
			ObjectSet set1 = q.Execute();
			while (set1.HasNext()) 
			{
				Logger.Log(oc, set1.Next());
			}
		}
      
		public static void LogAll() 
		{
			ObjectSet set1 = oc.Get(null);
			while (set1.HasNext()) 
			{
				Logger.Log(oc, set1.Next());
			}
		}
      
		public static ExtObjectContainer ObjectContainer() 
		{
			if (oc == null) 
			{
				Open();
			}
			return oc;
		}
      
		public static int Occurrences(Object obj) 
		{
			Query q = oc.Query();
			q.Constrain(ClassOf(obj));
			return q.Execute().Size();
		}
      
		public static ExtObjectContainer Open() 
		{
			if (runServer && clientServer && objectServer == null) 
			{
				objectServer = Db4o.OpenServer(FILE_SERVER, SERVER_PORT);
				objectServer.GrantAccess(DB4O_USER, DB4O_PASSWORD);
				objectServer.Ext().Configure().MessageLevel(0);
			}
			if (clientServer) 
			{
				try 
				{
                    if (EMBEDDED_CLIENT)
                    {
                        oc = objectServer.OpenClient().Ext();
                    }
                    else
                    {
                        oc = Db4o.OpenClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).Ext();
                    }
                    
				}  
				catch (Exception e) 
				{
					j4o.lang.JavaSystem.PrintStackTrace(e);
					return null;
				}
			} 
			else 
			{
				oc = Db4o.OpenFile(FILE_SOLO).Ext();
			}
			return oc;
		}

		public static ObjectContainer ReOpenServer()
		{
			if(runServer && clientServer)
			{
				Close();
				objectServer.Close();
				objectServer = null;
				try 
				{
					Thread.Sleep(100);
				} 
				catch (Exception e) 
				{
				}
				return Open();
			}
			else
			{
				return ReOpen();
			}
		}
      
		public static Query Query() 
		{
			return ObjectContainer().Query();
		}

		public static void ReOpenAll()
		{
			if (Tester.IsClientServer())
			{
				Tester.ReOpenServer();
			}
			Tester.ReOpen();
		}
      
		public static ObjectContainer ReOpen() 
		{
			Close();
			return Open();
		}
      
		public static void RollBack() 
		{
			ObjectContainer().Rollback();
		}

		public static ObjectServer Server()
		{
			return objectServer;
		}

		public static void Store(Object obj) 
		{
			ObjectContainer().Set(obj);
		}
      
		public static void PrintStatistics() 
		{
			Statistics.Main(new String[] { FILE_SOLO });
		}
	}
}

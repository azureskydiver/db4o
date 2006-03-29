/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Diagnostics;
using com.db4o.foundation;
using j4o.lang;
using j4o.io;
using com.db4o;
using com.db4o.ext;
using com.db4o.query;
using com.db4o.tools;

namespace com.db4o.test 
{
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

		public static void Main(String[] args) 
		{
			throw new RuntimeException("This class is not intended to be run. Run AllTests.java.");
		}
      
		public static bool canCheckFileSize() 
		{
			if (currentRunner != null) 
			{
				return !clientServer || !currentRunner.REMOTE_SERVER;
			}
			return false;
		}

		private static Type classOf(Object obj)
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
				return ((Class)obj).getNetType();
			}
			return obj.GetType();
		}

		public static void closeAll()
		{
			Tester.close();
			if (Tester.isClientServer())
			{
				Tester.server().close();
			}
		}
      
		public static void close() 
		{
			if (null != oc) 
			{
				while (!oc.close()) 
				{
				}
				oc = null;
			}
		}
      
		public static void commit() 
		{
			oc.commit();
		}
		
		public static void commitSync(ExtObjectContainer client1, ExtObjectContainer client2) 
		{
			client1.setSemaphore("sem", 0);
			client1.commit();
			client1.releaseSemaphore("sem");
			Tester.ensure(client2.setSemaphore("sem", 5000));
			client2.releaseSemaphore("sem");
		}
		

		public static ObjectServer currentServer()
		{
			if(clientServer && runServer)
			{
				return objectServer;
			}
			return null;
		}

		public static void delete() 
		{
			new File(FILE_SOLO).delete();
			new File(FILE_SERVER).delete();
		}
      
		public static void delete(Object obj) 
		{
			objectContainer().delete(obj);
		}
      
		public static void deleteAllInstances(Object obj) 
		{
			try 
			{
				Query q = objectContainer().query();
				q.constrain(classOf(obj));
				ObjectSet set1 = q.execute();
				while (set1.hasNext()) 
				{
					objectContainer().delete(set1.next());
				}
			}  
			catch (Exception e) 
			{
				error(e);
			}
		}
      
		public static void end() 
		{
			close();
			if (objectServer != null) 
			{
				Thread.sleep(1000);
				objectServer.close();
				objectServer = null;
			}
		}
      
		public static bool ensure(bool condition) 
		{
			return ensure(string.Empty, condition);
		}

		public static bool ensure(string message, bool condition) 
		{
			assertionCount++;
			if (!condition) 
			{
				error(message);
				return false;
			}
			return true;
		}
		
		public static bool ensureEquals(object expected, object actual)
		{
			return ensureEquals(expected, actual, null);
		}

		public static bool ensureEquals(object expected, object actual, string message)
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
			return ensure(text, eq);
		}
      
		public static void ensureOccurrences(Object obj, int count) 
		{
			int occ = occurrences(obj);
			ensure(occ == count);
			if(occ != count)
			{
				Console.WriteLine("Expected: " + count + " Found: " + occ);
			}
            
		}

		public static void runIfTestMethod(j4o.lang.reflect.Method method, object target)
		{
			string name = method.getName();
			if (name.StartsWith("test") || name.StartsWith("Test")) 
			{
				try 
				{
					method.invoke(target, null);
				}  
				catch (Exception e) 
				{
					Tester.error(e);     
				}
			}
		}
      
		public static void error() 
		{
			error(string.Empty);
		}

		public static void error(string message) 
		{
			errorCount++;
			Console.WriteLine("!!! TEST CASE FAILED !!! [" + message + "]" + stackTrace());
		}

		public static string stackTrace()
		{
#if CF_1_0 || CF_2_0
			return "";
#else
			return new StackTrace(true).ToString();
#endif
		}

		public static void error(Exception error)
		{   
#if CF_1_0 || CF_2_0
			Console.WriteLine("!!! TEST CASE FAILED !!! [" + error.Message + "]");
#else
			errorCount++;
			error = getRootCause(error);
			Console.WriteLine("!!! TEST CASE FAILED !!! [" + error.Message + "]" + error.StackTrace);

#endif
		}

		private static Exception getRootCause(Exception error)
		{
			return null != error.GetBaseException()
				? error.GetBaseException()
				: error;
		}

		public static int fileLength() 
		{
			String fileName1 = clientServer ? FILE_SERVER : FILE_SOLO;
			try 
			{
				return (int)j4o.lang.JavaSystem.getLengthOf(new File(fileName1));
			}  
			catch (System.IO.IOException e) 
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
			return 0;
		}
      
		public static void forEach(Object obj, Visitor4 vis) 
		{
			ObjectContainer con1 = objectContainer();
			con1.deactivate(obj, Int32.MaxValue);
			ObjectSet set1 = oc.get(obj);
			while (set1.hasNext()) 
			{
				vis.visit(set1.next());
			}
		}
      
		public static Object getOne(Object obj) 
		{
			Query q = oc.query();
			q.constrain(classOf(obj));
			ObjectSet set = q.execute();
			if (set.size() != 1) 
			{
				error();
			}
			return set.next();
		}

		public static bool isClientServer()
		{
			return currentServer() != null;
		}
      
		public static void log(Query q) 
		{
			ObjectSet set1 = q.execute();
			while (set1.hasNext()) 
			{
				Logger.log(oc, set1.next());
			}
		}
      
		public static void logAll() 
		{
			ObjectSet set1 = oc.get(null);
			while (set1.hasNext()) 
			{
				Logger.log(oc, set1.next());
			}
		}
      
		public static ExtObjectContainer objectContainer() 
		{
			if (oc == null) 
			{
				open();
			}
			return oc;
		}
      
		public static int occurrences(Object obj) 
		{
			Query q = oc.query();
			q.constrain(classOf(obj));
			return q.execute().size();
		}
      
		public static ExtObjectContainer open() 
		{
			if (runServer && clientServer && objectServer == null) 
			{
				objectServer = Db4o.openServer(FILE_SERVER, SERVER_PORT);
				objectServer.grantAccess(DB4O_USER, DB4O_PASSWORD);
				objectServer.ext().configure().messageLevel(0);
			}
			if (clientServer) 
			{
				try 
				{
                    if (EMBEDDED_CLIENT)
                    {
                        oc = objectServer.openClient().ext();
                    }
                    else
                    {
                        oc = Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).ext();
                    }
                    
				}  
				catch (Exception e) 
				{
					j4o.lang.JavaSystem.printStackTrace(e);
					return null;
				}
			} 
			else 
			{
				oc = Db4o.openFile(FILE_SOLO).ext();
			}
			return oc;
		}

		public static ObjectContainer reOpenServer()
		{
			if(runServer && clientServer)
			{
				close();
				objectServer.close();
				objectServer = null;
				try 
				{
					Thread.sleep(100);
				} 
				catch (Exception e) 
				{
				}
				return open();
			}
			else
			{
				return reOpen();
			}
		}
      
		public static Query query() 
		{
			return objectContainer().query();
		}

		public static void reOpenAll()
		{
			if (Tester.isClientServer())
			{
				Tester.reOpenServer();
			}
			Tester.reOpen();
		}
      
		public static ObjectContainer reOpen() 
		{
			close();
			return open();
		}
      
		public static void rollBack() 
		{
			objectContainer().rollback();
		}

		public static ObjectServer server()
		{
			return objectServer;
		}

		public static void store(Object obj) 
		{
			objectContainer().set(obj);
		}
      
		public static void printStatistics() 
		{
			Statistics.Main(new String[] { FILE_SOLO });
		}
	}
}

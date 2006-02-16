using System;
using com.db4o;

namespace com.db4o.f1
{
	public class Util
	{
		public readonly static string YapFileName = "formula1.yap";
		
		public readonly static int ServerPort = 0xdb40;
		
		public readonly static string ServerUser = "user";
		
		public readonly static string ServerPassword = "password";

		public static void listResult(ObjectSet result)
		{
			Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				Console.WriteLine(item);
			}
		}

		public static void listRefreshedResult(ObjectContainer container, ObjectSet items, int depth)
		{
			Console.WriteLine(items.Count);
			foreach (object item in items)
			{	
				container.ext().refresh(item, depth);
				Console.WriteLine(item);
			}
		}
		
		public static void retrieveAll(ObjectContainer db) 
		{
			ObjectSet result = db.get(typeof(Object));
			listResult(result);
		}
		
		public static void deleteAll(ObjectContainer db) 
		{
			ObjectSet result = db.get(typeof(Object));
			foreach (object item in result)
			{
				db.delete(item);
			}
		}		
	}
}

using System;
using com.db4o;

namespace com.db4odoc.f1
{
	public class Util
	{
		public readonly static string YapFileName = "formula1.yap";
		
		public readonly static int ServerPort = 0xdb40;
		
		public readonly static string ServerUser = "user";
		
		public readonly static string ServerPassword = "password";

		public static void ListResult(ObjectSet result)
		{
			Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				Console.WriteLine(item);
			}
		}

		public static void ListRefreshedResult(ObjectContainer container, ObjectSet items, int depth)
		{
			Console.WriteLine(items.Count);
			foreach (object item in items)
			{	
				container.Ext().Refresh(item, depth);
				Console.WriteLine(item);
			}
		}
		
		public static void RetrieveAll(ObjectContainer db) 
		{
			ObjectSet result = db.Get(typeof(Object));
			ListResult(result);
		}
		
		public static void DeleteAll(ObjectContainer db) 
		{
			ObjectSet result = db.Get(typeof(Object));
			foreach (object item in result)
			{
				db.Delete(item);
			}
		}		
	}
}

namespace com.db4o.f1
{
    using System;
    using com.db4o;
    
    public class Util
    {
        public readonly static string YapFileName = "formula1.yap";
		
		public readonly static int ServerPort = 0xdb40;
		
        public readonly static string ServerUser = "user";
		
        public readonly static string ServerPassword = "password";

        public static void listResult(ObjectSet result)
        {
            Console.WriteLine(result.size());
            while (result.hasNext())
            {
                Console.WriteLine(result.next());
            }
        }

		public static void listRefreshedResult(ObjectContainer container, ObjectSet items, int depth)
		{
			Console.WriteLine(items.size());
			while (items.hasNext())
			{
				object item = items.next();
				container.ext().refresh(item, depth);
				Console.WriteLine(item);
			}
		}
		
		public static void deleteAll(ObjectContainer db) {
        	ObjectSet result = db.get(typeof(Object));
        	while(result.hasNext()){
	            db.delete(result.next());
	        }
    	}		
    }
}

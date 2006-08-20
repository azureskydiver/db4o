/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

namespace com.db4odoc.f1.blobs
{
	using System.IO;
	using System;
	
	using com.db4o;
	using com.db4o.query;
	using com.db4odoc.f1;

	public class BlobExample: Util {

		 public static void Main(string[] args)
		 {
			StoreCars();
			RetrieveCars();
		}
			
		public static void StoreCars() {
			File.Delete(Util.YapFileName);
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try {
					Car car1=new Car("Ferrari");
					db.Set(car1);
					StoreImage(car1);
					Car car2=new Car("BMW");
					db.Set(car2);
					StoreImage(car2);
			}  finally {
				db.Close();
				} 
		}
		  
		public static void StoreImage(Car car) {
				CarImage img = car.CarImage;
				try {
		    		img.ReadFile();
				} catch (Exception ex){
		    		Console.WriteLine(ex.Message);
				}
			}
		  
		public static void RetrieveCars() {
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try {
				Query query = db.Query();
				query.Constrain(typeof(Car));
				ObjectSet result = query.Execute();
				GetImages(result);
			}  finally {
				db.Close();
				} 
		}
		 
		private static  void GetImages(ObjectSet result){
			while(result.HasNext()) {
					Car car = (Car)(result.Next());
					Console.WriteLine(car);
					CarImage img = car.CarImage;
					try {
	            		img.WriteFile();
					} catch (Exception ex){
	            		Console.WriteLine(ex.Message);
					}
				}
		}
	}
}
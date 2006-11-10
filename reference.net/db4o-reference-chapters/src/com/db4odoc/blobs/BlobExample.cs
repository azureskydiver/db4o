/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.Blobs
{
	using System.IO;
	using System;

    using Db4objects.Db4o;
    using Db4objects.Db4o.Query;
	using com.db4odoc.f1;

	public class BlobExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args)
		{
			StoreCars();
			RetrieveCars();
		}
		// end Main
			
		public static void StoreCars() 
		{
			File.Delete(YapFileName);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Car car1=new Car("Ferrari");
				db.Set(car1);
				StoreImage(car1);
				Car car2=new Car("BMW");
				db.Set(car2);
				StoreImage(car2);
			}  
			finally 
			{
				db.Close();
			} 
		}
		// end StoreCars
		  
		public static void StoreImage(Car car) 
		{
			CarImage img = car.CarImage;
			try 
			{
				img.ReadFile();
			} 
			catch (Exception ex)
			{
				Console.WriteLine(ex.Message);
			}
		}
		// end StoreImage
		  
		public static void RetrieveCars() 
		{
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IQuery query = db.Query();
				query.Constrain(typeof(Car));
				IObjectSet result = query.Execute();
				GetImages(result);
			}  
			finally 
			{
				db.Close();
			} 
		}
		// end RetrieveCars
		 
		private static  void GetImages(IObjectSet result)
		{
			while(result.HasNext()) 
			{
				Car car = (Car)(result.Next());
				Console.WriteLine(car);
				CarImage img = car.CarImage;
				try 
				{
					img.WriteFile();
				} 
				catch (Exception ex)
				{
					Console.WriteLine(ex.Message);
				}
			}
		}
		// end GetImages
	}
}
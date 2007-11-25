/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Blobs
{
	using System;
	using System.IO;

    using Db4objects.Db4o;
    using Db4objects.Db4o.Query;

	public class BlobExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args)
		{
			StoreCars();
			RetrieveCars();
		}
		// end Main

        private static void StoreCars() 
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void StoreImage(Car car) 
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

        private static void RetrieveCars() 
		{
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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
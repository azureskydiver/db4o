/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using com.db4o;
using com.db4o.config;

namespace com.db4odoc.f1.diagnostics
{

	public class CarTranslator: ObjectConstructor 
	{
		public object OnStore(ObjectContainer container,	object applicationObject) 
		{
			Car car =(Car)applicationObject;

			string fullModel;
			if (HasYear(car.Model))
			{
				fullModel = car.Model;
			} 
			else 
			{
				fullModel = car.Model + GetYear(car.Model);
			}
			return fullModel;
		}

		private string GetYear(string carModel)
		{
			if (carModel.Equals("BMW"))
			{
				return " 2002";
			} 
			else 
			{
				return " 1999";
			}
		}
		  
		private bool HasYear(string carModel)
		{
			return false;
		}
		  
		public object OnInstantiate(ObjectContainer container, object storedObject) 
		{
			string model=(string)storedObject;
			return new Car(model);
		}

		public void OnActivate(ObjectContainer container, 
			object applicationObject, object storedObject) 
		{
		}

		public j4o.lang.Class StoredClass()
		{
			return  j4o.lang.Class.GetClassForType(typeof(string));
		}
	}
}
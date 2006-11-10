/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Evaluations
{

	public class CarTranslator: IObjectConstructor 
	{
		public object OnStore(IObjectContainer container,	object applicationObject) 
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
		  
		public object OnInstantiate(IObjectContainer container, object storedObject) 
		{
			string model=(string)storedObject;
			return new Car(model);
		}

		public void OnActivate(IObjectContainer container, 
			object applicationObject, object storedObject) 
		{
		}

		
        #region IObjectTranslator Members


        System.Type IObjectTranslator.StoredClass()
        {
            return typeof(string);
        }

        #endregion
    }
}
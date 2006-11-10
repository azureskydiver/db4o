/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Remote
{   
	public class Car
	{
		string _model;
	
		public Car()
		{
		}

		public Car(string model)
		{
			_model = model;
		}
        
		public string Model
		{
			get
			{
				return _model;
			}
			set
			{
				_model = value;
			}
		}
        
		override public string ToString()
		{
			return  _model;
		}
	}
}


/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.Persist
{
	public class Car
	{
		private string _model;
		private int _temperature;
    
		public Car()
		{
		}
    
		public Car(string model)
		{
			this._model = model;
		}
    
		public string Model 
		{
			get 
			{
				return _model;
			}
			set 
			{
				this._model = value;
			}
		}
     	
		public int Temperature
		{
			get
			{
				return _temperature;
			}
			set 
			{
				_temperature = value;
			}
		}
    
		override public string ToString() 
		{
			return string.Format("{0}-{1} C",_model,_temperature);
		}
	}
}
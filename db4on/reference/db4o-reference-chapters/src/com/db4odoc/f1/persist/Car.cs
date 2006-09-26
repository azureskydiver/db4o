/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace com.db4odoc.f1.persist
{
	public class Car
	{
		private String _model;
		private int _temperature;
    
		public Car()
		{
		}
    
		public Car(String model)
		{
			this._model = model;
		}
    
		public String Model 
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
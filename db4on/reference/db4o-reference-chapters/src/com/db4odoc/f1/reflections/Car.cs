/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4odoc.f1.reflections
{

	public class Car
	{
		string _model;
		
		public Car(string model)
		{
			_model = model;
		}
		
		override public string ToString()
		{
			return  _model;
		}
	}
}

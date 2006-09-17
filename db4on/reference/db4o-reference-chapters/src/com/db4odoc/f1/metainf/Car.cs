/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
namespace com.db4odoc.f1.metainf
{   
	public class Car
	{
		string _model;
		Pilot _pilot;
	
		public Car(string model, Pilot pilot)
		{
			_model = model;
			_pilot = pilot;
		}
        
		public Pilot Pilot
		{
			get
			{
				return _pilot;
			}
		}
        
		override public string ToString()
		{
			return string.Format("{0}[{1}]", _model, _pilot);
		}
	}
}


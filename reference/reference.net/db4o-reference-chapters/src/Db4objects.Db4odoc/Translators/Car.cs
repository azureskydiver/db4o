/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;

namespace Db4objects.Db4odoc.Translators
{
	public class Car
	{
		string _model;
      
		public Car(string model) 
		{
		}
        public string Model
		{
			get
			{
				return _model;
			}
		}
        
		
		override public string ToString()
		{
			return  _model;
		}
	}
}

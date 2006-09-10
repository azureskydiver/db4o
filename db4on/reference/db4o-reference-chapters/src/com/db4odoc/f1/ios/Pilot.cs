/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;

namespace com.db4odoc.f1.ios
{
	public class Pilot
	{
		private string _name;
		
		public Pilot(string name) 
		{
			this._name=name;
		}

		override public string ToString() 
		{
			return _name;
		}
	}
}

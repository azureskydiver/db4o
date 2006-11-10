/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;

namespace Db4objects.Db4odoc.MetaInfo
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

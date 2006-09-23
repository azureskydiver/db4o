/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace com.db4odoc.f1.refactoring.newclasses
{
	public class Pilot
	{
		private Identity _name;
		
		public Pilot(Identity name) 
		{
			this._name=name;
		}

		public Identity Name 
		{
			get 
			{
				return _name;
			}
			set 
			{
				_name = value;
			}
		}

		override public string ToString() 
		{
			return _name.ToString();
		}
	}
}

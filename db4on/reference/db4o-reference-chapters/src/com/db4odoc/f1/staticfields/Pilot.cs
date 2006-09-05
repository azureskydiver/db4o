/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace com.db4odoc.f1.staticfields
{
	public class Pilot
	{
		private String _name;
		private PilotCategories _category;

		public Pilot(String name,PilotCategories category) 
		{
			this._name=name;
			this._category=category;
		}

		public PilotCategories Category
		{
			get 
			{
				return _category;
			}
		}

		public String Name
		{
			get 
			{
				return _name;
			}
		}

		override public string ToString() 
		{
			return string.Format("{0}/{1}", _name, _category);
		}
	}
}

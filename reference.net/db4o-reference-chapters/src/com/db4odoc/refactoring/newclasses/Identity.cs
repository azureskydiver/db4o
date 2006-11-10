/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.Refactoring.NewClasses
{
	public class Identity
	{
		private string  _name;
		private string _id;
	
		public Identity(string name, string id)
		{
			_name = name;
			_id = id;
		}
	
		public string Name
		{
			get 
			{
				return _name;
			}
			set
			{
				_name=value;
			}
		}
	
	
		public String toString() 
		{
			return string.Format("{0}[{1}]", _name, _id);
		}
	}
}

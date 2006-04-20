/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

namespace CFNativeQueriesEnabler.Tests.Subject
{
	public class Item
	{
		private string _name;

		public Item(string name)
		{
			_name = name;
		}

		public string Name
		{
			get { return _name; }
		}
	}
}
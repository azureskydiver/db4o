/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace Db4objects.Db4odoc.SelectivePersistence
{
	public class Test
	{
        [Db4objects.Db4o.Transient]
		string _transientField;
		string _persistentField;

		public Test(string transientField, string persistentField)
		{
			_transientField = transientField;
			_persistentField = persistentField;
		}

		public override String ToString() 
		{
			return "Test: persistent: " + _persistentField + ", transient: " + _transientField ;
		}
	}
}

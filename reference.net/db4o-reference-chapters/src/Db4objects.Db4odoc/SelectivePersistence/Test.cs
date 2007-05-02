/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
namespace Db4objects.Db4odoc.SelectivePersistence
{
	public class Test
	{
        [Db4objects.Db4o.Transient]
        // you can also use [NonSerializedAttribute]
        string _transientField;
		string _persistentField;

		public Test(string transientField, string persistentField)
		{
			_transientField = transientField;
			_persistentField = persistentField;
		}

		public override string ToString() 
		{
			return "Test: persistent: " + _persistentField + ", transient: " + _transientField ;
		}
	}
}

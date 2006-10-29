/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;

namespace com.db4odoc.f1.selpersist
{
	public class TestCusomized
	{
		[com.db4odoc.f1.selpersist.FieldTransient]
		string _transientField;
		string _persistentField;

		public TestCusomized(string transientField, string persistentField)
		{
			_transientField = transientField;
			_persistentField = persistentField;
		}

		public override String ToString() 
		{
			return "Customized test: persistent: " + _persistentField + ", transient: " + _transientField ;
		}
	}
}
/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
namespace Db4objects.Db4odoc.SelectivePersistence
{
	public class TestCustomized
	{
        [Db4objects.Db4odoc.SelectivePersistence.FieldTransient]
        string _transientField;
		string _persistentField;

		public TestCustomized(string transientField, string persistentField)
		{
			_transientField = transientField;
			_persistentField = persistentField;
		}

		public override string ToString() 
		{
			return "Customized test: persistent: " + _persistentField + ", transient: " + _transientField ;
		}
	}
}
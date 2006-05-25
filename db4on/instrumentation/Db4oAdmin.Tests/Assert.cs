/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

using System;

namespace Db4oAdmin.Tests
{
	class Assert
	{
		public static void AreEqual(object expected, object actual)
		{
			if (!object.Equals(expected, actual))
			{
				throw new ApplicationException(string.Format("'{0}' != '{1}'", expected, actual));
			}
		}
		
		public static void IsTrue(bool condition)
		{
			if (!condition) throw new ApplicationException();
		}
	}
}

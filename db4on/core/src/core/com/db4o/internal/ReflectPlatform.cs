/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

using System;

using j4o.lang;

namespace com.db4o.@internal
{
	internal class ReflectPlatform
	{
		public static Class ForName(string className)
		{
			try
			{
				return j4o.lang.Class.ForName(className);
			}
			catch
			{
				return null;
			}
		}

		public static object CreateInstance(string typeName)
		{
			try
			{
				return Activator.CreateInstance(ForName(typeName).GetNetType());	
			}
			catch
			{
				return null;
			}
		}
	}
}

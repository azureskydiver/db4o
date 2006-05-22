/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;

namespace com.db4o.config
{
	/// <exclude />
	public class TClass : ObjectConstructor
	{
		static readonly Class _stringClass = Class.GetClassForType(typeof(String));

		public void OnActivate(ObjectContainer objectContainer, object obj, object members)
		{
		}

		public Object OnInstantiate(ObjectContainer objectContainer, object obj)
		{
			try
			{
				return Class.ForName((String)obj);
			}
			catch (Exception exception)
			{
				return null;
			}
		}

		public Object OnStore(ObjectContainer objectContainer, object obj)
		{
			return ((Class)obj).GetName();
		}

		public Class StoredClass()
		{
			return _stringClass;
		}
	}
}
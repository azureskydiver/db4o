/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4odoc.f1.reflections
{
	public class LoggingReflector : com.db4o.reflect.Reflector
	{
		private LoggingArray _arrayHandler;
		private com.db4o.reflect.Reflector _parent;

		public LoggingReflector()
		{
		}

		public virtual com.db4o.reflect.ReflectArray Array()
		{
			if (_arrayHandler == null)
			{
				_arrayHandler = new LoggingArray(_parent);
			}
			return _arrayHandler;
		}

		public virtual bool ConstructorCallsSupported()
		{
			return true;
		}

		public virtual com.db4o.reflect.ReflectClass ForClass(j4o.lang.Class clazz)
		{
			com.db4o.reflect.ReflectClass rc = new com.db4o.reflect.net.NetClass(_parent, clazz);
			Console.WriteLine("ForClass: " + clazz+" -> "+(rc== null ? "" : rc.GetName()));    
			return rc;
		}

		public virtual com.db4o.reflect.ReflectClass ForName(string className)
		{
			try
			{
				j4o.lang.Class clazz = j4o.lang.Class.ForName(className);
				com.db4o.reflect.ReflectClass rc = ForClass(clazz);
				Console.WriteLine("ForName: " + clazz+" -> "+(rc== null ? "" : rc.GetName()));    
				return rc;
			}
			catch (j4o.lang.ClassNotFoundException e)
			{
				return null;
			}
		}

		public virtual com.db4o.reflect.ReflectClass ForObject(object a_object)
		{
			if (a_object == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass rc = _parent.ForClass(j4o.lang.Class.GetClassForObject(a_object));
			Console.WriteLine("ForObject:" + a_object+" -> "+(rc== null ? "" : rc.GetName()));
			return rc;
		}

		public virtual bool IsCollection(com.db4o.reflect.ReflectClass claxx)
		{
			return false;
		}

		public virtual void SetParent(com.db4o.reflect.Reflector reflector)
		{
			_parent = reflector;
		}

		public virtual object DeepClone(object context)
		{
			return new LoggingReflector();
		}
	}
}


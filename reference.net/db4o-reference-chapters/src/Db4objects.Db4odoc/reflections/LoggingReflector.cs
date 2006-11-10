/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;

namespace Db4objects.Db4odoc.Reflections
{
	public class LoggingReflector : Db4objects.Db4o.Reflect.IReflector 
	{
		private LoggingArray _arrayHandler;
		private Db4objects.Db4o.Reflect.IReflector _parent;

		public LoggingReflector()
		{
		}

		public virtual Db4objects.Db4o.Reflect.IReflectArray Array()
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

		public virtual Db4objects.Db4o.Reflect.IReflectClass ForName(string className)
		{
			try
			{
                System.Type clazz = System.Type.GetType(className);
                Db4objects.Db4o.Reflect.IReflectClass rc = ForClass(clazz);
				Console.WriteLine("ForName: " + clazz+" -> "+(rc== null ? "" : rc.GetName()));    
				return rc;
			}
            catch (System.TypeLoadException e)
			{
				return null;
			}
		}

        public virtual Db4objects.Db4o.Reflect.IReflectClass ForObject(object a_object)
		{
			if (a_object == null)
			{
				return null;
			}
			Db4objects.Db4o .Reflect .IReflectClass rc = _parent.ForClass(a_object.GetType());
			Console.WriteLine("ForObject:" + a_object+" -> "+(rc== null ? "" : rc.GetName()));
			return rc;
		}

        public virtual bool IsCollection(Db4objects.Db4o.Reflect.IReflectClass claxx)
		{
			return false;
		}

		public virtual object DeepClone(object context)
		{
			return new LoggingReflector();
		}

        public Db4objects.Db4o.Reflect.IReflectClass ForClass(Type clazz)
        {
            Db4objects.Db4o.Reflect.IReflectClass rc = new Db4objects.Db4o.Reflect.Net.NetClass(_parent, clazz);
            Console.WriteLine("ForClass: " + clazz + " -> " + (rc == null ? "" : rc.GetName()));
            return rc;
        }

        public void SetParent(Db4objects.Db4o.Reflect.IReflector reflector)
        {
            _parent = reflector;
        }

    }
}


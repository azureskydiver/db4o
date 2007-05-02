/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */
using System;
using Sharpen.Lang ;

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
            System.Type clazz = null;
			try
            {
                clazz = TypeReference.FromString(className).Resolve();
            }
            catch (System.Exception)
            {
            }
            Db4objects.Db4o.Reflect.IReflectClass rc = clazz == null
                ? null
                : new Db4objects.Db4o.Reflect.Net.NetClass(_parent, clazz);
            Console.WriteLine("ForName: " + clazz + " -> " + (rc == null ? "" : rc.GetName()));
            return rc;
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

        public virtual bool IsCollection(Db4objects.Db4o.Reflect.IReflectClass candidate)
		{
            bool result = false;
            if (candidate.IsArray())
            {
                result = false;
            }
            if (typeof(System.Collections.ICollection).IsAssignableFrom(
                ((Db4objects.Db4o.Reflect.Net.NetClass)candidate).GetNetType()))
            {
                result = true;
            }
            Console.WriteLine("Type " + candidate.GetName () + " is Collection " + result);
            return result;
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


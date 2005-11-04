/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;

namespace j4o.lang.reflect
{
    public class Method
	{
        private MethodInfo _methodInfo;

        internal Method(MethodInfo methodInfo)
		{
            this._methodInfo = methodInfo;
        }

		internal MethodInfo MethodInfo
		{
			get { return _methodInfo; }
		}

        public Object invoke(Object obj, Object[] args)
		{
            return _methodInfo.Invoke(obj, args);
        }

        public String getName()
		{
            return _methodInfo.Name;
        }

		public j4o.lang.Class[] getParameterTypes() 
		{
			ParameterInfo[] parameters = _methodInfo.GetParameters();
			j4o.lang.Class[] types = new j4o.lang.Class[parameters.Length];
			for (int i=0; i<parameters.Length; ++i)
			{
				types[i] = j4o.lang.Class.getClassForType(parameters[i].ParameterType);
			}
			return types;
		}

		public j4o.lang.Class getReturnType() 
		{
			return j4o.lang.Class.getClassForType(_methodInfo.ReturnType);
		}
    }
}


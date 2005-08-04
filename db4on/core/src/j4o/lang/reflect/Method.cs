/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;

namespace j4o.lang.reflect
{
    public class Method
	{
        private MethodInfo methodInfo;

        internal Method(MethodInfo methodInfo)
		{
            this.methodInfo = methodInfo;
        }

        public Object invoke(Object obj, Object[] args)
		{
            return methodInfo.Invoke(obj, args);
        }

        public String getName()
		{
            return methodInfo.Name;
        }

		public j4o.lang.Class[] getParameterTypes() 
		{
			ParameterInfo[] parameters = methodInfo.GetParameters();
			j4o.lang.Class[] types = new j4o.lang.Class[parameters.Length];
			for (int i=0; i<parameters.Length; ++i)
			{
				types[i] = j4o.lang.Class.getClassForType(parameters[i].ParameterType);
			}
			return types;
		}

		public j4o.lang.Class getReturnType() 
		{
			return j4o.lang.Class.getClassForType(methodInfo.ReturnType);
		}
    }
}

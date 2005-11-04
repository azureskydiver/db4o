/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;
using j4o.lang;

namespace j4o.lang.reflect {

    public class Constructor {

        private ConstructorInfo constructorInfo;

        internal Constructor(ConstructorInfo constructorInfo) {
            this.constructorInfo = constructorInfo;
        }

        public Class[] getParameterTypes() {
            ParameterInfo[] parameterInfos = constructorInfo.GetParameters();
            Class[] classes = new Class[parameterInfos.Length];
            for(int i = 0; i < parameterInfos.Length; i++) {
                classes[i] = Class.getClassForType(parameterInfos[i].ParameterType);
            }
            return classes;
        }

        public Object newInstance(Object[] parameters) {
            return constructorInfo.Invoke(parameters);
        }
    }
}

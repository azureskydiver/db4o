/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Reflection;

namespace j4o.lang {

    delegate int IdentityHashCodeProviderDelegate(object obj);

    public class IdentityHashCodeProvider {

        public static int identityHashCode(object obj) {
            if(obj == null) {
                return 0;
            }
            return platformDelegate(obj);
        }

        private static MethodInfo hashMethod;

        static IdentityHashCodeProviderDelegate platformDelegate =
            createDelegate();

        static IdentityHashCodeProviderDelegate createDelegate() {

            Assembly assembly = typeof(object).Assembly;

            // .NET Framework 1.1
            try {
                Type t = assembly.GetType(
                    "System.Runtime.CompilerServices.RuntimeHelpers");
                hashMethod = t.GetMethod(
                    "GetHashCode",
                    BindingFlags.Public |
                    BindingFlags.Static);
                if(hashMethod != null) {
                    return new IdentityHashCodeProviderDelegate(
                        IdentityHashCodeProvider.InvokeStatic);
                }
            } catch(Exception e) {
            }

            // CompactFramework
            try {
                Type t = assembly.GetType("System.PInvoke.EE");
                hashMethod = t.GetMethod(
                    "Object_GetHashCode",
                    BindingFlags.Public |
                    BindingFlags.NonPublic |
                    BindingFlags.Static);
                if(hashMethod != null) {
                    return new IdentityHashCodeProviderDelegate(
                        IdentityHashCodeProvider.InvokeStatic);
                }
            } catch(Exception e) {
            }

            // .NET Framework 1.0
            hashMethod = (typeof(System.Object)).GetMethod("GetHashCode");
            return new IdentityHashCodeProviderDelegate(
                IdentityHashCodeProvider.InvokeObject);
        }

        static int InvokeObject(object obj) {
            return (int)hashMethod.Invoke(obj, null);
        }

        static int InvokeStatic(object obj) {
            return (int)hashMethod.Invoke(null, new object[] {
                obj
            });
        }
    }
}

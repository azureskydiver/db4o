/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
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

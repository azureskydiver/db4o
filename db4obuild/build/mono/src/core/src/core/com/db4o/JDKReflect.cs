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
namespace com.db4o
{
	/// <summary>
	/// package and class name are hard-referenced in JavaOnly#jdk()
	/// TODO: may need to use this on instead of JDK on .NET.
	/// </summary>
	/// <remarks>
	/// package and class name are hard-referenced in JavaOnly#jdk()
	/// TODO: may need to use this on instead of JDK on .NET. Check!
	/// </remarks>
	internal class JDKReflect : com.db4o.JDK
	{
		internal override j4o.lang.Class constructorClass()
		{
			return j4o.lang.Class.getClassForType(typeof(j4o.lang.reflect.Constructor));
		}

		/// <summary>
		/// use for system classes only, since not ClassLoader
		/// or Reflector-aware
		/// </summary>
		internal sealed override bool methodIsAvailable(string className, string methodName
			, j4o.lang.Class[] _params)
		{
			try
			{
				j4o.lang.Class clazz = j4o.lang.Class.forName(className);
				if (clazz.getMethod(methodName, _params) != null)
				{
					return true;
				}
				return false;
			}
			catch (System.Exception t)
			{
			}
			return false;
		}

		/// <summary>
		/// use for system classes only, since not ClassLoader
		/// or Reflector-aware
		/// </summary>
		protected virtual object invoke(object obj, string methodName, j4o.lang.Class[] paramClasses
			, object[] _params)
		{
			return invoke(j4o.lang.Class.getClassForObject(obj).getName(), methodName, paramClasses
				, _params, obj);
		}

		/// <summary>
		/// use for system classes only, since not ClassLoader
		/// or Reflector-aware
		/// </summary>
		protected virtual object invoke(string className, string methodName, j4o.lang.Class[]
			 paramClasses, object[] _params, object onObject)
		{
			try
			{
				j4o.lang.reflect.Method method = getMethod(className, methodName, paramClasses);
				return method.invoke(onObject, _params);
			}
			catch (System.Exception t)
			{
			}
			return null;
		}

		/// <summary>
		/// calling this "method" will break C# conversion with the old converter
		/// use for system classes only, since not ClassLoader
		/// or Reflector-aware
		/// </summary>
		protected virtual j4o.lang.reflect.Method getMethod(string className, string methodName
			, j4o.lang.Class[] paramClasses)
		{
			try
			{
				j4o.lang.Class clazz = j4o.lang.Class.forName(className);
				j4o.lang.reflect.Method method = clazz.getMethod(methodName, paramClasses);
				return method;
			}
			catch (System.Exception t)
			{
			}
			return null;
		}
	}
}

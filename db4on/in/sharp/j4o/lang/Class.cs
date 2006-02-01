/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using System.Reflection;
using com.db4o.query;
using j4o.lang.reflect;

namespace j4o.lang
{
	public class Class
	{
		private static IDictionary _typeToClassMap = new Hashtable();

		private static IDictionary _typeNameToClassMap = new Hashtable();

		private static Type[] PRIMITIVE_TYPES = {
		                                        	typeof (DateTime), typeof (Decimal)
		                                        };

		private Type _type;
		private String _name;
		private bool _primitive;

		public Class(Type type)
		{
			_type = type;
			_primitive = type.IsPrimitive;
			//getName();
			for (int i = 0; i < PRIMITIVE_TYPES.Length; i++)
			{
				if (type == PRIMITIVE_TYPES[i])
				{
					_primitive = true;
					break;
				}
			}
		}

		internal static BindingFlags declared()
		{
			return BindingFlags.DeclaredOnly | BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public;
		}

		public override bool Equals(object obj)
		{
			Class clazz = obj as Class;
			return clazz != null && clazz._type == _type;
		}

		public static Class forName(String name)
		{
			if (null == name)
			{
				return null;
			}

			lock (_typeNameToClassMap.SyncRoot)
			{
				Class returnValue = (Class) _typeNameToClassMap[name];
				if (null != returnValue)
				{
					return returnValue;
				}

				try
				{
					Type t = TypeReference.FromString(name).Resolve();
					returnValue = getClassForType(t);
					_typeNameToClassMap[name] = returnValue;
				}
				catch (Exception ex)
				{
					throw new ClassNotFoundException(name, ex);
				}
				return returnValue;
			}
		}

		public static Class getClassForObject(object obj)
		{
			return getClassForType(obj.GetType());
		}

		public static Class getClassForType(Type forType)
		{
			if (forType == null)
			{
				return null;
			}
			if (forType.IsPointer)
			{
				return null;
			}

            // TODO: need to find another place for
            // this condition
            /*
			if (forType.IsSubclassOf(typeof(Delegate)) 
				&& forType != typeof(EvaluationDelegate))
			{
				return null;
			}
            */
			
			lock (_typeToClassMap.SyncRoot)
			{
				Class clazz = (Class) _typeToClassMap[forType];
				if (clazz == null)
				{
					clazz = new Class(forType);
					_typeToClassMap[forType] = clazz;
				}
				return clazz;
			}
		}

		public Class getComponentType()
		{
			return getClassForType(_type.GetElementType());
		}

		public Constructor[] getDeclaredConstructors()
		{
			ConstructorInfo[] constructorInfos = _type.GetConstructors(declared());
			Constructor[] constructors = new Constructor[constructorInfos.Length];
			for (int i = 0; i < constructorInfos.Length; i++)
			{
				constructors[i] = new Constructor(constructorInfos[i]);
			}
			return constructors;
		}

		public Field getDeclaredField(String name)
		{
			return getField(_type.GetField(name, declared() | BindingFlags.Static));
		}

		public Field[] getDeclaredFields()
		{
			FieldInfo[] fieldInfos = _type.GetFields(declared() | BindingFlags.Static);
			Field[] fields = new Field[fieldInfos.Length];
			for (int i = 0; i < fieldInfos.Length; i++)
			{
				fields[i] = getField(fieldInfos[i]);
			}
			return fields;
		}

		public Method getDeclaredMethod(String name, Class[] parameterTypes)
		{
			return getMethod(_type.GetMethod(name, declared(), null, getTypes(parameterTypes), null));
		}

		public Method[] getDeclaredMethods()
		{
			MethodInfo[] methodInfos = _type.GetMethods(declared());
			Method[] methods = new Method[methodInfos.Length];
			for (int i = 0; i < methodInfos.Length; i++)
			{
				methods[i] = new Method(methodInfos[i]);
			}
			return methods;
		}

		private Field getField(FieldInfo fieldInfo)
		{
			if (fieldInfo == null)
			{
				return null;
			}
			return new Field(fieldInfo, _type.GetEvent(fieldInfo.Name, declared()));
		}

		public Field getField(String name)
		{
			return getField(_type.GetField(name));
		}

		public Method getMethod(String name, Class[] parameterTypes)
		{
			return getMethod(_type.GetMethod(name, getTypes(parameterTypes)));
		}

		public Method[] getMethods()
		{
			MethodInfo[] methods = _type.GetMethods();
			Method[] result = new Method[methods.Length];
			for (int i = 0; i < methods.Length; ++i)
			{
				result[i] = getMethod(methods[i]);
			}
			return result;
		}

		private Method getMethod(MethodInfo methodInfo)
		{
			if (methodInfo == null)
			{
				return null;
			}
			return new Method(methodInfo);
		}

		public int getModifiers()
		{
			int modifiers = 0;
			if (_type.IsAbstract)
			{
				modifiers |= Modifier.ABSTRACT;
			}
			if (_type.IsPublic || _type.IsNestedPublic)
			{
				modifiers |= Modifier.PUBLIC;
			}
			if (_type.IsNestedPrivate)
			{
				modifiers |= Modifier.PRIVATE;
			}
			if (_type.IsInterface)
			{
				modifiers |= Modifier.INTERFACE;
			}
			return modifiers;
		}

		public String getName()
		{
			if (_name == null)
			{
				_name = TypeReference.FromType(_type).GetUnversionedName();
			}
			return _name;
		}

		public Type getNetType()
		{
			return _type;
		}

		public Class getSuperclass()
		{
			return getClassForType(_type.BaseType);
		}

		public static Type[] getTypes(Class[] classes)
		{
			if (classes == null)
			{
				return new Type[] {};
			}
			Type[] types = new Type[classes.Length];
			for (int i = 0; i < types.Length; i++)
			{
				types[i] = classes[i].getNetType();
			}
			return types;
		}

		public bool isArray()
		{
			return _type.IsArray;
		}

		public bool isAssignableFrom(Class clazz)
		{
			return _type.IsAssignableFrom(clazz._type);
		}

		public bool isInstance(object obj)
		{
			if (obj == null)
			{
				return false;
			}
			if (_type.IsInterface)
			{
				return _type.IsAssignableFrom(obj.GetType());
			}
			return obj.GetType() == _type;
		}

		public bool isInterface()
		{
			return _type.IsInterface;
		}

		public bool isPrimitive()
		{
			return _primitive;
		}

		public Object newInstance()
		{
			return Activator.CreateInstance(_type);
		}
	}
}

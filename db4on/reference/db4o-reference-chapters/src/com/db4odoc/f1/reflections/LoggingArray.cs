/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.reflect;
using com.db4o.reflect.net;

namespace com.db4odoc.f1.reflections
{
	public class LoggingArray : com.db4o.reflect.ReflectArray
	{
		private readonly com.db4o.reflect.Reflector _reflector;

		internal LoggingArray(com.db4o.reflect.Reflector reflector)
		{
			_reflector = reflector;
		}

		public virtual int[] Dimensions(object arr)
		{
			return new int[] { GetLength(arr) };
		}

		public virtual int Flatten(object a_shaped, int[] a_dimensions, int a_currentDimension
			, object[] a_flat, int a_flatElement)
		{
			object[] shaped = (object[])a_shaped;
			System.Array.Copy(shaped, 0, a_flat, 0, shaped.Length);
			return shaped.Length;
		}

		public virtual object Get(object onArray, int index)
		{
			return ((object[])onArray)[index];
		}

		public virtual com.db4o.reflect.ReflectClass GetComponentType(com.db4o.reflect.ReflectClass
			a_class)
		{
			while (a_class.IsArray()) 
			{
				a_class = a_class.GetComponentType();
			}
			return a_class;
		}

		public virtual int GetLength(object array)
		{
			return ((object[])array).Length;
		}

		public virtual bool IsNDimensional(com.db4o.reflect.ReflectClass a_class)
		{
			return false;
		}

		private static Type GetNetType(ReflectClass a_class)
		{
			return ((NetClass)a_class).GetNetType();
		}

		public virtual object NewInstance(com.db4o.reflect.ReflectClass componentType, int
			length)
		{
			return System.Array.CreateInstance(GetNetType(componentType), length);
		}

		public virtual object NewInstance(com.db4o.reflect.ReflectClass componentType, int[]
			dimensions)
		{
			return NewInstance(componentType, dimensions[0]);
		}

		public virtual void Set(object onArray, int index, object element)
		{
			((object[])onArray)[index] = element;
			return;
		}

		public virtual int Shape(object[] a_flat, int a_flatElement, object a_shaped, int[]
			a_dimensions, int a_currentDimension)
		{
			object[] shaped = (object[])a_shaped;
			System.Array.Copy(a_flat, 0, shaped, 0, a_flat.Length);
			return a_flat.Length;
		}
	}
}


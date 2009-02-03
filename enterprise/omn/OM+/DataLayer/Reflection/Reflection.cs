/* Copyright (C) 2004 - 2009  db4objects Inc.  http://www.db4o.com */

using System;
using System.Collections.Generic;
using Db4objects.Db4o.Reflect;
using Sharpen.Lang;

namespace OManager.DataLayer.Reflection
{
	public interface IType
	{
		object Cast(object value);

		string DisplayName { get; }
		string FullName { get; }
		bool HasIdentity { get; }
		bool IsEditable { get; }
		bool IsPrimitive { get; }
		bool IsCollection { get; }
		bool IsArray { get; }
		bool IsNullable { get; }
	}

	public class TypeResolver
	{
		private readonly IReflector _reflector;
		private readonly IDictionary<string, IType> _resolved = new Dictionary<string, IType>();

		public TypeResolver(IReflector reflector)
		{
			_reflector = reflector;
		}

		public IType Resolve(string typeFQN)
		{
			return Resolve(_reflector.ForName(typeFQN));
		}

		public IType Resolve(IReflectClass klass)
		{
			string className = klass.GetName();
			if (!_resolved.ContainsKey(className))
			{
				_resolved[className] = new Type(klass);
			}

			return _resolved[className];
		}
	}

	public class Type : IType
	{
		private readonly IReflectClass _type;

		public Type(IReflectClass type)
		{
			_type = type;
		}

		public object Cast(object value)
		{
			System.Type type = System.Type.GetType(FullName);
			if (null == type)
				return null;

			if (IsNullable)
			{
				type = type.GetGenericArguments()[0];
			}
			
			return Convert.ChangeType(value, type);
		}

		public string DisplayName
		{
			get
			{
				return NormalizeName(FullName);
			}
		}

		public string FullName
		{
			get
			{
				return _type.GetName();
			}
		}

		public bool HasIdentity
		{
			get
			{
				System.Type type = System.Type.GetType(_type.GetName());
				return type == null || (!type.IsValueType && !IsString());
			}
		}

		public bool IsEditable
		{
			get
			{
				return IsPrimitive || IsNullable;
			}
		}

		public bool IsPrimitive
		{
			get { return _type.IsPrimitive() || IsString(); }
		}

		public bool IsCollection
		{
			get { return _type.IsCollection(); }
		}

		public bool IsArray
		{
			get { return  _type.IsArray(); }
		}

		public bool IsNullable
		{
			get
			{
				string name = typeof(Nullable<>).FullName;
				return (_type.GetName().StartsWith(name) && !_type.IsArray());
			}
		}

		public override string ToString()
		{
			return "Type( " + _type + " )";
		}

		private bool IsString()
		{
			return _type.GetName().StartsWith(typeof(string).FullName);
		}
		
		private string NormalizeName(string typeName)
		{
			return RemoveAssemblyName(NormalizeNullable(typeName));
		}

		private string NormalizeNullable(string typeName)
		{
			return IsNullable 
				? DecorateNullableName(typeName)
				: typeName;
		}

		private static string DecorateNullableName(string typeName)
		{
			GenericTypeReference typeRef = (GenericTypeReference)TypeReference.FromString(typeName);
			TypeReference wrappedType = typeRef.GenericArguments[0];

			return "Nullable<" + wrappedType.SimpleName + ">";
		}

		private static string RemoveAssemblyName(string typeName)
		{
			int index = typeName.IndexOf(',');
			return index >= 0 ? typeName.Remove(index) : typeName;
		}
	}
}

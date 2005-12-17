/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */
using System;
using System.Reflection;
using System.Text;

namespace j4o.lang
{	
	public abstract class TypeReference
	{
		public static TypeReference FromString(string s)
		{
			if (null == s) throw new ArgumentNullException("s");
			return new TypeReferenceParser(s).Parse();
		}
		
		public static TypeReference FromType(System.Type type)
		{
			if (null == type) throw new ArgumentNullException("type");
#if CF_1_0
			StringBuilder builder = new StringBuilder();
			builder.Append(type.FullName);
			builder.Append(", ");
			builder.Append(type.Assembly.FullName);
			return FromString(builder.ToString());
#else
			return FromString(type.AssemblyQualifiedName);
#endif
		}

		public abstract string SimpleName
		{
			get;
		}

		public abstract AssemblyName AssemblyName
		{
			get;
		}

		public abstract System.Type Resolve();

		public abstract void AppendTypeName(StringBuilder builder);

		public override string ToString()
		{
			return GetUnversionedName();
		}

		public string GetUnversionedName()
		{
			StringBuilder builder = new StringBuilder();
			AppendUnversionedName(builder);
			return builder.ToString();
		}

		internal virtual void AppendUnversionedName(StringBuilder builder)
		{
			AppendTypeName(builder);
			AppendUnversionedAssemblyName(builder);
		}

		protected void AppendUnversionedAssemblyName(StringBuilder builder)
		{
			AssemblyName assemblyName = this.AssemblyName;
			if (null == assemblyName) return;

			builder.Append(", ");
			builder.Append(assemblyName.Name);
		}
	}

	public class SimpleTypeReference : TypeReference
	{
		protected string _simpleName;

		protected AssemblyName _assemblyName;

		internal SimpleTypeReference(string simpleName)
		{
			_simpleName = simpleName;
		}

		public override string SimpleName
		{
			get { return _simpleName; }
		}
		
		public override AssemblyName AssemblyName
		{
			get { return _assemblyName; }
		}

		public override System.Type Resolve()
		{
            return _assemblyName == null
                ? Type.GetType(SimpleName, true)
                : ResolveAssembly().GetType(SimpleName);
		}

		public override void AppendTypeName(StringBuilder builder)
		{
			builder.Append(SimpleName);
		}

		public override bool Equals(object obj)
		{
			SimpleTypeReference other = obj as SimpleTypeReference;
			if (null == other) return false;
			return _simpleName == other._simpleName;
		}

		internal void SetSimpleName(string simpleName)
		{
			_simpleName = simpleName;
		}

		internal void SetAssemblyName(AssemblyName assemblyName)
		{
			_assemblyName = assemblyName;
		}

		private System.Reflection.Assembly ResolveAssembly()
		{
			Assembly found = null;
			try
			{
				found = Assembly.Load(_assemblyName);
			}
			catch (Exception)
			{
				AssemblyName unversioned = (AssemblyName)_assemblyName.Clone();
				unversioned.Version = null;
#if CF_1_0
                found = Assembly.Load(unversioned);
#else
				try
				{
					found = Assembly.Load(unversioned);
				}
				catch (Exception)
				{
					found = Assembly.LoadWithPartialName(unversioned.FullName);
					if (null == found)
					{
						throw;
					}
				}
#endif
			}
			return found;
		}
	}

	public abstract class QualifiedTypeReference : TypeReference
	{
		protected TypeReference _elementType;

		protected QualifiedTypeReference(TypeReference elementType)
		{
			_elementType = elementType;
		}

		public override string SimpleName
		{
			get
			{
				return _elementType.SimpleName;
			}
		}

		public override AssemblyName AssemblyName
		{
			get { return _elementType.AssemblyName; }
		}

		public override void AppendTypeName(StringBuilder builder)
		{
			_elementType.AppendTypeName(builder);
			AppendQualifier(builder);
		}

		protected abstract void AppendQualifier(StringBuilder builder);
	}

	public class PointerTypeReference : QualifiedTypeReference
	{
		public PointerTypeReference(TypeReference elementType)
			: base(elementType)
		{
		}

		protected override void AppendQualifier(StringBuilder builder)
		{
			builder.Append('*');
		}

		public override Type Resolve()
		{
			return _elementType.Resolve().MakePointerType();
		}
	}

	public class ArrayTypeReference : QualifiedTypeReference
	{
		private int _rank;

		internal ArrayTypeReference(TypeReference elementType, int rank)
			: base(elementType)
		{
			_rank = rank;
		}

		public override Type Resolve()
		{
			return Array.CreateInstance(_elementType.Resolve(), new int[_rank]).GetType();
		}

		protected override void AppendQualifier(StringBuilder builder)
		{
			builder.Append('[');
			for (int i = 1; i < _rank; ++i)
			{
				builder.Append(',');
			}
			builder.Append(']');
		}
	}

	public class GenericTypeReference : SimpleTypeReference
	{
		TypeReference[] _genericArguments;

		internal GenericTypeReference(string simpleName, TypeReference[] genericArguments)
			: base(simpleName)
		{
			_genericArguments = genericArguments;
		}

		public TypeReference[] GenericArguments
		{
			get { return _genericArguments; }
		}

		public override Type Resolve()
		{
			Type baseType = base.Resolve();
			return _genericArguments.Length > 0
				? baseType.MakeGenericType(Resolve(_genericArguments))
				: baseType;
		}

		Type[] Resolve(TypeReference[] typeRefs)
		{
			Type[] types = new Type[typeRefs.Length];
			for (int i = 0; i < types.Length; ++i)
			{
				types[i] = typeRefs[i].Resolve();
			}
			return types;
		}

		public override void AppendTypeName(StringBuilder builder)
		{
			builder.Append(_simpleName);
			AppendUnversionedGenericArguments(builder);
		}

		private void AppendUnversionedGenericArguments(StringBuilder builder)
		{
			if (_genericArguments.Length == 0) return;

			builder.Append("[");
			for (int i = 0; i < _genericArguments.Length; ++i)
			{
				if (i > 0) builder.Append(", ");
				builder.Append("[");
				_genericArguments[i].AppendUnversionedName(builder);
				builder.Append("]");
			}
			builder.Append("]");
		}
	}
}






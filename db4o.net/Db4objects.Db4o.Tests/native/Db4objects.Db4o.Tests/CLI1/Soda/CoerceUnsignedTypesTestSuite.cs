using System;
using System.Reflection;
using Db4oUnit.Extensions;
using Db4oUnit.Extensions.Fixtures;
using Db4oUnit.Fixtures;

namespace Db4objects.Db4o.Tests.CLI1.Soda
{
	public class CoerceUnsignedTypesTestSuite : FixtureBasedTestSuite, IDb4oTestCase
	{
		public override Type[] TestUnits()
		{
			return new[] { typeof (CoerceUnsignedTypesTestUnit) };
		}

		public override IFixtureProvider[] FixtureProviders()
		{
			return new[]
			       	{
			       		new Db4oFixtureProvider(),
						TestVariables.FieldTypeFixtureProvider,
			       	};
		}
	}

	class TestVariables
	{
		public static readonly FixtureVariable Types = new FixtureVariable("Types");
		
		public static readonly IFixtureProvider FieldTypeFixtureProvider = new SimpleFixtureProvider(
				Types,
				new object[]
				{
					new TypeSpec(typeof(uint), uint.MinValue, uint.MaxValue),
					//new TypeSpec(typeof(ulong), ulong.MinValue, ulong.MaxValue),
					//new TypeSpec(typeof(ushort), ushort.MinValue, ushort.MaxValue)
				});

		public static TypeSpec Current
		{
			get
			{
				return (TypeSpec) Types.Value;
			}
		}
	}

	internal class TypeSpec
	{
		public readonly Type Type;
		public readonly object MinValue;
		public readonly object MaxValue;
		public readonly object InvalidValue = -42;

		public TypeSpec(Type type, object minValue, object maxValue)
		{
			Type = type;
			MinValue = minValue;
			MaxValue = maxValue;
		}

		public string TypeName
		{
			get { return Type.Name; }
		}

		public object NewMinValue()
		{
			return NewInstance(MinValue);
		}

		public object NewMaxValue()
		{
			return NewInstance(MaxValue);
		}
		
		private object NewInstance(object value)
		{
			var genericItem = typeof(UnsignedItem<>).MakeGenericType(Type);
			var ctor = genericItem.GetConstructor(BindingFlags.NonPublic | BindingFlags.Instance, null, new[] { Type } , null);
			return ctor.Invoke(new[]{ value });
		}

		public override string ToString()
		{
			return "UnsignedItem<" + Type.Name + ">[" + MinValue + ", "  + MaxValue + "]";
		}
	}

	internal class UnsignedItem<T> where T : struct, IComparable<T>
	{
		private readonly T _value;

		internal UnsignedItem(T value)
		{
			_value = value;
		}

		public T Value
		{
			get { return _value; }
		}

		public static bool operator==(UnsignedItem<T> left, UnsignedItem<T> right)
		{
			return left.Value.CompareTo(right.Value) == 0;
		}

		public static bool operator !=(UnsignedItem<T> left, UnsignedItem<T> right)
		{
			return !(left == right);
		}

		public override bool Equals(object obj)
		{
			if (obj == null) return false;

			if (obj.GetType() != typeof(UnsignedItem<T>)) return false;

			var other = (UnsignedItem<T>) obj;

			return other.Value.CompareTo(_value) == 0;
		}
	}
}

namespace com.db4o.foundation
{
	/// <summary>yes/no/dontknow data type</summary>
	/// <exclude></exclude>
	[System.Serializable]
	public sealed class TernaryBool
	{
		private const int NO_ID = -1;

		private const int YES_ID = 1;

		private const int UNSPECIFIED_ID = 0;

		public static readonly com.db4o.foundation.TernaryBool NO = new com.db4o.foundation.TernaryBool
			(NO_ID);

		public static readonly com.db4o.foundation.TernaryBool YES = new com.db4o.foundation.TernaryBool
			(YES_ID);

		public static readonly com.db4o.foundation.TernaryBool UNSPECIFIED = new com.db4o.foundation.TernaryBool
			(UNSPECIFIED_ID);

		private readonly int _value;

		private TernaryBool(int value)
		{
			_value = value;
		}

		public bool BooleanValue(bool defaultValue)
		{
			switch (_value)
			{
				case NO_ID:
				{
					return false;
				}

				case YES_ID:
				{
					return true;
				}

				default:
				{
					return defaultValue;
					break;
				}
			}
		}

		public bool Unspecified()
		{
			return this == UNSPECIFIED;
		}

		public bool DefiniteYes()
		{
			return this == YES;
		}

		public bool DefiniteNo()
		{
			return this == NO;
		}

		public static com.db4o.foundation.TernaryBool ForBoolean(bool value)
		{
			return (value ? YES : NO);
		}

		public override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null || j4o.lang.JavaSystem.GetClassForObject(this) != j4o.lang.JavaSystem.GetClassForObject
				(obj))
			{
				return false;
			}
			com.db4o.foundation.TernaryBool tb = (com.db4o.foundation.TernaryBool)obj;
			return _value == tb._value;
		}

		public override int GetHashCode()
		{
			return _value;
		}

		private object ReadResolve()
		{
			switch (_value)
			{
				case NO_ID:
				{
					return NO;
				}

				case YES_ID:
				{
					return YES;
				}

				default:
				{
					return UNSPECIFIED;
					break;
				}
			}
		}
	}
}

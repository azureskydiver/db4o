namespace com.db4o.config
{
	/// <summary>
	/// a simple Alias for a single Class or Type, using #equals() on
	/// the names in the resolve method.
	/// </summary>
	/// <remarks>
	/// a simple Alias for a single Class or Type, using #equals() on
	/// the names in the resolve method.
	/// <br /><br />See
	/// <see cref="com.db4o.config.Alias">com.db4o.config.Alias</see>
	/// for concrete examples.
	/// </remarks>
	public class TypeAlias : com.db4o.config.Alias
	{
		private readonly string _storedType;

		private readonly string _runtimeType;

		public TypeAlias(string storedType, string runtimeType)
		{
			if (null == storedType || null == runtimeType)
			{
				throw new System.ArgumentException();
			}
			_storedType = storedType;
			_runtimeType = runtimeType;
		}

		/// <summary>returns the stored type name if the alias was written for the passed runtime type name
		/// 	</summary>
		public virtual string ResolveRuntimeName(string runtimeTypeName)
		{
			return _runtimeType.Equals(runtimeTypeName) ? _storedType : null;
		}

		/// <summary>returns the runtime type name if the alias was written for the passed stored type name
		/// 	</summary>
		public virtual string ResolveStoredName(string storedTypeName)
		{
			return _storedType.Equals(storedTypeName) ? _runtimeType : null;
		}
	}
}

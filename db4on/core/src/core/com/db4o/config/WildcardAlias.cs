namespace com.db4o.config
{
	/// <summary>
	/// Wildcard Alias functionality to create aliases for packages,
	/// namespaces or multiple similar named classes.
	/// </summary>
	/// <remarks>
	/// Wildcard Alias functionality to create aliases for packages,
	/// namespaces or multiple similar named classes. One single '*'
	/// wildcard character is supported in the names.
	/// <br /><br />See
	/// <see cref="com.db4o.config.Alias">com.db4o.config.Alias</see>
	/// for concrete examples.
	/// </remarks>
	public class WildcardAlias : com.db4o.config.Alias
	{
		private readonly com.db4o.config.WildcardAlias.WildcardPattern _storedPattern;

		private readonly com.db4o.config.WildcardAlias.WildcardPattern _runtimePattern;

		public WildcardAlias(string storedPattern, string runtimePattern)
		{
			if (null == storedPattern)
			{
				throw new System.ArgumentNullException("storedPattern");
			}
			if (null == runtimePattern)
			{
				throw new System.ArgumentNullException("runtimePattern");
			}
			_storedPattern = new com.db4o.config.WildcardAlias.WildcardPattern(storedPattern);
			_runtimePattern = new com.db4o.config.WildcardAlias.WildcardPattern(runtimePattern
				);
		}

		/// <summary>resolving is done through simple pattern matching</summary>
		public virtual string ResolveRuntimeName(string runtimeTypeName)
		{
			string match = _runtimePattern.Matches(runtimeTypeName);
			return match != null ? _storedPattern.Inject(match) : null;
		}

		/// <summary>resolving is done through simple pattern matching</summary>
		public virtual string ResolveStoredName(string storedTypeName)
		{
			string match = _storedPattern.Matches(storedTypeName);
			return match != null ? _runtimePattern.Inject(match) : null;
		}

		internal class WildcardPattern
		{
			private string _head;

			private string _tail;

			public WildcardPattern(string pattern)
			{
				string[] parts = Split(pattern);
				_head = parts[0];
				_tail = parts[1];
			}

			public virtual string Inject(string s)
			{
				return _head + s + _tail;
			}

			public virtual string Matches(string s)
			{
				if (!s.StartsWith(_head) || !s.EndsWith(_tail))
				{
					return null;
				}
				return j4o.lang.JavaSystem.Substring(s, _head.Length, s.Length - _tail.Length);
			}

			private void InvalidPattern()
			{
				throw new System.ArgumentException("only one '*' character");
			}

			internal virtual string[] Split(string pattern)
			{
				int index = pattern.IndexOf('*');
				if (-1 == index || index != pattern.LastIndexOf('*'))
				{
					InvalidPattern();
				}
				return new string[] { j4o.lang.JavaSystem.Substring(pattern, 0, index), j4o.lang.JavaSystem.Substring
					(pattern, index + 1) };
			}
		}
	}
}

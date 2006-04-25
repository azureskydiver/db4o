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
			if (null == storedPattern || null == runtimePattern)
			{
				throw new System.ArgumentException();
			}
			_storedPattern = new com.db4o.config.WildcardAlias.WildcardPattern(storedPattern);
			_runtimePattern = new com.db4o.config.WildcardAlias.WildcardPattern(runtimePattern
				);
		}

		/// <summary>resolving is done through simple pattern matching</summary>
		public virtual string resolve(string runtimeType)
		{
			string match = _runtimePattern.matches(runtimeType);
			return match != null ? _storedPattern.inject(match) : null;
		}

		internal class WildcardPattern
		{
			private string _head;

			private string _tail;

			public WildcardPattern(string pattern)
			{
				string[] parts = split(pattern);
				_head = parts[0];
				_tail = parts[1];
			}

			public virtual string inject(string s)
			{
				return _head + s + _tail;
			}

			public virtual string matches(string s)
			{
				if (!s.StartsWith(_head) || !s.EndsWith(_tail))
				{
					return null;
				}
				return j4o.lang.JavaSystem.substring(s, j4o.lang.JavaSystem.getLengthOf(_head), j4o.lang.JavaSystem.getLengthOf
					(s) - j4o.lang.JavaSystem.getLengthOf(_tail));
			}

			private void invalidPattern()
			{
				throw new System.ArgumentException("only one '*' character");
			}

			internal virtual string[] split(string pattern)
			{
				int index = pattern.IndexOf('*');
				if (-1 == index || index != pattern.LastIndexOf('*'))
				{
					invalidPattern();
				}
				return new string[] { j4o.lang.JavaSystem.substring(pattern, 0, index), j4o.lang.JavaSystem.substring
					(pattern, index + 1) };
			}
		}
	}
}

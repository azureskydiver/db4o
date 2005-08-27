namespace com.db4o
{
	/// <summary>
	/// Class metadata to be stored to the database file
	/// Don't obfuscate.
	/// </summary>
	/// <remarks>
	/// Class metadata to be stored to the database file
	/// Don't obfuscate.
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class MetaClass : com.db4o.Internal4
	{
		public string name;

		public com.db4o.MetaField[] fields;

		public MetaClass()
		{
		}

		public MetaClass(string name)
		{
			this.name = name;
		}

		internal virtual com.db4o.MetaField ensureField(com.db4o.Transaction trans, string
			 a_name)
		{
			if (fields != null)
			{
				for (int i = 0; i < fields.Length; i++)
				{
					if (fields[i].name.Equals(a_name))
					{
						return fields[i];
					}
				}
				com.db4o.MetaField[] temp = new com.db4o.MetaField[fields.Length + 1];
				j4o.lang.JavaSystem.arraycopy(fields, 0, temp, 0, fields.Length);
				fields = temp;
			}
			else
			{
				fields = new com.db4o.MetaField[1];
			}
			com.db4o.MetaField newMetaField = new com.db4o.MetaField(a_name);
			fields[fields.Length - 1] = newMetaField;
			trans.i_stream.setInternal(trans, newMetaField, com.db4o.YapConst.UNSPECIFIED, false
				);
			trans.i_stream.setInternal(trans, this, com.db4o.YapConst.UNSPECIFIED, false);
			return newMetaField;
		}
	}
}

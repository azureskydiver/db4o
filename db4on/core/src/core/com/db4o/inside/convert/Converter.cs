namespace com.db4o.inside.convert
{
	/// <exclude></exclude>
	public class Converter
	{
		public static readonly int VERSION = com.db4o.inside.marshall.MarshallerFamily.LEGACY
			 ? 0 : 5;

		private com.db4o.foundation.Hashtable4 _conversions;

		public static bool Convert(com.db4o.YapFile file, com.db4o.header.FileHeader0 fileHeader
			)
		{
			if (fileHeader.ConverterVersion() >= VERSION)
			{
				return false;
			}
			com.db4o.inside.convert.Converter converter = new com.db4o.inside.convert.Converter
				();
			converter.Run(file, fileHeader);
			return true;
		}

		private Converter()
		{
			_conversions = new com.db4o.foundation.Hashtable4(1);
			new com.db4o.inside.convert.conversions.CommonConversions(this);
		}

		public virtual void Register(int idx, com.db4o.inside.convert.Conversion conversion
			)
		{
			if (_conversions.Get(idx) != null)
			{
				com.db4o.inside.Exceptions4.ShouldNeverHappen();
			}
			_conversions.Put(idx, conversion);
		}

		private void Run(com.db4o.YapFile file, com.db4o.header.FileHeader0 fileHeader)
		{
			int start = fileHeader.ConverterVersion();
			for (int i = start; i <= VERSION; i++)
			{
				com.db4o.inside.convert.Conversion conversion = (com.db4o.inside.convert.Conversion
					)_conversions.Get(i);
				if (conversion != null)
				{
					conversion.SetFile(file);
					conversion.Run();
				}
			}
			fileHeader.ConverterVersion(VERSION);
			fileHeader.WriteVariablePart1();
		}
	}
}

namespace com.db4o.inside.convert
{
	/// <exclude></exclude>
	public abstract class ConversionStage
	{
		public sealed class ClassCollectionAvailableStage : com.db4o.inside.convert.ConversionStage
		{
			public ClassCollectionAvailableStage(com.db4o.YapFile file, com.db4o.header.FileHeader0
				 header) : base(file, header)
			{
			}

			public override void Accept(com.db4o.inside.convert.Conversion conversion)
			{
				conversion.Convert(this);
			}
		}

		public sealed class SystemUpStage : com.db4o.inside.convert.ConversionStage
		{
			public SystemUpStage(com.db4o.YapFile file, com.db4o.header.FileHeader0 header) : 
				base(file, header)
			{
			}

			public override void Accept(com.db4o.inside.convert.Conversion conversion)
			{
				conversion.Convert(this);
			}
		}

		private com.db4o.YapFile _file;

		private com.db4o.header.FileHeader0 _header;

		protected ConversionStage(com.db4o.YapFile file, com.db4o.header.FileHeader0 header
			)
		{
			_file = file;
			_header = header;
		}

		public virtual com.db4o.YapFile File()
		{
			return _file;
		}

		public virtual com.db4o.header.FileHeader0 Header()
		{
			return _header;
		}

		public abstract void Accept(com.db4o.inside.convert.Conversion conversion);
	}
}

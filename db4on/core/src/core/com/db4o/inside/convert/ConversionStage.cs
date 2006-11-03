namespace com.db4o.inside.convert
{
	/// <exclude></exclude>
	public abstract class ConversionStage
	{
		public sealed class ClassCollectionAvailableStage : com.db4o.inside.convert.ConversionStage
		{
			public ClassCollectionAvailableStage(com.db4o.YapFile file) : base(file)
			{
			}

			public override void Accept(com.db4o.inside.convert.Conversion conversion)
			{
				conversion.Convert(this);
			}
		}

		public sealed class SystemUpStage : com.db4o.inside.convert.ConversionStage
		{
			public SystemUpStage(com.db4o.YapFile file) : base(file)
			{
			}

			public override void Accept(com.db4o.inside.convert.Conversion conversion)
			{
				conversion.Convert(this);
			}
		}

		private com.db4o.YapFile _file;

		protected ConversionStage(com.db4o.YapFile file)
		{
			_file = file;
		}

		public virtual com.db4o.YapFile File()
		{
			return _file;
		}

		public virtual com.db4o.inside.SystemData SystemData()
		{
			return _file.SystemData();
		}

		public abstract void Accept(com.db4o.inside.convert.Conversion conversion);
	}
}

namespace com.db4o.@internal.convert
{
	/// <exclude></exclude>
	public abstract class ConversionStage
	{
		public sealed class ClassCollectionAvailableStage : com.db4o.@internal.convert.ConversionStage
		{
			public ClassCollectionAvailableStage(com.db4o.@internal.LocalObjectContainer file
				) : base(file)
			{
			}

			public override void Accept(com.db4o.@internal.convert.Conversion conversion)
			{
				conversion.Convert(this);
			}
		}

		public sealed class SystemUpStage : com.db4o.@internal.convert.ConversionStage
		{
			public SystemUpStage(com.db4o.@internal.LocalObjectContainer file) : base(file)
			{
			}

			public override void Accept(com.db4o.@internal.convert.Conversion conversion)
			{
				conversion.Convert(this);
			}
		}

		private com.db4o.@internal.LocalObjectContainer _file;

		protected ConversionStage(com.db4o.@internal.LocalObjectContainer file)
		{
			_file = file;
		}

		public virtual com.db4o.@internal.LocalObjectContainer File()
		{
			return _file;
		}

		public virtual com.db4o.@internal.SystemData SystemData()
		{
			return _file.SystemData();
		}

		public abstract void Accept(com.db4o.@internal.convert.Conversion conversion);
	}
}

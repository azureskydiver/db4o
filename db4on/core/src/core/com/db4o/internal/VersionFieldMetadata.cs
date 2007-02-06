namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class VersionFieldMetadata : com.db4o.@internal.VirtualFieldMetadata
	{
		internal VersionFieldMetadata(com.db4o.@internal.ObjectContainerBase stream) : base
			()
		{
			i_name = com.db4o.ext.VirtualField.VERSION;
			i_handler = new com.db4o.@internal.handlers.LongHandler(stream);
		}

		public override void AddFieldIndex(com.db4o.@internal.marshall.MarshallerFamily mf
			, com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.StatefulBuffer writer
			, com.db4o.@internal.slots.Slot oldSlot)
		{
			writer.WriteLong(writer.GetStream().GenerateTimeStampId());
		}

		public override void Delete(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes, bool isUpdate)
		{
			a_bytes.IncrementOffset(LinkLength());
		}

		internal override void Instantiate1(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.ObjectReference
			 a_yapObject, com.db4o.@internal.Buffer a_bytes)
		{
			a_yapObject.VirtualAttributes().i_version = a_bytes.ReadLong();
		}

		internal override void Marshall1(com.db4o.@internal.ObjectReference a_yapObject, 
			com.db4o.@internal.StatefulBuffer a_bytes, bool a_migrating, bool a_new)
		{
			com.db4o.@internal.ObjectContainerBase stream = a_bytes.GetStream().i_parent;
			com.db4o.@internal.VirtualAttributes va = a_yapObject.VirtualAttributes();
			if (!a_migrating)
			{
				va.i_version = stream.GenerateTimeStampId();
			}
			if (va == null)
			{
				a_bytes.WriteLong(0);
			}
			else
			{
				a_bytes.WriteLong(va.i_version);
			}
		}

		public override int LinkLength()
		{
			return com.db4o.@internal.Const4.LONG_LENGTH;
		}

		internal override void MarshallIgnore(com.db4o.@internal.Buffer writer)
		{
			writer.WriteLong(0);
		}
	}
}

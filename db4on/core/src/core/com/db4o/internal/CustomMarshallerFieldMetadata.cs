namespace com.db4o.@internal
{
	/// <exclude></exclude>
	internal sealed class CustomMarshallerFieldMetadata : com.db4o.@internal.FieldMetadata
	{
		private readonly com.db4o.config.ObjectMarshaller _marshaller;

		public CustomMarshallerFieldMetadata(com.db4o.@internal.ClassMetadata containingClass
			, com.db4o.config.ObjectMarshaller marshaller) : base(containingClass, marshaller
			)
		{
			_marshaller = marshaller;
		}

		public override void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, object obj)
		{
			header.AddBaseLength(LinkLength());
		}

		public override void DefragField(com.db4o.@internal.marshall.MarshallerFamily mf, 
			com.db4o.@internal.ReaderPair readers)
		{
			readers.IncrementOffset(LinkLength());
		}

		public override void Delete(com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes, bool isUpdate)
		{
			IncrementOffset(a_bytes);
		}

		public override bool HasIndex()
		{
			return false;
		}

		public override object GetOrCreate(com.db4o.@internal.Transaction trans, object onObject
			)
		{
			return onObject;
		}

		public override void Set(object onObject, object obj)
		{
		}

		public override void Instantiate(com.db4o.@internal.marshall.MarshallerFamily mf, 
			com.db4o.@internal.ObjectReference @ref, object onObject, com.db4o.@internal.StatefulBuffer
			 reader)
		{
			_marshaller.ReadFields(onObject, reader._buffer, reader._offset);
			IncrementOffset(reader);
		}

		public override void Marshall(com.db4o.@internal.ObjectReference yo, object obj, 
			com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 writer, com.db4o.@internal.Config4Class config, bool isNew)
		{
			_marshaller.WriteFields(obj, writer._buffer, writer._offset);
			IncrementOffset(writer);
		}

		public override int LinkLength()
		{
			return _marshaller.MarshalledFieldLength();
		}
	}
}

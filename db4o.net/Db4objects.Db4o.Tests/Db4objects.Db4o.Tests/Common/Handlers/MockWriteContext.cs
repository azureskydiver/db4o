/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Marshall;
using Db4objects.Db4o.Typehandlers;

namespace Db4objects.Db4o.Tests.Common.Handlers
{
	public class MockWriteContext : Db4objects.Db4o.Tests.Common.Handlers.MockMarshallingContext
		, IWriteContext
	{
		public MockWriteContext(IObjectContainer objectContainer) : base(objectContainer)
		{
		}

		public virtual void WriteObject(ITypeHandler4 handler, object obj)
		{
			handler.Write(this, obj);
		}

		public virtual void WriteAny(object obj)
		{
			ClassMetadata classMetadata = Container().ClassMetadataForObject(obj);
			WriteInt(classMetadata.GetID());
			classMetadata.Write(this, obj);
		}

		public virtual IReservedBuffer Reserve(int length)
		{
			IReservedBuffer reservedBuffer = new _IReservedBuffer_28(this);
			Seek(Offset() + length);
			return reservedBuffer;
		}

		private sealed class _IReservedBuffer_28 : IReservedBuffer
		{
			public _IReservedBuffer_28(MockWriteContext _enclosing)
			{
				this._enclosing = _enclosing;
				this.reservedOffset = this._enclosing.Offset();
			}

			private readonly int reservedOffset;

			public void WriteBytes(byte[] bytes)
			{
				int currentOffset = this._enclosing.Offset();
				this._enclosing.Seek(this.reservedOffset);
				this._enclosing.WriteBytes(bytes);
				this._enclosing.Seek(currentOffset);
			}

			private readonly MockWriteContext _enclosing;
		}
	}
}

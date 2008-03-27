/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System.IO;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Internal.Marshall;
using Db4objects.Db4o.Internal.Query.Processor;
using Db4objects.Db4o.Internal.Slots;
using Db4objects.Db4o.Marshall;

namespace Db4objects.Db4o.Internal.Handlers
{
	/// <exclude></exclude>
	public class ArrayHandler0 : ArrayHandler2
	{
		/// <exception cref="Db4oIOException"></exception>
		public override void Delete(IDeleteContext context)
		{
			context.ReadSlot();
			context.DefragmentRecommended();
		}

		/// <exception cref="Db4oIOException"></exception>
		public override void ReadCandidates(int handlerVersion, ByteArrayBuffer reader, QCandidates
			 candidates)
		{
			Transaction transaction = candidates.Transaction();
			ByteArrayBuffer arrayBuffer = reader.ReadEmbeddedObject(transaction);
			int count = ElementCount(transaction, arrayBuffer);
			for (int i = 0; i < count; i++)
			{
				candidates.AddByIdentity(new QCandidate(candidates, null, arrayBuffer.ReadInt(), 
					true));
			}
		}

		public override object Read(IReadContext readContext)
		{
			IInternalReadContext context = (IInternalReadContext)readContext;
			ByteArrayBuffer buffer = (ByteArrayBuffer)context.ReadIndirectedBuffer();
			if (buffer == null)
			{
				return null;
			}
			// With the following line we ask the context to work with 
			// a different buffer. Should this logic ever be needed by
			// a user handler, it should be implemented by using a Queue
			// in the UnmarshallingContext.
			// The buffer has to be set back from the outside!  See below
			IReadWriteBuffer contextBuffer = context.Buffer(buffer);
			object array = base.Read(context);
			// The context buffer has to be set back.
			context.Buffer(contextBuffer);
			return array;
		}

		public static void Defragment(IDefragmentContext context, ArrayHandler handler)
		{
			int sourceAddress = context.SourceBuffer().ReadInt();
			int length = context.SourceBuffer().ReadInt();
			if (sourceAddress == 0 && length == 0)
			{
				context.TargetBuffer().WriteInt(0);
				context.TargetBuffer().WriteInt(0);
				return;
			}
			Slot slot = context.AllocateMappedTargetSlot(sourceAddress, length);
			ByteArrayBuffer sourceBuffer = null;
			try
			{
				sourceBuffer = context.SourceBufferByAddress(sourceAddress, length);
			}
			catch (IOException exc)
			{
				throw new Db4oIOException(exc);
			}
			DefragmentContextImpl payloadContext = new DefragmentContextImpl(sourceBuffer, (DefragmentContextImpl
				)context);
			handler.Defrag1(payloadContext);
			payloadContext.WriteToTarget(slot.Address());
			context.TargetBuffer().WriteInt(slot.Address());
			context.TargetBuffer().WriteInt(length);
		}

		public override void Defragment(IDefragmentContext context)
		{
			Defragment(context, this);
		}

		public override void Defrag2(IDefragmentContext context)
		{
			int elements = ReadElementsDefrag(context);
			for (int i = 0; i < elements; i++)
			{
				DelegateTypeHandler().Defragment(context);
			}
		}
	}
}

/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Marshall;
using Db4objects.Db4o.Internal.Slots;

namespace Db4objects.Db4o.Internal.Marshall
{
	/// <exclude></exclude>
	internal class ObjectMarshaller0 : ObjectMarshaller
	{
		public override void AddFieldIndices(ClassMetadata yc, ObjectHeaderAttributes attributes
			, StatefulBuffer writer, Slot oldSlot)
		{
			ObjectMarshaller.TraverseFieldCommand command = new _TraverseFieldCommand_16(this
				, yc, writer, oldSlot);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _TraverseFieldCommand_16 : ObjectMarshaller.TraverseFieldCommand
		{
			public _TraverseFieldCommand_16(ObjectMarshaller0 _enclosing, ClassMetadata yc, StatefulBuffer
				 writer, Slot oldSlot)
			{
				this._enclosing = _enclosing;
				this.yc = yc;
				this.writer = writer;
				this.oldSlot = oldSlot;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, ClassMetadata
				 containingClass)
			{
				field.AddFieldIndex(this._enclosing._family, yc, writer, oldSlot);
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly ClassMetadata yc;

			private readonly StatefulBuffer writer;

			private readonly Slot oldSlot;
		}

		public override TreeInt CollectFieldIDs(TreeInt tree, ClassMetadata yc, ObjectHeaderAttributes
			 attributes, StatefulBuffer writer, string name)
		{
			TreeInt[] ret = new TreeInt[] { tree };
			ObjectMarshaller.TraverseFieldCommand command = new _TraverseFieldCommand_26(this
				, name, ret, writer);
			TraverseFields(yc, writer, attributes, command);
			return ret[0];
		}

		private sealed class _TraverseFieldCommand_26 : ObjectMarshaller.TraverseFieldCommand
		{
			public _TraverseFieldCommand_26(ObjectMarshaller0 _enclosing, string name, TreeInt[]
				 ret, StatefulBuffer writer)
			{
				this._enclosing = _enclosing;
				this.name = name;
				this.ret = ret;
				this.writer = writer;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, ClassMetadata
				 containingClass)
			{
				if (name.Equals(field.GetName()))
				{
					ret[0] = field.CollectIDs(this._enclosing._family, ret[0], writer);
				}
				else
				{
					field.IncrementOffset(writer);
				}
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly string name;

			private readonly TreeInt[] ret;

			private readonly StatefulBuffer writer;
		}

		public override void DeleteMembers(ClassMetadata yc, ObjectHeaderAttributes attributes
			, StatefulBuffer writer, int type, bool isUpdate)
		{
			ObjectMarshaller.TraverseFieldCommand command = new _TraverseFieldCommand_40(this
				, writer, isUpdate);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _TraverseFieldCommand_40 : ObjectMarshaller.TraverseFieldCommand
		{
			public _TraverseFieldCommand_40(ObjectMarshaller0 _enclosing, StatefulBuffer writer
				, bool isUpdate)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.isUpdate = isUpdate;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, ClassMetadata
				 containingClass)
			{
				field.Delete(this._enclosing._family, writer, isUpdate);
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly StatefulBuffer writer;

			private readonly bool isUpdate;
		}

		public override bool FindOffset(ClassMetadata yc, ObjectHeaderAttributes attributes
			, Db4objects.Db4o.Internal.Buffer writer, FieldMetadata field)
		{
			bool[] ret = new bool[] { false };
			ObjectMarshaller.TraverseFieldCommand command = new _TraverseFieldCommand_50(this
				, field, ret, writer);
			TraverseFields(yc, writer, attributes, command);
			return ret[0];
		}

		private sealed class _TraverseFieldCommand_50 : ObjectMarshaller.TraverseFieldCommand
		{
			public _TraverseFieldCommand_50(ObjectMarshaller0 _enclosing, FieldMetadata field
				, bool[] ret, Db4objects.Db4o.Internal.Buffer writer)
			{
				this._enclosing = _enclosing;
				this.field = field;
				this.ret = ret;
				this.writer = writer;
			}

			public override void ProcessField(FieldMetadata curField, bool isNull, ClassMetadata
				 containingClass)
			{
				if (curField == field)
				{
					ret[0] = true;
					this.Cancel();
					return;
				}
				writer.IncrementOffset(curField.LinkLength());
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly FieldMetadata field;

			private readonly bool[] ret;

			private readonly Db4objects.Db4o.Internal.Buffer writer;
		}

		protected int HeaderLength()
		{
			return Const4.OBJECT_LENGTH + Const4.ID_LENGTH;
		}

		public override void InstantiateFields(ClassMetadata yc, ObjectHeaderAttributes attributes
			, ObjectReference @ref, object onObject, StatefulBuffer writer)
		{
			ObjectMarshaller.TraverseFieldCommand command = new _TraverseFieldCommand_69(this
				, @ref, onObject, writer);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _TraverseFieldCommand_69 : ObjectMarshaller.TraverseFieldCommand
		{
			public _TraverseFieldCommand_69(ObjectMarshaller0 _enclosing, ObjectReference @ref
				, object onObject, StatefulBuffer writer)
			{
				this._enclosing = _enclosing;
				this.@ref = @ref;
				this.onObject = onObject;
				this.writer = writer;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, ClassMetadata
				 containingClass)
			{
				bool ok = false;
				try
				{
					field.Instantiate(this._enclosing._family, @ref, onObject, writer);
					ok = true;
				}
				catch (CorruptionException)
				{
				}
				finally
				{
					if (!ok)
					{
						this.Cancel();
					}
				}
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly ObjectReference @ref;

			private readonly object onObject;

			private readonly StatefulBuffer writer;
		}

		/// <param name="@ref"></param>
		protected virtual int LinkLength(FieldMetadata yf, ObjectReference @ref)
		{
			return yf.LinkLength();
		}

		/// <param name="yf"></param>
		/// <param name="yo"></param>
		protected virtual int MarshalledLength(FieldMetadata yf, ObjectReference yo)
		{
			return 0;
		}

		public override StatefulBuffer MarshallNew(Transaction a_trans, ObjectReference yo
			, int a_updateDepth)
		{
			throw new NotSupportedException();
		}

		public override void MarshallUpdate(Transaction trans, int updateDepth, ObjectReference
			 yapObject, object obj)
		{
			throw new NotSupportedException();
		}

		public override ObjectHeaderAttributes ReadHeaderAttributes(Db4objects.Db4o.Internal.Buffer
			 reader)
		{
			return null;
		}

		public override object ReadIndexEntry(ClassMetadata clazz, ObjectHeaderAttributes
			 attributes, FieldMetadata field, StatefulBuffer reader)
		{
			if (clazz == null)
			{
				return null;
			}
			if (!FindOffset(clazz, attributes, reader, field))
			{
				return null;
			}
			try
			{
				return field.ReadIndexEntry(_family, reader);
			}
			catch (CorruptionException exc)
			{
				throw new FieldIndexException(exc, field);
			}
		}

		public override void ReadVirtualAttributes(Transaction trans, ClassMetadata yc, ObjectReference
			 yo, ObjectHeaderAttributes attributes, Db4objects.Db4o.Internal.Buffer reader)
		{
			ObjectMarshaller.TraverseFieldCommand command = new _TraverseFieldCommand_137(this
				, trans, reader, yo);
			TraverseFields(yc, reader, attributes, command);
		}

		private sealed class _TraverseFieldCommand_137 : ObjectMarshaller.TraverseFieldCommand
		{
			public _TraverseFieldCommand_137(ObjectMarshaller0 _enclosing, Transaction trans, 
				Db4objects.Db4o.Internal.Buffer reader, ObjectReference yo)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
				this.reader = reader;
				this.yo = yo;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, ClassMetadata
				 containingClass)
			{
				field.ReadVirtualAttribute(trans, reader, yo);
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly Transaction trans;

			private readonly Db4objects.Db4o.Internal.Buffer reader;

			private readonly ObjectReference yo;
		}

		protected override bool IsNull(IFieldListInfo fieldList, int fieldIndex)
		{
			return false;
		}

		public override void DefragFields(ClassMetadata yapClass, ObjectHeader header, BufferPair
			 readers)
		{
		}

		public override void WriteObjectClassID(Db4objects.Db4o.Internal.Buffer reader, int
			 id)
		{
			reader.WriteInt(id);
		}

		public override void SkipMarshallerInfo(Db4objects.Db4o.Internal.Buffer reader)
		{
		}
	}
}

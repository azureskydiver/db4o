namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	public class ObjectMarshaller1 : com.db4o.@internal.marshall.ObjectMarshaller
	{
		public override void AddFieldIndices(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer writer, com.db4o.@internal.slots.Slot
			 oldSlot)
		{
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass15
				(this, writer, yc, oldSlot);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass15 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass15(ObjectMarshaller1 _enclosing, com.db4o.@internal.StatefulBuffer
				 writer, com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.slots.Slot oldSlot
				)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.yc = yc;
				this.oldSlot = oldSlot;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				if (isNull)
				{
					field.AddIndexEntry(writer.GetTransaction(), writer.GetID(), null);
				}
				else
				{
					field.AddFieldIndex(this._enclosing._family, yc, writer, oldSlot);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.@internal.StatefulBuffer writer;

			private readonly com.db4o.@internal.ClassMetadata yc;

			private readonly com.db4o.@internal.slots.Slot oldSlot;
		}

		public override com.db4o.@internal.TreeInt CollectFieldIDs(com.db4o.@internal.TreeInt
			 tree, com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer writer, string name)
		{
			com.db4o.@internal.TreeInt[] ret = { tree };
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass30
				(this, name, ret, writer);
			TraverseFields(yc, writer, attributes, command);
			return ret[0];
		}

		private sealed class _AnonymousInnerClass30 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass30(ObjectMarshaller1 _enclosing, string name, com.db4o.@internal.TreeInt[]
				 ret, com.db4o.@internal.StatefulBuffer writer)
			{
				this._enclosing = _enclosing;
				this.name = name;
				this.ret = ret;
				this.writer = writer;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				if (isNull)
				{
					return;
				}
				if (name.Equals(field.GetName()))
				{
					ret[0] = field.CollectIDs(this._enclosing._family, ret[0], writer);
				}
				else
				{
					field.IncrementOffset(writer);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly string name;

			private readonly com.db4o.@internal.TreeInt[] ret;

			private readonly com.db4o.@internal.StatefulBuffer writer;
		}

		public override void DeleteMembers(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer writer, int type, bool isUpdate)
		{
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass48
				(this, writer, isUpdate);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass48 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass48(ObjectMarshaller1 _enclosing, com.db4o.@internal.StatefulBuffer
				 writer, bool isUpdate)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.isUpdate = isUpdate;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				if (isNull)
				{
					field.RemoveIndexEntry(writer.GetTransaction(), writer.GetID(), null);
				}
				else
				{
					field.Delete(this._enclosing._family, writer, isUpdate);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.@internal.StatefulBuffer writer;

			private readonly bool isUpdate;
		}

		public override bool FindOffset(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.Buffer reader, com.db4o.@internal.FieldMetadata 
			field)
		{
			bool[] ret = { false };
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass62
				(this, field, ret, reader);
			TraverseFields(yc, reader, attributes, command);
			return ret[0];
		}

		private sealed class _AnonymousInnerClass62 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass62(ObjectMarshaller1 _enclosing, com.db4o.@internal.FieldMetadata
				 field, bool[] ret, com.db4o.@internal.Buffer reader)
			{
				this._enclosing = _enclosing;
				this.field = field;
				this.ret = ret;
				this.reader = reader;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata curField, bool
				 isNull, com.db4o.@internal.ClassMetadata containingClass)
			{
				if (curField == field)
				{
					ret[0] = !isNull;
					this.Cancel();
					return;
				}
				if (!isNull)
				{
					curField.IncrementOffset(reader);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.@internal.FieldMetadata field;

			private readonly bool[] ret;

			private readonly com.db4o.@internal.Buffer reader;
		}

		public override void InstantiateFields(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.ObjectReference yapObject, object onObject, com.db4o.@internal.StatefulBuffer
			 writer)
		{
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass79
				(this, onObject, yapObject, writer);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass79 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass79(ObjectMarshaller1 _enclosing, object onObject, com.db4o.@internal.ObjectReference
				 yapObject, com.db4o.@internal.StatefulBuffer writer)
			{
				this._enclosing = _enclosing;
				this.onObject = onObject;
				this.yapObject = yapObject;
				this.writer = writer;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				if (isNull)
				{
					field.Set(onObject, null);
					return;
				}
				try
				{
					field.Instantiate(this._enclosing._family, yapObject, onObject, writer);
				}
				catch (com.db4o.CorruptionException)
				{
					this.Cancel();
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly object onObject;

			private readonly com.db4o.@internal.ObjectReference yapObject;

			private readonly com.db4o.@internal.StatefulBuffer writer;
		}

		private void Marshall(com.db4o.@internal.ObjectReference yo, object obj, com.db4o.@internal.marshall.ObjectHeaderAttributes1
			 attributes, com.db4o.@internal.StatefulBuffer writer, bool isNew)
		{
			com.db4o.@internal.ClassMetadata yc = yo.GetYapClass();
			WriteObjectClassID(writer, yc.GetID());
			attributes.Write(writer);
			yc.CheckUpdateDepth(writer);
			com.db4o.@internal.Transaction trans = writer.GetTransaction();
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass102
				(this, trans, writer, obj, yo, isNew);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass102 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass102(ObjectMarshaller1 _enclosing, com.db4o.@internal.Transaction
				 trans, com.db4o.@internal.StatefulBuffer writer, object obj, com.db4o.@internal.ObjectReference
				 yo, bool isNew)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
				this.writer = writer;
				this.obj = obj;
				this.yo = yo;
				this.isNew = isNew;
			}

			public override int FieldCount(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.Buffer
				 reader)
			{
				reader.WriteInt(yapClass.i_fields.Length);
				return yapClass.i_fields.Length;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				if (isNull)
				{
					field.AddIndexEntry(trans, writer.GetID(), null);
					return;
				}
				object child = field.GetOrCreate(trans, obj);
				if (child is com.db4o.@internal.Db4oTypeImpl)
				{
					child = ((com.db4o.@internal.Db4oTypeImpl)child).StoredTo(trans);
				}
				field.Marshall(yo, child, this._enclosing._family, writer, containingClass.ConfigOrAncestorConfig
					(), isNew);
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.@internal.Transaction trans;

			private readonly com.db4o.@internal.StatefulBuffer writer;

			private readonly object obj;

			private readonly com.db4o.@internal.ObjectReference yo;

			private readonly bool isNew;
		}

		public override com.db4o.@internal.StatefulBuffer MarshallNew(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.ObjectReference yo, int a_updateDepth)
		{
			com.db4o.@internal.marshall.ObjectHeaderAttributes1 attributes = new com.db4o.@internal.marshall.ObjectHeaderAttributes1
				(yo);
			com.db4o.@internal.StatefulBuffer writer = CreateWriterForNew(a_trans, yo, a_updateDepth
				, attributes.ObjectLength());
			Marshall(yo, yo.GetObject(), attributes, writer, true);
			return writer;
		}

		public override void MarshallUpdate(com.db4o.@internal.Transaction trans, int updateDepth
			, com.db4o.@internal.ObjectReference yo, object obj)
		{
			com.db4o.@internal.marshall.ObjectHeaderAttributes1 attributes = new com.db4o.@internal.marshall.ObjectHeaderAttributes1
				(yo);
			com.db4o.@internal.StatefulBuffer writer = CreateWriterForUpdate(trans, updateDepth
				, yo.GetID(), 0, attributes.ObjectLength());
			if (trans.i_file != null)
			{
				trans.i_file.GetSlotForUpdate(writer);
			}
			Marshall(yo, obj, attributes, writer, false);
			MarshallUpdateWrite(trans, yo, obj, writer);
		}

		public override com.db4o.@internal.marshall.ObjectHeaderAttributes ReadHeaderAttributes
			(com.db4o.@internal.Buffer reader)
		{
			return new com.db4o.@internal.marshall.ObjectHeaderAttributes1(reader);
		}

		public override object ReadIndexEntry(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.FieldMetadata yf, com.db4o.@internal.StatefulBuffer
			 reader)
		{
			if (yc == null)
			{
				return null;
			}
			if (!FindOffset(yc, attributes, reader, yf))
			{
				return null;
			}
			return yf.ReadIndexEntry(_family, reader);
		}

		public override void ReadVirtualAttributes(com.db4o.@internal.Transaction trans, 
			com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.ObjectReference yo, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.Buffer reader)
		{
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass186
				(this, trans, reader, yo);
			TraverseFields(yc, reader, attributes, command);
		}

		private sealed class _AnonymousInnerClass186 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass186(ObjectMarshaller1 _enclosing, com.db4o.@internal.Transaction
				 trans, com.db4o.@internal.Buffer reader, com.db4o.@internal.ObjectReference yo)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
				this.reader = reader;
				this.yo = yo;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				if (!isNull)
				{
					field.ReadVirtualAttribute(trans, reader, yo);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.@internal.Transaction trans;

			private readonly com.db4o.@internal.Buffer reader;

			private readonly com.db4o.@internal.ObjectReference yo;
		}

		protected override bool IsNull(com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, int fieldIndex)
		{
			return ((com.db4o.@internal.marshall.ObjectHeaderAttributes1)attributes).IsNull(fieldIndex
				);
		}

		public override void DefragFields(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeader
			 header, com.db4o.@internal.ReaderPair readers)
		{
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass201
				(this, readers);
			TraverseFields(yc, null, header._headerAttributes, command);
		}

		private sealed class _AnonymousInnerClass201 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass201(ObjectMarshaller1 _enclosing, com.db4o.@internal.ReaderPair
				 readers)
			{
				this._enclosing = _enclosing;
				this.readers = readers;
			}

			public override int FieldCount(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.Buffer
				 reader)
			{
				return readers.ReadInt();
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				if (!isNull)
				{
					field.DefragField(this._enclosing._family, readers);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.@internal.ReaderPair readers;
		}

		public override void WriteObjectClassID(com.db4o.@internal.Buffer reader, int id)
		{
			reader.WriteInt(-id);
		}

		public override void SkipMarshallerInfo(com.db4o.@internal.Buffer reader)
		{
			reader.IncrementOffset(1);
		}
	}
}

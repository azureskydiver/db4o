namespace com.db4o.inside.marshall
{
	/// <exclude></exclude>
	public class ObjectMarshaller1 : com.db4o.inside.marshall.ObjectMarshaller
	{
		public override void AddFieldIndices(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter writer, com.db4o.inside.slots.Slot oldSlot)
		{
			com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass14
				(this, writer, yc, oldSlot);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass14 : com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass14(ObjectMarshaller1 _enclosing, com.db4o.YapWriter writer
				, com.db4o.YapClass yc, com.db4o.inside.slots.Slot oldSlot)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.yc = yc;
				this.oldSlot = oldSlot;
			}

			public override void ProcessField(com.db4o.YapField field, bool isNull, com.db4o.YapClass
				 containingClass)
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

			private readonly com.db4o.YapWriter writer;

			private readonly com.db4o.YapClass yc;

			private readonly com.db4o.inside.slots.Slot oldSlot;
		}

		public override com.db4o.TreeInt CollectFieldIDs(com.db4o.TreeInt tree, com.db4o.YapClass
			 yc, com.db4o.inside.marshall.ObjectHeaderAttributes attributes, com.db4o.YapWriter
			 writer, string name)
		{
			com.db4o.TreeInt[] ret = { tree };
			com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass29
				(this, name, ret, writer);
			TraverseFields(yc, writer, attributes, command);
			return ret[0];
		}

		private sealed class _AnonymousInnerClass29 : com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass29(ObjectMarshaller1 _enclosing, string name, com.db4o.TreeInt[]
				 ret, com.db4o.YapWriter writer)
			{
				this._enclosing = _enclosing;
				this.name = name;
				this.ret = ret;
				this.writer = writer;
			}

			public override void ProcessField(com.db4o.YapField field, bool isNull, com.db4o.YapClass
				 containingClass)
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

			private readonly com.db4o.TreeInt[] ret;

			private readonly com.db4o.YapWriter writer;
		}

		public override void DeleteMembers(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapWriter writer, int type, bool isUpdate)
		{
			com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass47
				(this, writer, isUpdate);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass47 : com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass47(ObjectMarshaller1 _enclosing, com.db4o.YapWriter writer
				, bool isUpdate)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.isUpdate = isUpdate;
			}

			public override void ProcessField(com.db4o.YapField field, bool isNull, com.db4o.YapClass
				 containingClass)
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

			private readonly com.db4o.YapWriter writer;

			private readonly bool isUpdate;
		}

		public override bool FindOffset(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapReader reader, com.db4o.YapField field)
		{
			bool[] ret = { false };
			com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass61
				(this, field, ret, reader);
			TraverseFields(yc, reader, attributes, command);
			return ret[0];
		}

		private sealed class _AnonymousInnerClass61 : com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass61(ObjectMarshaller1 _enclosing, com.db4o.YapField field
				, bool[] ret, com.db4o.YapReader reader)
			{
				this._enclosing = _enclosing;
				this.field = field;
				this.ret = ret;
				this.reader = reader;
			}

			public override void ProcessField(com.db4o.YapField curField, bool isNull, com.db4o.YapClass
				 containingClass)
			{
				if (curField == field)
				{
					ret[0] = !isNull;
					this.Cancel();
					return;
				}
				if (!isNull)
				{
					reader.IncrementOffset(curField.LinkLength());
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.YapField field;

			private readonly bool[] ret;

			private readonly com.db4o.YapReader reader;
		}

		public override void InstantiateFields(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapObject yapObject, object onObject, com.db4o.YapWriter writer
			)
		{
			com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass78
				(this, onObject, yapObject, writer);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass78 : com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass78(ObjectMarshaller1 _enclosing, object onObject, com.db4o.YapObject
				 yapObject, com.db4o.YapWriter writer)
			{
				this._enclosing = _enclosing;
				this.onObject = onObject;
				this.yapObject = yapObject;
				this.writer = writer;
			}

			public override void ProcessField(com.db4o.YapField field, bool isNull, com.db4o.YapClass
				 containingClass)
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
				catch (com.db4o.CorruptionException e)
				{
					this.Cancel();
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly object onObject;

			private readonly com.db4o.YapObject yapObject;

			private readonly com.db4o.YapWriter writer;
		}

		private void Marshall(com.db4o.YapObject yo, object obj, com.db4o.inside.marshall.ObjectHeaderAttributes1
			 attributes, com.db4o.YapWriter writer, bool isNew)
		{
			com.db4o.YapClass yc = yo.GetYapClass();
			WriteObjectClassID(writer, yc.GetID());
			attributes.Write(writer);
			yc.CheckUpdateDepth(writer);
			com.db4o.Transaction trans = writer.GetTransaction();
			com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass101
				(this, trans, writer, obj, yo, isNew);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass101 : com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass101(ObjectMarshaller1 _enclosing, com.db4o.Transaction
				 trans, com.db4o.YapWriter writer, object obj, com.db4o.YapObject yo, bool isNew
				)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
				this.writer = writer;
				this.obj = obj;
				this.yo = yo;
				this.isNew = isNew;
			}

			public override int FieldCount(com.db4o.YapClass yapClass, com.db4o.YapReader reader
				)
			{
				reader.WriteInt(yapClass.i_fields.Length);
				return yapClass.i_fields.Length;
			}

			public override void ProcessField(com.db4o.YapField field, bool isNull, com.db4o.YapClass
				 containingClass)
			{
				if (isNull)
				{
					field.AddIndexEntry(trans, writer.GetID(), null);
					return;
				}
				object child = field.GetOrCreate(trans, obj);
				if (child is com.db4o.Db4oTypeImpl)
				{
					child = ((com.db4o.Db4oTypeImpl)child).StoredTo(trans);
				}
				field.Marshall(yo, child, this._enclosing._family, writer, containingClass.ConfigOrAncestorConfig
					(), isNew);
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.Transaction trans;

			private readonly com.db4o.YapWriter writer;

			private readonly object obj;

			private readonly com.db4o.YapObject yo;

			private readonly bool isNew;
		}

		public override com.db4o.YapWriter MarshallNew(com.db4o.Transaction a_trans, com.db4o.YapObject
			 yo, int a_updateDepth)
		{
			com.db4o.inside.marshall.ObjectHeaderAttributes1 attributes = new com.db4o.inside.marshall.ObjectHeaderAttributes1
				(yo);
			com.db4o.YapWriter writer = CreateWriterForNew(a_trans, yo, a_updateDepth, attributes
				.ObjectLength());
			Marshall(yo, yo.GetObject(), attributes, writer, true);
			return writer;
		}

		public override void MarshallUpdate(com.db4o.Transaction trans, int updateDepth, 
			com.db4o.YapObject yo, object obj)
		{
			com.db4o.inside.marshall.ObjectHeaderAttributes1 attributes = new com.db4o.inside.marshall.ObjectHeaderAttributes1
				(yo);
			com.db4o.YapWriter writer = CreateWriterForUpdate(trans, updateDepth, yo.GetID(), 
				0, attributes.ObjectLength());
			if (trans.i_file != null)
			{
				trans.i_file.GetSlotForUpdate(writer);
			}
			Marshall(yo, obj, attributes, writer, false);
			MarshallUpdateWrite(trans, yo, obj, writer);
		}

		public override com.db4o.inside.marshall.ObjectHeaderAttributes ReadHeaderAttributes
			(com.db4o.YapReader reader)
		{
			return new com.db4o.inside.marshall.ObjectHeaderAttributes1(reader);
		}

		public override object ReadIndexEntry(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.YapField yf, com.db4o.YapWriter reader)
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

		public override void ReadVirtualAttributes(com.db4o.Transaction trans, com.db4o.YapClass
			 yc, com.db4o.YapObject yo, com.db4o.inside.marshall.ObjectHeaderAttributes attributes
			, com.db4o.YapReader reader)
		{
			com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass185
				(this, trans, reader, yo);
			TraverseFields(yc, reader, attributes, command);
		}

		private sealed class _AnonymousInnerClass185 : com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass185(ObjectMarshaller1 _enclosing, com.db4o.Transaction
				 trans, com.db4o.YapReader reader, com.db4o.YapObject yo)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
				this.reader = reader;
				this.yo = yo;
			}

			public override void ProcessField(com.db4o.YapField field, bool isNull, com.db4o.YapClass
				 containingClass)
			{
				if (!isNull)
				{
					field.ReadVirtualAttribute(trans, reader, yo);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.Transaction trans;

			private readonly com.db4o.YapReader reader;

			private readonly com.db4o.YapObject yo;
		}

		protected override bool IsNull(com.db4o.inside.marshall.ObjectHeaderAttributes attributes
			, int fieldIndex)
		{
			return ((com.db4o.inside.marshall.ObjectHeaderAttributes1)attributes).IsNull(fieldIndex
				);
		}

		public override void DefragFields(com.db4o.YapClass yc, com.db4o.inside.marshall.ObjectHeader
			 header, com.db4o.YapReader source, com.db4o.YapReader target, com.db4o.IDMapping
			 mapping)
		{
			com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass200
				(this, target, source, mapping);
			TraverseFields(yc, source, header._headerAttributes, command);
		}

		private sealed class _AnonymousInnerClass200 : com.db4o.inside.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass200(ObjectMarshaller1 _enclosing, com.db4o.YapReader target
				, com.db4o.YapReader source, com.db4o.IDMapping mapping)
			{
				this._enclosing = _enclosing;
				this.target = target;
				this.source = source;
				this.mapping = mapping;
			}

			public override int FieldCount(com.db4o.YapClass yapClass, com.db4o.YapReader reader
				)
			{
				target.IncrementOffset(com.db4o.YapConst.INT_LENGTH);
				return source.ReadInt();
			}

			public override void ProcessField(com.db4o.YapField field, bool isNull, com.db4o.YapClass
				 containingClass)
			{
				if (!isNull)
				{
					field.GetHandler().Defrag(this._enclosing._family, source, target, mapping);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly com.db4o.YapReader target;

			private readonly com.db4o.YapReader source;

			private readonly com.db4o.IDMapping mapping;
		}

		public override void WriteObjectClassID(com.db4o.YapReader reader, int id)
		{
			reader.WriteInt(-id);
		}

		public override void SkipMarshallerInfo(com.db4o.YapReader reader)
		{
			reader.IncrementOffset(1);
		}
	}
}

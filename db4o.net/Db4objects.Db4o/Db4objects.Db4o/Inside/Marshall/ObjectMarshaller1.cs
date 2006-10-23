namespace Db4objects.Db4o.Inside.Marshall
{
	/// <exclude></exclude>
	public class ObjectMarshaller1 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller
	{
		public override void AddFieldIndices(Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, Db4objects.Db4o.YapWriter writer, Db4objects.Db4o.Inside.Slots.Slot
			 oldSlot)
		{
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass14(this, writer, yc, oldSlot);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass14 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass14(ObjectMarshaller1 _enclosing, Db4objects.Db4o.YapWriter
				 writer, Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Slots.Slot oldSlot)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.yc = yc;
				this.oldSlot = oldSlot;
			}

			public override void ProcessField(Db4objects.Db4o.YapField field, bool isNull, Db4objects.Db4o.YapClass
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

			private readonly Db4objects.Db4o.YapWriter writer;

			private readonly Db4objects.Db4o.YapClass yc;

			private readonly Db4objects.Db4o.Inside.Slots.Slot oldSlot;
		}

		public override Db4objects.Db4o.TreeInt CollectFieldIDs(Db4objects.Db4o.TreeInt tree
			, Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, Db4objects.Db4o.YapWriter writer, string name)
		{
			Db4objects.Db4o.TreeInt[] ret = { tree };
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass29(this, name, ret, writer);
			TraverseFields(yc, writer, attributes, command);
			return ret[0];
		}

		private sealed class _AnonymousInnerClass29 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass29(ObjectMarshaller1 _enclosing, string name, Db4objects.Db4o.TreeInt[]
				 ret, Db4objects.Db4o.YapWriter writer)
			{
				this._enclosing = _enclosing;
				this.name = name;
				this.ret = ret;
				this.writer = writer;
			}

			public override void ProcessField(Db4objects.Db4o.YapField field, bool isNull, Db4objects.Db4o.YapClass
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

			private readonly Db4objects.Db4o.TreeInt[] ret;

			private readonly Db4objects.Db4o.YapWriter writer;
		}

		public override void MapStringIDs(Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, Db4objects.Db4o.YapReader reader, Db4objects.Db4o.IIDMapping mapping
			, int sourceBaseID, int targetBaseID)
		{
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass47(this, sourceBaseID, reader, targetBaseID, mapping);
			TraverseFields(yc, reader, attributes, command);
		}

		private sealed class _AnonymousInnerClass47 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass47(ObjectMarshaller1 _enclosing, int sourceBaseID, Db4objects.Db4o.YapReader
				 reader, int targetBaseID, Db4objects.Db4o.IIDMapping mapping)
			{
				this._enclosing = _enclosing;
				this.sourceBaseID = sourceBaseID;
				this.reader = reader;
				this.targetBaseID = targetBaseID;
				this.mapping = mapping;
			}

			public override void ProcessField(Db4objects.Db4o.YapField field, bool isNull, Db4objects.Db4o.YapClass
				 containingClass)
			{
				if (isNull)
				{
					return;
				}
				if (field.HasIndex() && (field.GetHandler() is Db4objects.Db4o.YapString))
				{
					int sourceID = sourceBaseID + reader._offset;
					int targetID = targetBaseID + reader._offset;
					mapping.MapIDs(sourceID, targetID);
				}
				field.IncrementOffset(reader);
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly int sourceBaseID;

			private readonly Db4objects.Db4o.YapReader reader;

			private readonly int targetBaseID;

			private readonly Db4objects.Db4o.IIDMapping mapping;
		}

		public override void DeleteMembers(Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, Db4objects.Db4o.YapWriter writer, int type, bool isUpdate)
		{
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass64(this, writer, isUpdate);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass64 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass64(ObjectMarshaller1 _enclosing, Db4objects.Db4o.YapWriter
				 writer, bool isUpdate)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.isUpdate = isUpdate;
			}

			public override void ProcessField(Db4objects.Db4o.YapField field, bool isNull, Db4objects.Db4o.YapClass
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

			private readonly Db4objects.Db4o.YapWriter writer;

			private readonly bool isUpdate;
		}

		public override bool FindOffset(Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, Db4objects.Db4o.YapReader reader, Db4objects.Db4o.YapField field)
		{
			bool[] ret = { false };
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass78(this, field, ret, reader);
			TraverseFields(yc, reader, attributes, command);
			return ret[0];
		}

		private sealed class _AnonymousInnerClass78 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass78(ObjectMarshaller1 _enclosing, Db4objects.Db4o.YapField
				 field, bool[] ret, Db4objects.Db4o.YapReader reader)
			{
				this._enclosing = _enclosing;
				this.field = field;
				this.ret = ret;
				this.reader = reader;
			}

			public override void ProcessField(Db4objects.Db4o.YapField curField, bool isNull, 
				Db4objects.Db4o.YapClass containingClass)
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

			private readonly Db4objects.Db4o.YapField field;

			private readonly bool[] ret;

			private readonly Db4objects.Db4o.YapReader reader;
		}

		public override void InstantiateFields(Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, Db4objects.Db4o.YapObject yapObject, object onObject, Db4objects.Db4o.YapWriter
			 writer)
		{
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass95(this, onObject, yapObject, writer);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass95 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass95(ObjectMarshaller1 _enclosing, object onObject, Db4objects.Db4o.YapObject
				 yapObject, Db4objects.Db4o.YapWriter writer)
			{
				this._enclosing = _enclosing;
				this.onObject = onObject;
				this.yapObject = yapObject;
				this.writer = writer;
			}

			public override void ProcessField(Db4objects.Db4o.YapField field, bool isNull, Db4objects.Db4o.YapClass
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
				catch (Db4objects.Db4o.CorruptionException e)
				{
					this.Cancel();
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly object onObject;

			private readonly Db4objects.Db4o.YapObject yapObject;

			private readonly Db4objects.Db4o.YapWriter writer;
		}

		private void Marshall(Db4objects.Db4o.YapObject yo, object obj, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes1
			 attributes, Db4objects.Db4o.YapWriter writer, bool isNew)
		{
			Db4objects.Db4o.YapClass yc = yo.GetYapClass();
			WriteObjectClassID(writer, yc.GetID());
			attributes.Write(writer);
			yc.CheckUpdateDepth(writer);
			Db4objects.Db4o.Transaction trans = writer.GetTransaction();
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass118(this, trans, writer, obj, yo, isNew);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass118 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass118(ObjectMarshaller1 _enclosing, Db4objects.Db4o.Transaction
				 trans, Db4objects.Db4o.YapWriter writer, object obj, Db4objects.Db4o.YapObject 
				yo, bool isNew)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
				this.writer = writer;
				this.obj = obj;
				this.yo = yo;
				this.isNew = isNew;
			}

			public override int FieldCount(Db4objects.Db4o.YapClass yapClass, Db4objects.Db4o.YapReader
				 reader)
			{
				reader.WriteInt(yapClass.i_fields.Length);
				return yapClass.i_fields.Length;
			}

			public override void ProcessField(Db4objects.Db4o.YapField field, bool isNull, Db4objects.Db4o.YapClass
				 containingClass)
			{
				if (isNull)
				{
					field.AddIndexEntry(trans, writer.GetID(), null);
					return;
				}
				object child = field.GetOrCreate(trans, obj);
				if (child is Db4objects.Db4o.IDb4oTypeImpl)
				{
					child = ((Db4objects.Db4o.IDb4oTypeImpl)child).StoredTo(trans);
				}
				field.Marshall(yo, child, this._enclosing._family, writer, containingClass.ConfigOrAncestorConfig
					(), isNew);
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly Db4objects.Db4o.Transaction trans;

			private readonly Db4objects.Db4o.YapWriter writer;

			private readonly object obj;

			private readonly Db4objects.Db4o.YapObject yo;

			private readonly bool isNew;
		}

		public override Db4objects.Db4o.YapWriter MarshallNew(Db4objects.Db4o.Transaction
			 a_trans, Db4objects.Db4o.YapObject yo, int a_updateDepth)
		{
			Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes1 attributes = new Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes1
				(yo);
			Db4objects.Db4o.YapWriter writer = CreateWriterForNew(a_trans, yo, a_updateDepth, 
				attributes.ObjectLength());
			Marshall(yo, yo.GetObject(), attributes, writer, true);
			return writer;
		}

		public override void MarshallUpdate(Db4objects.Db4o.Transaction trans, int updateDepth
			, Db4objects.Db4o.YapObject yo, object obj)
		{
			Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes1 attributes = new Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes1
				(yo);
			Db4objects.Db4o.YapWriter writer = CreateWriterForUpdate(trans, updateDepth, yo.GetID
				(), 0, attributes.ObjectLength());
			if (trans.i_file != null)
			{
				trans.i_file.GetSlotForUpdate(writer);
			}
			Marshall(yo, obj, attributes, writer, false);
			MarshallUpdateWrite(trans, yo, obj, writer);
		}

		public override Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes ReadHeaderAttributes
			(Db4objects.Db4o.YapReader reader)
		{
			return new Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes1(reader);
		}

		public override object ReadIndexEntry(Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, Db4objects.Db4o.YapField yf, Db4objects.Db4o.YapWriter reader)
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

		public override void ReadVirtualAttributes(Db4objects.Db4o.Transaction trans, Db4objects.Db4o.YapClass
			 yc, Db4objects.Db4o.YapObject yo, Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, Db4objects.Db4o.YapReader reader)
		{
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass202(this, trans, reader, yo);
			TraverseFields(yc, reader, attributes, command);
		}

		private sealed class _AnonymousInnerClass202 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass202(ObjectMarshaller1 _enclosing, Db4objects.Db4o.Transaction
				 trans, Db4objects.Db4o.YapReader reader, Db4objects.Db4o.YapObject yo)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
				this.reader = reader;
				this.yo = yo;
			}

			public override void ProcessField(Db4objects.Db4o.YapField field, bool isNull, Db4objects.Db4o.YapClass
				 containingClass)
			{
				if (!isNull)
				{
					field.ReadVirtualAttribute(trans, reader, yo);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly Db4objects.Db4o.Transaction trans;

			private readonly Db4objects.Db4o.YapReader reader;

			private readonly Db4objects.Db4o.YapObject yo;
		}

		protected override bool IsNull(Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes
			 attributes, int fieldIndex)
		{
			return ((Db4objects.Db4o.Inside.Marshall.ObjectHeaderAttributes1)attributes).IsNull
				(fieldIndex);
		}

		public override void DefragFields(Db4objects.Db4o.YapClass yc, Db4objects.Db4o.Inside.Marshall.ObjectHeader
			 header, Db4objects.Db4o.ReaderPair readers)
		{
			Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand command = new 
				_AnonymousInnerClass217(this, readers);
			TraverseFields(yc, null, header._headerAttributes, command);
		}

		private sealed class _AnonymousInnerClass217 : Db4objects.Db4o.Inside.Marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass217(ObjectMarshaller1 _enclosing, Db4objects.Db4o.ReaderPair
				 readers)
			{
				this._enclosing = _enclosing;
				this.readers = readers;
			}

			public override int FieldCount(Db4objects.Db4o.YapClass yapClass, Db4objects.Db4o.YapReader
				 reader)
			{
				return readers.ReadInt();
			}

			public override void ProcessField(Db4objects.Db4o.YapField field, bool isNull, Db4objects.Db4o.YapClass
				 containingClass)
			{
				if (!isNull)
				{
					field.DefragField(this._enclosing._family, readers);
				}
			}

			private readonly ObjectMarshaller1 _enclosing;

			private readonly Db4objects.Db4o.ReaderPair readers;
		}

		public override void WriteObjectClassID(Db4objects.Db4o.YapReader reader, int id)
		{
			reader.WriteInt(-id);
		}

		public override void SkipMarshallerInfo(Db4objects.Db4o.YapReader reader)
		{
			reader.IncrementOffset(1);
		}
	}
}

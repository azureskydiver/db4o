namespace com.db4o.@internal.marshall
{
	/// <exclude></exclude>
	internal class ObjectMarshaller0 : com.db4o.@internal.marshall.ObjectMarshaller
	{
		public override void AddFieldIndices(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer writer, com.db4o.@internal.slots.Slot
			 oldSlot)
		{
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass15
				(this, yc, writer, oldSlot);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass15 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass15(ObjectMarshaller0 _enclosing, com.db4o.@internal.ClassMetadata
				 yc, com.db4o.@internal.StatefulBuffer writer, com.db4o.@internal.slots.Slot oldSlot
				)
			{
				this._enclosing = _enclosing;
				this.yc = yc;
				this.writer = writer;
				this.oldSlot = oldSlot;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				field.AddFieldIndex(this._enclosing._family, yc, writer, oldSlot);
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly com.db4o.@internal.ClassMetadata yc;

			private readonly com.db4o.@internal.StatefulBuffer writer;

			private readonly com.db4o.@internal.slots.Slot oldSlot;
		}

		public override com.db4o.@internal.TreeInt CollectFieldIDs(com.db4o.@internal.TreeInt
			 tree, com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer writer, string name)
		{
			com.db4o.@internal.TreeInt[] ret = { tree };
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass25
				(this, name, ret, writer);
			TraverseFields(yc, writer, attributes, command);
			return ret[0];
		}

		private sealed class _AnonymousInnerClass25 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass25(ObjectMarshaller0 _enclosing, string name, com.db4o.@internal.TreeInt[]
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

			private readonly com.db4o.@internal.TreeInt[] ret;

			private readonly com.db4o.@internal.StatefulBuffer writer;
		}

		public override void DeleteMembers(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.StatefulBuffer writer, int type, bool isUpdate)
		{
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass39
				(this, writer, isUpdate);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass39 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass39(ObjectMarshaller0 _enclosing, com.db4o.@internal.StatefulBuffer
				 writer, bool isUpdate)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.isUpdate = isUpdate;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				field.Delete(this._enclosing._family, writer, isUpdate);
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly com.db4o.@internal.StatefulBuffer writer;

			private readonly bool isUpdate;
		}

		public override bool FindOffset(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.Buffer writer, com.db4o.@internal.FieldMetadata 
			field)
		{
			bool[] ret = { false };
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass49
				(this, field, ret, writer);
			TraverseFields(yc, writer, attributes, command);
			return ret[0];
		}

		private sealed class _AnonymousInnerClass49 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass49(ObjectMarshaller0 _enclosing, com.db4o.@internal.FieldMetadata
				 field, bool[] ret, com.db4o.@internal.Buffer writer)
			{
				this._enclosing = _enclosing;
				this.field = field;
				this.ret = ret;
				this.writer = writer;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata curField, bool
				 isNull, com.db4o.@internal.ClassMetadata containingClass)
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

			private readonly com.db4o.@internal.FieldMetadata field;

			private readonly bool[] ret;

			private readonly com.db4o.@internal.Buffer writer;
		}

		protected int HeaderLength()
		{
			return com.db4o.@internal.Const4.OBJECT_LENGTH + com.db4o.@internal.Const4.ID_LENGTH;
		}

		public override void InstantiateFields(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, com.db4o.@internal.ObjectReference yapObject, object onObject, com.db4o.@internal.StatefulBuffer
			 writer)
		{
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass68
				(this, yapObject, onObject, writer);
			TraverseFields(yc, writer, attributes, command);
		}

		private sealed class _AnonymousInnerClass68 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass68(ObjectMarshaller0 _enclosing, com.db4o.@internal.ObjectReference
				 yapObject, object onObject, com.db4o.@internal.StatefulBuffer writer)
			{
				this._enclosing = _enclosing;
				this.yapObject = yapObject;
				this.onObject = onObject;
				this.writer = writer;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				try
				{
					field.Instantiate(this._enclosing._family, yapObject, onObject, writer);
				}
				catch (com.db4o.CorruptionException)
				{
					this.Cancel();
				}
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly com.db4o.@internal.ObjectReference yapObject;

			private readonly object onObject;

			private readonly com.db4o.@internal.StatefulBuffer writer;
		}

		private int LinkLength(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.ObjectReference
			 yo)
		{
			int length = com.db4o.@internal.Const4.INT_LENGTH;
			if (yc.i_fields != null)
			{
				for (int i = 0; i < yc.i_fields.Length; i++)
				{
					length += LinkLength(yc.i_fields[i], yo);
				}
			}
			if (yc.i_ancestor != null)
			{
				length += LinkLength(yc.i_ancestor, yo);
			}
			return length;
		}

		protected virtual int LinkLength(com.db4o.@internal.FieldMetadata yf, com.db4o.@internal.ObjectReference
			 yo)
		{
			return yf.LinkLength();
		}

		private void Marshall(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.ObjectReference
			 a_yapObject, object a_object, com.db4o.@internal.StatefulBuffer writer, bool a_new
			)
		{
			MarshallDeclaredFields(yapClass, a_yapObject, a_object, writer, a_new);
		}

		private void MarshallDeclaredFields(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.ObjectReference
			 yapObject, object @object, com.db4o.@internal.StatefulBuffer writer, bool isNew
			)
		{
			com.db4o.@internal.Config4Class config = yapClass.ConfigOrAncestorConfig();
			com.db4o.@internal.Transaction trans = writer.GetTransaction();
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass108
				(this, writer, trans, @object, yapObject, config, isNew);
			TraverseFields(yapClass, writer, ReadHeaderAttributes(writer), command);
		}

		private sealed class _AnonymousInnerClass108 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass108(ObjectMarshaller0 _enclosing, com.db4o.@internal.StatefulBuffer
				 writer, com.db4o.@internal.Transaction trans, object @object, com.db4o.@internal.ObjectReference
				 yapObject, com.db4o.@internal.Config4Class config, bool isNew)
			{
				this._enclosing = _enclosing;
				this.writer = writer;
				this.trans = trans;
				this.@object = @object;
				this.yapObject = yapObject;
				this.config = config;
				this.isNew = isNew;
			}

			public override int FieldCount(com.db4o.@internal.ClassMetadata yc, com.db4o.@internal.Buffer
				 reader)
			{
				writer.WriteInt(yc.i_fields.Length);
				return yc.i_fields.Length;
			}

			public override void ProcessField(com.db4o.@internal.FieldMetadata field, bool isNull
				, com.db4o.@internal.ClassMetadata containingClass)
			{
				object obj = field.GetOrCreate(trans, @object);
				if (obj is com.db4o.@internal.Db4oTypeImpl)
				{
					obj = ((com.db4o.@internal.Db4oTypeImpl)obj).StoredTo(trans);
				}
				field.Marshall(yapObject, obj, this._enclosing._family, writer, config, isNew);
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly com.db4o.@internal.StatefulBuffer writer;

			private readonly com.db4o.@internal.Transaction trans;

			private readonly object @object;

			private readonly com.db4o.@internal.ObjectReference yapObject;

			private readonly com.db4o.@internal.Config4Class config;

			private readonly bool isNew;
		}

		protected virtual int MarshalledLength(com.db4o.@internal.FieldMetadata yf, com.db4o.@internal.ObjectReference
			 yo)
		{
			return 0;
		}

		public override com.db4o.@internal.StatefulBuffer MarshallNew(com.db4o.@internal.Transaction
			 a_trans, com.db4o.@internal.ObjectReference yo, int a_updateDepth)
		{
			com.db4o.@internal.StatefulBuffer writer = CreateWriterForNew(a_trans, yo, a_updateDepth
				, ObjectLength(yo));
			com.db4o.@internal.ClassMetadata yc = yo.GetYapClass();
			object obj = yo.GetObject();
			if (yc.IsPrimitive())
			{
				((com.db4o.@internal.PrimitiveFieldHandler)yc).i_handler.WriteNew(com.db4o.@internal.marshall.MarshallerFamily
					.Current(), obj, false, writer, true, false);
			}
			else
			{
				WriteObjectClassID(writer, yc.GetID());
				yc.CheckUpdateDepth(writer);
				Marshall(yc, yo, obj, writer, true);
			}
			return writer;
		}

		public override void MarshallUpdate(com.db4o.@internal.Transaction trans, int updateDepth
			, com.db4o.@internal.ObjectReference yapObject, object obj)
		{
			com.db4o.@internal.StatefulBuffer writer = CreateWriterForUpdate(trans, updateDepth
				, yapObject.GetID(), 0, ObjectLength(yapObject));
			com.db4o.@internal.ClassMetadata yapClass = yapObject.GetYapClass();
			yapClass.CheckUpdateDepth(writer);
			writer.WriteInt(yapClass.GetID());
			Marshall(yapClass, yapObject, obj, writer, false);
			MarshallUpdateWrite(trans, yapObject, obj, writer);
		}

		private int ObjectLength(com.db4o.@internal.ObjectReference yo)
		{
			return HeaderLength() + LinkLength(yo.GetYapClass(), yo);
		}

		public override com.db4o.@internal.marshall.ObjectHeaderAttributes ReadHeaderAttributes
			(com.db4o.@internal.Buffer reader)
		{
			return null;
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
			com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand command = new _AnonymousInnerClass190
				(this, trans, reader, yo);
			TraverseFields(yc, reader, attributes, command);
		}

		private sealed class _AnonymousInnerClass190 : com.db4o.@internal.marshall.ObjectMarshaller.TraverseFieldCommand
		{
			public _AnonymousInnerClass190(ObjectMarshaller0 _enclosing, com.db4o.@internal.Transaction
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
				field.ReadVirtualAttribute(trans, reader, yo);
			}

			private readonly ObjectMarshaller0 _enclosing;

			private readonly com.db4o.@internal.Transaction trans;

			private readonly com.db4o.@internal.Buffer reader;

			private readonly com.db4o.@internal.ObjectReference yo;
		}

		protected override bool IsNull(com.db4o.@internal.marshall.ObjectHeaderAttributes
			 attributes, int fieldIndex)
		{
			return false;
		}

		public override void DefragFields(com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.marshall.ObjectHeader
			 header, com.db4o.@internal.ReaderPair readers)
		{
		}

		public override void WriteObjectClassID(com.db4o.@internal.Buffer reader, int id)
		{
			reader.WriteInt(id);
		}

		public override void SkipMarshallerInfo(com.db4o.@internal.Buffer reader)
		{
		}
	}
}

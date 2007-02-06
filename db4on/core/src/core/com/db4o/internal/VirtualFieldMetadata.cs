namespace com.db4o.@internal
{
	/// <summary>
	/// TODO: refactor for symmetric inheritance - don't inherit from YapField and override,
	/// instead extract an abstract superclass from YapField and let both YapField and this class implement
	/// </summary>
	/// <exclude></exclude>
	public abstract class VirtualFieldMetadata : com.db4o.@internal.FieldMetadata
	{
		internal VirtualFieldMetadata() : base(null)
		{
		}

		public abstract override void AddFieldIndex(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.ClassMetadata yapClass, com.db4o.@internal.StatefulBuffer
			 a_writer, com.db4o.@internal.slots.Slot oldSlot);

		public override bool Alive()
		{
			return true;
		}

		public override void CalculateLengths(com.db4o.@internal.Transaction trans, com.db4o.@internal.marshall.ObjectHeaderAttributes
			 header, object obj)
		{
			header.AddBaseLength(LinkLength());
		}

		internal override bool CanAddToQuery(string fieldName)
		{
			return fieldName.Equals(GetName());
		}

		public override bool CanUseNullBitmap()
		{
			return false;
		}

		internal override void CollectConstraints(com.db4o.@internal.Transaction a_trans, 
			com.db4o.@internal.query.processor.QConObject a_parent, object a_template, com.db4o.foundation.Visitor4
			 a_visitor)
		{
		}

		internal override void Deactivate(com.db4o.@internal.Transaction a_trans, object 
			a_onObject, int a_depth)
		{
		}

		public abstract override void Delete(com.db4o.@internal.marshall.MarshallerFamily
			 mf, com.db4o.@internal.StatefulBuffer a_bytes, bool isUpdate);

		public override object GetOrCreate(com.db4o.@internal.Transaction a_trans, object
			 a_OnObject)
		{
			return null;
		}

		public override bool NeedsArrayAndPrimitiveInfo()
		{
			return false;
		}

		public override bool NeedsHandlerId()
		{
			return false;
		}

		public override void Instantiate(com.db4o.@internal.marshall.MarshallerFamily mf, 
			com.db4o.@internal.ObjectReference a_yapObject, object a_onObject, com.db4o.@internal.StatefulBuffer
			 a_bytes)
		{
			a_yapObject.ProduceVirtualAttributes();
			Instantiate1(a_bytes.GetTransaction(), a_yapObject, a_bytes);
		}

		internal abstract void Instantiate1(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.ObjectReference
			 a_yapObject, com.db4o.@internal.Buffer a_bytes);

		public override void LoadHandler(com.db4o.@internal.ObjectContainerBase a_stream)
		{
		}

		public sealed override void Marshall(com.db4o.@internal.ObjectReference a_yapObject
			, object a_object, com.db4o.@internal.marshall.MarshallerFamily mf, com.db4o.@internal.StatefulBuffer
			 a_bytes, com.db4o.@internal.Config4Class a_config, bool a_new)
		{
			com.db4o.@internal.Transaction trans = a_bytes.GetTransaction();
			if (!trans.SupportsVirtualFields())
			{
				MarshallIgnore(a_bytes);
				return;
			}
			com.db4o.@internal.ObjectContainerBase stream = trans.Stream();
			com.db4o.@internal.HandlerRegistry handlers = stream.i_handlers;
			bool migrating = false;
			if (stream._replicationCallState != com.db4o.@internal.Const4.NONE)
			{
				if (stream._replicationCallState == com.db4o.@internal.Const4.OLD)
				{
					migrating = true;
					if (a_yapObject.VirtualAttributes() == null)
					{
						object obj = a_yapObject.GetObject();
						com.db4o.@internal.ObjectReference migrateYapObject = null;
						com.db4o.@internal.replication.MigrationConnection mgc = handlers.i_migration;
						if (mgc != null)
						{
							migrateYapObject = mgc.ReferenceFor(obj);
							if (migrateYapObject == null)
							{
								migrateYapObject = mgc.Peer(stream).GetYapObject(obj);
							}
						}
						if (migrateYapObject != null)
						{
							com.db4o.@internal.VirtualAttributes migrateAttributes = migrateYapObject.VirtualAttributes
								();
							if (migrateAttributes != null && migrateAttributes.i_database != null)
							{
								migrating = true;
								a_yapObject.SetVirtualAttributes((com.db4o.@internal.VirtualAttributes)migrateAttributes
									.ShallowClone());
								migrateAttributes.i_database.Bind(trans);
							}
						}
					}
				}
				else
				{
					com.db4o.@internal.replication.Db4oReplicationReferenceProvider provider = handlers
						._replicationReferenceProvider;
					object parentObject = a_yapObject.GetObject();
					com.db4o.@internal.replication.Db4oReplicationReference @ref = provider.ReferenceFor
						(parentObject);
					if (@ref != null)
					{
						migrating = true;
						com.db4o.@internal.VirtualAttributes va = a_yapObject.ProduceVirtualAttributes();
						va.i_version = @ref.Version();
						va.i_uuid = @ref.LongPart();
						va.i_database = @ref.SignaturePart();
					}
				}
			}
			if (a_yapObject.VirtualAttributes() == null)
			{
				a_yapObject.ProduceVirtualAttributes();
				migrating = false;
			}
			Marshall1(a_yapObject, a_bytes, migrating, a_new);
		}

		internal abstract void Marshall1(com.db4o.@internal.ObjectReference a_yapObject, 
			com.db4o.@internal.StatefulBuffer a_bytes, bool a_migrating, bool a_new);

		internal abstract void MarshallIgnore(com.db4o.@internal.Buffer writer);

		public override void ReadVirtualAttribute(com.db4o.@internal.Transaction a_trans, 
			com.db4o.@internal.Buffer a_reader, com.db4o.@internal.ObjectReference a_yapObject
			)
		{
			if (!a_trans.SupportsVirtualFields())
			{
				a_reader.IncrementOffset(LinkLength());
				return;
			}
			Instantiate1(a_trans, a_yapObject, a_reader);
		}

		public override bool IsVirtual()
		{
			return true;
		}

		protected override object IndexEntryFor(object indexEntry)
		{
			return indexEntry;
		}

		protected override com.db4o.@internal.ix.Indexable4 IndexHandler(com.db4o.@internal.ObjectContainerBase
			 stream)
		{
			return i_handler;
		}
	}
}

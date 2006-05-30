namespace com.db4o
{
	internal abstract class YapFieldVirtual : com.db4o.YapField
	{
		internal YapFieldVirtual() : base(null)
		{
		}

		public abstract override void AddFieldIndex(com.db4o.inside.marshall.MarshallerFamily
			 mf, com.db4o.YapWriter a_writer, bool a_new);

		public override void AppendEmbedded2(com.db4o.YapWriter a_bytes)
		{
			a_bytes.IncrementOffset(LinkLength());
		}

		public override bool Alive()
		{
			return true;
		}

		public override void CalculateLengths(com.db4o.Transaction trans, com.db4o.inside.marshall.ObjectHeaderAttributes
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

		internal override void CollectConstraints(com.db4o.Transaction a_trans, com.db4o.QConObject
			 a_parent, object a_template, com.db4o.foundation.Visitor4 a_visitor)
		{
		}

		internal override void Deactivate(com.db4o.Transaction a_trans, object a_onObject
			, int a_depth)
		{
		}

		public abstract override void Delete(com.db4o.inside.marshall.MarshallerFamily mf
			, com.db4o.YapWriter a_bytes, bool isUpdate);

		public override object GetOrCreate(com.db4o.Transaction a_trans, object a_OnObject
			)
		{
			return null;
		}

		internal override int OwnLength(com.db4o.YapStream a_stream)
		{
			return a_stream.StringIO().ShortLength(i_name);
		}

		internal override void InitIndex(com.db4o.Transaction systemTrans, com.db4o.MetaIndex
			 metaIndex)
		{
			if (i_index == null)
			{
				i_index = new com.db4o.inside.ix.Index4(systemTrans, GetHandler(), metaIndex, false
					);
			}
		}

		public override void Instantiate(com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapObject
			 a_yapObject, object a_onObject, com.db4o.YapWriter a_bytes)
		{
			if (a_yapObject.i_virtualAttributes == null)
			{
				a_yapObject.i_virtualAttributes = new com.db4o.VirtualAttributes();
			}
			Instantiate1(a_bytes.GetTransaction(), a_yapObject, a_bytes);
		}

		internal abstract void Instantiate1(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, com.db4o.YapReader a_bytes);

		internal override void LoadHandler(com.db4o.YapStream a_stream)
		{
		}

		public sealed override void Marshall(com.db4o.YapObject a_yapObject, object a_object
			, com.db4o.inside.marshall.MarshallerFamily mf, com.db4o.YapWriter a_bytes, com.db4o.Config4Class
			 a_config, bool a_new)
		{
			com.db4o.Transaction trans = a_bytes.i_trans;
			if (!trans.SupportsVirtualFields())
			{
				MarshallIgnore(a_bytes);
				return;
			}
			com.db4o.YapStream stream = trans.i_stream;
			com.db4o.YapHandlers handlers = stream.i_handlers;
			bool migrating = false;
			if (stream._replicationCallState != com.db4o.YapConst.NONE)
			{
				if (stream._replicationCallState == com.db4o.YapConst.OLD)
				{
					migrating = true;
					if (a_yapObject.i_virtualAttributes == null)
					{
						object obj = a_yapObject.GetObject();
						com.db4o.YapObject migrateYapObject = null;
						com.db4o.inside.replication.MigrationConnection mgc = handlers.i_migration;
						if (mgc != null)
						{
							migrateYapObject = mgc.ReferenceFor(obj);
							if (migrateYapObject == null)
							{
								migrateYapObject = mgc.Peer(stream).GetYapObject(obj);
							}
						}
						if (migrateYapObject != null && migrateYapObject.i_virtualAttributes != null && migrateYapObject
							.i_virtualAttributes.i_database != null)
						{
							migrating = true;
							a_yapObject.i_virtualAttributes = (com.db4o.VirtualAttributes)migrateYapObject.i_virtualAttributes
								.ShallowClone();
							if (migrateYapObject.i_virtualAttributes.i_database != null)
							{
								migrateYapObject.i_virtualAttributes.i_database.Bind(trans);
							}
						}
					}
				}
				else
				{
					com.db4o.inside.replication.Db4oReplicationReferenceProvider provider = handlers.
						_replicationReferenceProvider;
					object parentObject = a_yapObject.GetObject();
					com.db4o.inside.replication.Db4oReplicationReference _ref = provider.ReferenceFor
						(parentObject);
					if (_ref != null)
					{
						migrating = true;
						if (a_yapObject.i_virtualAttributes == null)
						{
							a_yapObject.i_virtualAttributes = new com.db4o.VirtualAttributes();
						}
						com.db4o.VirtualAttributes va = a_yapObject.i_virtualAttributes;
						va.i_version = _ref.Version();
						va.i_uuid = _ref.LongPart();
						va.i_database = _ref.SignaturePart();
					}
				}
			}
			if (a_yapObject.i_virtualAttributes == null)
			{
				a_yapObject.i_virtualAttributes = new com.db4o.VirtualAttributes();
				migrating = false;
			}
			Marshall1(a_yapObject, a_bytes, migrating, a_new);
		}

		internal abstract void Marshall1(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, bool a_migrating, bool a_new);

		internal abstract void MarshallIgnore(com.db4o.YapWriter writer);

		public override void ReadVirtualAttribute(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader, com.db4o.YapObject a_yapObject)
		{
			if (!a_trans.SupportsVirtualFields())
			{
				a_reader.IncrementOffset(LinkLength());
				return;
			}
			Instantiate1(a_trans, a_yapObject, a_reader);
		}

		internal override void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			, com.db4o.YapClass a_onClass)
		{
			a_writer.WriteShortString(trans, i_name);
		}
	}
}

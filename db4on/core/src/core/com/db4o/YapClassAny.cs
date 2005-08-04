namespace com.db4o
{
	/// <summary>Undefined YapClass used for members of type Object.</summary>
	/// <remarks>Undefined YapClass used for members of type Object.</remarks>
	internal sealed class YapClassAny : com.db4o.YapClass
	{
		public YapClassAny(com.db4o.YapStream stream) : base(stream, stream.i_handlers.ICLASS_OBJECT
			)
		{
		}

		public override bool canHold(com.db4o.reflect.ReflectClass claxx)
		{
			return true;
		}

		public static void appendEmbedded(com.db4o.YapWriter a_bytes)
		{
			com.db4o.YapClass yc = readYapClass(a_bytes);
			if (yc != null)
			{
				yc.appendEmbedded1(a_bytes);
			}
		}

		public override void cascadeActivation(com.db4o.Transaction a_trans, object a_object
			, int a_depth, bool a_activate)
		{
			com.db4o.YapClass yc = a_trans.i_stream.getYapClass(a_trans.reflector().forObject
				(a_object), false);
			if (yc != null)
			{
				yc.cascadeActivation(a_trans, a_object, a_depth, a_activate);
			}
		}

		public override void deleteEmbedded(com.db4o.YapWriter a_bytes)
		{
			int objectID = a_bytes.readInt();
			if (objectID > 0)
			{
				com.db4o.YapWriter reader = a_bytes.getStream().readWriterByID(a_bytes.getTransaction
					(), objectID);
				if (reader != null)
				{
					reader.setCascadeDeletes(a_bytes.cascadeDeletes());
					com.db4o.YapClass yapClass = readYapClass(reader);
					if (yapClass != null)
					{
						yapClass.deleteEmbedded1(reader, objectID);
					}
				}
			}
		}

		public override int getID()
		{
			return 11;
		}

		public override bool hasField(com.db4o.YapStream a_stream, string a_path)
		{
			return a_stream.i_classCollection.fieldExists(a_path);
		}

		internal override bool hasIndex()
		{
			return false;
		}

		public override bool holdsAnyClass()
		{
			return true;
		}

		internal override bool isStrongTyped()
		{
			return false;
		}

		public override com.db4o.YapDataType readArrayWrapper(com.db4o.Transaction a_trans
			, com.db4o.YapReader[] a_bytes)
		{
			int id = 0;
			int offset = a_bytes[0]._offset;
			try
			{
				id = a_bytes[0].readInt();
			}
			catch (System.Exception e)
			{
			}
			a_bytes[0]._offset = offset;
			if (id != 0)
			{
				com.db4o.YapWriter reader = a_trans.i_stream.readWriterByID(a_trans, id);
				if (reader != null)
				{
					com.db4o.YapClass yc = readYapClass(reader);
					try
					{
						if (yc != null)
						{
							a_bytes[0] = reader;
							return yc.readArrayWrapper1(a_bytes);
						}
					}
					catch (System.Exception e)
					{
					}
				}
			}
			return null;
		}

		internal static com.db4o.YapClass readYapClass(com.db4o.YapWriter a_reader)
		{
			return a_reader.getStream().getYapClass(a_reader.readInt());
		}

		public override bool supportsIndex()
		{
			return false;
		}
	}
}

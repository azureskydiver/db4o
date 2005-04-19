/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <exclude></exclude>
	public class YapField : com.db4o.ext.StoredField
	{
		private com.db4o.YapClass i_yapClass;

		private int i_arrayPosition;

		protected string i_name;

		private bool i_isArray;

		private bool i_isNArray;

		private bool i_isPrimitive;

		private com.db4o.reflect.ReflectField i_javaField;

		protected com.db4o.YapDataType i_handler;

		private int i_handlerID;

		private int i_state;

		private const int NOT_LOADED = 0;

		private const int UNAVAILABLE = -1;

		private const int AVAILABLE = 1;

		protected com.db4o.IxField i_index;

		private com.db4o.Config4Field i_config;

		private com.db4o.Db4oTypeImpl i_db4oType;

		internal static readonly com.db4o.YapField[] EMPTY_ARRAY = new com.db4o.YapField[
			0];

		internal YapField(com.db4o.YapClass a_yapClass)
		{
			i_yapClass = a_yapClass;
		}

		internal YapField(com.db4o.YapClass a_yapClass, com.db4o.config.ObjectTranslator 
			a_translator)
		{
			i_yapClass = a_yapClass;
			init(a_yapClass, j4o.lang.Class.getClassForObject(a_translator).getName(), 0);
			i_state = AVAILABLE;
			com.db4o.YapStream stream = getStream();
			i_handler = stream.i_handlers.handlerForClass(stream, stream.reflector().forClass
				(a_translator.storedClass()));
		}

		internal YapField(com.db4o.YapClass a_yapClass, com.db4o.reflect.ReflectField a_field
			, com.db4o.YapDataType a_handler)
		{
			init(a_yapClass, a_field.getName(), 0);
			i_javaField = a_field;
			i_javaField.setAccessible();
			i_handler = a_handler;
			configure(a_field.getType());
			checkDb4oType();
			i_state = AVAILABLE;
		}

		internal virtual void addFieldIndex(com.db4o.YapWriter a_writer, bool a_new)
		{
			if (i_index == null)
			{
				a_writer.incrementOffset(linkLength());
			}
			else
			{
				try
				{
					addIndexEntry(i_handler.readIndexObject(a_writer), a_writer);
				}
				catch (com.db4o.CorruptionException e)
				{
				}
			}
		}

		protected virtual void addIndexEntry(object a_object, com.db4o.YapWriter a_bytes)
		{
			addIndexEntry(a_bytes.getTransaction(), a_bytes.getID(), a_object);
		}

		internal virtual void addIndexEntry(com.db4o.Transaction a_trans, int a_id, object
			 a_object)
		{
			i_handler.prepareLastIoComparison(a_trans, a_object);
			com.db4o.IxFieldTransaction ift = getIndex(a_trans).dirtyFieldTransaction(a_trans
				);
			ift.add(new com.db4o.IxAdd(ift, a_id, i_handler.indexEntry(a_object)));
		}

		public virtual bool alive()
		{
			if (i_state == AVAILABLE)
			{
				return true;
			}
			if (i_state == NOT_LOADED)
			{
				if (i_handler == null)
				{
					i_handler = loadJavaField1();
					if (i_handler != null)
					{
						if (i_handlerID == 0)
						{
							i_handlerID = i_handler.getID();
						}
						else
						{
							if (i_handler.getID() != i_handlerID)
							{
								i_handler = null;
							}
						}
					}
				}
				loadJavaField();
				if (i_handler != null)
				{
					i_handler = wrapHandlerToArrays(getStream(), i_handler);
					i_state = AVAILABLE;
					checkDb4oType();
				}
				else
				{
					i_state = UNAVAILABLE;
				}
			}
			return i_state == AVAILABLE;
		}

		public virtual void appendEmbedded2(com.db4o.YapWriter a_bytes)
		{
			if (alive())
			{
				i_handler.appendEmbedded3(a_bytes);
			}
			else
			{
				a_bytes.incrementOffset(com.db4o.YapConst.YAPID_LENGTH);
			}
		}

		internal virtual bool canHold(com.db4o.reflect.ReflectClass claxx)
		{
			if (claxx == null)
			{
				return !i_isPrimitive;
			}
			return i_handler.canHold(claxx);
		}

		public virtual bool canLoadByIndex(com.db4o.QConObject a_qco, com.db4o.QE a_evaluator
			)
		{
			if (i_handler is com.db4o.YapClass)
			{
				if (a_evaluator is com.db4o.QEIdentity)
				{
					com.db4o.YapClass yc = (com.db4o.YapClass)i_handler;
					yc.i_lastID = a_qco.getObjectID();
					return true;
				}
				return false;
			}
			return true;
		}

		internal virtual void cascadeActivation(com.db4o.Transaction a_trans, object a_object
			, int a_depth, bool a_activate)
		{
			if (alive())
			{
				try
				{
					object cascadeTo = getOrCreate(a_trans, a_object);
					if (cascadeTo != null && i_handler != null)
					{
						i_handler.cascadeActivation(a_trans, cascadeTo, a_depth, a_activate);
					}
				}
				catch (System.Exception e)
				{
				}
			}
		}

		private void checkDb4oType()
		{
			if (i_javaField != null)
			{
				if (getStream().i_handlers.ICLASS_DB4OTYPE.isAssignableFrom(i_javaField.getType()
					))
				{
					i_db4oType = com.db4o.YapHandlers.getDb4oType(i_javaField.getType());
				}
			}
		}

		internal virtual void collectConstraints(com.db4o.Transaction a_trans, com.db4o.QConObject
			 a_parent, object a_template, com.db4o.Visitor4 a_visitor)
		{
			object obj = getOn(a_trans, a_template);
			if (obj != null)
			{
				com.db4o.Collection4 objs = com.db4o.Platform.flattenCollection(a_trans.i_stream, 
					obj);
				com.db4o.Iterator4 j = objs.iterator();
				while (j.hasNext())
				{
					obj = j.next();
					if (obj != null)
					{
						if (i_isPrimitive)
						{
							if (i_handler is com.db4o.YapJavaClass)
							{
								if (obj.Equals(((com.db4o.YapJavaClass)i_handler).primitiveNull()))
								{
									return;
								}
							}
						}
						if (com.db4o.Platform.ignoreAsConstraint(obj))
						{
							return;
						}
						if (!a_parent.hasObjectInParentPath(obj))
						{
							a_visitor.visit(new com.db4o.QConObject(a_trans, a_parent, qField(a_trans), obj));
						}
					}
				}
			}
		}

		internal virtual com.db4o.TreeInt collectIDs(com.db4o.TreeInt tree, com.db4o.YapWriter
			 a_bytes)
		{
			if (alive())
			{
				if (i_handler is com.db4o.YapClass)
				{
					tree = (com.db4o.TreeInt)com.db4o.Tree.add(tree, new com.db4o.TreeInt(a_bytes.readInt
						()));
				}
				else
				{
					if (i_handler is com.db4o.YapArray)
					{
						tree = ((com.db4o.YapArray)i_handler).collectIDs(tree, a_bytes);
					}
				}
			}
			return tree;
		}

		internal virtual void configure(com.db4o.reflect.ReflectClass a_class)
		{
			i_isPrimitive = a_class.isPrimitive();
			i_isArray = a_class.isArray();
			if (i_isArray)
			{
				com.db4o.reflect.ReflectArray reflectArray = getStream().reflector().array();
				i_isNArray = reflectArray.isNDimensional(a_class);
				a_class = reflectArray.getComponentType(a_class);
				if (i_isNArray)
				{
					i_handler = new com.db4o.YapArrayN(getStream(), i_handler, i_isPrimitive);
				}
				else
				{
					i_handler = new com.db4o.YapArray(getStream(), i_handler, i_isPrimitive);
				}
			}
		}

		internal virtual void deactivate(com.db4o.Transaction a_trans, object a_onObject, 
			int a_depth)
		{
			if (!alive())
			{
				return;
			}
			try
			{
				bool isEnumClass = i_yapClass.isEnum();
				if (i_isPrimitive && !i_isArray)
				{
					if (!isEnumClass)
					{
						i_javaField.set(a_onObject, ((com.db4o.YapJavaClass)i_handler).primitiveNull());
					}
					return;
				}
				if (a_depth > 0)
				{
					cascadeActivation(a_trans, a_onObject, a_depth, false);
				}
				if (!isEnumClass)
				{
					i_javaField.set(a_onObject, null);
				}
			}
			catch (System.Exception t)
			{
			}
		}

		internal virtual void delete(com.db4o.YapWriter a_bytes)
		{
			if (alive())
			{
				if (i_index != null)
				{
					int offset = a_bytes._offset;
					object obj = null;
					try
					{
						obj = i_handler.read(a_bytes);
					}
					catch (com.db4o.CorruptionException e)
					{
					}
					i_handler.prepareComparison(obj);
					com.db4o.IxFieldTransaction ift = i_index.dirtyFieldTransaction(a_bytes.getTransaction
						());
					ift.add(new com.db4o.IxRemove(ift, a_bytes.getID(), i_handler.indexEntry(obj)));
					a_bytes._offset = offset;
				}
				bool dotnetValueType = false;
				dotnetValueType = com.db4o.Platform.isValueType(i_handler.classReflector());
				if ((i_config != null && i_config.i_cascadeOnDelete == 1) || dotnetValueType)
				{
					int preserveCascade = a_bytes.cascadeDeletes();
					a_bytes.setCascadeDeletes(1);
					i_handler.deleteEmbedded(a_bytes);
					a_bytes.setCascadeDeletes(preserveCascade);
				}
				else
				{
					if (i_config != null && i_config.i_cascadeOnDelete == -1)
					{
						int preserveCascade = a_bytes.cascadeDeletes();
						a_bytes.setCascadeDeletes(0);
						i_handler.deleteEmbedded(a_bytes);
						a_bytes.setCascadeDeletes(preserveCascade);
					}
					else
					{
						i_handler.deleteEmbedded(a_bytes);
					}
				}
			}
		}

		public override bool Equals(object obj)
		{
			if (obj is com.db4o.YapField)
			{
				com.db4o.YapField yapField = (com.db4o.YapField)obj;
				yapField.alive();
				alive();
				return yapField.i_isPrimitive == i_isPrimitive && yapField.i_handler.equals(i_handler
					) && yapField.i_name.Equals(i_name);
			}
			return false;
		}

		public virtual object get(object a_onObject)
		{
			if (i_yapClass != null)
			{
				com.db4o.YapStream stream = i_yapClass.getStream();
				if (stream != null)
				{
					lock (stream.i_lock)
					{
						stream.checkClosed();
						com.db4o.YapObject yo = stream.getYapObject(a_onObject);
						if (yo != null)
						{
							int id = yo.getID();
							if (id > 0)
							{
								com.db4o.YapWriter writer = stream.readWriterByID(stream.getTransaction(), id);
								if (writer != null)
								{
									if (i_yapClass.findOffset(writer, this))
									{
										try
										{
											return read(writer);
										}
										catch (com.db4o.CorruptionException e)
										{
										}
									}
								}
							}
						}
					}
				}
			}
			return null;
		}

		public virtual string getName()
		{
			return i_name;
		}

		internal virtual com.db4o.YapClass getFieldYapClass(com.db4o.YapStream a_stream)
		{
			return i_handler.getYapClass(a_stream);
		}

		internal virtual com.db4o.IxField getIndex(com.db4o.Transaction a_trans)
		{
			return i_index;
		}

		internal virtual com.db4o.Tree getIndexRoot(com.db4o.Transaction a_trans)
		{
			return getIndex(a_trans).getFieldTransaction(a_trans).getRoot();
		}

		internal virtual com.db4o.YapDataType getHandler()
		{
			return i_handler;
		}

		internal virtual object getOn(com.db4o.Transaction a_trans, object a_OnObject)
		{
			if (alive())
			{
				try
				{
					return i_javaField.get(a_OnObject);
				}
				catch (System.Exception t)
				{
				}
			}
			return null;
		}

		/// <summary>
		/// dirty hack for com.db4o.types some of them need to be set automatically
		/// TODO: Derive from YapField for Db4oTypes
		/// </summary>
		internal virtual object getOrCreate(com.db4o.Transaction a_trans, object a_OnObject
			)
		{
			if (alive())
			{
				try
				{
					object obj = i_javaField.get(a_OnObject);
					if (i_db4oType != null)
					{
						if (obj == null)
						{
							obj = i_db4oType.createDefault(a_trans);
							i_javaField.set(a_OnObject, obj);
						}
					}
					return obj;
				}
				catch (System.Exception t)
				{
				}
			}
			return null;
		}

		internal virtual com.db4o.YapClass getParentYapClass()
		{
			return i_yapClass;
		}

		public virtual com.db4o.reflect.ReflectClass getStoredType()
		{
			return i_handler.classReflector();
		}

		public virtual com.db4o.YapStream getStream()
		{
			if (i_yapClass == null)
			{
				return null;
			}
			return i_yapClass.getStream();
		}

		internal virtual bool hasIndex()
		{
			return i_index != null;
		}

		internal virtual void incrementOffset(com.db4o.YapReader a_bytes)
		{
			a_bytes.incrementOffset(linkLength());
		}

		internal virtual void init(com.db4o.YapClass a_yapClass, string a_name, int syntheticforJad
			)
		{
			i_yapClass = a_yapClass;
			i_name = a_name;
			if (a_yapClass.i_config != null)
			{
				i_config = a_yapClass.i_config.configField(a_name);
			}
		}

		internal virtual void initConfigOnUp(com.db4o.Transaction trans)
		{
			if (i_config != null)
			{
				i_config.initOnUp(trans, this);
			}
		}

		internal virtual void initIndex(com.db4o.Transaction systemTrans, com.db4o.MetaIndex
			 metaIndex)
		{
			if (supportsIndex())
			{
				i_index = new com.db4o.IxField(systemTrans, this, metaIndex);
			}
		}

		internal virtual void instantiate(com.db4o.YapObject a_yapObject, object a_onObject
			, com.db4o.YapWriter a_bytes)
		{
			if (alive())
			{
				object toSet = null;
				try
				{
					toSet = read(a_bytes);
				}
				catch (System.Exception e)
				{
					throw new com.db4o.CorruptionException();
				}
				if (i_db4oType != null)
				{
					if (toSet != null)
					{
						((com.db4o.Db4oTypeImpl)toSet).setTrans(a_bytes.getTransaction());
					}
				}
				try
				{
					i_javaField.set(a_onObject, toSet);
				}
				catch (System.Exception t)
				{
				}
			}
		}

		public virtual bool isArray()
		{
			return i_isArray;
		}

		public virtual int linkLength()
		{
			alive();
			if (i_handler == null)
			{
				return com.db4o.YapConst.YAPID_LENGTH;
			}
			return i_handler.linkLength();
		}

		internal virtual void loadHandler(com.db4o.YapStream a_stream)
		{
			if (i_handlerID < 1)
			{
				i_handler = null;
			}
			else
			{
				if (i_handlerID <= a_stream.i_handlers.maxTypeID())
				{
					i_handler = a_stream.i_handlers.getHandler(i_handlerID);
				}
				else
				{
					i_handler = a_stream.getYapClass(i_handlerID);
				}
			}
		}

		private void loadJavaField()
		{
			com.db4o.YapDataType handler = loadJavaField1();
			if (handler == null || (!handler.equals(i_handler)))
			{
				i_javaField = null;
				i_state = UNAVAILABLE;
			}
		}

		private com.db4o.YapDataType loadJavaField1()
		{
			try
			{
				com.db4o.YapStream stream = i_yapClass.getStream();
				i_javaField = i_yapClass.classReflector().getDeclaredField(i_name);
				if (i_javaField == null)
				{
					return null;
				}
				i_javaField.setAccessible();
				stream.showInternalClasses(true);
				com.db4o.YapDataType handler = stream.i_handlers.handlerForClass(stream, i_javaField
					.getType());
				stream.showInternalClasses(false);
				return handler;
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		internal virtual void marshall(com.db4o.YapObject a_yapObject, object a_object, com.db4o.YapWriter
			 a_bytes, com.db4o.Config4Class a_config, bool a_new)
		{
			if (a_object != null && ((a_config != null && (a_config.i_cascadeOnUpdate == 1)) 
				|| (i_config != null && (i_config.i_cascadeOnUpdate == 1))))
			{
				int min = 1;
				if (i_yapClass.isCollection(a_object))
				{
					com.db4o.reflect.generic.GenericReflector reflector = i_yapClass.reflector();
					min = reflector.collectionUpdateDepth(reflector.forObject(a_object));
				}
				int updateDepth = a_bytes.getUpdateDepth();
				if (updateDepth < min)
				{
					a_bytes.setUpdateDepth(min);
				}
				i_handler.writeNew(a_object, a_bytes);
				a_bytes.setUpdateDepth(updateDepth);
			}
			else
			{
				i_handler.writeNew(a_object, a_bytes);
			}
			if (i_index != null)
			{
				addIndexEntry(a_object, a_bytes);
			}
		}

		internal virtual int ownLength(com.db4o.YapStream a_stream)
		{
			return a_stream.stringIO().shortLength(i_name) + 1 + com.db4o.YapConst.YAPID_LENGTH;
		}

		internal virtual com.db4o.YapComparable prepareComparison(object obj)
		{
			if (alive())
			{
				i_handler.prepareComparison(obj);
				return i_handler;
			}
			return null;
		}

		internal virtual com.db4o.QField qField(com.db4o.Transaction a_trans)
		{
			int yapClassID = 0;
			if (i_yapClass != null)
			{
				yapClassID = i_yapClass.getID();
			}
			return new com.db4o.QField(a_trans, i_name, this, yapClassID, i_arrayPosition);
		}

		internal virtual object read(com.db4o.YapWriter a_bytes)
		{
			if (!alive())
			{
				return null;
			}
			return i_handler.read(a_bytes);
		}

		internal virtual object readQuery(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader)
		{
			return i_handler.readQuery(a_trans, a_reader, false);
		}

		internal virtual com.db4o.YapField readThis(com.db4o.YapStream a_stream, com.db4o.YapReader
			 a_reader)
		{
			try
			{
				i_name = a_stream.i_handlers.i_stringHandler.readShort(a_reader);
			}
			catch (com.db4o.CorruptionException ce)
			{
				i_handler = null;
				return this;
			}
			if (i_name.IndexOf(com.db4o.YapConst.VIRTUAL_FIELD_PREFIX) == 0)
			{
				com.db4o.YapFieldVirtual[] virtuals = a_stream.i_handlers.i_virtualFields;
				for (int i = 0; i < virtuals.Length; i++)
				{
					if (i_name.Equals(virtuals[i].i_name))
					{
						return virtuals[i];
					}
				}
			}
			init(i_yapClass, i_name, 0);
			i_handlerID = a_reader.readInt();
			com.db4o.YapBit yb = new com.db4o.YapBit(a_reader.readByte());
			i_isPrimitive = yb.get();
			i_isArray = yb.get();
			i_isNArray = yb.get();
			return this;
		}

		public virtual void readVirtualAttribute(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader, com.db4o.YapObject a_yapObject)
		{
			a_reader.incrementOffset(i_handler.linkLength());
		}

		internal virtual void refresh()
		{
			com.db4o.YapDataType handler = loadJavaField1();
			if (handler != null)
			{
				handler = wrapHandlerToArrays(getStream(), handler);
				if (handler.equals(i_handler))
				{
					return;
				}
			}
			i_javaField = null;
			i_state = UNAVAILABLE;
		}

		public virtual void rename(string newName)
		{
			com.db4o.YapStream stream = i_yapClass.getStream();
			if (!stream.isClient())
			{
				i_name = newName;
				i_yapClass.setStateDirty();
				i_yapClass.write(stream, stream.getSystemTransaction());
			}
			else
			{
				com.db4o.Db4o.throwRuntimeException(58);
			}
		}

		internal virtual void setArrayPosition(int a_index)
		{
			i_arrayPosition = a_index;
		}

		internal virtual void setName(string a_name)
		{
			i_name = a_name;
		}

		internal virtual bool supportsIndex()
		{
			return alive() && i_handler.supportsIndex();
		}

		private com.db4o.YapDataType wrapHandlerToArrays(com.db4o.YapStream a_stream, com.db4o.YapDataType
			 a_handler)
		{
			if (i_isNArray)
			{
				a_handler = new com.db4o.YapArrayN(a_stream, a_handler, i_isPrimitive);
			}
			else
			{
				if (i_isArray)
				{
					a_handler = new com.db4o.YapArray(a_stream, a_handler, i_isPrimitive);
				}
			}
			return a_handler;
		}

		internal virtual void writeThis(com.db4o.YapWriter a_writer, com.db4o.YapClass a_onClass
			)
		{
			alive();
			a_writer.writeShortString(i_name);
			if (i_handler is com.db4o.YapClass)
			{
				if (i_handler.getID() == 0)
				{
					a_writer.getStream().needsUpdate(a_onClass);
				}
			}
			int wrapperID = 0;
			try
			{
				wrapperID = i_handler.getID();
			}
			catch (System.Exception e)
			{
			}
			if (wrapperID == 0)
			{
				wrapperID = i_handlerID;
			}
			a_writer.writeInt(wrapperID);
			com.db4o.YapBit yb = new com.db4o.YapBit(0);
			yb.set(i_handler is com.db4o.YapArrayN);
			yb.set(i_handler is com.db4o.YapArray);
			yb.set(i_isPrimitive);
			a_writer.append(yb.getByte());
		}

		public override string ToString()
		{
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			if (i_yapClass != null)
			{
				sb.append(i_yapClass.getName());
				sb.append(".");
				sb.append(getName());
			}
			return sb.ToString();
		}

		public virtual string toString(com.db4o.YapWriter writer, com.db4o.YapObject yapObject
			, int depth, int maxDepth)
		{
			string str = "\n Field " + i_name;
			if (!alive())
			{
				writer.incrementOffset(linkLength());
			}
			else
			{
				object obj = null;
				try
				{
					obj = read(writer);
				}
				catch (System.Exception e)
				{
				}
				if (obj == null)
				{
					str += "\n [null]";
				}
				else
				{
					str += "\n  " + obj.ToString();
				}
			}
			return str;
		}
	}
}

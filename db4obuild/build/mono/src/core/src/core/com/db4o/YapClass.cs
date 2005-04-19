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
	public class YapClass : com.db4o.YapMeta, com.db4o.YapDataType, com.db4o.ext.StoredClass
		, com.db4o.UseSystemTransaction
	{
		private com.db4o.Collection4 i_addMembersDependancies;

		internal com.db4o.YapClass i_ancestor;

		internal com.db4o.Config4Class i_config;

		internal com.db4o.YapField[] i_fields;

		private com.db4o.ClassIndex i_index;

		protected string i_name;

		protected int i_objectLength;

		protected readonly com.db4o.YapStream i_stream;

		internal byte[] i_nameBytes;

		private com.db4o.YapReader i_reader;

		private com.db4o.Db4oTypeImpl i_db4oType;

		private com.db4o.reflect.ReflectClass _reflector;

		private bool _isEnum;

		internal bool i_dontCallConstructors;

		private com.db4o.EventDispatcher _eventDispatcher;

		internal int i_lastID;

		internal YapClass(com.db4o.YapStream stream, com.db4o.reflect.ReflectClass reflector
			)
		{
			i_stream = stream;
			_reflector = reflector;
		}

		internal virtual void activateFields(com.db4o.Transaction a_trans, object a_object
			, int a_depth)
		{
			if (dispatchEvent(a_trans.i_stream, a_object, com.db4o.EventDispatcher.CAN_ACTIVATE
				))
			{
				activateFields1(a_trans, a_object, a_depth);
			}
		}

		internal virtual void activateFields1(com.db4o.Transaction a_trans, object a_object
			, int a_depth)
		{
			for (int i = 0; i < i_fields.Length; i++)
			{
				i_fields[i].cascadeActivation(a_trans, a_object, a_depth, true);
			}
			if (i_ancestor != null)
			{
				i_ancestor.activateFields1(a_trans, a_object, a_depth);
			}
		}

		internal void addFieldIndices(com.db4o.YapWriter a_writer, bool a_new)
		{
			if (hasIndex() || hasVirtualAttributes())
			{
				readObjectHeader(a_writer, a_writer.getID());
				addFieldIndices1(a_writer, a_new);
			}
		}

		private void addFieldIndices1(com.db4o.YapWriter a_writer, bool a_new)
		{
			int fieldCount = a_writer.readInt();
			for (int i = 0; i < fieldCount; i++)
			{
				i_fields[i].addFieldIndex(a_writer, a_new);
			}
			if (i_ancestor != null)
			{
				i_ancestor.addFieldIndices1(a_writer, a_new);
			}
		}

		internal virtual void addMembers(com.db4o.YapStream a_stream)
		{
			bitTrue(com.db4o.YapConst.CHECKED_CHANGES);
			if (i_config != null)
			{
				com.db4o.config.ObjectTranslator ot = i_config.getTranslator();
				if (ot != null)
				{
					if (!(i_fields != null && i_fields.Length > 0 && j4o.lang.Class.getClassForObject
						(ot).getName().Equals(i_fields[0].getName())))
					{
						i_stream.setDirty(this);
					}
					i_fields = new com.db4o.YapField[1];
					i_fields[0] = new com.db4o.YapFieldTranslator(this, ot);
					setStateOK();
					return;
				}
			}
			if (a_stream.detectSchemaChanges())
			{
				com.db4o.Iterator4 m;
				bool found;
				bool dirty = isDirty();
				com.db4o.YapField field;
				com.db4o.YapDataType wrapper;
				com.db4o.Collection4 members = new com.db4o.Collection4();
				members.addAll(i_fields);
				if (generateVersionNumbers())
				{
					if (!hasVersionField())
					{
						members.add(a_stream.i_handlers.i_indexes.i_fieldVersion);
						dirty = true;
					}
				}
				if (generateUUIDs())
				{
					if (!hasUUIDField())
					{
						members.add(a_stream.i_handlers.i_indexes.i_fieldUUID);
						dirty = true;
					}
				}
				com.db4o.reflect.ReflectField[] fields = classReflector().getDeclaredFields();
				for (int i = 0; i < fields.Length; i++)
				{
					if (storeField(fields[i]))
					{
						wrapper = a_stream.i_handlers.handlerForClass(a_stream, fields[i].getType());
						if (wrapper == null)
						{
							continue;
						}
						field = new com.db4o.YapField(this, fields[i], wrapper);
						found = false;
						m = members.iterator();
						while (m.hasNext())
						{
							if (((com.db4o.YapField)m.next()).Equals(field))
							{
								found = true;
								break;
							}
						}
						if (found)
						{
							continue;
						}
						dirty = true;
						members.add(field);
					}
				}
				if (dirty)
				{
					i_stream.setDirty(this);
					i_fields = new com.db4o.YapField[members.size()];
					members.toArray(i_fields);
					for (int i = 0; i < i_fields.Length; i++)
					{
						i_fields[i].setArrayPosition(i);
					}
				}
				else
				{
					if (members.size() == 0)
					{
						i_fields = new com.db4o.YapField[0];
					}
				}
			}
			else
			{
				if (i_fields == null)
				{
					i_fields = new com.db4o.YapField[0];
				}
			}
			setStateOK();
		}

		internal virtual void addMembersAddDependancy(com.db4o.YapClass a_yapClass)
		{
			if (i_addMembersDependancies == null)
			{
				i_addMembersDependancies = new com.db4o.Collection4();
			}
			i_addMembersDependancies.add(a_yapClass);
		}

		internal virtual void addToIndex(com.db4o.YapFile a_stream, com.db4o.Transaction 
			a_trans, int a_id)
		{
			if (a_stream.maintainsIndices())
			{
				addToIndex1(a_stream, a_trans, a_id);
			}
		}

		internal virtual void addToIndex1(com.db4o.YapFile a_stream, com.db4o.Transaction
			 a_trans, int a_id)
		{
			if (i_ancestor != null)
			{
				i_ancestor.addToIndex1(a_stream, a_trans, a_id);
			}
			if (hasIndex())
			{
				a_trans.addToClassIndex(getID(), a_id);
			}
		}

		internal virtual bool allowsQueries()
		{
			return hasIndex();
		}

		public virtual void appendEmbedded1(com.db4o.YapWriter a_bytes)
		{
			int length = readFieldLength(a_bytes);
			for (int i = 0; i < length; i++)
			{
				i_fields[i].appendEmbedded2(a_bytes);
			}
			if (i_ancestor != null)
			{
				i_ancestor.appendEmbedded1(a_bytes);
			}
		}

		public virtual void appendEmbedded3(com.db4o.YapWriter a_bytes)
		{
			a_bytes.incrementOffset(linkLength());
		}

		public virtual bool canHold(com.db4o.reflect.ReflectClass claxx)
		{
			if (claxx == null)
			{
				return true;
			}
			if (_reflector != null)
			{
				if (reflector().isCollection(classReflector()))
				{
					return true;
				}
				return classReflector().isAssignableFrom(claxx);
			}
			return false;
		}

		public virtual void cascadeActivation(com.db4o.Transaction a_trans, object a_object
			, int a_depth, bool a_activate)
		{
			com.db4o.Config4Class config = configOrAncestorConfig();
			if (config != null)
			{
				if (a_activate)
				{
					a_depth = config.adjustActivationDepth(a_depth);
				}
			}
			if (a_depth > 0)
			{
				com.db4o.YapStream stream = a_trans.i_stream;
				if (a_activate)
				{
					stream.stillToActivate(a_object, a_depth - 1);
				}
				else
				{
					stream.stillToDeactivate(a_object, a_depth - 1, false);
				}
			}
		}

		internal virtual void checkChanges()
		{
			if (stateOK())
			{
				if (!bitIsTrue(com.db4o.YapConst.CHECKED_CHANGES))
				{
					bitTrue(com.db4o.YapConst.CHECKED_CHANGES);
					if (i_ancestor != null)
					{
						i_ancestor.checkChanges();
					}
					if (_reflector != null)
					{
						addMembers(i_stream);
						if (!i_stream.isClient())
						{
							write(i_stream, i_stream.getSystemTransaction());
						}
					}
				}
			}
		}

		internal virtual void checkUpdateDepth(com.db4o.YapWriter a_bytes)
		{
			int depth = a_bytes.getUpdateDepth();
			com.db4o.Config4Class config = configOrAncestorConfig();
			if (depth == com.db4o.YapConst.UNSPECIFIED)
			{
				depth = checkUpdateDepthUnspecified(a_bytes.getStream());
			}
			if (config != null && (config.i_cascadeOnDelete == 1 || config.i_cascadeOnUpdate 
				== 1))
			{
				int depthBorder = reflector().collectionUpdateDepth(classReflector());
				if (depth < depthBorder)
				{
					depth = depthBorder;
				}
			}
			a_bytes.setUpdateDepth(depth - 1);
		}

		internal virtual int checkUpdateDepthUnspecified(com.db4o.YapStream a_stream)
		{
			int depth = a_stream.i_config.i_updateDepth + 1;
			if (i_config != null && i_config.i_updateDepth != 0)
			{
				depth = i_config.i_updateDepth + 1;
			}
			if (i_ancestor != null)
			{
				int ancestordepth = i_ancestor.checkUpdateDepthUnspecified(a_stream);
				if (ancestordepth > depth)
				{
					return ancestordepth;
				}
			}
			return depth;
		}

		internal virtual void collectConstraints(com.db4o.Transaction a_trans, com.db4o.QConObject
			 a_parent, object a_object, com.db4o.Visitor4 a_visitor)
		{
			if (i_fields != null)
			{
				for (int i = 0; i < i_fields.Length; i++)
				{
					i_fields[i].collectConstraints(a_trans, a_parent, a_object, a_visitor);
				}
			}
			if (i_ancestor != null)
			{
				i_ancestor.collectConstraints(a_trans, a_parent, a_object, a_visitor);
			}
		}

		internal virtual com.db4o.TreeInt collectFieldIDs(com.db4o.TreeInt tree, com.db4o.YapWriter
			 a_bytes, string name)
		{
			int length = readFieldLength(a_bytes);
			for (int i = 0; i < length; i++)
			{
				if (name.Equals(i_fields[i].getName()))
				{
					tree = i_fields[i].collectIDs(tree, a_bytes);
				}
				else
				{
					i_fields[i].incrementOffset(a_bytes);
				}
			}
			if (i_ancestor != null)
			{
				return i_ancestor.collectFieldIDs(tree, a_bytes, name);
			}
			return tree;
		}

		internal bool configInstantiates()
		{
			return i_config != null && i_config.instantiates();
		}

		internal virtual com.db4o.Config4Class configOrAncestorConfig()
		{
			if (i_config != null)
			{
				return i_config;
			}
			if (i_ancestor != null)
			{
				return i_ancestor.configOrAncestorConfig();
			}
			return null;
		}

		public virtual void copyValue(object a_from, object a_to)
		{
		}

		private bool createConstructor(com.db4o.YapStream a_stream, string a_name)
		{
			com.db4o.reflect.ReflectClass claxx;
			try
			{
				claxx = a_stream.reflector().forName(a_name);
			}
			catch (System.Exception t)
			{
				claxx = null;
			}
			return createConstructor(a_stream, claxx, a_name, true);
		}

		private bool createConstructor(com.db4o.YapStream a_stream, com.db4o.reflect.ReflectClass
			 a_class, string a_name, bool errMessages)
		{
			_reflector = a_class;
			_eventDispatcher = com.db4o.EventDispatcher.forClass(a_stream, a_class);
			if (a_class != null)
			{
				_isEnum = com.db4o.YapConst.CLASS_ENUM != null && (a_stream.i_handlers.ICLASS_ENUM
					.isAssignableFrom(a_class));
			}
			if (configInstantiates())
			{
				return true;
			}
			if (a_class != null)
			{
				if (a_stream.i_handlers.ICLASS_TRANSIENTCLASS.isAssignableFrom(a_class))
				{
					a_class = null;
				}
			}
			if (a_class == null)
			{
				if (a_name == null || a_name.IndexOf("com.db4o") != 0)
				{
					if (errMessages)
					{
						a_stream.logMsg(23, a_name);
					}
				}
				setStateDead();
				return false;
			}
			if (a_stream.i_handlers.createConstructor(a_class, !callConstructor()))
			{
				return true;
			}
			setStateDead();
			if (errMessages)
			{
				a_stream.logMsg(7, a_name);
			}
			if (a_stream.i_config.i_exceptionsOnNotStorable)
			{
				throw new com.db4o.ext.ObjectNotStorableException(a_class);
			}
			return false;
		}

		public virtual void deactivate(com.db4o.Transaction a_trans, object a_object, int
			 a_depth)
		{
			if (dispatchEvent(a_trans.i_stream, a_object, com.db4o.EventDispatcher.CAN_DEACTIVATE
				))
			{
				deactivate1(a_trans, a_object, a_depth);
				dispatchEvent(a_trans.i_stream, a_object, com.db4o.EventDispatcher.DEACTIVATE);
			}
		}

		internal virtual void deactivate1(com.db4o.Transaction a_trans, object a_object, 
			int a_depth)
		{
			for (int i = 0; i < i_fields.Length; i++)
			{
				i_fields[i].deactivate(a_trans, a_object, a_depth);
			}
			if (i_ancestor != null)
			{
				i_ancestor.deactivate1(a_trans, a_object, a_depth);
			}
		}

		internal virtual void delete(com.db4o.YapWriter a_bytes, object a_object)
		{
			readObjectHeader(a_bytes, a_bytes.getID());
			delete1(a_bytes, a_object);
		}

		internal virtual void delete1(com.db4o.YapWriter a_bytes, object a_object)
		{
			removeFromIndex(a_bytes.getTransaction(), a_bytes.getID());
			deleteMembers(a_bytes, a_bytes.getTransaction().i_stream.i_handlers.arrayType(a_object
				));
		}

		public virtual void deleteEmbedded(com.db4o.YapWriter a_bytes)
		{
			if (a_bytes.cascadeDeletes() > 0)
			{
				int id = a_bytes.readInt();
				if (id > 0)
				{
					deleteEmbedded1(a_bytes, id);
				}
			}
			else
			{
				a_bytes.incrementOffset(linkLength());
			}
		}

		internal virtual void deleteEmbedded1(com.db4o.YapWriter a_bytes, int a_id)
		{
			if (a_bytes.cascadeDeletes() > 0)
			{
				com.db4o.YapStream stream = a_bytes.getStream();
				object obj = stream.getByID2(a_bytes.getTransaction(), a_id);
				int cascade = a_bytes.cascadeDeletes() - 1;
				if (obj != null)
				{
					if (isCollection(obj))
					{
						cascade += reflector().collectionUpdateDepth(reflector().forObject(obj)) - 1;
					}
				}
				com.db4o.YapObject yo = stream.getYapObject(a_id);
				if (yo != null)
				{
					a_bytes.getStream().delete3(a_bytes.getTransaction(), yo, obj, cascade, false);
				}
			}
		}

		internal virtual void deleteMembers(com.db4o.YapWriter a_bytes, int a_type)
		{
			try
			{
				com.db4o.Config4Class config = configOrAncestorConfig();
				if (config != null && (config.i_cascadeOnDelete == 1))
				{
					int preserveCascade = a_bytes.cascadeDeletes();
					if (reflector().isCollection(classReflector()))
					{
						int newCascade = preserveCascade + reflector().collectionUpdateDepth(classReflector
							()) - 3;
						if (newCascade < 1)
						{
							newCascade = 1;
						}
						a_bytes.setCascadeDeletes(newCascade);
					}
					else
					{
						a_bytes.setCascadeDeletes(1);
					}
					deleteMembers1(a_bytes, a_type);
					a_bytes.setCascadeDeletes(preserveCascade);
				}
				else
				{
					deleteMembers1(a_bytes, a_type);
				}
			}
			catch (System.Exception e)
			{
			}
		}

		private void deleteMembers1(com.db4o.YapWriter a_bytes, int a_type)
		{
			int length = readFieldLength(a_bytes);
			for (int i = 0; i < length; i++)
			{
				i_fields[i].delete(a_bytes);
			}
			if (i_ancestor != null)
			{
				i_ancestor.deleteMembers(a_bytes, a_type);
			}
		}

		internal bool dispatchEvent(com.db4o.YapStream stream, object obj, int message)
		{
			if (_eventDispatcher != null)
			{
				if (stream.dispatchsEvents())
				{
					return _eventDispatcher.dispatch(stream, obj, message);
				}
			}
			return true;
		}

		public bool equals(com.db4o.YapDataType a_dataType)
		{
			return (this == a_dataType);
		}

		internal virtual bool findOffset(com.db4o.YapReader a_bytes, com.db4o.YapField a_field
			)
		{
			if (a_bytes == null)
			{
				return false;
			}
			a_bytes._offset = 0;
			a_bytes.incrementOffset(com.db4o.YapConst.YAPID_LENGTH);
			return findOffset1(a_bytes, a_field);
		}

		internal virtual bool findOffset1(com.db4o.YapReader a_bytes, com.db4o.YapField a_field
			)
		{
			int length = com.db4o.Debug.atHome ? readFieldLengthSodaAtHome(a_bytes) : readFieldLength
				(a_bytes);
			for (int i = 0; i < length; i++)
			{
				if (i_fields[i] == a_field)
				{
					return true;
				}
				a_bytes.incrementOffset(i_fields[i].linkLength());
			}
			if (i_ancestor != null)
			{
				return i_ancestor.findOffset1(a_bytes, a_field);
			}
			else
			{
				return false;
			}
		}

		internal virtual void forEachYapField(com.db4o.Visitor4 visitor)
		{
			if (i_fields != null)
			{
				for (int i = 0; i < i_fields.Length; i++)
				{
					visitor.visit(i_fields[i]);
				}
			}
			if (i_ancestor != null)
			{
				i_ancestor.forEachYapField(visitor);
			}
		}

		private bool generateUUIDs()
		{
			if (i_stream is com.db4o.YapFile)
			{
				com.db4o.YapFile yf = (com.db4o.YapFile)i_stream;
				int configValue = (i_config == null) ? 0 : i_config.i_generateUUIDs;
				if (yf.i_bootRecord == null)
				{
					return false;
				}
				return generate1(yf.i_bootRecord.i_generateUUIDs, configValue);
			}
			return false;
		}

		private bool generateVersionNumbers()
		{
			if (i_stream is com.db4o.YapFile)
			{
				com.db4o.YapFile yf = (com.db4o.YapFile)i_stream;
				int configValue = (i_config == null) ? 0 : i_config.i_generateVersionNumbers;
				if (yf.i_bootRecord == null)
				{
					return false;
				}
				return generate1(yf.i_bootRecord.i_generateVersionNumbers, configValue);
			}
			return false;
		}

		private bool generate1(int bootRecordValue, int configValue)
		{
			if (bootRecordValue < 0)
			{
				return false;
			}
			if (configValue < 0)
			{
				return false;
			}
			if (bootRecordValue > 1)
			{
				return true;
			}
			return configValue > 0;
		}

		internal virtual com.db4o.YapClass getAncestor()
		{
			return i_ancestor;
		}

		internal virtual object getComparableObject(object forObject)
		{
			if (i_config != null)
			{
				if (i_config.i_queryAttributeProvider != null)
				{
					return i_config.i_queryAttributeProvider.attribute(forObject);
				}
			}
			return forObject;
		}

		internal virtual com.db4o.YapClass getHigherHierarchy(com.db4o.YapClass a_yapClass
			)
		{
			com.db4o.YapClass yc = getHigherHierarchy1(a_yapClass);
			if (yc != null)
			{
				return yc;
			}
			return a_yapClass.getHigherHierarchy1(this);
		}

		private com.db4o.YapClass getHigherHierarchy1(com.db4o.YapClass a_yapClass)
		{
			if (a_yapClass == this)
			{
				return this;
			}
			if (i_ancestor != null)
			{
				return i_ancestor.getHigherHierarchy1(a_yapClass);
			}
			return null;
		}

		internal virtual com.db4o.YapClass getHigherOrCommonHierarchy(com.db4o.YapClass a_yapClass
			)
		{
			com.db4o.YapClass yc = getHigherHierarchy1(a_yapClass);
			if (yc != null)
			{
				return yc;
			}
			if (i_ancestor != null)
			{
				yc = i_ancestor.getHigherOrCommonHierarchy(a_yapClass);
				if (yc != null)
				{
					return yc;
				}
			}
			return a_yapClass.getHigherHierarchy1(this);
		}

		internal override byte getIdentifier()
		{
			return com.db4o.YapConst.YAPCLASS;
		}

		public virtual long[] getIDs()
		{
			lock (i_stream.i_lock)
			{
				if (stateOK())
				{
					return getIDs(i_stream.getTransaction());
				}
				return new long[0];
			}
		}

		public virtual long[] getIDs(com.db4o.Transaction trans)
		{
			if (stateOK() && hasIndex())
			{
				return getIndex().getInternalIDs(trans, getID());
			}
			return new long[0];
		}

		internal virtual bool hasIndex()
		{
			return i_db4oType == null || i_db4oType.hasClassIndex();
		}

		private bool hasUUIDField()
		{
			if (i_ancestor != null)
			{
				if (i_ancestor.hasUUIDField())
				{
					return true;
				}
			}
			if (i_fields != null)
			{
				for (int i = 0; i < i_fields.Length; i++)
				{
					if (i_fields[i] is com.db4o.YapFieldUUID)
					{
						return true;
					}
				}
			}
			return false;
		}

		private bool hasVersionField()
		{
			if (i_ancestor != null)
			{
				if (i_ancestor.hasVersionField())
				{
					return true;
				}
			}
			if (i_fields != null)
			{
				for (int i = 0; i < i_fields.Length; i++)
				{
					if (i_fields[i] is com.db4o.YapFieldVersion)
					{
						return true;
					}
				}
			}
			return false;
		}

		internal virtual com.db4o.ClassIndex getIndex()
		{
			if (stateOK() && i_index != null)
			{
				if (!i_index.isActive())
				{
					i_index.setStateDirty();
					i_index.read(i_stream.getSystemTransaction());
				}
				return i_index;
			}
			return null;
		}

		internal com.db4o.Tree getIndex(com.db4o.Transaction a_trans)
		{
			if (hasIndex())
			{
				com.db4o.ClassIndex ci = getIndex();
				if (ci != null)
				{
					return ci.cloneForYapClass(a_trans, getID());
				}
			}
			return null;
		}

		internal com.db4o.TreeInt getIndexRoot()
		{
			if (hasIndex())
			{
				com.db4o.ClassIndex ci = getIndex();
				return (com.db4o.TreeInt)ci.i_root;
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectClass classReflector()
		{
			return _reflector;
		}

		internal virtual com.db4o.YapClass[] getMembersDependancies()
		{
			if (i_addMembersDependancies == null)
			{
				return new com.db4o.YapClass[0];
			}
			com.db4o.YapClass[] ret = new com.db4o.YapClass[i_addMembersDependancies.size()];
			i_addMembersDependancies.toArray(ret);
			i_addMembersDependancies = null;
			return ret;
		}

		public virtual string getName()
		{
			return i_name;
		}

		public virtual com.db4o.ext.StoredClass getParentStoredClass()
		{
			return getAncestor();
		}

		public virtual com.db4o.ext.StoredField[] getStoredFields()
		{
			lock (i_stream.i_lock)
			{
				if (i_fields == null)
				{
					return null;
				}
				com.db4o.ext.StoredField[] fields = new com.db4o.ext.StoredField[i_fields.Length];
				j4o.lang.JavaSystem.arraycopy(i_fields, 0, fields, 0, i_fields.Length);
				return fields;
			}
		}

		internal virtual com.db4o.YapStream getStream()
		{
			return i_stream;
		}

		public virtual int getType()
		{
			return com.db4o.YapConst.TYPE_CLASS;
		}

		public virtual com.db4o.YapClass getYapClass(com.db4o.YapStream a_stream)
		{
			return this;
		}

		public virtual com.db4o.YapField getYapField(string name)
		{
			com.db4o.YapField[] yf = new com.db4o.YapField[1];
			forEachYapField(new _AnonymousInnerClass793(this, name, yf));
			return yf[0];
		}

		private sealed class _AnonymousInnerClass793 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass793(YapClass _enclosing, string name, com.db4o.YapField[]
				 yf)
			{
				this._enclosing = _enclosing;
				this.name = name;
				this.yf = yf;
			}

			public void visit(object obj)
			{
				if (name.Equals(((com.db4o.YapField)obj).getName()))
				{
					yf[0] = (com.db4o.YapField)obj;
				}
			}

			private readonly YapClass _enclosing;

			private readonly string name;

			private readonly com.db4o.YapField[] yf;
		}

		public virtual bool hasField(com.db4o.YapStream a_stream, string a_field)
		{
			if (reflector().isCollection(classReflector()))
			{
				return true;
			}
			return getYapField(a_field) != null;
		}

		internal virtual bool hasVirtualAttributes()
		{
			return hasVersionField() || hasUUIDField();
		}

		public virtual bool holdsAnyClass()
		{
			return reflector().isCollection(classReflector());
		}

		internal virtual void incrementFieldsOffset1(com.db4o.YapReader a_bytes)
		{
			int length = com.db4o.Debug.atHome ? readFieldLengthSodaAtHome(a_bytes) : readFieldLength
				(a_bytes);
			for (int i = 0; i < length; i++)
			{
				i_fields[i].incrementOffset(a_bytes);
			}
		}

		public virtual object indexObject(com.db4o.Transaction a_trans, object a_object)
		{
			return a_object;
		}

		internal virtual bool init(com.db4o.YapStream a_stream, com.db4o.YapClass a_ancestor
			, com.db4o.reflect.ReflectClass claxx, bool errMessages)
		{
			i_ancestor = a_ancestor;
			i_config = a_stream.i_config.configClass(claxx.getName());
			if (!createConstructor(a_stream, claxx, claxx.getName(), false))
			{
				return false;
			}
			checkDb4oType();
			if (allowsQueries())
			{
				i_index = a_stream.createClassIndex(this);
			}
			i_name = claxx.getName();
			i_ancestor = a_ancestor;
			bitTrue(com.db4o.YapConst.CHECKED_CHANGES);
			return true;
		}

		internal virtual void initConfigOnUp(com.db4o.Transaction systemTrans)
		{
			if (i_config != null)
			{
				systemTrans.i_stream.showInternalClasses(true);
				i_config.initOnUp(systemTrans);
				if (i_fields != null)
				{
					for (int i = 0; i < i_fields.Length; i++)
					{
						i_fields[i].initConfigOnUp(systemTrans);
					}
				}
				systemTrans.i_stream.showInternalClasses(false);
			}
		}

		internal virtual void initOnUp(com.db4o.Transaction systemTrans)
		{
			if (stateOK())
			{
				initConfigOnUp(systemTrans);
				storeStaticFieldValues(systemTrans, false);
			}
		}

		internal virtual object instantiate(com.db4o.YapObject a_yapObject, object a_object
			, com.db4o.YapWriter a_bytes, bool a_addToIDTree)
		{
			com.db4o.YapStream stream = a_bytes.getStream();
			com.db4o.Transaction trans = a_bytes.getTransaction();
			bool create = (a_object == null);
			if (i_config != null)
			{
				a_bytes.setInstantiationDepth(i_config.adjustActivationDepth(a_bytes.getInstantiationDepth
					()));
			}
			bool doFields = (a_bytes.getInstantiationDepth() > 0) || (i_config != null && (i_config
				.i_cascadeOnActivate == 1));
			if (create)
			{
				if (configInstantiates())
				{
					int bytesOffset = a_bytes._offset;
					a_bytes.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
					try
					{
						a_object = i_config.instantiate(stream, i_fields[0].read(a_bytes));
					}
					catch (System.Exception e)
					{
						com.db4o.Db4o.logErr(stream.i_config, 6, classReflector().getName(), e);
						return null;
					}
					a_bytes._offset = bytesOffset;
				}
				else
				{
					if (_reflector == null)
					{
						return null;
					}
					stream.instantiating(true);
					try
					{
						a_object = _reflector.newInstance();
					}
					catch (j4o.lang.NoSuchMethodError e)
					{
						stream.logMsg(7, classReflector().getName());
						stream.instantiating(false);
						return null;
					}
					catch (System.Exception e)
					{
						stream.instantiating(false);
						return null;
					}
					stream.instantiating(false);
				}
				if (a_object is com.db4o.Db4oTypeImpl)
				{
					((com.db4o.Db4oTypeImpl)a_object).setTrans(a_bytes.getTransaction());
					((com.db4o.Db4oTypeImpl)a_object).setYapObject(a_yapObject);
				}
				a_yapObject.setObjectWeak(stream, a_object);
				stream.hcTreeAdd(a_yapObject);
			}
			else
			{
				if (!stream.i_refreshInsteadOfActivate)
				{
					if (a_yapObject.isActive())
					{
						doFields = false;
					}
				}
			}
			if (a_addToIDTree)
			{
				a_yapObject.addToIDTree(stream);
			}
			if (doFields)
			{
				if (dispatchEvent(stream, a_object, com.db4o.EventDispatcher.CAN_ACTIVATE))
				{
					a_yapObject.setStateClean();
					instantiateFields(a_yapObject, a_object, a_bytes);
					dispatchEvent(stream, a_object, com.db4o.EventDispatcher.ACTIVATE);
				}
				else
				{
					if (create)
					{
						a_yapObject.setStateDeactivated();
					}
				}
			}
			else
			{
				if (create)
				{
					a_yapObject.setStateDeactivated();
				}
				else
				{
					if (a_bytes.getInstantiationDepth() > 1)
					{
						activateFields(trans, a_object, a_bytes.getInstantiationDepth() - 1);
					}
				}
			}
			return a_object;
		}

		internal virtual object instantiateTransient(com.db4o.YapObject a_yapObject, object
			 a_object, com.db4o.YapWriter a_bytes)
		{
			com.db4o.YapStream stream = a_bytes.getStream();
			if (configInstantiates())
			{
				int bytesOffset = a_bytes._offset;
				a_bytes.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
				try
				{
					a_object = i_config.instantiate(stream, i_fields[0].read(a_bytes));
				}
				catch (System.Exception e)
				{
					com.db4o.Db4o.logErr(stream.i_config, 6, classReflector().getName(), e);
					return null;
				}
				a_bytes._offset = bytesOffset;
			}
			else
			{
				if (_reflector == null)
				{
					return null;
				}
				stream.instantiating(true);
				try
				{
					a_object = _reflector.newInstance();
				}
				catch (j4o.lang.NoSuchMethodError e)
				{
					stream.logMsg(7, classReflector().getName());
					stream.instantiating(false);
					return null;
				}
				catch (System.Exception e)
				{
					stream.instantiating(false);
					return null;
				}
				stream.instantiating(false);
			}
			stream.peeked(a_yapObject.getID(), a_object);
			instantiateFields(a_yapObject, a_object, a_bytes);
			return a_object;
		}

		internal virtual void instantiateFields(com.db4o.YapObject a_yapObject, object a_onObject
			, com.db4o.YapWriter a_bytes)
		{
			int length = readFieldLength(a_bytes);
			try
			{
				for (int i = 0; i < length; i++)
				{
					i_fields[i].instantiate(a_yapObject, a_onObject, a_bytes);
				}
				if (i_ancestor != null)
				{
					i_ancestor.instantiateFields(a_yapObject, a_onObject, a_bytes);
				}
			}
			catch (com.db4o.CorruptionException ce)
			{
			}
		}

		public virtual object indexEntry(object a_object)
		{
			return System.Convert.ToInt32(i_lastID);
		}

		public virtual bool isArray()
		{
			return reflector().isCollection(classReflector());
		}

		internal virtual bool isCollection(object obj)
		{
			return reflector().isCollection(reflector().forObject(obj));
		}

		public override bool isDirty()
		{
			if (!stateOK())
			{
				return false;
			}
			return base.isDirty();
		}

		internal virtual bool isEnum()
		{
			return _isEnum;
		}

		internal virtual bool isPrimitive()
		{
			return false;
		}

		/// <summary>no any, primitive, array or other tricks.</summary>
		/// <remarks>
		/// no any, primitive, array or other tricks. overriden in YapClassAny and
		/// YapClassPrimitive
		/// </remarks>
		internal virtual bool isStrongTyped()
		{
			return true;
		}

		internal virtual void marshall(com.db4o.YapObject a_yapObject, object a_object, com.db4o.YapWriter
			 a_bytes, bool a_new)
		{
			com.db4o.Config4Class config = configOrAncestorConfig();
			a_bytes.writeInt(i_fields.Length);
			for (int i = 0; i < i_fields.Length; i++)
			{
				object obj = i_fields[i].getOrCreate(a_bytes.getTransaction(), a_object);
				if (obj is com.db4o.Db4oTypeImpl)
				{
					obj = ((com.db4o.Db4oTypeImpl)obj).storedTo(a_bytes.getTransaction());
				}
				i_fields[i].marshall(a_yapObject, obj, a_bytes, config, a_new);
			}
			if (i_ancestor != null)
			{
				i_ancestor.marshall(a_yapObject, a_object, a_bytes, a_new);
			}
		}

		internal virtual void marshallNew(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, object a_object)
		{
			checkUpdateDepth(a_bytes);
			marshall(a_yapObject, a_object, a_bytes, true);
		}

		internal virtual void marshallUpdate(com.db4o.Transaction a_trans, int a_id, int 
			a_updateDepth, com.db4o.YapObject a_yapObject, object a_object)
		{
			int length = objectLength();
			com.db4o.YapWriter writer = new com.db4o.YapWriter(a_trans, length);
			writer.setUpdateDepth(a_updateDepth);
			checkUpdateDepth(writer);
			writer.useSlot(a_id, 0, length);
			writer.writeInt(getID());
			marshall(a_yapObject, a_object, writer, false);
			com.db4o.YapStream stream = a_trans.i_stream;
			stream.writeUpdate(this, writer);
			if (a_yapObject.isActive())
			{
				a_yapObject.setStateClean();
			}
			a_yapObject.endProcessing();
			dispatchEvent(stream, a_object, com.db4o.EventDispatcher.UPDATE);
		}

		internal virtual int memberLength()
		{
			int length = com.db4o.YapConst.YAPINT_LENGTH;
			if (i_ancestor != null)
			{
				length += i_ancestor.memberLength();
			}
			if (i_fields != null)
			{
				for (int i = 0; i < i_fields.Length; i++)
				{
					length += i_fields[i].linkLength();
				}
			}
			return length;
		}

		internal bool callConstructor()
		{
			i_dontCallConstructors = !callConstructor1();
			return !i_dontCallConstructors;
		}

		private bool callConstructor1()
		{
			int res = callConstructorSpecialized();
			if (res != com.db4o.YapConst.DEFAULT)
			{
				return res == com.db4o.YapConst.YES;
			}
			return (i_stream.i_config.i_callConstructors == com.db4o.YapConst.YES);
		}

		private int callConstructorSpecialized()
		{
			if (i_config != null)
			{
				int res = i_config.callConstructor();
				if (res != com.db4o.YapConst.DEFAULT)
				{
					return res;
				}
			}
			if (i_ancestor != null)
			{
				return i_ancestor.callConstructorSpecialized();
			}
			return com.db4o.YapConst.DEFAULT;
		}

		internal virtual int objectLength()
		{
			if (i_objectLength == 0)
			{
				i_objectLength = memberLength() + com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst
					.YAPID_LENGTH;
			}
			return i_objectLength;
		}

		internal override int ownLength()
		{
			int len = i_stream.stringIO().shortLength(getName()) + com.db4o.YapConst.OBJECT_LENGTH
				 + (com.db4o.YapConst.YAPINT_LENGTH * 2) + (com.db4o.YapConst.YAPID_LENGTH * 2);
			if (i_fields != null)
			{
				for (int i = 0; i < i_fields.Length; i++)
				{
					len += i_fields[i].ownLength(i_stream);
				}
			}
			return len;
		}

		public virtual com.db4o.reflect.ReflectClass primitiveClassReflector()
		{
			return null;
		}

		internal virtual void purge()
		{
			if (i_index != null)
			{
				if (!i_index.isDirty())
				{
					i_index.clear();
					i_index.setStateDeactivated();
				}
			}
		}

		public virtual object read(com.db4o.YapWriter a_bytes)
		{
			try
			{
				int id = a_bytes.readInt();
				int depth = a_bytes.getInstantiationDepth() - 1;
				com.db4o.Transaction trans = a_bytes.getTransaction();
				com.db4o.YapStream stream = trans.i_stream;
				if (a_bytes.getUpdateDepth() == com.db4o.YapConst.TRANSIENT)
				{
					return stream.peekPersisted1(trans, id, depth);
				}
				if (com.db4o.Platform.isValueType(classReflector()))
				{
					if (depth < 1)
					{
						depth = 1;
					}
					com.db4o.YapObject yo = stream.getYapObject(id);
					if (yo != null)
					{
						object obj = yo.getObject();
						if (obj == null)
						{
							stream.yapObjectGCd(yo);
						}
						else
						{
							yo.activate(trans, obj, depth, false);
							return yo.getObject();
						}
					}
					return new com.db4o.YapObject(id).read(trans, null, null, depth, com.db4o.YapConst
						.ADD_TO_ID_TREE, false);
				}
				else
				{
					object ret = stream.getByID2(trans, id);
					if (ret is com.db4o.Db4oTypeImpl)
					{
						depth = ((com.db4o.Db4oTypeImpl)ret).adjustReadDepth(depth);
					}
					stream.stillToActivate(ret, depth);
					return ret;
				}
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		public virtual object readQuery(com.db4o.Transaction a_trans, com.db4o.YapReader 
			a_reader, bool a_toArray)
		{
			try
			{
				return a_trans.i_stream.getByID2(a_trans, a_reader.readInt());
			}
			catch (System.Exception e)
			{
			}
			return null;
		}

		public virtual com.db4o.YapDataType readArrayWrapper(com.db4o.Transaction a_trans
			, com.db4o.YapReader[] a_bytes)
		{
			if (isArray())
			{
				return this;
			}
			return null;
		}

		public virtual com.db4o.YapDataType readArrayWrapper1(com.db4o.YapReader[] a_bytes
			)
		{
			if (isArray())
			{
				if (com.db4o.Platform.isCollectionTranslator(this.i_config))
				{
					a_bytes[0].incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
					return new com.db4o.YapArray(i_stream, null, false);
				}
				incrementFieldsOffset1(a_bytes[0]);
				if (i_ancestor != null)
				{
					return i_ancestor.readArrayWrapper1(a_bytes);
				}
			}
			return null;
		}

		public virtual void readCandidates(com.db4o.YapReader a_bytes, com.db4o.QCandidates
			 a_candidates)
		{
			int id = 0;
			int offset = a_bytes._offset;
			try
			{
				id = a_bytes.readInt();
			}
			catch (System.Exception e)
			{
			}
			a_bytes._offset = offset;
			if (id != 0)
			{
				com.db4o.Transaction trans = a_candidates.i_trans;
				object obj = trans.i_stream.getByID1(trans, id);
				if (obj != null)
				{
					int[] idgen = { -2 };
					a_candidates.i_trans.i_stream.activate1(trans, obj, 2);
					com.db4o.Platform.forEachCollectionElement(obj, new _AnonymousInnerClass1302(this
						, trans, idgen, a_candidates));
				}
			}
		}

		private sealed class _AnonymousInnerClass1302 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass1302(YapClass _enclosing, com.db4o.Transaction trans, 
				int[] idgen, com.db4o.QCandidates a_candidates)
			{
				this._enclosing = _enclosing;
				this.trans = trans;
				this.idgen = idgen;
				this.a_candidates = a_candidates;
			}

			public void visit(object elem)
			{
				int elemid = (int)trans.i_stream.getID(elem);
				if (elemid == 0)
				{
					elemid = idgen[0]--;
				}
				a_candidates.addByIdentity(new com.db4o.QCandidate(a_candidates, elem, elemid, true
					));
			}

			private readonly YapClass _enclosing;

			private readonly com.db4o.Transaction trans;

			private readonly int[] idgen;

			private readonly com.db4o.QCandidates a_candidates;
		}

		internal virtual int readFieldLength(com.db4o.YapReader a_bytes)
		{
			int length = a_bytes.readInt();
			if (length > i_fields.Length)
			{
				return i_fields.Length;
			}
			return length;
		}

		internal virtual int readFieldLengthSodaAtHome(com.db4o.YapReader a_bytes)
		{
			return 0;
		}

		public virtual object readIndexEntry(com.db4o.YapReader a_reader)
		{
			return System.Convert.ToInt32(a_reader.readInt());
		}

		public virtual object readIndexObject(com.db4o.YapWriter a_writer)
		{
			return readIndexEntry(a_writer);
		}

		internal virtual byte[] readName(com.db4o.Transaction a_trans)
		{
			i_reader = a_trans.i_stream.readReaderByID(a_trans, getID());
			if (i_reader != null)
			{
				return readName1(a_trans, i_reader);
			}
			return null;
		}

		internal virtual byte[] readName1(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader)
		{
			i_reader = a_reader;
			try
			{
				int len = a_reader.readInt();
				len = len * a_trans.i_stream.stringIO().bytesPerChar();
				i_nameBytes = new byte[len];
				j4o.lang.JavaSystem.arraycopy(a_reader._buffer, a_reader._offset, i_nameBytes, 0, 
					len);
				i_nameBytes = com.db4o.Platform.updateClassName(i_nameBytes);
				a_reader.incrementOffset(len + com.db4o.YapConst.YAPINT_LENGTH);
				setStateUnread();
				bitFalse(com.db4o.YapConst.CHECKED_CHANGES);
				bitFalse(com.db4o.YapConst.STATIC_FIELDS_STORED);
				return i_nameBytes;
			}
			catch (System.Exception t)
			{
				setStateDead();
			}
			return null;
		}

		internal virtual void readObjectHeader(com.db4o.YapReader a_reader, int a_objectID
			)
		{
			a_reader.incrementOffset(com.db4o.YapConst.YAPID_LENGTH);
		}

		internal virtual void readVirtualAttributes(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject)
		{
			int id = a_yapObject.getID();
			com.db4o.YapStream stream = a_trans.i_stream;
			com.db4o.YapReader reader = stream.readReaderByID(a_trans, id);
			readObjectHeader(reader, id);
			readVirtualAttributes1(a_trans, reader, a_yapObject);
		}

		private void readVirtualAttributes1(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader, com.db4o.YapObject a_yapObject)
		{
			int length = readFieldLength(a_reader);
			for (int i = 0; i < length; i++)
			{
				i_fields[i].readVirtualAttribute(a_trans, a_reader, a_yapObject);
			}
			if (i_ancestor != null)
			{
				i_ancestor.readVirtualAttributes1(a_trans, a_reader, a_yapObject);
			}
		}

		internal virtual com.db4o.reflect.generic.GenericReflector reflector()
		{
			return i_stream.reflector();
		}

		public virtual void rename(string newName)
		{
			if (!i_stream.isClient())
			{
				int tempState = i_state;
				setStateOK();
				i_name = newName;
				setStateDirty();
				write(i_stream, i_stream.getSystemTransaction());
				i_state = tempState;
			}
			else
			{
				com.db4o.Db4o.throwRuntimeException(58);
			}
		}

		internal virtual void createConfigAndConstructor(com.db4o.Hashtable4 a_byteHashTable
			, com.db4o.YapStream a_stream, com.db4o.reflect.ReflectClass a_class)
		{
			if (a_class == null)
			{
				if (i_nameBytes != null)
				{
					i_name = a_stream.stringIO().read(i_nameBytes);
				}
			}
			else
			{
				i_name = a_class.getName();
			}
			i_config = i_stream.i_config.configClass(i_name);
			if (a_class == null)
			{
				createConstructor(a_stream, i_name);
			}
			else
			{
				createConstructor(a_stream, a_class, i_name, true);
			}
			if (i_nameBytes != null)
			{
				a_byteHashTable.remove(i_nameBytes);
				i_nameBytes = null;
			}
		}

		internal virtual bool readThis()
		{
			if (stateUnread())
			{
				setStateOK();
				setStateClean();
				forceRead();
				return true;
			}
			return false;
		}

		internal virtual void forceRead()
		{
			if (i_reader != null && bitIsFalse(com.db4o.YapConst.READING))
			{
				bitTrue(com.db4o.YapConst.READING);
				i_ancestor = i_stream.getYapClass(i_reader.readInt());
				if (i_dontCallConstructors)
				{
					createConstructor(i_stream, classReflector(), i_name, true);
				}
				checkDb4oType();
				int indexID = i_reader.readInt();
				if (hasIndex())
				{
					i_index = i_stream.createClassIndex(this);
					if (indexID > 0)
					{
						i_index.setID(i_stream, indexID);
					}
					i_index.setStateDeactivated();
				}
				i_fields = new com.db4o.YapField[i_reader.readInt()];
				for (int i = 0; i < i_fields.Length; i++)
				{
					i_fields[i] = new com.db4o.YapField(this);
					i_fields[i].setArrayPosition(i);
				}
				for (int i = 0; i < i_fields.Length; i++)
				{
					i_fields[i] = i_fields[i].readThis(i_stream, i_reader);
				}
				for (int i = 0; i < i_fields.Length; i++)
				{
					i_fields[i].loadHandler(i_stream);
				}
				i_nameBytes = null;
				i_reader = null;
				bitFalse(com.db4o.YapConst.READING);
			}
		}

		private void checkDb4oType()
		{
			com.db4o.reflect.ReflectClass claxx = classReflector();
			if (claxx != null && i_stream.i_handlers.ICLASS_DB4OTYPEIMPL.isAssignableFrom(claxx
				))
			{
				try
				{
					i_db4oType = (com.db4o.Db4oTypeImpl)claxx.newInstance();
				}
				catch (System.Exception e)
				{
				}
			}
		}

		internal override void readThis(com.db4o.Transaction a_trans, com.db4o.YapReader 
			a_reader)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal virtual void refresh()
		{
			if (!stateUnread())
			{
				createConstructor(i_stream, i_name);
				bitFalse(com.db4o.YapConst.CHECKED_CHANGES);
				checkChanges();
				if (i_fields != null)
				{
					for (int i = 0; i < i_fields.Length; i++)
					{
						i_fields[i].refresh();
					}
				}
			}
		}

		internal virtual void removeFromIndex(com.db4o.Transaction ta, int id)
		{
			if (hasIndex())
			{
				ta.removeFromClassIndex(getID(), id);
			}
			if (i_ancestor != null)
			{
				i_ancestor.removeFromIndex(ta, id);
			}
		}

		internal virtual bool renameField(string a_from, string a_to)
		{
			bool renamed = false;
			for (int i = 0; i < i_fields.Length; i++)
			{
				if (i_fields[i].getName().Equals(a_to))
				{
					i_stream.logMsg(9, "class:" + getName() + " field:" + a_to);
					return false;
				}
			}
			for (int i = 0; i < i_fields.Length; i++)
			{
				if (i_fields[i].getName().Equals(a_from))
				{
					i_fields[i].setName(a_to);
					renamed = true;
				}
			}
			return renamed;
		}

		internal override void setID(com.db4o.YapStream a_stream, int a_id)
		{
			base.setID(a_stream, a_id);
		}

		internal virtual void setName(string a_name)
		{
			i_name = a_name;
		}

		private void setStateDead()
		{
			bitTrue(com.db4o.YapConst.DEAD);
			bitFalse(com.db4o.YapConst.CONTINUE);
		}

		private void setStateUnread()
		{
			bitFalse(com.db4o.YapConst.DEAD);
			bitTrue(com.db4o.YapConst.CONTINUE);
		}

		private void setStateOK()
		{
			bitFalse(com.db4o.YapConst.DEAD);
			bitFalse(com.db4o.YapConst.CONTINUE);
		}

		internal virtual bool stateDead()
		{
			return bitIsTrue(com.db4o.YapConst.DEAD);
		}

		private bool stateOK()
		{
			return bitIsFalse(com.db4o.YapConst.CONTINUE) && bitIsFalse(com.db4o.YapConst.DEAD
				) && bitIsFalse(com.db4o.YapConst.READING);
		}

		internal bool stateOKAndAncestors()
		{
			if (!stateOK() || i_fields == null)
			{
				return false;
			}
			if (i_ancestor != null)
			{
				return i_ancestor.stateOKAndAncestors();
			}
			return true;
		}

		internal virtual bool stateUnread()
		{
			return bitIsTrue(com.db4o.YapConst.CONTINUE) && bitIsFalse(com.db4o.YapConst.DEAD
				) && bitIsFalse(com.db4o.YapConst.READING);
		}

		internal virtual bool storeField(com.db4o.reflect.ReflectField a_field)
		{
			if (a_field.isStatic())
			{
				return false;
			}
			if (a_field.isTransient())
			{
				com.db4o.Config4Class config = configOrAncestorConfig();
				if (config == null)
				{
					return false;
				}
				if (!config.i_storeTransientFields)
				{
					return false;
				}
			}
			return com.db4o.Platform.canSetAccessible() || a_field.isPublic();
		}

		public virtual com.db4o.ext.StoredField storedField(string a_name, object a_type)
		{
			lock (i_stream.i_lock)
			{
				com.db4o.YapClass yc = i_stream.getYapClass(i_stream.i_config.reflectorFor(a_type
					), false);
				if (i_fields != null)
				{
					for (int i = 0; i < i_fields.Length; i++)
					{
						if (i_fields[i].getName().Equals(a_name))
						{
							if (yc == null || yc == i_fields[i].getFieldYapClass(i_stream))
							{
								return (i_fields[i]);
							}
						}
					}
				}
				return null;
			}
		}

		internal virtual void storeStaticFieldValues(com.db4o.Transaction trans, bool force
			)
		{
			if (!bitIsTrue(com.db4o.YapConst.STATIC_FIELDS_STORED) || force)
			{
				bitTrue(com.db4o.YapConst.STATIC_FIELDS_STORED);
				bool store = (i_config != null && i_config.i_persistStaticFieldValues) || com.db4o.Platform
					.storeStaticFieldValues(trans.reflector(), classReflector());
				if (store)
				{
					com.db4o.YapStream stream = trans.i_stream;
					stream.showInternalClasses(true);
					com.db4o.query.Query q = stream.querySharpenBug(trans);
					q.constrain(com.db4o.YapConst.CLASS_STATICCLASS);
					q.descend("name").constrain(i_name);
					com.db4o.StaticClass sc = new com.db4o.StaticClass();
					sc.name = i_name;
					com.db4o.ObjectSet os = q.execute();
					com.db4o.StaticField[] oldFields = null;
					if (os.size() > 0)
					{
						sc = (com.db4o.StaticClass)os.next();
						stream.activate1(trans, sc, 4);
						oldFields = sc.fields;
					}
					com.db4o.reflect.ReflectField[] fields = classReflector().getDeclaredFields();
					com.db4o.Collection4 newFields = new com.db4o.Collection4();
					for (int i = 0; i < fields.Length; i++)
					{
						if (fields[i].isStatic())
						{
							fields[i].setAccessible();
							string fieldName = fields[i].getName();
							object value = fields[i].get(null);
							bool handled = false;
							if (oldFields != null)
							{
								for (int j = 0; j < oldFields.Length; j++)
								{
									if (fieldName.Equals(oldFields[j].name))
									{
										if (oldFields[j].value != null && value != null && j4o.lang.Class.getClassForObject
											(oldFields[j].value) == j4o.lang.Class.getClassForObject(value))
										{
											long id = stream.getID1(trans, oldFields[j].value);
											if (id > 0)
											{
												if (oldFields[j].value != value)
												{
													stream.bind1(trans, value, id);
													stream.refresh(value, int.MaxValue);
													oldFields[j].value = value;
												}
												handled = true;
											}
										}
										if (!handled)
										{
											oldFields[j].value = value;
											if (!stream.isClient())
											{
												stream.setInternal(trans, oldFields[j], true);
											}
										}
										newFields.add(oldFields[j]);
										handled = true;
										break;
									}
								}
							}
							if (!handled)
							{
								newFields.add(new com.db4o.StaticField(fieldName, value));
							}
						}
					}
					if (newFields.size() > 0)
					{
						sc.fields = new com.db4o.StaticField[newFields.size()];
						newFields.toArray(sc.fields);
						if (!stream.isClient())
						{
							stream.setInternal(trans, sc, true);
						}
					}
					stream.showInternalClasses(false);
				}
			}
		}

		public virtual bool supportsIndex()
		{
			return true;
		}

		public override string ToString()
		{
			return i_name;
		}

		internal override bool writeObjectBegin()
		{
			if (!stateOK())
			{
				return false;
			}
			return base.writeObjectBegin();
		}

		public virtual void writeIndexEntry(com.db4o.YapWriter a_writer, object a_object)
		{
			a_writer.writeInt(((int)a_object));
		}

		public virtual void writeNew(object a_object, com.db4o.YapWriter a_bytes)
		{
			if (a_object == null)
			{
				a_bytes.writeInt(0);
				i_lastID = 0;
			}
			else
			{
				i_lastID = a_bytes.getStream().setInternal(a_bytes.getTransaction(), a_object, a_bytes
					.getUpdateDepth(), true);
				a_bytes.writeInt(i_lastID);
			}
		}

		internal override void writeThis(com.db4o.YapWriter a_writer)
		{
			a_writer.writeShortString(i_name);
			a_writer.writeInt(0);
			writeIDOf(i_ancestor, a_writer);
			writeIDOf(i_index, a_writer);
			if (i_fields == null)
			{
				a_writer.writeInt(0);
			}
			else
			{
				a_writer.writeInt(i_fields.Length);
				for (int i = 0; i < i_fields.Length; i++)
				{
					i_fields[i].writeThis(a_writer, this);
				}
			}
			com.db4o.YapClassCollection ycc = a_writer.i_trans.i_stream.i_classCollection;
			ycc.yapClassRequestsInitOnUp(this);
		}

		private com.db4o.reflect.ReflectClass i_compareTo;

		public virtual void prepareLastIoComparison(com.db4o.Transaction a_trans, object 
			obj)
		{
			prepareComparison(obj);
		}

		public virtual com.db4o.YapComparable prepareComparison(object obj)
		{
			if (obj != null)
			{
				if (obj is int)
				{
					i_lastID = ((int)obj);
				}
				else
				{
					i_lastID = (int)i_stream.getID(obj);
				}
				i_compareTo = reflector().forObject(obj);
			}
			else
			{
				i_compareTo = null;
			}
			return this;
		}

		public virtual int compareTo(object a_obj)
		{
			if (a_obj is int)
			{
				return ((int)a_obj) - i_lastID;
			}
			return -1;
		}

		public virtual bool isEqual(object obj)
		{
			if (obj == null)
			{
				return i_compareTo == null;
			}
			return i_compareTo.isAssignableFrom(reflector().forObject(obj));
		}

		public virtual bool isGreater(object obj)
		{
			return false;
		}

		public virtual bool isSmaller(object obj)
		{
			return false;
		}

		public virtual string toString(com.db4o.YapWriter writer, com.db4o.YapObject yapObject
			, int depth, int maxDepth)
		{
			int length = readFieldLength(writer);
			string str = "";
			for (int i = 0; i < length; i++)
			{
				str += i_fields[i].toString(writer, yapObject, depth + 1, maxDepth);
			}
			if (i_ancestor != null)
			{
				str += i_ancestor.toString(writer, yapObject, depth, maxDepth);
			}
			return str;
		}
	}
}

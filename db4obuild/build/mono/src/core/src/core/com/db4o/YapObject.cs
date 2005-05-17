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
	public sealed class YapObject : com.db4o.YapMeta, com.db4o.ext.ObjectInfo
	{
		private com.db4o.YapClass i_yapClass;

		internal object i_object;

		internal com.db4o.VirtualAttributes i_virtualAttributes;

		private com.db4o.YapObject id_preceding;

		private com.db4o.YapObject id_subsequent;

		private int id_size;

		private com.db4o.YapObject hc_preceding;

		private com.db4o.YapObject hc_subsequent;

		private int hc_size;

		private int hc_code;

		internal YapObject(int a_id)
		{
			i_id = a_id;
		}

		internal YapObject(com.db4o.YapClass a_yapClass, int a_id)
		{
			i_yapClass = a_yapClass;
			i_id = a_id;
		}

		internal void activate(com.db4o.Transaction ta, object a_object, int a_depth, bool
			 a_refresh)
		{
			activate1(ta, a_object, a_depth, a_refresh);
			ta.i_stream.activate3CheckStill(ta);
		}

		internal void activate1(com.db4o.Transaction ta, object a_object, int a_depth, bool
			 a_refresh)
		{
			if (a_object is com.db4o.Db4oTypeImpl)
			{
				a_depth = ((com.db4o.Db4oTypeImpl)a_object).adjustReadDepth(a_depth);
			}
			if (a_depth > 0)
			{
				com.db4o.YapStream stream = ta.i_stream;
				if (a_refresh)
				{
					if (stream.i_config.i_messageLevel > com.db4o.YapConst.ACTIVATION)
					{
						stream.message("" + getID() + " refresh " + i_yapClass.getName());
					}
				}
				else
				{
					if (isActive())
					{
						if (a_object != null)
						{
							if (a_depth > 1)
							{
								if (i_yapClass.i_config != null)
								{
									a_depth = i_yapClass.i_config.adjustActivationDepth(a_depth);
								}
								i_yapClass.activateFields(ta, a_object, a_depth);
							}
							return;
						}
					}
					if (stream.i_config.i_messageLevel > com.db4o.YapConst.ACTIVATION)
					{
						stream.message("" + getID() + " activate " + i_yapClass.getName());
					}
				}
				read(ta, null, a_object, a_depth, com.db4o.YapConst.ADD_MEMBERS_TO_ID_TREE_ONLY, 
					false);
			}
		}

		internal void addToIDTree(com.db4o.YapStream a_stream)
		{
			if (!(i_yapClass is com.db4o.YapClassPrimitive))
			{
				a_stream.idTreeAdd(this);
			}
		}

		/// <summary>return false if class not completely initialized, otherwise true *</summary>
		internal bool continueSet(com.db4o.Transaction a_trans, int a_updateDepth)
		{
			if (bitIsTrue(com.db4o.YapConst.CONTINUE))
			{
				if (!i_yapClass.stateOKAndAncestors())
				{
					return false;
				}
				com.db4o.YapStream stream = a_trans.i_stream;
				bitFalse(com.db4o.YapConst.CONTINUE);
				object obj = getObject();
				int id = getID();
				int length = ownLength();
				int address = -1;
				if (!stream.isClient())
				{
					address = ((com.db4o.YapFile)stream).getSlot(length);
				}
				a_trans.setPointer(id, address, length);
				com.db4o.YapWriter writer = new com.db4o.YapWriter(a_trans, length);
				writer.useSlot(id, address, length);
				writer.setUpdateDepth(a_updateDepth);
				writer.writeInt(i_yapClass.getID());
				i_yapClass.marshallNew(this, writer, obj);
				stream.writeNew(i_yapClass, writer);
				i_yapClass.dispatchEvent(stream, obj, com.db4o.EventDispatcher.NEW);
				i_object = stream.i_references.createYapRef(this, obj);
				setStateClean();
				endProcessing();
			}
			return true;
		}

		internal void deactivate(com.db4o.Transaction a_trans, int a_depth)
		{
			if (a_depth > 0)
			{
				object obj = getObject();
				if (obj != null)
				{
					if (obj is com.db4o.Db4oTypeImpl)
					{
						((com.db4o.Db4oTypeImpl)obj).preDeactivate();
					}
					com.db4o.YapStream stream = a_trans.i_stream;
					if (stream.i_config.i_messageLevel > com.db4o.YapConst.ACTIVATION)
					{
						stream.message("" + getID() + " deactivate " + i_yapClass.getName());
					}
					setStateDeactivated();
					i_yapClass.deactivate(a_trans, obj, a_depth);
				}
			}
		}

		internal override byte getIdentifier()
		{
			return com.db4o.YapConst.YAPOBJECT;
		}

		public object getObject()
		{
			if (com.db4o.Platform.hasWeakReferences())
			{
				return com.db4o.Platform.getYapRefObject(i_object);
			}
			return i_object;
		}

		private com.db4o.Transaction getTrans()
		{
			if (i_yapClass != null)
			{
				com.db4o.YapStream stream = i_yapClass.getStream();
				if (stream != null)
				{
					return stream.getTransaction();
				}
			}
			return null;
		}

		public com.db4o.ext.Db4oUUID getUUID()
		{
			com.db4o.VirtualAttributes va = virtualAttributes(getTrans());
			if (va != null && va.i_database != null)
			{
				return new com.db4o.ext.Db4oUUID(va.i_uuid, va.i_database.i_signature);
			}
			return null;
		}

		public long getVersion()
		{
			com.db4o.VirtualAttributes va = virtualAttributes(getTrans());
			if (va == null)
			{
				return 0;
			}
			return va.i_version;
		}

		internal com.db4o.YapClass getYapClass()
		{
			return i_yapClass;
		}

		internal override int ownLength()
		{
			return i_yapClass.objectLength();
		}

		internal object read(com.db4o.Transaction ta, com.db4o.YapWriter a_reader, object
			 a_object, int a_instantiationDepth, int addToIDTree, bool checkIDTree)
		{
			if (beginProcessing())
			{
				com.db4o.YapStream stream = ta.i_stream;
				if (a_reader == null)
				{
					a_reader = stream.readWriterByID(ta, getID());
				}
				if (a_reader != null)
				{
					i_yapClass = readYapClass(a_reader);
					if (i_yapClass == null)
					{
						return null;
					}
					if (checkIDTree)
					{
						com.db4o.YapObject classCreationSideEffect = stream.getYapObject(getID());
						if (classCreationSideEffect != null)
						{
							object obj = classCreationSideEffect.getObject();
							if (obj != null)
							{
								return obj;
							}
							stream.yapObjectGCd(classCreationSideEffect);
						}
					}
					a_reader.setInstantiationDepth(a_instantiationDepth);
					a_reader.setUpdateDepth(addToIDTree);
					if (addToIDTree == com.db4o.YapConst.TRANSIENT)
					{
						a_object = i_yapClass.instantiateTransient(this, a_object, a_reader);
					}
					else
					{
						a_object = i_yapClass.instantiate(this, a_object, a_reader, addToIDTree == com.db4o.YapConst
							.ADD_TO_ID_TREE);
					}
				}
				endProcessing();
			}
			return a_object;
		}

		internal object readPrefetch(com.db4o.YapStream a_stream, com.db4o.Transaction ta
			, com.db4o.YapWriter a_reader)
		{
			object readObject = null;
			if (beginProcessing())
			{
				i_yapClass = readYapClass(a_reader);
				if (i_yapClass == null)
				{
					return null;
				}
				a_reader.setInstantiationDepth(i_yapClass.configOrAncestorConfig() == null ? 1 : 
					0);
				readObject = i_yapClass.instantiate(this, getObject(), a_reader, true);
				endProcessing();
			}
			return readObject;
		}

		internal sealed override void readThis(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_bytes)
		{
		}

		private com.db4o.YapClass readYapClass(com.db4o.YapWriter a_reader)
		{
			return a_reader.getStream().getYapClass(a_reader.readInt());
		}

		internal override void setID(com.db4o.YapStream a_stream, int a_id)
		{
			i_id = a_id;
		}

		internal void setObjectWeak(com.db4o.YapStream a_stream, object a_object)
		{
			if (a_stream.i_references._weak)
			{
				if (i_object != null)
				{
					com.db4o.Platform.killYapRef(i_object);
				}
				i_object = com.db4o.Platform.createYapRef(a_stream.i_references._queue, this, a_object
					);
			}
			else
			{
				i_object = a_object;
			}
		}

		internal void setObject(object a_object)
		{
			i_object = a_object;
		}

		internal void setStateOnRead(com.db4o.YapWriter reader)
		{
		}

		/// <summary>
		/// return true for complex objects to instruct YapStream to add to lookup trees
		/// and to perform delayed storage through call to continueset further up the stack.
		/// </summary>
		/// <remarks>
		/// return true for complex objects to instruct YapStream to add to lookup trees
		/// and to perform delayed storage through call to continueset further up the stack.
		/// </remarks>
		internal bool store(com.db4o.Transaction a_trans, com.db4o.YapClass a_yapClass, object
			 a_object, int a_updateDepth)
		{
			i_object = a_object;
			writeObjectBegin();
			com.db4o.YapStream stream = a_trans.i_stream;
			i_yapClass = a_yapClass;
			if (i_yapClass.getID() != com.db4o.YapHandlers.ANY_ID)
			{
				setID(stream, stream.newUserObject());
				beginProcessing();
				bitTrue(com.db4o.YapConst.CONTINUE);
				if (!(i_yapClass is com.db4o.YapClassPrimitive))
				{
					return true;
				}
				else
				{
					continueSet(a_trans, a_updateDepth);
				}
			}
			return false;
		}

		internal com.db4o.VirtualAttributes virtualAttributes(com.db4o.Transaction a_trans
			)
		{
			if (i_virtualAttributes == null && i_yapClass.hasVirtualAttributes())
			{
				if (a_trans != null)
				{
					i_virtualAttributes = new com.db4o.VirtualAttributes();
					i_yapClass.readVirtualAttributes(a_trans, this);
				}
			}
			return i_virtualAttributes;
		}

		internal override void writeThis(com.db4o.YapWriter a_writer)
		{
		}

		internal void writeUpdate(com.db4o.Transaction a_trans, int a_updatedepth)
		{
			continueSet(a_trans, a_updatedepth);
			if (beginProcessing())
			{
				object obj = getObject();
				if (i_yapClass.dispatchEvent(a_trans.i_stream, obj, com.db4o.EventDispatcher.CAN_UPDATE
					))
				{
					if ((!isActive()) || obj == null)
					{
						endProcessing();
						return;
					}
					if (a_trans.i_stream.i_config.i_messageLevel > com.db4o.YapConst.STATE)
					{
						a_trans.i_stream.message("" + getID() + " update " + i_yapClass.getName());
					}
					setStateClean();
					a_trans.writeUpdateDeleteMembers(getID(), i_yapClass, a_trans.i_stream.i_handlers
						.arrayType(obj), 0);
					i_yapClass.marshallUpdate(a_trans, getID(), a_updatedepth, this, obj);
				}
				else
				{
					endProcessing();
				}
			}
		}

		/// <summary>HCTREE ****</summary>
		internal com.db4o.YapObject hc_add(com.db4o.YapObject a_add)
		{
			object obj = a_add.getObject();
			if (obj != null)
			{
				a_add.hc_preceding = null;
				a_add.hc_subsequent = null;
				a_add.hc_size = 1;
				a_add.hc_code = hc_getCode(obj);
				return hc_add1(a_add);
			}
			else
			{
				return this;
			}
		}

		private com.db4o.YapObject hc_add1(com.db4o.YapObject a_new)
		{
			int cmp = hc_compare(a_new);
			if (cmp < 0)
			{
				if (hc_preceding == null)
				{
					hc_preceding = a_new;
					hc_size++;
				}
				else
				{
					hc_preceding = hc_preceding.hc_add1(a_new);
					if (hc_subsequent == null)
					{
						return hc_rotateRight();
					}
					else
					{
						return hc_balance();
					}
				}
			}
			else
			{
				if (hc_subsequent == null)
				{
					hc_subsequent = a_new;
					hc_size++;
				}
				else
				{
					hc_subsequent = hc_subsequent.hc_add1(a_new);
					if (hc_preceding == null)
					{
						return hc_rotateLeft();
					}
					else
					{
						return hc_balance();
					}
				}
			}
			return this;
		}

		private com.db4o.YapObject hc_balance()
		{
			int cmp = hc_subsequent.hc_size - hc_preceding.hc_size;
			if (cmp < -2)
			{
				return hc_rotateRight();
			}
			else
			{
				if (cmp > 2)
				{
					return hc_rotateLeft();
				}
				else
				{
					hc_size = hc_preceding.hc_size + hc_subsequent.hc_size + 1;
					return this;
				}
			}
		}

		private void hc_calculateSize()
		{
			if (hc_preceding == null)
			{
				if (hc_subsequent == null)
				{
					hc_size = 1;
				}
				else
				{
					hc_size = hc_subsequent.hc_size + 1;
				}
			}
			else
			{
				if (hc_subsequent == null)
				{
					hc_size = hc_preceding.hc_size + 1;
				}
				else
				{
					hc_size = hc_preceding.hc_size + hc_subsequent.hc_size + 1;
				}
			}
		}

		private int hc_compare(com.db4o.YapObject a_to)
		{
			int cmp = a_to.hc_code - hc_code;
			if (cmp == 0)
			{
				cmp = a_to.i_id - i_id;
			}
			return cmp;
		}

		internal com.db4o.YapObject hc_find(object obj)
		{
			return hc_find(hc_getCode(obj), obj);
		}

		private com.db4o.YapObject hc_find(int a_id, object obj)
		{
			int cmp = a_id - hc_code;
			if (cmp < 0)
			{
				if (hc_preceding != null)
				{
					return hc_preceding.hc_find(a_id, obj);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (hc_subsequent != null)
					{
						return hc_subsequent.hc_find(a_id, obj);
					}
				}
				else
				{
					if (obj == getObject())
					{
						return this;
					}
					if (hc_preceding != null)
					{
						com.db4o.YapObject inPreceding = hc_preceding.hc_find(a_id, obj);
						if (inPreceding != null)
						{
							return inPreceding;
						}
					}
					if (hc_subsequent != null)
					{
						return hc_subsequent.hc_find(a_id, obj);
					}
				}
			}
			return null;
		}

		private int hc_getCode(object obj)
		{
			int hcode = j4o.lang.JavaSystem.identityHashCode(obj);
			if (hcode < 0)
			{
				hcode = ~hcode;
			}
			return hcode;
		}

		private com.db4o.YapObject hc_rotateLeft()
		{
			com.db4o.YapObject tree = hc_subsequent;
			hc_subsequent = tree.hc_preceding;
			hc_calculateSize();
			tree.hc_preceding = this;
			if (tree.hc_subsequent == null)
			{
				tree.hc_size = 1 + hc_size;
			}
			else
			{
				tree.hc_size = 1 + hc_size + tree.hc_subsequent.hc_size;
			}
			return tree;
		}

		private com.db4o.YapObject hc_rotateRight()
		{
			com.db4o.YapObject tree = hc_preceding;
			hc_preceding = tree.hc_subsequent;
			hc_calculateSize();
			tree.hc_subsequent = this;
			if (tree.hc_preceding == null)
			{
				tree.hc_size = 1 + hc_size;
			}
			else
			{
				tree.hc_size = 1 + hc_size + tree.hc_preceding.hc_size;
			}
			return tree;
		}

		private com.db4o.YapObject hc_rotateSmallestUp()
		{
			if (hc_preceding != null)
			{
				hc_preceding = hc_preceding.hc_rotateSmallestUp();
				return hc_rotateRight();
			}
			return this;
		}

		internal com.db4o.YapObject hc_remove(com.db4o.YapObject a_find)
		{
			if (this == a_find)
			{
				return hc_remove();
			}
			int cmp = hc_compare(a_find);
			if (cmp <= 0)
			{
				if (hc_preceding != null)
				{
					hc_preceding = hc_preceding.hc_remove(a_find);
				}
			}
			if (cmp >= 0)
			{
				if (hc_subsequent != null)
				{
					hc_subsequent = hc_subsequent.hc_remove(a_find);
				}
			}
			hc_calculateSize();
			return this;
		}

		private com.db4o.YapObject hc_remove()
		{
			if (hc_subsequent != null && hc_preceding != null)
			{
				hc_subsequent = hc_subsequent.hc_rotateSmallestUp();
				hc_subsequent.hc_preceding = hc_preceding;
				hc_subsequent.hc_calculateSize();
				return hc_subsequent;
			}
			if (hc_subsequent != null)
			{
				return hc_subsequent;
			}
			return hc_preceding;
		}

		/// <summary>IDTREE ****</summary>
		internal com.db4o.YapObject id_add(com.db4o.YapObject a_add)
		{
			a_add.id_preceding = null;
			a_add.id_subsequent = null;
			a_add.id_size = 1;
			return id_add1(a_add);
		}

		private com.db4o.YapObject id_add1(com.db4o.YapObject a_new)
		{
			int cmp = a_new.i_id - i_id;
			if (cmp < 0)
			{
				if (id_preceding == null)
				{
					id_preceding = a_new;
					id_size++;
				}
				else
				{
					id_preceding = id_preceding.id_add1(a_new);
					if (id_subsequent == null)
					{
						return id_rotateRight();
					}
					else
					{
						return id_balance();
					}
				}
			}
			else
			{
				if (id_subsequent == null)
				{
					id_subsequent = a_new;
					id_size++;
				}
				else
				{
					id_subsequent = id_subsequent.id_add1(a_new);
					if (id_preceding == null)
					{
						return id_rotateLeft();
					}
					else
					{
						return id_balance();
					}
				}
			}
			return this;
		}

		private com.db4o.YapObject id_balance()
		{
			int cmp = id_subsequent.id_size - id_preceding.id_size;
			if (cmp < -2)
			{
				return id_rotateRight();
			}
			else
			{
				if (cmp > 2)
				{
					return id_rotateLeft();
				}
				else
				{
					id_size = id_preceding.id_size + id_subsequent.id_size + 1;
					return this;
				}
			}
		}

		private void id_calculateSize()
		{
			if (id_preceding == null)
			{
				if (id_subsequent == null)
				{
					id_size = 1;
				}
				else
				{
					id_size = id_subsequent.id_size + 1;
				}
			}
			else
			{
				if (id_subsequent == null)
				{
					id_size = id_preceding.id_size + 1;
				}
				else
				{
					id_size = id_preceding.id_size + id_subsequent.id_size + 1;
				}
			}
		}

		internal com.db4o.YapObject id_find(int a_id)
		{
			int cmp = a_id - i_id;
			if (cmp > 0)
			{
				if (id_subsequent != null)
				{
					return id_subsequent.id_find(a_id);
				}
			}
			else
			{
				if (cmp < 0)
				{
					if (id_preceding != null)
					{
						return id_preceding.id_find(a_id);
					}
				}
				else
				{
					return this;
				}
			}
			return null;
		}

		private com.db4o.YapObject id_rotateLeft()
		{
			com.db4o.YapObject tree = id_subsequent;
			id_subsequent = tree.id_preceding;
			id_calculateSize();
			tree.id_preceding = this;
			if (tree.id_subsequent == null)
			{
				tree.id_size = id_size + 1;
			}
			else
			{
				tree.id_size = id_size + 1 + tree.id_subsequent.id_size;
			}
			return tree;
		}

		private com.db4o.YapObject id_rotateRight()
		{
			com.db4o.YapObject tree = id_preceding;
			id_preceding = tree.id_subsequent;
			id_calculateSize();
			tree.id_subsequent = this;
			if (tree.id_preceding == null)
			{
				tree.id_size = id_size + 1;
			}
			else
			{
				tree.id_size = id_size + 1 + tree.id_preceding.id_size;
			}
			return tree;
		}

		private com.db4o.YapObject id_rotateSmallestUp()
		{
			if (id_preceding != null)
			{
				id_preceding = id_preceding.id_rotateSmallestUp();
				return id_rotateRight();
			}
			return this;
		}

		internal com.db4o.YapObject id_remove(int a_id)
		{
			int cmp = a_id - i_id;
			if (cmp < 0)
			{
				if (id_preceding != null)
				{
					id_preceding = id_preceding.id_remove(a_id);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (id_subsequent != null)
					{
						id_subsequent = id_subsequent.id_remove(a_id);
					}
				}
				else
				{
					return id_remove();
				}
			}
			id_calculateSize();
			return this;
		}

		private com.db4o.YapObject id_remove()
		{
			if (id_subsequent != null && id_preceding != null)
			{
				id_subsequent = id_subsequent.id_rotateSmallestUp();
				id_subsequent.id_preceding = id_preceding;
				id_subsequent.id_calculateSize();
				return id_subsequent;
			}
			if (id_subsequent != null)
			{
				return id_subsequent;
			}
			return id_preceding;
		}

		public override string ToString()
		{
			try
			{
				int id = getID();
				string str = "YapObject\nID=" + id;
				if (i_yapClass != null)
				{
					com.db4o.YapStream stream = i_yapClass.getStream();
					if (stream != null && id > 0)
					{
						com.db4o.YapWriter writer = stream.readWriterByID(stream.getTransaction(), id);
						if (writer != null)
						{
							str += "\nAddress=" + writer.getAddress();
						}
						com.db4o.YapClass yc = readYapClass(writer);
						if (yc != i_yapClass)
						{
							str += "\nYapClass corruption";
						}
						else
						{
							str += yc.toString(writer, this, 0, 5);
						}
					}
				}
				object obj = getObject();
				if (obj == null)
				{
					str += "\nfor [null]";
				}
				else
				{
					string objToString = "";
					try
					{
						objToString = obj.ToString();
					}
					catch (System.Exception e)
					{
					}
					com.db4o.reflect.ReflectClass claxx = getYapClass().reflector().forObject(obj);
					str += "\n" + claxx.getName() + "\n" + objToString;
				}
				return str;
			}
			catch (System.Exception e)
			{
			}
			return "Exception in YapObject analyzer";
		}
	}
}

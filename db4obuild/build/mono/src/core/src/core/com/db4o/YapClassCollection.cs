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
	public sealed class YapClassCollection : com.db4o.YapMeta, com.db4o.UseSystemTransaction
	{
		private com.db4o.YapClass i_addingMembersTo;

		private com.db4o.Collection4 i_classes;

		private com.db4o.Hashtable4 i_creating;

		private readonly com.db4o.YapStream i_stream;

		private readonly com.db4o.Transaction i_systemTrans;

		private com.db4o.Hashtable4 i_yapClassByBytes;

		private com.db4o.Hashtable4 i_yapClassByClass;

		private com.db4o.Hashtable4 i_yapClassByID;

		private int i_yapClassCreationDepth;

		private com.db4o.Queue4 i_initYapClassesOnUp;

		internal YapClassCollection(com.db4o.Transaction a_trans)
		{
			i_systemTrans = a_trans;
			i_stream = a_trans.i_stream;
			i_initYapClassesOnUp = new com.db4o.Queue4();
		}

		internal void addYapClass(com.db4o.YapClass yapClass)
		{
			i_stream.setDirty(this);
			i_classes.add(yapClass);
			if (yapClass.stateUnread())
			{
				i_yapClassByBytes.put(yapClass.i_nameBytes, yapClass);
			}
			else
			{
				i_yapClassByClass.put(yapClass.classReflector(), yapClass);
			}
			if (yapClass.getID() == 0)
			{
				yapClass.write(i_stream, i_systemTrans);
			}
			i_yapClassByID.put(yapClass.getID(), yapClass);
		}

		private byte[] asBytes(string str)
		{
			return i_stream.stringIO().write(str);
		}

		internal void checkChanges()
		{
			com.db4o.Iterator4 i = i_classes.iterator();
			while (i.hasNext())
			{
				((com.db4o.YapClass)i.next()).checkChanges();
			}
		}

		/// <summary>We always work from parent to child.</summary>
		/// <remarks>
		/// We always work from parent to child. If the Child is a member on the
		/// parent, we have a circular dependancy problem. This method takes care.
		/// </remarks>
		private void classAddMembers(com.db4o.YapClass yapClass)
		{
			if (i_addingMembersTo != null)
			{
				i_addingMembersTo.addMembersAddDependancy(yapClass);
				return;
			}
			com.db4o.YapClass ancestor = yapClass.getAncestor();
			if (ancestor != null)
			{
				classAddMembers(ancestor);
			}
			i_addingMembersTo = yapClass;
			yapClass.addMembers(i_stream);
			yapClass.storeStaticFieldValues(i_systemTrans, true);
			i_addingMembersTo = null;
			com.db4o.YapClass[] dependancies = yapClass.getMembersDependancies();
			for (int i = 0; i < dependancies.Length; i++)
			{
				classAddMembers(dependancies[i]);
			}
			yapClass.setStateDirty();
			yapClass.write(i_stream, i_stream.getSystemTransaction());
			for (int i = 0; i < dependancies.Length; i++)
			{
				dependancies[i].setStateDirty();
				dependancies[i].write(i_stream, i_stream.getSystemTransaction());
			}
		}

		internal bool createYapClass(com.db4o.YapClass a_yapClass, com.db4o.reflect.ReflectClass
			 a_class)
		{
			i_yapClassCreationDepth++;
			com.db4o.reflect.ReflectClass superClass = a_class.getSuperclass();
			com.db4o.YapClass superYapClass = null;
			if (superClass != null && superClass != i_stream.i_handlers.ICLASS_OBJECT)
			{
				superYapClass = getYapClass(superClass, true);
			}
			bool ret = i_stream.createYapClass(a_yapClass, a_class, superYapClass);
			i_yapClassCreationDepth--;
			initYapClassesOnUp();
			return ret;
		}

		internal bool fieldExists(string a_field)
		{
			com.db4o.YapClassCollectionIterator i = iterator();
			while (i.hasNext())
			{
				if (i.nextClass().getYapField(a_field) != null)
				{
					return true;
				}
			}
			return false;
		}

		internal com.db4o.Collection4 forInterface(com.db4o.reflect.ReflectClass claxx)
		{
			com.db4o.Collection4 col = new com.db4o.Collection4();
			com.db4o.YapClassCollectionIterator i = iterator();
			while (i.hasNext())
			{
				com.db4o.YapClass yc = i.nextClass();
				com.db4o.reflect.ReflectClass candidate = yc.classReflector();
				if (!candidate.isInterface())
				{
					if (claxx.isAssignableFrom(candidate))
					{
						col.add(yc);
						com.db4o.Iterator4 j = col.iterator();
						while (j.hasNext())
						{
							com.db4o.YapClass existing = (com.db4o.YapClass)j.next();
							if (existing != yc)
							{
								com.db4o.YapClass higher = yc.getHigherHierarchy(existing);
								if (higher != null)
								{
									if (higher == yc)
									{
										col.remove(existing);
									}
									else
									{
										col.remove(yc);
									}
								}
							}
						}
					}
				}
			}
			return col;
		}

		internal override byte getIdentifier()
		{
			return com.db4o.YapConst.YAPCLASSCOLLECTION;
		}

		internal com.db4o.YapClass getYapClass(com.db4o.reflect.ReflectClass a_class, bool
			 a_create)
		{
			com.db4o.YapClass yapClass = (com.db4o.YapClass)i_yapClassByClass.get(a_class);
			if (yapClass == null)
			{
				yapClass = (com.db4o.YapClass)i_yapClassByBytes.remove(asBytes(a_class.getName())
					);
				readYapClass(yapClass, a_class);
			}
			if (yapClass != null || (!a_create))
			{
				return yapClass;
			}
			yapClass = (com.db4o.YapClass)i_creating.get(a_class);
			if (yapClass != null)
			{
				return yapClass;
			}
			yapClass = new com.db4o.YapClass(i_stream, a_class);
			i_creating.put(a_class, yapClass);
			if (!createYapClass(yapClass, a_class))
			{
				i_creating.remove(a_class);
				return null;
			}
			bool addMembers = false;
			if (i_yapClassByClass.get(a_class) == null)
			{
				addYapClass(yapClass);
				addMembers = true;
			}
			int id = yapClass.getID();
			if (id == 0)
			{
				yapClass.write(i_stream, i_stream.getSystemTransaction());
				id = yapClass.getID();
			}
			if (i_yapClassByID.get(id) == null)
			{
				i_yapClassByID.put(id, yapClass);
				addMembers = true;
			}
			if (addMembers || yapClass.i_fields == null)
			{
				classAddMembers(yapClass);
			}
			i_creating.remove(a_class);
			return yapClass;
		}

		internal com.db4o.YapClass getYapClass(int a_id)
		{
			return readYapClass((com.db4o.YapClass)i_yapClassByID.get(a_id), null);
		}

		public com.db4o.YapClass getYapClass(string a_name)
		{
			com.db4o.YapClass yapClass = (com.db4o.YapClass)i_yapClassByBytes.remove(asBytes(
				a_name));
			readYapClass(yapClass, null);
			if (yapClass == null)
			{
				com.db4o.YapClassCollectionIterator i = iterator();
				while (i.hasNext())
				{
					yapClass = i.nextClass();
					if (a_name.Equals(yapClass.getName()))
					{
						return yapClass;
					}
				}
				return null;
			}
			return yapClass;
		}

		public int getYapClassID(string name)
		{
			com.db4o.YapClass yc = (com.db4o.YapClass)i_yapClassByBytes.get(asBytes(name));
			if (yc != null)
			{
				return yc.getID();
			}
			return 0;
		}

		internal void initOnUp(com.db4o.Transaction systemTrans)
		{
			i_yapClassCreationDepth++;
			systemTrans.i_stream.showInternalClasses(true);
			com.db4o.Iterator4 i = i_classes.iterator();
			while (i.hasNext())
			{
				((com.db4o.YapClass)i.next()).initOnUp(systemTrans);
			}
			systemTrans.i_stream.showInternalClasses(false);
			i_yapClassCreationDepth--;
			initYapClassesOnUp();
		}

		internal void initTables(int a_size)
		{
			i_classes = new com.db4o.Collection4();
			i_yapClassByBytes = new com.db4o.Hashtable4(a_size);
			if (a_size < 16)
			{
				a_size = 16;
			}
			i_yapClassByClass = new com.db4o.Hashtable4(a_size);
			i_yapClassByID = new com.db4o.Hashtable4(a_size);
			i_creating = new com.db4o.Hashtable4(1);
		}

		private void initYapClassesOnUp()
		{
			if (i_yapClassCreationDepth == 0)
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i_initYapClassesOnUp.next();
				while (yc != null)
				{
					yc.initOnUp(i_systemTrans);
					yc = (com.db4o.YapClass)i_initYapClassesOnUp.next();
				}
			}
		}

		internal com.db4o.YapClassCollectionIterator iterator()
		{
			return new com.db4o.YapClassCollectionIterator(this, i_classes.i_first);
		}

		internal override int ownLength()
		{
			return com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst.YAPINT_LENGTH + (i_classes
				.size() * com.db4o.YapConst.YAPID_LENGTH);
		}

		internal void purge()
		{
			com.db4o.Iterator4 i = i_classes.iterator();
			while (i.hasNext())
			{
				((com.db4o.YapClass)i.next()).purge();
			}
		}

		internal sealed override void readThis(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader)
		{
			int classCount = a_reader.readInt();
			initTables(classCount);
			for (int i = classCount; i > 0; i--)
			{
				com.db4o.YapClass yapClass = new com.db4o.YapClass(i_stream, null);
				int id = a_reader.readInt();
				yapClass.setID(i_stream, id);
				i_classes.add(yapClass);
				i_yapClassByID.put(id, yapClass);
				i_yapClassByBytes.put(yapClass.readName(a_trans), yapClass);
			}
		}

		internal com.db4o.YapClass readYapClass(com.db4o.YapClass yapClass, com.db4o.reflect.ReflectClass
			 a_class)
		{
			i_yapClassCreationDepth++;
			if (yapClass != null && yapClass.stateUnread())
			{
				yapClass.createConfigAndConstructor(i_yapClassByBytes, i_stream, a_class);
				com.db4o.reflect.ReflectClass claxx = yapClass.classReflector();
				if (claxx != null)
				{
					i_yapClassByClass.put(claxx, yapClass);
					yapClass.readThis();
					yapClass.checkChanges();
					i_initYapClassesOnUp.add(yapClass);
				}
			}
			i_yapClassCreationDepth--;
			initYapClassesOnUp();
			return yapClass;
		}

		internal void refreshClasses()
		{
			com.db4o.YapClassCollection rereader = new com.db4o.YapClassCollection(i_systemTrans
				);
			rereader.i_id = i_id;
			rereader.read(i_stream.getSystemTransaction());
			com.db4o.Iterator4 i = rereader.i_classes.iterator();
			while (i.hasNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i.next();
				if (i_yapClassByID.get(yc.getID()) == null)
				{
					i_classes.add(yc);
					i_yapClassByID.put(yc.getID(), yc);
					if (yc.stateUnread())
					{
						i_yapClassByBytes.put(yc.readName(i_systemTrans), yc);
					}
					else
					{
						i_yapClassByClass.put(yc.classReflector(), yc);
					}
				}
			}
			i = i_classes.iterator();
			while (i.hasNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i.next();
				yc.refresh();
			}
		}

		internal void reReadYapClass(com.db4o.YapClass yapClass)
		{
			if (yapClass != null)
			{
				reReadYapClass(yapClass.i_ancestor);
				yapClass.readName(i_systemTrans);
				yapClass.forceRead();
				yapClass.setStateClean();
				yapClass.bitFalse(com.db4o.YapConst.CHECKED_CHANGES);
				yapClass.bitFalse(com.db4o.YapConst.READING);
				yapClass.bitFalse(com.db4o.YapConst.CONTINUE);
				yapClass.bitFalse(com.db4o.YapConst.DEAD);
				yapClass.checkChanges();
			}
		}

		public com.db4o.ext.StoredClass[] storedClasses()
		{
			com.db4o.Collection4 classes = new com.db4o.Collection4();
			com.db4o.Iterator4 i = i_classes.iterator();
			while (i.hasNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i.next();
				readYapClass(yc, null);
				if (yc.classReflector() == null)
				{
					yc.forceRead();
				}
				classes.add(yc);
			}
			com.db4o.ext.StoredClass[] sclasses = new com.db4o.ext.StoredClass[classes.size()
				];
			classes.toArray(sclasses);
			return sclasses;
		}

		internal override void writeThis(com.db4o.YapWriter a_writer)
		{
			a_writer.writeInt(i_classes.size());
			com.db4o.Iterator4 i = i_classes.iterator();
			while (i.hasNext())
			{
				writeIDOf((com.db4o.YapClass)i.next(), a_writer);
			}
		}

		internal void yapClassRequestsInitOnUp(com.db4o.YapClass a_yc)
		{
			if (i_yapClassCreationDepth == 0)
			{
				a_yc.initOnUp(i_systemTrans);
			}
			else
			{
				i_initYapClassesOnUp.add(a_yc);
			}
		}

		internal void yapFields(string a_field, com.db4o.Visitor4 a_visitor)
		{
			com.db4o.YapClassCollectionIterator i = iterator();
			while (i.hasNext())
			{
				com.db4o.YapClass yc = i.nextClass();
				yc.forEachYapField(new _AnonymousInnerClass396(this, a_field, a_visitor, yc));
			}
		}

		private sealed class _AnonymousInnerClass396 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass396(YapClassCollection _enclosing, string a_field, com.db4o.Visitor4
				 a_visitor, com.db4o.YapClass yc)
			{
				this._enclosing = _enclosing;
				this.a_field = a_field;
				this.a_visitor = a_visitor;
				this.yc = yc;
			}

			public void visit(object obj)
			{
				com.db4o.YapField yf = (com.db4o.YapField)obj;
				if (yf.alive() && a_field.Equals(yf.getName()))
				{
					a_visitor.visit(new object[] { yc, yf });
				}
			}

			private readonly YapClassCollection _enclosing;

			private readonly string a_field;

			private readonly com.db4o.Visitor4 a_visitor;

			private readonly com.db4o.YapClass yc;
		}
	}
}

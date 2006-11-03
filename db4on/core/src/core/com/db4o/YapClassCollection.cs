namespace com.db4o
{
	/// <exclude></exclude>
	public sealed class YapClassCollection : com.db4o.YapMeta
	{
		private com.db4o.foundation.Collection4 i_classes;

		private com.db4o.foundation.Hashtable4 i_creating;

		private readonly com.db4o.Transaction _systemTransaction;

		private com.db4o.foundation.Hashtable4 i_yapClassByBytes;

		private com.db4o.foundation.Hashtable4 i_yapClassByClass;

		private com.db4o.foundation.Hashtable4 i_yapClassByID;

		private int i_yapClassCreationDepth;

		private com.db4o.foundation.Queue4 i_initYapClassesOnUp;

		private readonly com.db4o.PendingClassInits _classInits;

		internal YapClassCollection(com.db4o.Transaction systemTransaction)
		{
			_systemTransaction = systemTransaction;
			i_initYapClassesOnUp = new com.db4o.foundation.Queue4();
			_classInits = new com.db4o.PendingClassInits(_systemTransaction);
		}

		public void AddYapClass(com.db4o.YapClass yapClass)
		{
			Stream().SetDirtyInSystemTransaction(this);
			i_classes.Add(yapClass);
			if (yapClass.StateUnread())
			{
				i_yapClassByBytes.Put(yapClass.i_nameBytes, yapClass);
			}
			else
			{
				i_yapClassByClass.Put(yapClass.ClassReflector(), yapClass);
			}
			if (yapClass.GetID() == 0)
			{
				yapClass.Write(_systemTransaction);
			}
			i_yapClassByID.Put(yapClass.GetID(), yapClass);
		}

		private byte[] AsBytes(string str)
		{
			return Stream().StringIO().Write(str);
		}

		internal void AttachQueryNode(string fieldName, com.db4o.foundation.Visitor4 a_visitor
			)
		{
			com.db4o.YapClassCollectionIterator i = Iterator();
			while (i.MoveNext())
			{
				com.db4o.YapClass yc = i.CurrentClass();
				if (!yc.IsInternal())
				{
					yc.ForEachYapField(new _AnonymousInnerClass59(this, fieldName, a_visitor, yc));
				}
			}
		}

		private sealed class _AnonymousInnerClass59 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass59(YapClassCollection _enclosing, string fieldName, com.db4o.foundation.Visitor4
				 a_visitor, com.db4o.YapClass yc)
			{
				this._enclosing = _enclosing;
				this.fieldName = fieldName;
				this.a_visitor = a_visitor;
				this.yc = yc;
			}

			public void Visit(object obj)
			{
				com.db4o.YapField yf = (com.db4o.YapField)obj;
				if (yf.CanAddToQuery(fieldName))
				{
					a_visitor.Visit(new object[] { yc, yf });
				}
			}

			private readonly YapClassCollection _enclosing;

			private readonly string fieldName;

			private readonly com.db4o.foundation.Visitor4 a_visitor;

			private readonly com.db4o.YapClass yc;
		}

		internal void CheckChanges()
		{
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.YapClass)i.Current).CheckChanges();
			}
		}

		internal bool CreateYapClass(com.db4o.YapClass a_yapClass, com.db4o.reflect.ReflectClass
			 a_class)
		{
			i_yapClassCreationDepth++;
			com.db4o.reflect.ReflectClass superClass = a_class.GetSuperclass();
			com.db4o.YapClass superYapClass = null;
			if (superClass != null && !superClass.Equals(Stream().i_handlers.ICLASS_OBJECT))
			{
				superYapClass = GetYapClass(superClass, true);
			}
			bool ret = Stream().CreateYapClass(a_yapClass, a_class, superYapClass);
			i_yapClassCreationDepth--;
			InitYapClassesOnUp();
			return ret;
		}

		internal bool FieldExists(string a_field)
		{
			com.db4o.YapClassCollectionIterator i = Iterator();
			while (i.MoveNext())
			{
				if (i.CurrentClass().GetYapField(a_field) != null)
				{
					return true;
				}
			}
			return false;
		}

		internal com.db4o.foundation.Collection4 ForInterface(com.db4o.reflect.ReflectClass
			 claxx)
		{
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			com.db4o.YapClassCollectionIterator i = Iterator();
			while (i.MoveNext())
			{
				com.db4o.YapClass yc = i.CurrentClass();
				com.db4o.reflect.ReflectClass candidate = yc.ClassReflector();
				if (!candidate.IsInterface())
				{
					if (claxx.IsAssignableFrom(candidate))
					{
						col.Add(yc);
						System.Collections.IEnumerator j = new com.db4o.foundation.Collection4(col).GetEnumerator
							();
						while (j.MoveNext())
						{
							com.db4o.YapClass existing = (com.db4o.YapClass)j.Current;
							if (existing != yc)
							{
								com.db4o.YapClass higher = yc.GetHigherHierarchy(existing);
								if (higher != null)
								{
									if (higher == yc)
									{
										col.Remove(existing);
									}
									else
									{
										col.Remove(yc);
									}
								}
							}
						}
					}
				}
			}
			return col;
		}

		public override byte GetIdentifier()
		{
			return com.db4o.YapConst.YAPCLASSCOLLECTION;
		}

		internal com.db4o.YapClass GetActiveYapClass(com.db4o.reflect.ReflectClass a_class
			)
		{
			return (com.db4o.YapClass)i_yapClassByClass.Get(a_class);
		}

		internal com.db4o.YapClass GetYapClass(com.db4o.reflect.ReflectClass a_class, bool
			 a_create)
		{
			com.db4o.YapClass yapClass = (com.db4o.YapClass)i_yapClassByClass.Get(a_class);
			if (yapClass == null)
			{
				yapClass = (com.db4o.YapClass)i_yapClassByBytes.Remove(GetNameBytes(a_class.GetName
					()));
				ReadYapClass(yapClass, a_class);
			}
			if (yapClass != null || (!a_create))
			{
				return yapClass;
			}
			yapClass = (com.db4o.YapClass)i_creating.Get(a_class);
			if (yapClass != null)
			{
				return yapClass;
			}
			yapClass = new com.db4o.YapClass(Stream(), a_class);
			i_creating.Put(a_class, yapClass);
			if (!CreateYapClass(yapClass, a_class))
			{
				i_creating.Remove(a_class);
				return null;
			}
			bool addMembers = false;
			if (i_yapClassByClass.Get(a_class) == null)
			{
				AddYapClass(yapClass);
				addMembers = true;
			}
			int id = yapClass.GetID();
			if (id == 0)
			{
				yapClass.Write(Stream().GetSystemTransaction());
				id = yapClass.GetID();
			}
			if (i_yapClassByID.Get(id) == null)
			{
				i_yapClassByID.Put(id, yapClass);
				addMembers = true;
			}
			if (addMembers || yapClass.i_fields == null)
			{
				_classInits.Process(yapClass);
			}
			i_creating.Remove(a_class);
			Stream().SetDirtyInSystemTransaction(this);
			return yapClass;
		}

		internal com.db4o.YapClass GetYapClass(int a_id)
		{
			return ReadYapClass((com.db4o.YapClass)i_yapClassByID.Get(a_id), null);
		}

		public com.db4o.YapClass GetYapClass(string a_name)
		{
			com.db4o.YapClass yapClass = (com.db4o.YapClass)i_yapClassByBytes.Remove(GetNameBytes
				(a_name));
			ReadYapClass(yapClass, null);
			if (yapClass == null)
			{
				com.db4o.YapClassCollectionIterator i = Iterator();
				while (i.MoveNext())
				{
					yapClass = (com.db4o.YapClass)i.Current;
					if (a_name.Equals(yapClass.GetName()))
					{
						ReadYapClass(yapClass, null);
						return yapClass;
					}
				}
				return null;
			}
			return yapClass;
		}

		public int GetYapClassID(string name)
		{
			com.db4o.YapClass yc = (com.db4o.YapClass)i_yapClassByBytes.Get(GetNameBytes(name
				));
			if (yc != null)
			{
				return yc.GetID();
			}
			return 0;
		}

		private byte[] GetNameBytes(string name)
		{
			return AsBytes(ResolveAlias(name));
		}

		private string ResolveAlias(string name)
		{
			return Stream().ConfigImpl().ResolveAlias(name);
		}

		internal void InitOnUp(com.db4o.Transaction systemTrans)
		{
			i_yapClassCreationDepth++;
			systemTrans.Stream().ShowInternalClasses(true);
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.YapClass)i.Current).InitOnUp(systemTrans);
			}
			systemTrans.Stream().ShowInternalClasses(false);
			i_yapClassCreationDepth--;
			InitYapClassesOnUp();
		}

		internal void InitTables(int a_size)
		{
			i_classes = new com.db4o.foundation.Collection4();
			i_yapClassByBytes = new com.db4o.foundation.Hashtable4(a_size);
			if (a_size < 16)
			{
				a_size = 16;
			}
			i_yapClassByClass = new com.db4o.foundation.Hashtable4(a_size);
			i_yapClassByID = new com.db4o.foundation.Hashtable4(a_size);
			i_creating = new com.db4o.foundation.Hashtable4(1);
		}

		private void InitYapClassesOnUp()
		{
			if (i_yapClassCreationDepth == 0)
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i_initYapClassesOnUp.Next();
				while (yc != null)
				{
					yc.InitOnUp(_systemTransaction);
					yc = (com.db4o.YapClass)i_initYapClassesOnUp.Next();
				}
			}
		}

		public com.db4o.YapClassCollectionIterator Iterator()
		{
			return new com.db4o.YapClassCollectionIterator(this, new com.db4o.foundation.ArrayIterator4
				(i_classes.ToArray()));
		}

		private class ClassIDIterator : com.db4o.foundation.MappingIterator
		{
			public ClassIDIterator(com.db4o.foundation.Collection4 classes) : base(classes.GetEnumerator
				())
			{
			}

			protected override object Map(object current)
			{
				return ((com.db4o.YapClass)current).GetID();
			}
		}

		public System.Collections.IEnumerator Ids()
		{
			return new com.db4o.YapClassCollection.ClassIDIterator(i_classes);
		}

		public override int OwnLength()
		{
			return com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst.INT_LENGTH + (i_classes
				.Size() * com.db4o.YapConst.ID_LENGTH);
		}

		internal void Purge()
		{
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.YapClass)i.Current).Purge();
			}
		}

		public sealed override void ReadThis(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader)
		{
			int classCount = a_reader.ReadInt();
			InitTables(classCount);
			for (int i = classCount; i > 0; i--)
			{
				com.db4o.YapClass yapClass = new com.db4o.YapClass(Stream(), null);
				int id = a_reader.ReadInt();
				yapClass.SetID(id);
				i_classes.Add(yapClass);
				i_yapClassByID.Put(id, yapClass);
				i_yapClassByBytes.Put(yapClass.ReadName(a_trans), yapClass);
			}
			ApplyReadAs();
		}

		private void ApplyReadAs()
		{
			com.db4o.foundation.Hashtable4 readAs = Stream().ConfigImpl().ReadAs();
			readAs.ForEachKey(new _AnonymousInnerClass321(this, readAs));
		}

		private sealed class _AnonymousInnerClass321 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass321(YapClassCollection _enclosing, com.db4o.foundation.Hashtable4
				 readAs)
			{
				this._enclosing = _enclosing;
				this.readAs = readAs;
			}

			public void Visit(object a_object)
			{
				string dbName = (string)a_object;
				byte[] dbbytes = this._enclosing.GetNameBytes(dbName);
				string useName = (string)readAs.Get(dbName);
				byte[] useBytes = this._enclosing.GetNameBytes(useName);
				if (this._enclosing.i_yapClassByBytes.Get(useBytes) == null)
				{
					com.db4o.YapClass yc = (com.db4o.YapClass)this._enclosing.i_yapClassByBytes.Get(dbbytes
						);
					if (yc != null)
					{
						yc.i_nameBytes = useBytes;
						yc.SetConfig(this._enclosing.Stream().ConfigImpl().ConfigClass(dbName));
						this._enclosing.i_yapClassByBytes.Put(dbbytes, null);
						this._enclosing.i_yapClassByBytes.Put(useBytes, yc);
					}
				}
			}

			private readonly YapClassCollection _enclosing;

			private readonly com.db4o.foundation.Hashtable4 readAs;
		}

		public com.db4o.YapClass ReadYapClass(com.db4o.YapClass yapClass, com.db4o.reflect.ReflectClass
			 a_class)
		{
			if (yapClass != null && !yapClass.StateUnread())
			{
				return yapClass;
			}
			i_yapClassCreationDepth++;
			if (yapClass != null && yapClass.StateUnread())
			{
				yapClass.CreateConfigAndConstructor(i_yapClassByBytes, Stream(), a_class);
				com.db4o.reflect.ReflectClass claxx = yapClass.ClassReflector();
				if (claxx != null)
				{
					i_yapClassByClass.Put(claxx, yapClass);
					yapClass.ReadThis();
					yapClass.CheckChanges();
					i_initYapClassesOnUp.Add(yapClass);
				}
			}
			i_yapClassCreationDepth--;
			InitYapClassesOnUp();
			return yapClass;
		}

		public void RefreshClasses()
		{
			com.db4o.YapClassCollection rereader = new com.db4o.YapClassCollection(_systemTransaction
				);
			rereader.i_id = i_id;
			rereader.Read(Stream().GetSystemTransaction());
			System.Collections.IEnumerator i = rereader.i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i.Current;
				if (i_yapClassByID.Get(yc.GetID()) == null)
				{
					i_classes.Add(yc);
					i_yapClassByID.Put(yc.GetID(), yc);
					if (yc.StateUnread())
					{
						i_yapClassByBytes.Put(yc.ReadName(_systemTransaction), yc);
					}
					else
					{
						i_yapClassByClass.Put(yc.ClassReflector(), yc);
					}
				}
			}
			i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i.Current;
				yc.Refresh();
			}
		}

		internal void ReReadYapClass(com.db4o.YapClass yapClass)
		{
			if (yapClass != null)
			{
				ReReadYapClass(yapClass.i_ancestor);
				yapClass.ReadName(_systemTransaction);
				yapClass.ForceRead();
				yapClass.SetStateClean();
				yapClass.BitFalse(com.db4o.YapConst.CHECKED_CHANGES);
				yapClass.BitFalse(com.db4o.YapConst.READING);
				yapClass.BitFalse(com.db4o.YapConst.CONTINUE);
				yapClass.BitFalse(com.db4o.YapConst.DEAD);
				yapClass.CheckChanges();
			}
		}

		public com.db4o.ext.StoredClass[] StoredClasses()
		{
			com.db4o.foundation.Collection4 classes = new com.db4o.foundation.Collection4();
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i.Current;
				ReadYapClass(yc, null);
				if (yc.ClassReflector() == null)
				{
					yc.ForceRead();
				}
				classes.Add(yc);
			}
			ApplyReadAs();
			com.db4o.ext.StoredClass[] sclasses = new com.db4o.ext.StoredClass[classes.Size()
				];
			classes.ToArray(sclasses);
			return sclasses;
		}

		public void WriteAllClasses()
		{
			com.db4o.ext.StoredClass[] storedClasses = StoredClasses();
			for (int i = 0; i < storedClasses.Length; i++)
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)storedClasses[i];
				yc.SetStateDirty();
			}
			for (int i = 0; i < storedClasses.Length; i++)
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)storedClasses[i];
				yc.Write(_systemTransaction);
			}
		}

		public override void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			)
		{
			a_writer.WriteInt(i_classes.Size());
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				a_writer.WriteIDOf(trans, i.Current);
			}
		}

		public override string ToString()
		{
			return base.ToString();
			string str = string.Empty;
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.YapClass yc = (com.db4o.YapClass)i.Current;
				str += yc.GetID() + " " + yc + "\r\n";
			}
			return str;
		}

		public static void Defrag(com.db4o.ReaderPair readers)
		{
			int numClasses = readers.ReadInt();
			for (int classIdx = 0; classIdx < numClasses; classIdx++)
			{
				readers.CopyID();
			}
		}

		private com.db4o.YapStream Stream()
		{
			return _systemTransaction.Stream();
		}

		public override void SetID(int a_id)
		{
			if (Stream().IsClient())
			{
				base.SetID(a_id);
				return;
			}
			if (i_id == 0)
			{
				SystemData().ClassCollectionID(a_id);
			}
			base.SetID(a_id);
		}

		private com.db4o.inside.SystemData SystemData()
		{
			return _systemTransaction.i_file.SystemData();
		}
	}
}

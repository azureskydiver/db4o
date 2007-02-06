namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public sealed class ClassMetadataRepository : com.db4o.@internal.PersistentBase
	{
		private com.db4o.foundation.Collection4 i_classes;

		private com.db4o.foundation.Hashtable4 i_creating;

		private readonly com.db4o.@internal.Transaction _systemTransaction;

		private com.db4o.foundation.Hashtable4 i_yapClassByBytes;

		private com.db4o.foundation.Hashtable4 i_yapClassByClass;

		private com.db4o.foundation.Hashtable4 i_yapClassByID;

		private int i_yapClassCreationDepth;

		private com.db4o.foundation.Queue4 i_initYapClassesOnUp;

		private readonly com.db4o.@internal.PendingClassInits _classInits;

		internal ClassMetadataRepository(com.db4o.@internal.Transaction systemTransaction
			)
		{
			_systemTransaction = systemTransaction;
			i_initYapClassesOnUp = new com.db4o.foundation.Queue4();
			_classInits = new com.db4o.@internal.PendingClassInits(_systemTransaction);
		}

		public void AddYapClass(com.db4o.@internal.ClassMetadata yapClass)
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

		public void AttachQueryNode(string fieldName, com.db4o.foundation.Visitor4 a_visitor
			)
		{
			com.db4o.@internal.ClassMetadataIterator i = Iterator();
			while (i.MoveNext())
			{
				com.db4o.@internal.ClassMetadata yc = i.CurrentClass();
				if (!yc.IsInternal())
				{
					yc.ForEachYapField(new _AnonymousInnerClass67(this, fieldName, a_visitor, yc));
				}
			}
		}

		private sealed class _AnonymousInnerClass67 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass67(ClassMetadataRepository _enclosing, string fieldName
				, com.db4o.foundation.Visitor4 a_visitor, com.db4o.@internal.ClassMetadata yc)
			{
				this._enclosing = _enclosing;
				this.fieldName = fieldName;
				this.a_visitor = a_visitor;
				this.yc = yc;
			}

			public void Visit(object obj)
			{
				com.db4o.@internal.FieldMetadata yf = (com.db4o.@internal.FieldMetadata)obj;
				if (yf.CanAddToQuery(fieldName))
				{
					a_visitor.Visit(new object[] { yc, yf });
				}
			}

			private readonly ClassMetadataRepository _enclosing;

			private readonly string fieldName;

			private readonly com.db4o.foundation.Visitor4 a_visitor;

			private readonly com.db4o.@internal.ClassMetadata yc;
		}

		internal void CheckChanges()
		{
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.@internal.ClassMetadata)i.Current).CheckChanges();
			}
		}

		internal bool CreateYapClass(com.db4o.@internal.ClassMetadata a_yapClass, com.db4o.reflect.ReflectClass
			 a_class)
		{
			i_yapClassCreationDepth++;
			com.db4o.reflect.ReflectClass superClass = a_class.GetSuperclass();
			com.db4o.@internal.ClassMetadata superYapClass = null;
			if (superClass != null && !superClass.Equals(Stream().i_handlers.ICLASS_OBJECT))
			{
				superYapClass = ProduceYapClass(superClass);
			}
			bool ret = Stream().CreateYapClass(a_yapClass, a_class, superYapClass);
			i_yapClassCreationDepth--;
			InitYapClassesOnUp();
			return ret;
		}

		public static void Defrag(com.db4o.@internal.ReaderPair readers)
		{
			int numClasses = readers.ReadInt();
			for (int classIdx = 0; classIdx < numClasses; classIdx++)
			{
				readers.CopyID();
			}
		}

		private void EnsureAllClassesRead()
		{
			bool allClassesRead = false;
			while (!allClassesRead)
			{
				com.db4o.foundation.Collection4 unreadClasses = new com.db4o.foundation.Collection4
					();
				int numClasses = i_classes.Size();
				System.Collections.IEnumerator classIter = i_classes.GetEnumerator();
				while (classIter.MoveNext())
				{
					com.db4o.@internal.ClassMetadata yapClass = (com.db4o.@internal.ClassMetadata)classIter
						.Current;
					if (yapClass.StateUnread())
					{
						unreadClasses.Add(yapClass);
					}
				}
				System.Collections.IEnumerator unreadIter = unreadClasses.GetEnumerator();
				while (unreadIter.MoveNext())
				{
					com.db4o.@internal.ClassMetadata yapClass = (com.db4o.@internal.ClassMetadata)unreadIter
						.Current;
					ReadYapClass(yapClass, null);
					if (yapClass.ClassReflector() == null)
					{
						yapClass.ForceRead();
					}
				}
				allClassesRead = (i_classes.Size() == numClasses);
			}
			ApplyReadAs();
		}

		internal bool FieldExists(string a_field)
		{
			com.db4o.@internal.ClassMetadataIterator i = Iterator();
			while (i.MoveNext())
			{
				if (i.CurrentClass().GetYapField(a_field) != null)
				{
					return true;
				}
			}
			return false;
		}

		public com.db4o.foundation.Collection4 ForInterface(com.db4o.reflect.ReflectClass
			 claxx)
		{
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			com.db4o.@internal.ClassMetadataIterator i = Iterator();
			while (i.MoveNext())
			{
				com.db4o.@internal.ClassMetadata yc = i.CurrentClass();
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
							com.db4o.@internal.ClassMetadata existing = (com.db4o.@internal.ClassMetadata)j.Current;
							if (existing != yc)
							{
								com.db4o.@internal.ClassMetadata higher = yc.GetHigherHierarchy(existing);
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
			return com.db4o.@internal.Const4.YAPCLASSCOLLECTION;
		}

		internal com.db4o.@internal.ClassMetadata GetActiveYapClass(com.db4o.reflect.ReflectClass
			 a_class)
		{
			return (com.db4o.@internal.ClassMetadata)i_yapClassByClass.Get(a_class);
		}

		internal com.db4o.@internal.ClassMetadata GetYapClass(com.db4o.reflect.ReflectClass
			 a_class)
		{
			com.db4o.@internal.ClassMetadata yapClass = (com.db4o.@internal.ClassMetadata)i_yapClassByClass
				.Get(a_class);
			if (yapClass != null)
			{
				return yapClass;
			}
			yapClass = (com.db4o.@internal.ClassMetadata)i_yapClassByBytes.Remove(GetNameBytes
				(a_class.GetName()));
			ReadYapClass(yapClass, a_class);
			return yapClass;
		}

		internal com.db4o.@internal.ClassMetadata ProduceYapClass(com.db4o.reflect.ReflectClass
			 a_class)
		{
			com.db4o.@internal.ClassMetadata yapClass = GetYapClass(a_class);
			if (yapClass != null)
			{
				return yapClass;
			}
			yapClass = (com.db4o.@internal.ClassMetadata)i_creating.Get(a_class);
			if (yapClass != null)
			{
				return yapClass;
			}
			yapClass = new com.db4o.@internal.ClassMetadata(Stream(), a_class);
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

		internal com.db4o.@internal.ClassMetadata GetYapClass(int id)
		{
			return ReadYapClass((com.db4o.@internal.ClassMetadata)i_yapClassByID.Get(id), null
				);
		}

		public com.db4o.@internal.ClassMetadata GetYapClass(string a_name)
		{
			com.db4o.@internal.ClassMetadata yapClass = (com.db4o.@internal.ClassMetadata)i_yapClassByBytes
				.Remove(GetNameBytes(a_name));
			ReadYapClass(yapClass, null);
			if (yapClass == null)
			{
				com.db4o.@internal.ClassMetadataIterator i = Iterator();
				while (i.MoveNext())
				{
					yapClass = (com.db4o.@internal.ClassMetadata)i.Current;
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
			com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)i_yapClassByBytes
				.Get(GetNameBytes(name));
			if (yc != null)
			{
				return yc.GetID();
			}
			return 0;
		}

		internal byte[] GetNameBytes(string name)
		{
			return AsBytes(ResolveAliasRuntimeName(name));
		}

		private string ResolveAliasRuntimeName(string name)
		{
			return Stream().ConfigImpl().ResolveAliasRuntimeName(name);
		}

		internal void InitOnUp(com.db4o.@internal.Transaction systemTrans)
		{
			i_yapClassCreationDepth++;
			systemTrans.Stream().ShowInternalClasses(true);
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.@internal.ClassMetadata)i.Current).InitOnUp(systemTrans);
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
				com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)i_initYapClassesOnUp
					.Next();
				while (yc != null)
				{
					yc.InitOnUp(_systemTransaction);
					yc = (com.db4o.@internal.ClassMetadata)i_initYapClassesOnUp.Next();
				}
			}
		}

		public com.db4o.@internal.ClassMetadataIterator Iterator()
		{
			return new com.db4o.@internal.ClassMetadataIterator(this, new com.db4o.foundation.ArrayIterator4
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
				return ((com.db4o.@internal.ClassMetadata)current).GetID();
			}
		}

		public System.Collections.IEnumerator Ids()
		{
			return new com.db4o.@internal.ClassMetadataRepository.ClassIDIterator(i_classes);
		}

		public override int OwnLength()
		{
			return com.db4o.@internal.Const4.OBJECT_LENGTH + com.db4o.@internal.Const4.INT_LENGTH
				 + (i_classes.Size() * com.db4o.@internal.Const4.ID_LENGTH);
		}

		internal void Purge()
		{
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.@internal.ClassMetadata)i.Current).Purge();
			}
		}

		public sealed override void ReadThis(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.Buffer
			 a_reader)
		{
			int classCount = a_reader.ReadInt();
			InitTables(classCount);
			com.db4o.@internal.ObjectContainerBase stream = Stream();
			int[] ids = new int[classCount];
			for (int i = 0; i < classCount; ++i)
			{
				ids[i] = a_reader.ReadInt();
			}
			com.db4o.@internal.StatefulBuffer[] yapWriters = stream.ReadWritersByIDs(a_trans, 
				ids);
			for (int i = 0; i < classCount; ++i)
			{
				com.db4o.@internal.ClassMetadata yapClass = new com.db4o.@internal.ClassMetadata(
					stream, null);
				yapClass.SetID(ids[i]);
				i_classes.Add(yapClass);
				i_yapClassByID.Put(ids[i], yapClass);
				byte[] name = yapClass.ReadName1(a_trans, yapWriters[i]);
				i_yapClassByBytes.Put(name, yapClass);
			}
			ApplyReadAs();
		}

		internal com.db4o.foundation.Hashtable4 ClassByBytes()
		{
			return i_yapClassByBytes;
		}

		private void ApplyReadAs()
		{
			com.db4o.foundation.Hashtable4 readAs = Stream().ConfigImpl().ReadAs();
			readAs.ForEachKey(new _AnonymousInnerClass383(this, readAs));
		}

		private sealed class _AnonymousInnerClass383 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass383(ClassMetadataRepository _enclosing, com.db4o.foundation.Hashtable4
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
				if (this._enclosing.ClassByBytes().Get(useBytes) == null)
				{
					com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)this._enclosing
						.ClassByBytes().Get(dbbytes);
					if (yc != null)
					{
						yc.i_nameBytes = useBytes;
						yc.SetConfig(this._enclosing.Stream().ConfigImpl().ConfigClass(dbName));
						this._enclosing.ClassByBytes().Put(dbbytes, null);
						this._enclosing.ClassByBytes().Put(useBytes, yc);
					}
				}
			}

			private readonly ClassMetadataRepository _enclosing;

			private readonly com.db4o.foundation.Hashtable4 readAs;
		}

		public com.db4o.@internal.ClassMetadata ReadYapClass(com.db4o.@internal.ClassMetadata
			 yapClass, com.db4o.reflect.ReflectClass a_class)
		{
			if (yapClass == null)
			{
				return null;
			}
			if (!yapClass.StateUnread())
			{
				return yapClass;
			}
			i_yapClassCreationDepth++;
			yapClass.CreateConfigAndConstructor(i_yapClassByBytes, Stream(), a_class);
			com.db4o.reflect.ReflectClass claxx = yapClass.ClassReflector();
			if (claxx != null)
			{
				i_yapClassByClass.Put(claxx, yapClass);
				yapClass.ReadThis();
				yapClass.CheckChanges();
				i_initYapClassesOnUp.Add(yapClass);
			}
			i_yapClassCreationDepth--;
			InitYapClassesOnUp();
			return yapClass;
		}

		public void RefreshClasses()
		{
			com.db4o.@internal.ClassMetadataRepository rereader = new com.db4o.@internal.ClassMetadataRepository
				(_systemTransaction);
			rereader.i_id = i_id;
			rereader.Read(Stream().GetSystemTransaction());
			System.Collections.IEnumerator i = rereader.i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)i.Current;
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
				com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)i.Current;
				yc.Refresh();
			}
		}

		internal void ReReadYapClass(com.db4o.@internal.ClassMetadata yapClass)
		{
			if (yapClass != null)
			{
				ReReadYapClass(yapClass.i_ancestor);
				yapClass.ReadName(_systemTransaction);
				yapClass.ForceRead();
				yapClass.SetStateClean();
				yapClass.BitFalse(com.db4o.@internal.Const4.CHECKED_CHANGES);
				yapClass.BitFalse(com.db4o.@internal.Const4.READING);
				yapClass.BitFalse(com.db4o.@internal.Const4.CONTINUE);
				yapClass.BitFalse(com.db4o.@internal.Const4.DEAD);
				yapClass.CheckChanges();
			}
		}

		public com.db4o.ext.StoredClass[] StoredClasses()
		{
			EnsureAllClassesRead();
			com.db4o.ext.StoredClass[] sclasses = new com.db4o.ext.StoredClass[i_classes.Size
				()];
			i_classes.ToArray(sclasses);
			return sclasses;
		}

		public void WriteAllClasses()
		{
			com.db4o.ext.StoredClass[] storedClasses = StoredClasses();
			for (int i = 0; i < storedClasses.Length; i++)
			{
				com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)storedClasses
					[i];
				yc.SetStateDirty();
			}
			for (int i = 0; i < storedClasses.Length; i++)
			{
				com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)storedClasses
					[i];
				yc.Write(_systemTransaction);
			}
		}

		public override void WriteThis(com.db4o.@internal.Transaction trans, com.db4o.@internal.Buffer
			 a_writer)
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
			string str = "Active:\n";
			System.Collections.IEnumerator i = i_classes.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.@internal.ClassMetadata yc = (com.db4o.@internal.ClassMetadata)i.Current;
				str += yc.GetID() + " " + yc + "\n";
			}
			return str;
		}

		internal com.db4o.@internal.ObjectContainerBase Stream()
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

		private com.db4o.@internal.SystemData SystemData()
		{
			return _systemTransaction.i_file.SystemData();
		}
	}
}

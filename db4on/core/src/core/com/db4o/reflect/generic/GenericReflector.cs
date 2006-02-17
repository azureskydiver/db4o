namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericReflector : com.db4o.reflect.Reflector, com.db4o.foundation.DeepClone
	{
		private com.db4o.reflect.Reflector _delegate;

		private com.db4o.reflect.generic.GenericArrayReflector _array;

		private readonly com.db4o.foundation.Hashtable4 _classByName = new com.db4o.foundation.Hashtable4
			(1);

		private readonly com.db4o.foundation.Hashtable4 _classByClass = new com.db4o.foundation.Hashtable4
			(1);

		private readonly com.db4o.foundation.Collection4 _classes = new com.db4o.foundation.Collection4
			();

		private readonly com.db4o.foundation.Hashtable4 _classByID = new com.db4o.foundation.Hashtable4
			(1);

		private com.db4o.foundation.Collection4 _collectionPredicates = new com.db4o.foundation.Collection4
			();

		private com.db4o.foundation.Collection4 _collectionUpdateDepths = new com.db4o.foundation.Collection4
			();

		private com.db4o.foundation.Collection4 _pendingClasses = new com.db4o.foundation.Collection4
			();

		private com.db4o.Transaction _trans;

		private com.db4o.YapStream _stream;

		public GenericReflector(com.db4o.Transaction trans, com.db4o.reflect.Reflector delegateReflector
			)
		{
			setTransaction(trans);
			_delegate = delegateReflector;
			if (_delegate != null)
			{
				_delegate.setParent(this);
			}
		}

		public virtual object deepClone(object obj)
		{
			com.db4o.reflect.generic.GenericReflector myClone = new com.db4o.reflect.generic.GenericReflector
				(null, (com.db4o.reflect.Reflector)_delegate.deepClone(this));
			myClone._collectionPredicates = (com.db4o.foundation.Collection4)_collectionPredicates
				.deepClone(myClone);
			myClone._collectionUpdateDepths = (com.db4o.foundation.Collection4)_collectionUpdateDepths
				.deepClone(myClone);
			return myClone;
		}

		internal virtual com.db4o.YapStream getStream()
		{
			return _stream;
		}

		public virtual bool hasTransaction()
		{
			return _trans != null;
		}

		public virtual void setTransaction(com.db4o.Transaction trans)
		{
			if (trans != null)
			{
				_trans = trans;
				_stream = trans.i_stream;
			}
		}

		public virtual com.db4o.reflect.ReflectArray array()
		{
			if (_array == null)
			{
				_array = new com.db4o.reflect.generic.GenericArrayReflector(this);
			}
			return _array;
		}

		public virtual int collectionUpdateDepth(com.db4o.reflect.ReflectClass candidate)
		{
			com.db4o.foundation.Iterator4 i = _collectionUpdateDepths.iterator();
			while (i.hasNext())
			{
				com.db4o.reflect.generic.CollectionUpdateDepthEntry entry = (com.db4o.reflect.generic.CollectionUpdateDepthEntry
					)i.next();
				if (entry._predicate.match(candidate))
				{
					return entry._depth;
				}
			}
			return 2;
		}

		public virtual bool constructorCallsSupported()
		{
			return _delegate.constructorCallsSupported();
		}

		internal virtual com.db4o.reflect.generic.GenericClass ensureDelegate(com.db4o.reflect.ReflectClass
			 clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.generic.GenericClass claxx = (com.db4o.reflect.generic.GenericClass
				)_classByName.get(clazz.getName());
			if (claxx == null)
			{
				string name = clazz.getName();
				claxx = new com.db4o.reflect.generic.GenericClass(this, clazz, name, null);
				_classes.add(claxx);
				_classByName.put(name, claxx);
			}
			return claxx;
		}

		public virtual com.db4o.reflect.ReflectClass forClass(j4o.lang.Class clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass claxx = (com.db4o.reflect.ReflectClass)_classByClass
				.get(clazz);
			if (claxx != null)
			{
				return claxx;
			}
			claxx = forName(clazz.getName());
			if (claxx != null)
			{
				_classByClass.put(clazz, claxx);
				return claxx;
			}
			claxx = _delegate.forClass(clazz);
			if (claxx == null)
			{
				return null;
			}
			claxx = ensureDelegate(claxx);
			_classByClass.put(clazz, claxx);
			return claxx;
		}

		public virtual com.db4o.reflect.ReflectClass forName(string className)
		{
			com.db4o.reflect.ReflectClass clazz = (com.db4o.reflect.ReflectClass)_classByName
				.get(className);
			if (clazz != null)
			{
				return clazz;
			}
			clazz = _delegate.forName(className);
			if (clazz != null)
			{
				return ensureDelegate(clazz);
			}
			if (_stream == null)
			{
				return null;
			}
			if (_stream.i_classCollection != null)
			{
				int classID = _stream.i_classCollection.getYapClassID(className);
				if (classID > 0)
				{
					clazz = ensureClassInitialised(classID);
					_classByName.put(className, clazz);
					return clazz;
				}
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectClass forObject(object obj)
		{
			if (obj is com.db4o.reflect.generic.GenericObject)
			{
				return ((com.db4o.reflect.generic.GenericObject)obj)._class;
			}
			return _delegate.forObject(obj);
		}

		public virtual com.db4o.reflect.Reflector getDelegate()
		{
			return _delegate;
		}

		public virtual bool isCollection(com.db4o.reflect.ReflectClass candidate)
		{
			com.db4o.foundation.Iterator4 i = _collectionPredicates.iterator();
			while (i.hasNext())
			{
				if (((com.db4o.reflect.ReflectClassPredicate)i.next()).match(candidate))
				{
					return true;
				}
			}
			return _delegate.isCollection(candidate.getDelegate());
		}

		public virtual void registerCollection(j4o.lang.Class clazz)
		{
			registerCollection(classPredicate(clazz));
		}

		public virtual void registerCollection(com.db4o.reflect.ReflectClassPredicate predicate
			)
		{
			_collectionPredicates.add(predicate);
		}

		private com.db4o.reflect.ReflectClassPredicate classPredicate(j4o.lang.Class clazz
			)
		{
			com.db4o.reflect.ReflectClass collectionClass = forClass(clazz);
			com.db4o.reflect.ReflectClassPredicate predicate = new _AnonymousInnerClass198(this
				, collectionClass);
			return predicate;
		}

		private sealed class _AnonymousInnerClass198 : com.db4o.reflect.ReflectClassPredicate
		{
			public _AnonymousInnerClass198(GenericReflector _enclosing, com.db4o.reflect.ReflectClass
				 collectionClass)
			{
				this._enclosing = _enclosing;
				this.collectionClass = collectionClass;
			}

			public bool match(com.db4o.reflect.ReflectClass candidate)
			{
				return collectionClass.isAssignableFrom(candidate);
			}

			private readonly GenericReflector _enclosing;

			private readonly com.db4o.reflect.ReflectClass collectionClass;
		}

		public virtual void registerCollectionUpdateDepth(j4o.lang.Class clazz, int depth
			)
		{
			registerCollectionUpdateDepth(classPredicate(clazz), depth);
		}

		public virtual void registerCollectionUpdateDepth(com.db4o.reflect.ReflectClassPredicate
			 predicate, int depth)
		{
			_collectionUpdateDepths.add(new com.db4o.reflect.generic.CollectionUpdateDepthEntry
				(predicate, depth));
		}

		public virtual void register(com.db4o.reflect.generic.GenericClass clazz)
		{
			string name = clazz.getName();
			if (_classByName.get(name) == null)
			{
				_classByName.put(name, clazz);
				_classes.add(clazz);
			}
		}

		public virtual com.db4o.reflect.ReflectClass[] knownClasses()
		{
			readAll();
			com.db4o.foundation.Collection4 classes = new com.db4o.foundation.Collection4();
			com.db4o.foundation.Iterator4 i = _classes.iterator();
			while (i.hasNext())
			{
				com.db4o.reflect.generic.GenericClass clazz = (com.db4o.reflect.generic.GenericClass
					)i.next();
				if (!_stream.i_handlers.ICLASS_INTERNAL.isAssignableFrom(clazz))
				{
					if (!clazz.isSecondClass())
					{
						if (!clazz.isArray())
						{
							classes.add(clazz);
						}
					}
				}
			}
			com.db4o.reflect.ReflectClass[] ret = new com.db4o.reflect.ReflectClass[classes.size
				()];
			int j = 0;
			i = classes.iterator();
			while (i.hasNext())
			{
				ret[j++] = (com.db4o.reflect.ReflectClass)i.next();
			}
			return ret;
		}

		private void readAll()
		{
			int classCollectionID = _stream.i_classCollection.getID();
			com.db4o.YapWriter classcollreader = _stream.readWriterByID(_trans, classCollectionID
				);
			int numclasses = classcollreader.readInt();
			int[] classIDs = new int[numclasses];
			for (int classidx = 0; classidx < numclasses; classidx++)
			{
				classIDs[classidx] = classcollreader.readInt();
				ensureClassAvailability(classIDs[classidx]);
			}
			for (int classidx = 0; classidx < numclasses; classidx++)
			{
				ensureClassRead(classIDs[classidx]);
			}
		}

		private com.db4o.reflect.generic.GenericClass ensureClassInitialised(int id)
		{
			com.db4o.reflect.generic.GenericClass ret = ensureClassAvailability(id);
			while (_pendingClasses.size() > 0)
			{
				com.db4o.foundation.Collection4 pending = _pendingClasses;
				_pendingClasses = new com.db4o.foundation.Collection4();
				com.db4o.foundation.Iterator4 i = pending.iterator();
				while (i.hasNext())
				{
					ensureClassRead(((int)i.next()));
				}
			}
			return ret;
		}

		private com.db4o.reflect.generic.GenericClass ensureClassAvailability(int id)
		{
			if (id == 0)
			{
				return null;
			}
			com.db4o.reflect.generic.GenericClass ret = (com.db4o.reflect.generic.GenericClass
				)_classByID.get(id);
			if (ret != null)
			{
				return ret;
			}
			com.db4o.YapWriter classreader = _stream.readWriterByID(_trans, id);
			int namelength = classreader.readInt();
			string classname = _stream.stringIO().read(classreader, namelength);
			ret = (com.db4o.reflect.generic.GenericClass)_classByName.get(classname);
			if (ret != null)
			{
				_classByID.put(id, ret);
				_pendingClasses.add(id);
				return ret;
			}
			classreader.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
			int ancestorid = classreader.readInt();
			int fieldCount = classreader.readInt();
			com.db4o.reflect.ReflectClass nativeClass = _delegate.forName(classname);
			ret = new com.db4o.reflect.generic.GenericClass(this, nativeClass, classname, ensureClassAvailability
				(ancestorid));
			ret.setDeclaredFieldCount(fieldCount);
			_classByID.put(id, ret);
			_pendingClasses.add(id);
			return ret;
		}

		private void ensureClassRead(int id)
		{
			com.db4o.reflect.generic.GenericClass clazz = (com.db4o.reflect.generic.GenericClass
				)_classByID.get(id);
			com.db4o.YapWriter classreader = _stream.readWriterByID(_trans, id);
			int namelength = classreader.readInt();
			string classname = _stream.stringIO().read(classreader, namelength);
			if (_classByName.get(classname) != null)
			{
				return;
			}
			_classByName.put(classname, clazz);
			_classes.add(clazz);
			classreader.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH * 3);
			int numfields = classreader.readInt();
			com.db4o.reflect.generic.GenericField[] fields = new com.db4o.reflect.generic.GenericField
				[numfields];
			for (int i = 0; i < numfields; i++)
			{
				string fieldname = null;
				int fieldnamelength = classreader.readInt();
				fieldname = _stream.stringIO().read(classreader, fieldnamelength);
				if (fieldname.IndexOf(com.db4o.YapConst.VIRTUAL_FIELD_PREFIX) == 0)
				{
					fields[i] = new com.db4o.reflect.generic.GenericVirtualField(fieldname);
				}
				else
				{
					com.db4o.reflect.generic.GenericClass fieldClass = null;
					int handlerid = classreader.readInt();
					switch (handlerid)
					{
						case com.db4o.YapHandlers.ANY_ID:
						{
							fieldClass = (com.db4o.reflect.generic.GenericClass)forClass(j4o.lang.Class.getClassForType
								(typeof(object)));
							break;
						}

						case com.db4o.YapHandlers.ANY_ARRAY_ID:
						{
							fieldClass = ((com.db4o.reflect.generic.GenericClass)forClass(j4o.lang.Class.getClassForType
								(typeof(object)))).arrayClass();
							break;
						}

						default:
						{
							ensureClassAvailability(handlerid);
							fieldClass = (com.db4o.reflect.generic.GenericClass)_classByID.get(handlerid);
							break;
						}
					}
					com.db4o.YapBit attribs = new com.db4o.YapBit(classreader.readByte());
					bool isprimitive = attribs.get();
					bool isarray = attribs.get();
					bool ismultidimensional = attribs.get();
					fields[i] = new com.db4o.reflect.generic.GenericField(fieldname, fieldClass, isprimitive
						, isarray, ismultidimensional);
				}
			}
			clazz.initFields(fields);
		}

		public virtual void registerPrimitiveClass(int id, string name, com.db4o.reflect.generic.GenericConverter
			 converter)
		{
			com.db4o.reflect.generic.GenericClass existing = (com.db4o.reflect.generic.GenericClass
				)_classByID.get(id);
			if (existing != null)
			{
				if (null != converter)
				{
					existing.setSecondClass();
				}
				else
				{
					existing.setConverter(null);
				}
				return;
			}
			com.db4o.reflect.ReflectClass clazz = _delegate.forName(name);
			com.db4o.reflect.generic.GenericClass claxx = null;
			if (clazz != null)
			{
				claxx = ensureDelegate(clazz);
			}
			else
			{
				claxx = new com.db4o.reflect.generic.GenericClass(this, null, name, null);
				_classByName.put(name, claxx);
				claxx.initFields(new com.db4o.reflect.generic.GenericField[] { new com.db4o.reflect.generic.GenericField
					(null, null, true, false, false) });
				claxx.setConverter(converter);
				_classes.add(claxx);
			}
			claxx.setSecondClass();
			claxx.setPrimitive();
			_classByID.put(id, claxx);
		}

		public virtual void setParent(com.db4o.reflect.Reflector reflector)
		{
		}
	}
}

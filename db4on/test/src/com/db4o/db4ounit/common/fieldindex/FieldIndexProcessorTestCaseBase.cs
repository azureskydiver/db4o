namespace com.db4o.db4ounit.common.fieldindex
{
	public abstract class FieldIndexProcessorTestCaseBase : com.db4o.db4ounit.common.fieldindex.FieldIndexTestCaseBase
	{
		public FieldIndexProcessorTestCaseBase() : base()
		{
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			base.Configure(config);
			IndexField(config, typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem)
				, "foo");
			IndexField(config, typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem)
				, "bar");
			IndexField(config, typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem)
				, "child");
		}

		protected virtual com.db4o.query.Query CreateComplexItemQuery()
		{
			return CreateQuery(typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem)
				);
		}

		protected virtual com.db4o.@internal.fieldindex.IndexedNode SelectBestIndex(com.db4o.query.Query
			 query)
		{
			com.db4o.@internal.fieldindex.FieldIndexProcessor processor = CreateProcessor(query
				);
			return processor.SelectBestIndex();
		}

		protected virtual com.db4o.@internal.fieldindex.FieldIndexProcessor CreateProcessor
			(com.db4o.query.Query query)
		{
			com.db4o.@internal.query.processor.QCandidates candidates = GetQCandidates(query);
			return new com.db4o.@internal.fieldindex.FieldIndexProcessor(candidates);
		}

		private com.db4o.@internal.query.processor.QCandidates GetQCandidates(com.db4o.query.Query
			 query)
		{
			com.db4o.@internal.query.processor.QQueryBase.CreateCandidateCollectionResult result
				 = ((com.db4o.@internal.query.processor.QQuery)query).CreateCandidateCollection(
				);
			com.db4o.@internal.query.processor.QCandidates candidates = (com.db4o.@internal.query.processor.QCandidates
				)result.candidateCollection._element;
			return candidates;
		}

		protected virtual void AssertComplexItemIndex(string expectedFieldIndex, com.db4o.@internal.fieldindex.IndexedNode
			 node)
		{
			Db4oUnit.Assert.AreSame(ComplexItemIndex(expectedFieldIndex), node.GetIndex());
		}

		protected virtual com.db4o.@internal.btree.BTree FieldIndexBTree(System.Type clazz
			, string fieldName)
		{
			return GetYapClass(clazz).GetYapField(fieldName).GetIndex(null);
		}

		private com.db4o.@internal.ClassMetadata GetYapClass(System.Type clazz)
		{
			return Stream().GetYapClass(GetReflectClass(clazz));
		}

		private com.db4o.reflect.ReflectClass GetReflectClass(System.Type clazz)
		{
            return Db4oUnit.Extensions.Db4oUnitPlatform.GetReflectClass(Stream().Reflector(), clazz
				);
		}

		protected virtual com.db4o.@internal.btree.BTree ClassIndexBTree(System.Type clazz
			)
		{
			return ((com.db4o.@internal.classindex.BTreeClassIndexStrategy)GetYapClass(clazz)
				.Index()).Btree();
		}

		private com.db4o.@internal.btree.BTree ComplexItemIndex(string fieldName)
		{
			return FieldIndexBTree(typeof(com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem)
				, fieldName);
		}

		protected virtual int[] MapToObjectIds(com.db4o.query.Query itemQuery, int[] foos
			)
		{
			int[] lookingFor = com.db4o.db4ounit.common.foundation.IntArrays4.Clone(foos);
			int[] objectIds = new int[foos.Length];
			com.db4o.ObjectSet set = itemQuery.Execute();
			while (set.HasNext())
			{
				com.db4o.db4ounit.common.fieldindex.HasFoo item = (com.db4o.db4ounit.common.fieldindex.HasFoo
					)set.Next();
				for (int i = 0; i < lookingFor.Length; i++)
				{
					if (lookingFor[i] == item.GetFoo())
					{
						lookingFor[i] = -1;
						objectIds[i] = (int)Db().GetID(item);
						break;
					}
				}
			}
			int index = IndexOfNot(lookingFor, -1);
			if (-1 != index)
			{
				throw new System.ArgumentException("Foo '" + lookingFor[index] + "' not found!");
			}
			return objectIds;
		}

		public static int IndexOfNot(int[] array, int value)
		{
			for (int i = 0; i < array.Length; ++i)
			{
				if (value != array[i])
				{
					return i;
				}
			}
			return -1;
		}

		protected virtual void StoreComplexItems(int[] foos, int[] bars)
		{
			com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem last = null;
			for (int i = 0; i < foos.Length; i++)
			{
				last = new com.db4o.db4ounit.common.fieldindex.ComplexFieldIndexItem(foos[i], bars
					[i], last);
				Store(last);
			}
		}

		protected virtual void AssertTreeInt(int[] expectedValues, com.db4o.@internal.TreeInt
			 treeInt)
		{
			com.db4o.db4ounit.common.btree.ExpectingVisitor visitor = com.db4o.db4ounit.common.btree.BTreeAssert
				.CreateExpectingVisitor(expectedValues);
			treeInt.Traverse(new _AnonymousInnerClass120(this, visitor));
			visitor.AssertExpectations();
		}

		private sealed class _AnonymousInnerClass120 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass120(FieldIndexProcessorTestCaseBase _enclosing, com.db4o.db4ounit.common.btree.ExpectingVisitor
				 visitor)
			{
				this._enclosing = _enclosing;
				this.visitor = visitor;
			}

			public void Visit(object obj)
			{
				visitor.Visit(((com.db4o.@internal.TreeInt)obj)._key);
			}

			private readonly FieldIndexProcessorTestCaseBase _enclosing;

			private readonly com.db4o.db4ounit.common.btree.ExpectingVisitor visitor;
		}
	}
}

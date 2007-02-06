namespace com.db4o.@internal.query.processor
{
	/// <summary>QQuery is the users hook on our graph.</summary>
	/// <remarks>
	/// QQuery is the users hook on our graph.
	/// A QQuery is defined by it's constraints.
	/// NOTE: This is just a 'partial' base class to allow for variant implementations
	/// in db4oj and db4ojdk1.2. It assumes that itself is an instance of QQuery
	/// and should never be used explicitly.
	/// </remarks>
	/// <exclude></exclude>
	public abstract class QQueryBase : com.db4o.types.Unversioned
	{
		[System.NonSerialized]
		private static readonly com.db4o.@internal.IDGenerator i_orderingGenerator = new 
			com.db4o.@internal.IDGenerator();

		[System.NonSerialized]
		internal com.db4o.@internal.Transaction i_trans;

		public com.db4o.foundation.Collection4 i_constraints = new com.db4o.foundation.Collection4
			();

		public com.db4o.@internal.query.processor.QQuery i_parent;

		public string i_field;

		[System.NonSerialized]
		private com.db4o.config.QueryEvaluationMode _evaluationMode;

		public int _evaluationModeAsInt;

		public com.db4o.query.QueryComparator _comparator;

		[System.NonSerialized]
		private readonly com.db4o.@internal.query.processor.QQuery _this;

		protected QQueryBase()
		{
			_this = Cast(this);
		}

		protected QQueryBase(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.query.processor.QQuery
			 a_parent, string a_field)
		{
			_this = Cast(this);
			i_trans = a_trans;
			i_parent = a_parent;
			i_field = a_field;
		}

		internal virtual void AddConstraint(com.db4o.@internal.query.processor.QCon a_constraint
			)
		{
			i_constraints.Add(a_constraint);
		}

		private void AddConstraint(com.db4o.foundation.Collection4 col, object obj)
		{
			if (AttachToExistingConstraints(col, obj, true))
			{
				return;
			}
			if (AttachToExistingConstraints(col, obj, false))
			{
				return;
			}
			com.db4o.@internal.query.processor.QConObject newConstraint = new com.db4o.@internal.query.processor.QConObject
				(i_trans, null, null, obj);
			AddConstraint(newConstraint);
			col.Add(newConstraint);
		}

		private bool AttachToExistingConstraints(com.db4o.foundation.Collection4 col, object
			 obj, bool onlyForPaths)
		{
			bool found = false;
			System.Collections.IEnumerator j = IterateConstraints();
			while (j.MoveNext())
			{
				com.db4o.@internal.query.processor.QCon existingConstraint = (com.db4o.@internal.query.processor.QCon
					)j.Current;
				bool[] removeExisting = { false };
				if (!onlyForPaths || (existingConstraint is com.db4o.@internal.query.processor.QConPath
					))
				{
					com.db4o.@internal.query.processor.QCon newConstraint = existingConstraint.ShareParent
						(obj, removeExisting);
					if (newConstraint != null)
					{
						AddConstraint(newConstraint);
						col.Add(newConstraint);
						if (removeExisting[0])
						{
							RemoveConstraint(existingConstraint);
						}
						found = true;
						if (!onlyForPaths)
						{
							return true;
						}
					}
				}
			}
			return found;
		}

		/// <summary>Search for slot that corresponds to class.</summary>
		/// <remarks>
		/// Search for slot that corresponds to class. <br />If not found add it.
		/// <br />Constrain it. <br />
		/// </remarks>
		public virtual com.db4o.query.Constraint Constrain(object example)
		{
			lock (StreamLock())
			{
				example = com.db4o.@internal.Platform4.GetClassForType(example);
				com.db4o.reflect.ReflectClass claxx = ReflectClassForClass(example);
				if (claxx != null)
				{
					return AddClassConstraint(claxx);
				}
				com.db4o.@internal.query.processor.QConEvaluation eval = com.db4o.@internal.Platform4
					.EvaluationCreate(i_trans, example);
				if (eval != null)
				{
					return AddEvaluationToAllConstraints(eval);
				}
				com.db4o.foundation.Collection4 constraints = new com.db4o.foundation.Collection4
					();
				AddConstraint(constraints, example);
				return ToConstraint(constraints);
			}
		}

		private com.db4o.query.Constraint AddEvaluationToAllConstraints(com.db4o.@internal.query.processor.QConEvaluation
			 eval)
		{
			System.Collections.IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).AddConstraint(eval);
			}
			return null;
		}

		private com.db4o.query.Constraint AddClassConstraint(com.db4o.reflect.ReflectClass
			 claxx)
		{
			if (claxx.Equals(Stream().i_handlers.ICLASS_OBJECT))
			{
				return null;
			}
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			if (claxx.IsInterface())
			{
				return AddInterfaceConstraint(claxx);
			}
			System.Collections.IEnumerator constraintsIterator = IterateConstraints();
			while (constraintsIterator.MoveNext())
			{
				com.db4o.@internal.query.processor.QCon existingConstraint = (com.db4o.@internal.query.processor.QConObject
					)constraintsIterator.Current;
				bool[] removeExisting = { false };
				com.db4o.@internal.query.processor.QCon newConstraint = existingConstraint.ShareParentForClass
					(claxx, removeExisting);
				if (newConstraint != null)
				{
					AddConstraint(newConstraint);
					col.Add(newConstraint);
					if (removeExisting[0])
					{
						RemoveConstraint(existingConstraint);
					}
				}
			}
			if (col.Size() == 0)
			{
				com.db4o.@internal.query.processor.QConClass qcc = new com.db4o.@internal.query.processor.QConClass
					(i_trans, null, null, claxx);
				AddConstraint(qcc);
				return qcc;
			}
			return ToConstraint(col);
		}

		private com.db4o.query.Constraint AddInterfaceConstraint(com.db4o.reflect.ReflectClass
			 claxx)
		{
			com.db4o.foundation.Collection4 classes = Stream().ClassCollection().ForInterface
				(claxx);
			if (classes.Size() == 0)
			{
				com.db4o.@internal.query.processor.QConClass qcc = new com.db4o.@internal.query.processor.QConClass
					(i_trans, null, null, claxx);
				AddConstraint(qcc);
				return qcc;
			}
			System.Collections.IEnumerator i = classes.GetEnumerator();
			com.db4o.query.Constraint constr = null;
			while (i.MoveNext())
			{
				com.db4o.@internal.ClassMetadata yapClass = (com.db4o.@internal.ClassMetadata)i.Current;
				com.db4o.reflect.ReflectClass yapClassClaxx = yapClass.ClassReflector();
				if (yapClassClaxx != null)
				{
					if (!yapClassClaxx.IsInterface())
					{
						if (constr == null)
						{
							constr = Constrain(yapClassClaxx);
						}
						else
						{
							constr = constr.Or(Constrain(yapClass.ClassReflector()));
						}
					}
				}
			}
			return constr;
		}

		private com.db4o.reflect.ReflectClass ReflectClassForClass(object example)
		{
			if (example is com.db4o.reflect.ReflectClass)
			{
				return (com.db4o.reflect.ReflectClass)example;
			}
			if (example is j4o.lang.Class)
			{
				return i_trans.Reflector().ForClass((j4o.lang.Class)example);
			}
			return null;
		}

		public virtual com.db4o.query.Constraints Constraints()
		{
			lock (StreamLock())
			{
				com.db4o.query.Constraint[] constraints = new com.db4o.query.Constraint[i_constraints
					.Size()];
				i_constraints.ToArray(constraints);
				return new com.db4o.@internal.query.processor.QConstraints(i_trans, constraints);
			}
		}

		public virtual com.db4o.query.Query Descend(string a_field)
		{
			lock (StreamLock())
			{
				com.db4o.@internal.query.processor.QQuery query = new com.db4o.@internal.query.processor.QQuery
					(i_trans, _this, a_field);
				int[] run = { 1 };
				if (!Descend1(query, a_field, run))
				{
					if (run[0] == 1)
					{
						run[0] = 2;
						if (!Descend1(query, a_field, run))
						{
							return null;
						}
					}
				}
				return query;
			}
		}

		private bool Descend1(com.db4o.@internal.query.processor.QQuery query, string a_field
			, int[] run)
		{
			bool[] foundClass = { false };
			if (run[0] == 2 || i_constraints.Size() == 0)
			{
				run[0] = 0;
				bool[] anyClassCollected = { false };
				Stream().ClassCollection().AttachQueryNode(a_field, new _AnonymousInnerClass246(this
					, anyClassCollected));
			}
			System.Collections.IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				if (((com.db4o.@internal.query.processor.QCon)i.Current).Attach(query, a_field))
				{
					foundClass[0] = true;
				}
			}
			return foundClass[0];
		}

		private sealed class _AnonymousInnerClass246 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass246(QQueryBase _enclosing, bool[] anyClassCollected)
			{
				this._enclosing = _enclosing;
				this.anyClassCollected = anyClassCollected;
			}

			public void Visit(object obj)
			{
				object[] pair = ((object[])obj);
				com.db4o.@internal.ClassMetadata parentYc = (com.db4o.@internal.ClassMetadata)pair
					[0];
				com.db4o.@internal.FieldMetadata yf = (com.db4o.@internal.FieldMetadata)pair[1];
				com.db4o.@internal.ClassMetadata childYc = yf.GetFieldYapClass(this._enclosing.Stream
					());
				bool take = true;
				if (childYc is com.db4o.@internal.UntypedFieldHandler)
				{
					if (anyClassCollected[0])
					{
						take = false;
					}
					else
					{
						anyClassCollected[0] = true;
					}
				}
				if (take)
				{
					com.db4o.@internal.query.processor.QConClass qcc = new com.db4o.@internal.query.processor.QConClass
						(this._enclosing.i_trans, null, yf.QField(this._enclosing.i_trans), parentYc.ClassReflector
						());
					this._enclosing.AddConstraint(qcc);
				}
			}

			private readonly QQueryBase _enclosing;

			private readonly bool[] anyClassCollected;
		}

		public virtual com.db4o.ObjectSet Execute()
		{
			com.db4o.@internal.callbacks.Callbacks callbacks = Stream().Callbacks();
			callbacks.OnQueryStarted(Cast(this));
			com.db4o.@internal.query.result.QueryResult qresult = GetQueryResult();
			callbacks.OnQueryFinished(Cast(this));
			return new com.db4o.@internal.query.ObjectSetFacade(qresult);
		}

		public virtual com.db4o.@internal.query.result.QueryResult GetQueryResult()
		{
			lock (StreamLock())
			{
				if (i_constraints.Size() == 0)
				{
					return Stream().GetAll(i_trans);
				}
				com.db4o.@internal.query.result.QueryResult result = ClassOnlyQuery();
				if (result != null)
				{
					return result;
				}
				return Stream().ExecuteQuery(_this);
			}
		}

		protected virtual com.db4o.@internal.ObjectContainerBase Stream()
		{
			return i_trans.Stream();
		}

		private com.db4o.@internal.query.result.QueryResult ClassOnlyQuery()
		{
			if (i_constraints.Size() != 1 || _comparator != null)
			{
				return null;
			}
			com.db4o.query.Constraint constr = SingleConstraint();
			if (j4o.lang.JavaSystem.GetClassForObject(constr) != j4o.lang.JavaSystem.GetClassForType
				(typeof(com.db4o.@internal.query.processor.QConClass)))
			{
				return null;
			}
			com.db4o.@internal.query.processor.QConClass clazzconstr = (com.db4o.@internal.query.processor.QConClass
				)constr;
			com.db4o.@internal.ClassMetadata clazz = clazzconstr.i_yapClass;
			if (clazz == null)
			{
				return null;
			}
			if (clazzconstr.HasChildren() || clazz.IsArray())
			{
				return null;
			}
			com.db4o.@internal.query.result.QueryResult queryResult = Stream().ClassOnlyQuery
				(i_trans, clazz);
			if (queryResult == null)
			{
				return null;
			}
			Sort(queryResult);
			return queryResult;
		}

		private com.db4o.query.Constraint SingleConstraint()
		{
			return (com.db4o.query.Constraint)i_constraints.SingleElement();
		}

		public class CreateCandidateCollectionResult
		{
			public readonly bool checkDuplicates;

			public readonly bool topLevel;

			public readonly com.db4o.foundation.List4 candidateCollection;

			public CreateCandidateCollectionResult(com.db4o.foundation.List4 candidateCollection_
				, bool checkDuplicates_, bool topLevel_)
			{
				candidateCollection = candidateCollection_;
				topLevel = topLevel_;
				checkDuplicates = checkDuplicates_;
			}
		}

		public virtual System.Collections.IEnumerator ExecuteSnapshot()
		{
			com.db4o.@internal.query.processor.QQueryBase.CreateCandidateCollectionResult r = 
				CreateCandidateCollection();
			com.db4o.foundation.Collection4 executionPath = ExecutionPath(r);
			System.Collections.IEnumerator candidatesIterator = new com.db4o.foundation.Iterator4Impl
				(r.candidateCollection);
			com.db4o.foundation.Collection4 snapshots = new com.db4o.foundation.Collection4();
			while (candidatesIterator.MoveNext())
			{
				com.db4o.@internal.query.processor.QCandidates candidates = (com.db4o.@internal.query.processor.QCandidates
					)candidatesIterator.Current;
				snapshots.Add(candidates.ExecuteSnapshot(executionPath));
			}
			System.Collections.IEnumerator snapshotsIterator = snapshots.GetEnumerator();
			com.db4o.foundation.CompositeIterator4 resultingIDs = new com.db4o.foundation.CompositeIterator4
				(snapshotsIterator);
			if (!r.checkDuplicates)
			{
				return resultingIDs;
			}
			return CheckDuplicates(resultingIDs);
		}

		public virtual System.Collections.IEnumerator ExecuteLazy()
		{
			com.db4o.@internal.query.processor.QQueryBase.CreateCandidateCollectionResult r = 
				CreateCandidateCollection();
			com.db4o.foundation.Collection4 executionPath = ExecutionPath(r);
			System.Collections.IEnumerator candidateCollection = new com.db4o.foundation.Iterator4Impl
				(r.candidateCollection);
			com.db4o.foundation.MappingIterator executeCandidates = new _AnonymousInnerClass391
				(this, executionPath, candidateCollection);
			com.db4o.foundation.CompositeIterator4 resultingIDs = new com.db4o.foundation.CompositeIterator4
				(executeCandidates);
			if (!r.checkDuplicates)
			{
				return resultingIDs;
			}
			return CheckDuplicates(resultingIDs);
		}

		private sealed class _AnonymousInnerClass391 : com.db4o.foundation.MappingIterator
		{
			public _AnonymousInnerClass391(QQueryBase _enclosing, com.db4o.foundation.Collection4
				 executionPath, System.Collections.IEnumerator baseArg1) : base(baseArg1)
			{
				this._enclosing = _enclosing;
				this.executionPath = executionPath;
			}

			protected override object Map(object current)
			{
				return ((com.db4o.@internal.query.processor.QCandidates)current).ExecuteLazy(executionPath
					);
			}

			private readonly QQueryBase _enclosing;

			private readonly com.db4o.foundation.Collection4 executionPath;
		}

		private com.db4o.foundation.MappingIterator CheckDuplicates(com.db4o.foundation.CompositeIterator4
			 executeAllCandidates)
		{
			return new _AnonymousInnerClass407(this, executeAllCandidates);
		}

		private sealed class _AnonymousInnerClass407 : com.db4o.foundation.MappingIterator
		{
			public _AnonymousInnerClass407(QQueryBase _enclosing, com.db4o.foundation.CompositeIterator4
				 baseArg1) : base(baseArg1)
			{
				this._enclosing = _enclosing;
			}

			private com.db4o.@internal.TreeInt ids = new com.db4o.@internal.TreeInt(0);

			protected override object Map(object current)
			{
				int id = ((int)current);
				if (this.ids.Find(id) != null)
				{
					return com.db4o.foundation.MappingIterator.SKIP;
				}
				this.ids = (com.db4o.@internal.TreeInt)this.ids.Add(new com.db4o.@internal.TreeInt
					(id));
				return current;
			}

			private readonly QQueryBase _enclosing;
		}

		private com.db4o.foundation.Collection4 ExecutionPath(com.db4o.@internal.query.processor.QQueryBase.CreateCandidateCollectionResult
			 r)
		{
			return r.topLevel ? null : FieldPathFromTop();
		}

		public virtual void ExecuteLocal(com.db4o.@internal.query.result.IdListQueryResult
			 result)
		{
			com.db4o.@internal.query.processor.QQueryBase.CreateCandidateCollectionResult r = 
				CreateCandidateCollection();
			bool checkDuplicates = r.checkDuplicates;
			bool topLevel = r.topLevel;
			com.db4o.foundation.List4 candidateCollection = r.candidateCollection;
			if (candidateCollection != null)
			{
				com.db4o.foundation.Collection4 executionPath = topLevel ? null : FieldPathFromTop
					();
				System.Collections.IEnumerator i = new com.db4o.foundation.Iterator4Impl(candidateCollection
					);
				while (i.MoveNext())
				{
					((com.db4o.@internal.query.processor.QCandidates)i.Current).Execute();
				}
				if (candidateCollection._next != null)
				{
					checkDuplicates = true;
				}
				if (checkDuplicates)
				{
					result.CheckDuplicates();
				}
				com.db4o.@internal.ObjectContainerBase stream = Stream();
				i = new com.db4o.foundation.Iterator4Impl(candidateCollection);
				while (i.MoveNext())
				{
					com.db4o.@internal.query.processor.QCandidates candidates = (com.db4o.@internal.query.processor.QCandidates
						)i.Current;
					if (topLevel)
					{
						candidates.Traverse(result);
					}
					else
					{
						candidates.Traverse(new _AnonymousInnerClass461(this, executionPath, stream, result
							));
					}
				}
			}
			Sort(result);
		}

		private sealed class _AnonymousInnerClass461 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass461(QQueryBase _enclosing, com.db4o.foundation.Collection4
				 executionPath, com.db4o.@internal.ObjectContainerBase stream, com.db4o.@internal.query.result.IdListQueryResult
				 result)
			{
				this._enclosing = _enclosing;
				this.executionPath = executionPath;
				this.stream = stream;
				this.result = result;
			}

			public void Visit(object a_object)
			{
				com.db4o.@internal.query.processor.QCandidate candidate = (com.db4o.@internal.query.processor.QCandidate
					)a_object;
				if (candidate.Include())
				{
					com.db4o.@internal.TreeInt ids = new com.db4o.@internal.TreeInt(candidate._key);
					com.db4o.@internal.TreeInt[] idsNew = new com.db4o.@internal.TreeInt[1];
					System.Collections.IEnumerator itPath = executionPath.GetEnumerator();
					while (itPath.MoveNext())
					{
						idsNew[0] = null;
						string fieldName = (string)(itPath.Current);
						if (ids != null)
						{
							ids.Traverse(new _AnonymousInnerClass472(this, stream, idsNew, fieldName));
						}
						ids = idsNew[0];
					}
					if (ids != null)
					{
						ids.Traverse(new _AnonymousInnerClass492(this, result));
					}
				}
			}

			private sealed class _AnonymousInnerClass472 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass472(_AnonymousInnerClass461 _enclosing, com.db4o.@internal.ObjectContainerBase
					 stream, com.db4o.@internal.TreeInt[] idsNew, string fieldName)
				{
					this._enclosing = _enclosing;
					this.stream = stream;
					this.idsNew = idsNew;
					this.fieldName = fieldName;
				}

				public void Visit(object treeInt)
				{
					int id = ((com.db4o.@internal.TreeInt)treeInt)._key;
					com.db4o.@internal.StatefulBuffer reader = stream.ReadWriterByID(this._enclosing.
						_enclosing.i_trans, id);
					if (reader != null)
					{
						com.db4o.@internal.marshall.ObjectHeader oh = new com.db4o.@internal.marshall.ObjectHeader
							(stream, reader);
						idsNew[0] = oh.YapClass().CollectFieldIDs(oh._marshallerFamily, oh._headerAttributes
							, idsNew[0], reader, fieldName);
					}
				}

				private readonly _AnonymousInnerClass461 _enclosing;

				private readonly com.db4o.@internal.ObjectContainerBase stream;

				private readonly com.db4o.@internal.TreeInt[] idsNew;

				private readonly string fieldName;
			}

			private sealed class _AnonymousInnerClass492 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass492(_AnonymousInnerClass461 _enclosing, com.db4o.@internal.query.result.IdListQueryResult
					 result)
				{
					this._enclosing = _enclosing;
					this.result = result;
				}

				public void Visit(object treeInt)
				{
					result.AddKeyCheckDuplicates(((com.db4o.@internal.TreeInt)treeInt)._key);
				}

				private readonly _AnonymousInnerClass461 _enclosing;

				private readonly com.db4o.@internal.query.result.IdListQueryResult result;
			}

			private readonly QQueryBase _enclosing;

			private readonly com.db4o.foundation.Collection4 executionPath;

			private readonly com.db4o.@internal.ObjectContainerBase stream;

			private readonly com.db4o.@internal.query.result.IdListQueryResult result;
		}

		private com.db4o.foundation.Collection4 FieldPathFromTop()
		{
			com.db4o.@internal.query.processor.QQueryBase q = this;
			com.db4o.foundation.Collection4 fieldPath = new com.db4o.foundation.Collection4();
			while (q.i_parent != null)
			{
				fieldPath.Prepend(q.i_field);
				q = q.i_parent;
			}
			return fieldPath;
		}

		private void LogConstraints()
		{
		}

		public virtual com.db4o.@internal.query.processor.QQueryBase.CreateCandidateCollectionResult
			 CreateCandidateCollection()
		{
			bool checkDuplicates = false;
			bool topLevel = true;
			com.db4o.foundation.List4 candidateCollection = null;
			System.Collections.IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QCon qcon = (com.db4o.@internal.query.processor.QCon
					)i.Current;
				com.db4o.@internal.query.processor.QCon old = qcon;
				qcon = qcon.GetRoot();
				if (qcon != old)
				{
					checkDuplicates = true;
					topLevel = false;
				}
				com.db4o.@internal.ClassMetadata yc = qcon.GetYapClass();
				if (yc == null)
				{
					break;
				}
				candidateCollection = AddConstraintToCandidateCollection(candidateCollection, qcon
					);
			}
			return new com.db4o.@internal.query.processor.QQueryBase.CreateCandidateCollectionResult
				(candidateCollection, checkDuplicates, topLevel);
		}

		private com.db4o.foundation.List4 AddConstraintToCandidateCollection(com.db4o.foundation.List4
			 candidateCollection, com.db4o.@internal.query.processor.QCon qcon)
		{
			if (candidateCollection != null)
			{
				if (TryToAddToExistingCandidate(candidateCollection, qcon))
				{
					return candidateCollection;
				}
			}
			com.db4o.@internal.query.processor.QCandidates candidates = new com.db4o.@internal.query.processor.QCandidates
				(i_trans, qcon.GetYapClass(), null);
			candidates.AddConstraint(qcon);
			return new com.db4o.foundation.List4(candidateCollection, candidates);
		}

		private bool TryToAddToExistingCandidate(com.db4o.foundation.List4 candidateCollection
			, com.db4o.@internal.query.processor.QCon qcon)
		{
			System.Collections.IEnumerator j = new com.db4o.foundation.Iterator4Impl(candidateCollection
				);
			while (j.MoveNext())
			{
				com.db4o.@internal.query.processor.QCandidates candidates = (com.db4o.@internal.query.processor.QCandidates
					)j.Current;
				if (candidates.TryAddConstraint(qcon))
				{
					return true;
				}
			}
			return false;
		}

		public com.db4o.@internal.Transaction GetTransaction()
		{
			return i_trans;
		}

		internal virtual System.Collections.IEnumerator IterateConstraints()
		{
			return new com.db4o.foundation.Collection4(i_constraints).GetEnumerator();
		}

		public virtual com.db4o.query.Query OrderAscending()
		{
			lock (StreamLock())
			{
				SetOrdering(i_orderingGenerator.Next());
				return _this;
			}
		}

		public virtual com.db4o.query.Query OrderDescending()
		{
			lock (StreamLock())
			{
				SetOrdering(-i_orderingGenerator.Next());
				return _this;
			}
		}

		private void SetOrdering(int ordering)
		{
			System.Collections.IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).SetOrdering(ordering);
			}
		}

		public virtual void Marshall()
		{
			_evaluationModeAsInt = _evaluationMode.AsInt();
			System.Collections.IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).GetRoot().Marshall();
			}
		}

		public virtual void Unmarshall(com.db4o.@internal.Transaction a_trans)
		{
			_evaluationMode = com.db4o.config.QueryEvaluationMode.FromInt(_evaluationModeAsInt
				);
			i_trans = a_trans;
			System.Collections.IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).Unmarshall(a_trans);
			}
		}

		internal virtual void RemoveConstraint(com.db4o.@internal.query.processor.QCon a_constraint
			)
		{
			i_constraints.Remove(a_constraint);
		}

		internal virtual com.db4o.query.Constraint ToConstraint(com.db4o.foundation.Collection4
			 constraints)
		{
			if (constraints.Size() == 1)
			{
				return (com.db4o.query.Constraint)constraints.SingleElement();
			}
			else
			{
				if (constraints.Size() > 0)
				{
					com.db4o.query.Constraint[] constraintArray = new com.db4o.query.Constraint[constraints
						.Size()];
					constraints.ToArray(constraintArray);
					return new com.db4o.@internal.query.processor.QConstraints(i_trans, constraintArray
						);
				}
			}
			return null;
		}

		protected virtual object StreamLock()
		{
			return Stream().i_lock;
		}

		public virtual com.db4o.query.Query SortBy(com.db4o.query.QueryComparator comparator
			)
		{
			_comparator = comparator;
			return _this;
		}

		private void Sort(com.db4o.@internal.query.result.QueryResult result)
		{
			if (_comparator != null)
			{
				result.Sort(_comparator);
			}
		}

		private static com.db4o.@internal.query.processor.QQuery Cast(com.db4o.@internal.query.processor.QQueryBase
			 obj)
		{
			return (com.db4o.@internal.query.processor.QQuery)obj;
		}

		public virtual bool RequiresSort()
		{
			if (_comparator != null)
			{
				return true;
			}
			System.Collections.IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QCon qCon = (com.db4o.@internal.query.processor.QCon
					)i.Current;
				if (qCon.RequiresSort())
				{
					return true;
				}
			}
			return false;
		}

		public virtual com.db4o.query.QueryComparator Comparator()
		{
			return _comparator;
		}

		public virtual com.db4o.config.QueryEvaluationMode EvaluationMode()
		{
			return _evaluationMode;
		}

		public virtual void EvaluationMode(com.db4o.config.QueryEvaluationMode mode)
		{
			_evaluationMode = mode;
		}
	}
}

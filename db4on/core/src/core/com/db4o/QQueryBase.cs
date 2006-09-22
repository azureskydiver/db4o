namespace com.db4o
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
		[com.db4o.Transient]
		private static readonly com.db4o.IDGenerator i_orderingGenerator = new com.db4o.IDGenerator
			();

		[com.db4o.Transient]
		internal com.db4o.Transaction i_trans;

		public com.db4o.foundation.Collection4 i_constraints = new com.db4o.foundation.Collection4
			();

		public com.db4o.QQuery i_parent;

		public string i_field;

		public com.db4o.query.QueryComparator _comparator;

		private readonly com.db4o.QQuery _this;

		protected QQueryBase()
		{
			_this = Cast(this);
		}

		protected QQueryBase(com.db4o.Transaction a_trans, com.db4o.QQuery a_parent, string
			 a_field)
		{
			_this = Cast(this);
			i_trans = a_trans;
			i_parent = a_parent;
			i_field = a_field;
		}

		internal virtual void AddConstraint(com.db4o.QCon a_constraint)
		{
			i_constraints.Add(a_constraint);
		}

		private void AddConstraint(com.db4o.foundation.Collection4 col, object obj)
		{
			bool found = false;
			com.db4o.foundation.Iterator4 j = IterateConstraints();
			while (j.MoveNext())
			{
				com.db4o.QCon existingConstraint = (com.db4o.QCon)j.Current();
				bool[] removeExisting = { false };
				com.db4o.QCon newConstraint = existingConstraint.ShareParent(obj, removeExisting);
				if (newConstraint != null)
				{
					AddConstraint(newConstraint);
					col.Add(newConstraint);
					if (removeExisting[0])
					{
						RemoveConstraint(existingConstraint);
					}
					found = true;
				}
			}
			if (!found)
			{
				com.db4o.QConObject newConstraint = new com.db4o.QConObject(i_trans, null, null, 
					obj);
				AddConstraint(newConstraint);
				col.Add(newConstraint);
			}
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
				com.db4o.reflect.ReflectClass claxx = null;
				example = com.db4o.Platform4.GetClassForType(example);
				com.db4o.reflect.Reflector reflector = i_trans.Reflector();
				if (example is com.db4o.reflect.ReflectClass)
				{
					claxx = (com.db4o.reflect.ReflectClass)example;
				}
				else
				{
					if (example is j4o.lang.Class)
					{
						claxx = reflector.ForClass((j4o.lang.Class)example);
					}
				}
				if (claxx != null)
				{
					if (claxx.Equals(Stream().i_handlers.ICLASS_OBJECT))
					{
						return null;
					}
					com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
					if (claxx.IsInterface())
					{
						com.db4o.foundation.Collection4 classes = Stream().ClassCollection().ForInterface
							(claxx);
						if (classes.Size() == 0)
						{
							com.db4o.QConClass qcc = new com.db4o.QConClass(i_trans, null, null, claxx);
							AddConstraint(qcc);
							return qcc;
						}
						com.db4o.foundation.Iterator4 i = classes.Iterator();
						com.db4o.query.Constraint constr = null;
						while (i.MoveNext())
						{
							com.db4o.YapClass yapClass = (com.db4o.YapClass)i.Current();
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
					com.db4o.foundation.Iterator4 constraintsIterator = IterateConstraints();
					while (constraintsIterator.MoveNext())
					{
						com.db4o.QCon existingConstraint = (com.db4o.QConObject)constraintsIterator.Current
							();
						bool[] removeExisting = { false };
						com.db4o.QCon newConstraint = existingConstraint.ShareParentForClass(claxx, removeExisting
							);
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
						com.db4o.QConClass qcc = new com.db4o.QConClass(i_trans, null, null, claxx);
						AddConstraint(qcc);
						return qcc;
					}
					if (col.Size() == 1)
					{
						return FirstConstraint(col);
					}
					com.db4o.query.Constraint[] constraintArray = new com.db4o.query.Constraint[col.Size
						()];
					col.ToArray(constraintArray);
					return new com.db4o.QConstraints(i_trans, constraintArray);
				}
				com.db4o.QConEvaluation eval = com.db4o.Platform4.EvaluationCreate(i_trans, example
					);
				if (eval != null)
				{
					com.db4o.foundation.Iterator4 i = IterateConstraints();
					while (i.MoveNext())
					{
						((com.db4o.QCon)i.Current()).AddConstraint(eval);
					}
					return null;
				}
				com.db4o.foundation.Collection4 constraints = new com.db4o.foundation.Collection4
					();
				AddConstraint(constraints, example);
				return ToConstraint(constraints);
			}
		}

		private com.db4o.query.Constraint FirstConstraint(com.db4o.foundation.Collection4
			 col)
		{
			com.db4o.foundation.Iterator4 i = col.Iterator();
			i.MoveNext();
			return (com.db4o.query.Constraint)i.Current();
		}

		public virtual com.db4o.query.Constraints Constraints()
		{
			lock (StreamLock())
			{
				com.db4o.query.Constraint[] constraints = new com.db4o.query.Constraint[i_constraints
					.Size()];
				i_constraints.ToArray(constraints);
				return new com.db4o.QConstraints(i_trans, constraints);
			}
		}

		public virtual com.db4o.query.Query Descend(string a_field)
		{
			lock (StreamLock())
			{
				com.db4o.QQuery query = new com.db4o.QQuery(i_trans, _this, a_field);
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

		private bool Descend1(com.db4o.QQuery query, string a_field, int[] run)
		{
			bool[] foundClass = { false };
			if (run[0] == 2 || i_constraints.Size() == 0)
			{
				run[0] = 0;
				bool[] anyClassCollected = { false };
				Stream().ClassCollection().AttachQueryNode(a_field, new _AnonymousInnerClass221(this
					, anyClassCollected));
			}
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.MoveNext())
			{
				if (((com.db4o.QCon)i.Current()).Attach(query, a_field))
				{
					foundClass[0] = true;
				}
			}
			return foundClass[0];
		}

		private sealed class _AnonymousInnerClass221 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass221(QQueryBase _enclosing, bool[] anyClassCollected)
			{
				this._enclosing = _enclosing;
				this.anyClassCollected = anyClassCollected;
			}

			public void Visit(object obj)
			{
				object[] pair = ((object[])obj);
				com.db4o.YapClass parentYc = (com.db4o.YapClass)pair[0];
				com.db4o.YapField yf = (com.db4o.YapField)pair[1];
				com.db4o.YapClass childYc = yf.GetFieldYapClass(this._enclosing.Stream());
				bool take = true;
				if (childYc is com.db4o.YapClassAny)
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
					com.db4o.QConClass qcc = new com.db4o.QConClass(this._enclosing.i_trans, null, yf
						.QField(this._enclosing.i_trans), parentYc.ClassReflector());
					this._enclosing.AddConstraint(qcc);
				}
			}

			private readonly QQueryBase _enclosing;

			private readonly bool[] anyClassCollected;
		}

		public virtual com.db4o.ObjectSet Execute()
		{
			com.db4o.inside.callbacks.Callbacks callbacks = Stream().Callbacks();
			callbacks.OnQueryStarted(Cast(this));
			com.db4o.inside.query.QueryResult qresult = GetQueryResult();
			callbacks.OnQueryFinished(Cast(this));
			return new com.db4o.inside.query.ObjectSetFacade(qresult);
		}

		public virtual com.db4o.inside.query.QueryResult GetQueryResult()
		{
			lock (StreamLock())
			{
				com.db4o.YapStream stream = Stream();
				if (i_constraints.Size() == 0)
				{
					com.db4o.QueryResultImpl res = stream.CreateQResult(i_trans);
					stream.GetAll(i_trans, res);
					return res;
				}
				com.db4o.inside.query.QueryResult result = ClassOnlyQuery();
				if (result != null)
				{
					result.Reset();
					return result;
				}
				com.db4o.QueryResultImpl qResult = Stream().CreateQResult(i_trans);
				Execute1(qResult);
				return qResult;
			}
		}

		protected virtual com.db4o.YapStream Stream()
		{
			return i_trans.Stream();
		}

		private com.db4o.inside.query.QueryResult ClassOnlyQuery()
		{
			if (i_constraints.Size() != 1 || _comparator != null)
			{
				return null;
			}
			com.db4o.query.Constraint constr = FirstConstraint();
			if (j4o.lang.Class.GetClassForObject(constr) != j4o.lang.Class.GetClassForType(typeof(
				com.db4o.QConClass)))
			{
				return null;
			}
			com.db4o.QConClass clazzconstr = (com.db4o.QConClass)constr;
			com.db4o.YapClass clazz = clazzconstr.i_yapClass;
			if (clazz == null)
			{
				return null;
			}
			if (clazzconstr.HasChildren() || clazz.IsArray())
			{
				return null;
			}
			if (Stream().IsClient())
			{
				long[] ids = clazz.GetIDs(i_trans);
				com.db4o.QResultClient resClient = new com.db4o.QResultClient(i_trans, ids.Length
					);
				for (int i = 0; i < ids.Length; i++)
				{
					resClient.Add((int)ids[i]);
				}
				Sort(resClient);
				return resClient;
			}
			if (!clazz.HasIndex())
			{
				return null;
			}
			com.db4o.QueryResultImpl resLocal = new com.db4o.QueryResultImpl(i_trans);
			com.db4o.inside.classindex.ClassIndexStrategy index = clazz.Index();
			index.TraverseAll(i_trans, new _AnonymousInnerClass337(this, resLocal));
			Sort(resLocal);
			return resLocal;
		}

		private sealed class _AnonymousInnerClass337 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass337(QQueryBase _enclosing, com.db4o.QueryResultImpl resLocal
				)
			{
				this._enclosing = _enclosing;
				this.resLocal = resLocal;
			}

			public void Visit(object a_object)
			{
				resLocal.Add(((int)a_object));
			}

			private readonly QQueryBase _enclosing;

			private readonly com.db4o.QueryResultImpl resLocal;
		}

		private com.db4o.query.Constraint FirstConstraint()
		{
			com.db4o.foundation.Iterator4 iterator = IterateConstraints();
			if (!iterator.MoveNext())
			{
				return null;
			}
			return (com.db4o.query.Constraint)iterator.Current();
		}

		internal virtual void Execute1(com.db4o.QueryResultImpl result)
		{
			if (Stream().IsClient())
			{
				Marshall();
				((com.db4o.YapClient)Stream()).QueryExecute(_this, result);
			}
			else
			{
				ExecuteLocal(result);
			}
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

		internal virtual void ExecuteLocal(com.db4o.QueryResultImpl result)
		{
			com.db4o.QQueryBase.CreateCandidateCollectionResult r = CreateCandidateCollection
				();
			bool checkDuplicates = r.checkDuplicates;
			bool topLevel = r.topLevel;
			com.db4o.foundation.List4 candidateCollection = r.candidateCollection;
			if (candidateCollection != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(candidateCollection
					);
				while (i.MoveNext())
				{
					((com.db4o.QCandidates)i.Current()).Execute();
				}
				if (candidateCollection._next != null)
				{
					checkDuplicates = true;
				}
				if (checkDuplicates)
				{
					result.CheckDuplicates();
				}
				com.db4o.YapStream stream = Stream();
				i = new com.db4o.foundation.Iterator4Impl(candidateCollection);
				while (i.MoveNext())
				{
					com.db4o.QCandidates candidates = (com.db4o.QCandidates)i.Current();
					if (topLevel)
					{
						candidates.Traverse(result);
					}
					else
					{
						com.db4o.QQueryBase q = this;
						com.db4o.foundation.Collection4 fieldPath = new com.db4o.foundation.Collection4();
						while (q.i_parent != null)
						{
							fieldPath.Add(q.i_field);
							q = q.i_parent;
						}
						candidates.Traverse(new _AnonymousInnerClass418(this, fieldPath, stream, result));
					}
				}
			}
			Sort(result);
			result.Reset();
		}

		private sealed class _AnonymousInnerClass418 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass418(QQueryBase _enclosing, com.db4o.foundation.Collection4
				 fieldPath, com.db4o.YapStream stream, com.db4o.QueryResultImpl result)
			{
				this._enclosing = _enclosing;
				this.fieldPath = fieldPath;
				this.stream = stream;
				this.result = result;
			}

			public void Visit(object a_object)
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_object;
				if (candidate.Include())
				{
					com.db4o.TreeInt ids = new com.db4o.TreeInt(candidate._key);
					com.db4o.TreeInt[] idsNew = new com.db4o.TreeInt[1];
					com.db4o.foundation.Iterator4 itPath = fieldPath.Iterator();
					while (itPath.MoveNext())
					{
						idsNew[0] = null;
						string fieldName = (string)(itPath.Current());
						if (ids != null)
						{
							ids.Traverse(new _AnonymousInnerClass429(this, stream, idsNew, fieldName));
						}
						ids = idsNew[0];
					}
					if (ids != null)
					{
						ids.Traverse(new _AnonymousInnerClass449(this, result));
					}
				}
			}

			private sealed class _AnonymousInnerClass429 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass429(_AnonymousInnerClass418 _enclosing, com.db4o.YapStream
					 stream, com.db4o.TreeInt[] idsNew, string fieldName)
				{
					this._enclosing = _enclosing;
					this.stream = stream;
					this.idsNew = idsNew;
					this.fieldName = fieldName;
				}

				public void Visit(object treeInt)
				{
					int id = ((com.db4o.TreeInt)treeInt)._key;
					com.db4o.YapWriter reader = stream.ReadWriterByID(this._enclosing._enclosing.i_trans
						, id);
					if (reader != null)
					{
						com.db4o.inside.marshall.ObjectHeader oh = new com.db4o.inside.marshall.ObjectHeader
							(stream, reader);
						idsNew[0] = oh.YapClass().CollectFieldIDs(oh._marshallerFamily, oh._headerAttributes
							, idsNew[0], reader, fieldName);
					}
				}

				private readonly _AnonymousInnerClass418 _enclosing;

				private readonly com.db4o.YapStream stream;

				private readonly com.db4o.TreeInt[] idsNew;

				private readonly string fieldName;
			}

			private sealed class _AnonymousInnerClass449 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass449(_AnonymousInnerClass418 _enclosing, com.db4o.QueryResultImpl
					 result)
				{
					this._enclosing = _enclosing;
					this.result = result;
				}

				public void Visit(object treeInt)
				{
					result.AddKeyCheckDuplicates(((com.db4o.TreeInt)treeInt)._key);
				}

				private readonly _AnonymousInnerClass418 _enclosing;

				private readonly com.db4o.QueryResultImpl result;
			}

			private readonly QQueryBase _enclosing;

			private readonly com.db4o.foundation.Collection4 fieldPath;

			private readonly com.db4o.YapStream stream;

			private readonly com.db4o.QueryResultImpl result;
		}

		public virtual com.db4o.QQueryBase.CreateCandidateCollectionResult CreateCandidateCollection
			()
		{
			bool checkDuplicates = false;
			bool topLevel = true;
			com.db4o.foundation.List4 candidateCollection = null;
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.MoveNext())
			{
				com.db4o.QCon qcon = (com.db4o.QCon)i.Current();
				com.db4o.QCon old = qcon;
				qcon = qcon.GetRoot();
				if (qcon != old)
				{
					checkDuplicates = true;
					topLevel = false;
				}
				com.db4o.YapClass yc = qcon.GetYapClass();
				if (yc == null)
				{
					break;
				}
				candidateCollection = AddConstraintToCandidateCollection(candidateCollection, qcon
					);
			}
			return new com.db4o.QQueryBase.CreateCandidateCollectionResult(candidateCollection
				, checkDuplicates, topLevel);
		}

		private com.db4o.foundation.List4 AddConstraintToCandidateCollection(com.db4o.foundation.List4
			 candidateCollection, com.db4o.QCon qcon)
		{
			if (candidateCollection != null)
			{
				if (TryToAddToExistingCandidate(candidateCollection, qcon))
				{
					return candidateCollection;
				}
			}
			com.db4o.QCandidates candidates = new com.db4o.QCandidates(i_trans, qcon.GetYapClass
				(), null);
			candidates.AddConstraint(qcon);
			return new com.db4o.foundation.List4(candidateCollection, candidates);
		}

		private bool TryToAddToExistingCandidate(com.db4o.foundation.List4 candidateCollection
			, com.db4o.QCon qcon)
		{
			com.db4o.foundation.Iterator4 j = new com.db4o.foundation.Iterator4Impl(candidateCollection
				);
			while (j.MoveNext())
			{
				com.db4o.QCandidates candidates = (com.db4o.QCandidates)j.Current();
				if (candidates.TryAddConstraint(qcon))
				{
					return true;
				}
			}
			return false;
		}

		public com.db4o.Transaction GetTransaction()
		{
			return i_trans;
		}

		internal virtual com.db4o.foundation.Iterator4 IterateConstraints()
		{
			return i_constraints.Iterator();
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
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).SetOrdering(ordering);
			}
		}

		internal virtual void Marshall()
		{
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).GetRoot().Marshall();
			}
		}

		internal virtual void RemoveConstraint(com.db4o.QCon a_constraint)
		{
			i_constraints.Remove(a_constraint);
		}

		internal virtual void Unmarshall(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).Unmarshall(a_trans);
			}
		}

		internal virtual com.db4o.query.Constraint ToConstraint(com.db4o.foundation.Collection4
			 constraints)
		{
			if (constraints.Size() == 1)
			{
				return FirstConstraint();
			}
			else
			{
				if (constraints.Size() > 0)
				{
					com.db4o.query.Constraint[] constraintArray = new com.db4o.query.Constraint[constraints
						.Size()];
					constraints.ToArray(constraintArray);
					return new com.db4o.QConstraints(i_trans, constraintArray);
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

		private void Sort(com.db4o.QueryResultImpl result)
		{
			if (_comparator != null)
			{
				result.Sort(_comparator);
			}
		}

		private static com.db4o.QQuery Cast(com.db4o.QQueryBase obj)
		{
			return (com.db4o.QQuery)obj;
		}
	}
}

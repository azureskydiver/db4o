namespace com.db4o
{
	/// <summary>
	/// Holds the tree of
	/// <see cref="com.db4o.QCandidate">com.db4o.QCandidate</see>
	/// objects and the list of
	/// <see cref="com.db4o.QCon">com.db4o.QCon</see>
	/// during query evaluation.
	/// The query work (adding and removing nodes) happens here.
	/// Candidates during query evaluation.
	/// <see cref="com.db4o.QCandidate">com.db4o.QCandidate</see>
	/// objects are stored in i_root
	/// </summary>
	/// <exclude></exclude>
	public sealed class QCandidates : com.db4o.foundation.Visitor4
	{
		public readonly com.db4o.Transaction i_trans;

		private com.db4o.Tree i_root;

		private com.db4o.foundation.List4 i_constraints;

		internal com.db4o.YapClass i_yapClass;

		private com.db4o.QField i_field;

		internal com.db4o.QCon i_currentConstraint;

		internal com.db4o.Tree i_ordered;

		private int i_orderID;

		private com.db4o.IDGenerator _idGenerator;

		internal QCandidates(com.db4o.Transaction a_trans, com.db4o.YapClass a_yapClass, 
			com.db4o.QField a_field)
		{
			i_trans = a_trans;
			i_yapClass = a_yapClass;
			i_field = a_field;
			if (a_field == null || a_field.i_yapField == null || !(a_field.i_yapField.GetHandler
				() is com.db4o.YapClass))
			{
				return;
			}
			com.db4o.YapClass yc = (com.db4o.YapClass)a_field.i_yapField.GetHandler();
			if (i_yapClass == null)
			{
				i_yapClass = yc;
			}
			else
			{
				yc = i_yapClass.GetHigherOrCommonHierarchy(yc);
				if (yc != null)
				{
					i_yapClass = yc;
				}
			}
		}

		public com.db4o.QCandidate AddByIdentity(com.db4o.QCandidate candidate)
		{
			i_root = com.db4o.Tree.Add(i_root, candidate);
			if (candidate._size == 0)
			{
				return candidate.GetRoot();
			}
			return candidate;
		}

		internal void AddConstraint(com.db4o.QCon a_constraint)
		{
			i_constraints = new com.db4o.foundation.List4(i_constraints, a_constraint);
		}

		internal void AddOrder(com.db4o.QOrder a_order)
		{
			i_ordered = com.db4o.Tree.Add(i_ordered, a_order);
		}

		internal void ApplyOrdering(com.db4o.Tree a_ordered, int a_orderID)
		{
			if (a_ordered == null || i_root == null)
			{
				return;
			}
			if (a_orderID > 0)
			{
				a_orderID = -a_orderID;
			}
			bool major = (a_orderID - i_orderID) < 0;
			if (major)
			{
				i_orderID = a_orderID;
			}
			int[] placement = { 0 };
			i_root.Traverse(new _AnonymousInnerClass111(this, major, placement));
			placement[0] = 1;
			a_ordered.Traverse(new _AnonymousInnerClass120(this, placement, major));
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			i_root.Traverse(new _AnonymousInnerClass131(this, col));
			com.db4o.Tree[] newTree = { null };
			com.db4o.foundation.Iterator4 i = col.Iterator();
			while (i.MoveNext())
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)i.Current();
				candidate._preceding = null;
				candidate._subsequent = null;
				candidate._size = 1;
				newTree[0] = com.db4o.Tree.Add(newTree[0], candidate);
			}
			i_root = newTree[0];
		}

		private sealed class _AnonymousInnerClass111 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass111(QCandidates _enclosing, bool major, int[] placement
				)
			{
				this._enclosing = _enclosing;
				this.major = major;
				this.placement = placement;
			}

			public void Visit(object a_object)
			{
				((com.db4o.QCandidate)a_object).HintOrder(0, major);
				((com.db4o.QCandidate)a_object).HintOrder(placement[0]++, !major);
			}

			private readonly QCandidates _enclosing;

			private readonly bool major;

			private readonly int[] placement;
		}

		private sealed class _AnonymousInnerClass120 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass120(QCandidates _enclosing, int[] placement, bool major
				)
			{
				this._enclosing = _enclosing;
				this.placement = placement;
				this.major = major;
			}

			public void Visit(object a_object)
			{
				com.db4o.QOrder qo = (com.db4o.QOrder)a_object;
				com.db4o.QCandidate candidate = qo._candidate.GetRoot();
				candidate.HintOrder(placement[0]++, major);
			}

			private readonly QCandidates _enclosing;

			private readonly int[] placement;

			private readonly bool major;
		}

		private sealed class _AnonymousInnerClass131 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass131(QCandidates _enclosing, com.db4o.foundation.Collection4
				 col)
			{
				this._enclosing = _enclosing;
				this.col = col;
			}

			public void Visit(object a_object)
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_object;
				col.Add(candidate);
			}

			private readonly QCandidates _enclosing;

			private readonly com.db4o.foundation.Collection4 col;
		}

		internal void Collect(com.db4o.QCandidates a_candidates)
		{
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.MoveNext())
			{
				com.db4o.QCon qCon = (com.db4o.QCon)i.Current();
				SetCurrentConstraint(qCon);
				qCon.Collect(a_candidates);
			}
			SetCurrentConstraint(null);
		}

		internal void Execute()
		{
			bool foundIndex = ProcessFieldIndexes();
			if (!foundIndex)
			{
				LoadFromClassIndex();
			}
			Evaluate();
		}

		public int ClassIndexEntryCount()
		{
			return i_yapClass.IndexEntryCount(i_trans);
		}

		private bool ProcessFieldIndexes()
		{
			if (i_constraints == null)
			{
				return false;
			}
			if (com.db4o.inside.marshall.MarshallerFamily.BTREE_FIELD_INDEX)
			{
				com.db4o.inside.fieldindex.FieldIndexProcessor processor = new com.db4o.inside.fieldindex.FieldIndexProcessor
					(this);
				com.db4o.inside.fieldindex.FieldIndexProcessorResult result = processor.Run();
				if (result == com.db4o.inside.fieldindex.FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH
					)
				{
					return true;
				}
				if (result == com.db4o.inside.fieldindex.FieldIndexProcessorResult.NO_INDEX_FOUND
					)
				{
					return false;
				}
				i_root = com.db4o.TreeInt.ToQCandidate(result.found, this);
				return true;
			}
			if (com.db4o.inside.marshall.MarshallerFamily.OLD_FIELD_INDEX)
			{
				com.db4o.inside.ix.QxProcessor processor = new com.db4o.inside.ix.QxProcessor();
				if (processor.Run(this, ClassIndexEntryCount()))
				{
					i_root = processor.ToQCandidates(this);
					return true;
				}
			}
			return false;
		}

		internal void Evaluate()
		{
			if (i_constraints == null)
			{
				return;
			}
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.MoveNext())
			{
				com.db4o.QCon qCon = (com.db4o.QCon)i.Current();
				qCon.SetCandidates(this);
				qCon.EvaluateSelf();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).EvaluateSimpleChildren();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).EvaluateEvaluations();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).EvaluateCreateChildrenCandidates();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).EvaluateCollectChildren();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).EvaluateChildren();
			}
		}

		internal bool IsEmpty()
		{
			bool[] ret = new bool[] { true };
			Traverse(new _AnonymousInnerClass245(this, ret));
			return ret[0];
		}

		private sealed class _AnonymousInnerClass245 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass245(QCandidates _enclosing, bool[] ret)
			{
				this._enclosing = _enclosing;
				this.ret = ret;
			}

			public void Visit(object obj)
			{
				if (((com.db4o.QCandidate)obj)._include)
				{
					ret[0] = false;
				}
			}

			private readonly QCandidates _enclosing;

			private readonly bool[] ret;
		}

		internal bool Filter(com.db4o.foundation.Visitor4 a_host)
		{
			if (i_root != null)
			{
				i_root.Traverse(a_host);
				i_root = i_root.Filter(new _AnonymousInnerClass258(this));
			}
			return i_root != null;
		}

		private sealed class _AnonymousInnerClass258 : com.db4o.VisitorBoolean
		{
			public _AnonymousInnerClass258(QCandidates _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public bool IsVisit(object a_candidate)
			{
				return ((com.db4o.QCandidate)a_candidate)._include;
			}

			private readonly QCandidates _enclosing;
		}

		internal int GenerateCandidateId()
		{
			if (_idGenerator == null)
			{
				_idGenerator = new com.db4o.IDGenerator();
			}
			return -_idGenerator.Next();
		}

		public com.db4o.foundation.Iterator4 IterateConstraints()
		{
			if (i_constraints == null)
			{
				return com.db4o.foundation.Iterator4Impl.EMPTY;
			}
			return new com.db4o.foundation.Iterator4Impl(i_constraints);
		}

		internal sealed class TreeIntBuilder
		{
			public com.db4o.TreeInt tree;

			public void Add(com.db4o.TreeInt node)
			{
				tree = (com.db4o.TreeInt)com.db4o.Tree.Add(tree, node);
			}
		}

		internal void LoadFromClassIndex()
		{
			if (!IsEmpty())
			{
				return;
			}
			com.db4o.QCandidates.TreeIntBuilder result = new com.db4o.QCandidates.TreeIntBuilder
				();
			com.db4o.inside.classindex.ClassIndexStrategy index = i_yapClass.Index();
			index.TraverseAll(i_trans, new _AnonymousInnerClass297(this, result));
			i_root = result.tree;
			com.db4o.inside.diagnostic.DiagnosticProcessor dp = i_trans.Stream().i_handlers._diagnosticProcessor;
			if (dp.Enabled())
			{
				dp.LoadedFromClassIndex(i_yapClass);
			}
		}

		private sealed class _AnonymousInnerClass297 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass297(QCandidates _enclosing, com.db4o.QCandidates.TreeIntBuilder
				 result)
			{
				this._enclosing = _enclosing;
				this.result = result;
			}

			public void Visit(object obj)
			{
				result.Add(new com.db4o.QCandidate(this._enclosing, null, ((int)obj), true));
			}

			private readonly QCandidates _enclosing;

			private readonly com.db4o.QCandidates.TreeIntBuilder result;
		}

		internal void SetCurrentConstraint(com.db4o.QCon a_constraint)
		{
			i_currentConstraint = a_constraint;
		}

		internal void Traverse(com.db4o.foundation.Visitor4 a_visitor)
		{
			if (i_root != null)
			{
				i_root.Traverse(a_visitor);
			}
		}

		internal bool TryAddConstraint(com.db4o.QCon a_constraint)
		{
			if (i_field != null)
			{
				com.db4o.QField qf = a_constraint.GetField();
				if (qf != null)
				{
					if (i_field.i_name != qf.i_name)
					{
						return false;
					}
				}
			}
			if (i_yapClass == null || a_constraint.IsNullConstraint())
			{
				AddConstraint(a_constraint);
				return true;
			}
			com.db4o.YapClass yc = a_constraint.GetYapClass();
			if (yc != null)
			{
				yc = i_yapClass.GetHigherOrCommonHierarchy(yc);
				if (yc != null)
				{
					i_yapClass = yc;
					AddConstraint(a_constraint);
					return true;
				}
			}
			return false;
		}

		public void Visit(object a_tree)
		{
			com.db4o.QCandidate parent = (com.db4o.QCandidate)a_tree;
			if (parent.CreateChild(this))
			{
				return;
			}
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).VisitOnNull(parent.GetRoot());
			}
		}
	}
}

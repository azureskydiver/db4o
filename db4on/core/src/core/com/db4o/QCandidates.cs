namespace com.db4o
{
	/// <summary>Holds the tree of QCandidate objects and the list of QContraints during query evaluation.
	/// 	</summary>
	/// <remarks>
	/// Holds the tree of QCandidate objects and the list of QContraints during query evaluation.
	/// The query work (adding and removing nodes) happens here.
	/// Candidates during query evaluation. QCandidate objects are stored in i_root
	/// </remarks>
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
			i_root.Traverse(new _AnonymousInnerClass107(this, major, placement));
			placement[0] = 1;
			a_ordered.Traverse(new _AnonymousInnerClass116(this, placement, major));
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			i_root.Traverse(new _AnonymousInnerClass127(this, col));
			com.db4o.Tree[] newTree = { null };
			com.db4o.foundation.Iterator4 i = col.Iterator();
			while (i.HasNext())
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)i.Next();
				candidate._preceding = null;
				candidate._subsequent = null;
				candidate._size = 1;
				newTree[0] = com.db4o.Tree.Add(newTree[0], candidate);
			}
			i_root = newTree[0];
		}

		private sealed class _AnonymousInnerClass107 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass107(QCandidates _enclosing, bool major, int[] placement
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

		private sealed class _AnonymousInnerClass116 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass116(QCandidates _enclosing, int[] placement, bool major
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

		private sealed class _AnonymousInnerClass127 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass127(QCandidates _enclosing, com.db4o.foundation.Collection4
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
			while (i.HasNext())
			{
				com.db4o.QCon qCon = (com.db4o.QCon)i.Next();
				SetCurrentConstraint(qCon);
				qCon.Collect(a_candidates);
			}
			SetCurrentConstraint(null);
		}

		internal void Execute()
		{
			int limit = i_yapClass.IndexEntryCount(i_trans);
			bool fromClassIndex = true;
			if (i_constraints != null)
			{
				com.db4o.inside.ix.QxProcessor processor = new com.db4o.inside.ix.QxProcessor();
				if (processor.Run(this, limit))
				{
					i_root = processor.ToQCandidates(this);
					fromClassIndex = false;
				}
			}
			if (fromClassIndex)
			{
				LoadFromClassIndex();
			}
			Evaluate();
		}

		internal void Evaluate()
		{
			if (i_constraints == null)
			{
				return;
			}
			com.db4o.foundation.Iterator4 i = IterateConstraints();
			while (i.HasNext())
			{
				((com.db4o.QCon)i.Next()).EvaluateSelf();
			}
			i = IterateConstraints();
			while (i.HasNext())
			{
				((com.db4o.QCon)i.Next()).EvaluateSimpleChildren();
			}
			i = IterateConstraints();
			while (i.HasNext())
			{
				((com.db4o.QCon)i.Next()).EvaluateEvaluations();
			}
			i = IterateConstraints();
			while (i.HasNext())
			{
				((com.db4o.QCon)i.Next()).EvaluateCreateChildrenCandidates();
			}
			i = IterateConstraints();
			while (i.HasNext())
			{
				((com.db4o.QCon)i.Next()).EvaluateCollectChildren();
			}
			i = IterateConstraints();
			while (i.HasNext())
			{
				((com.db4o.QCon)i.Next()).EvaluateChildren();
			}
		}

		internal bool IsEmpty()
		{
			bool[] ret = new bool[] { true };
			Traverse(new _AnonymousInnerClass217(this, ret));
			return ret[0];
		}

		private sealed class _AnonymousInnerClass217 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass217(QCandidates _enclosing, bool[] ret)
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
				i_root = i_root.Filter(new _AnonymousInnerClass230(this));
			}
			return i_root != null;
		}

		private sealed class _AnonymousInnerClass230 : com.db4o.VisitorBoolean
		{
			public _AnonymousInnerClass230(QCandidates _enclosing)
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

		internal void LoadFromClassIndex()
		{
			if (!IsEmpty())
			{
				return;
			}
			i_root = com.db4o.TreeInt.ToQCandidate((com.db4o.TreeInt)i_yapClass.GetIndex(i_trans
				), this);
			if (i_trans.i_stream.i_handlers._diagnosticProcessor.Enabled())
			{
				string name = i_yapClass.GetName();
				if (name.IndexOf("com.db4o.") != 0)
				{
					i_trans.i_stream.i_handlers._diagnosticProcessor.OnDiagnostic(new com.db4o.inside.diagnostic.DiagnosticMessage
						(name + " : Query candidate set could not be loaded from a field index.\n" + "  Consider indexing the fields that you want to query for using: \n"
						 + "  Db4o.configure().objectClass([class]).objectField([fieldName]).indexed(true);"
						));
				}
			}
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
			while (i.HasNext())
			{
				((com.db4o.QCon)i.Next()).VisitOnNull(parent.GetRoot());
			}
		}
	}
}

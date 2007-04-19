using System;
using System.Collections;
using System.Text;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Classindex;
using Db4objects.Db4o.Internal.Diagnostic;
using Db4objects.Db4o.Internal.Fieldindex;
using Db4objects.Db4o.Internal.Marshall;
using Db4objects.Db4o.Internal.Query.Processor;

namespace Db4objects.Db4o.Internal.Query.Processor
{
	/// <summary>
	/// Holds the tree of
	/// <see cref="QCandidate">QCandidate</see>
	/// objects and the list of
	/// <see cref="QCon">QCon</see>
	/// during query evaluation.
	/// The query work (adding and removing nodes) happens here.
	/// Candidates during query evaluation.
	/// <see cref="QCandidate">QCandidate</see>
	/// objects are stored in i_root
	/// </summary>
	/// <exclude></exclude>
	public sealed class QCandidates : IVisitor4
	{
		public readonly LocalTransaction i_trans;

		public Tree i_root;

		private List4 i_constraints;

		internal ClassMetadata i_yapClass;

		private QField i_field;

		internal QCon i_currentConstraint;

		internal Tree i_ordered;

		private int _majorOrderingID;

		private IDGenerator _idGenerator;

		internal QCandidates(LocalTransaction a_trans, ClassMetadata a_yapClass, QField a_field
			)
		{
			i_trans = a_trans;
			i_yapClass = a_yapClass;
			i_field = a_field;
			if (a_field == null || a_field.i_yapField == null || !(a_field.i_yapField.GetHandler
				() is ClassMetadata))
			{
				return;
			}
			ClassMetadata yc = (ClassMetadata)a_field.i_yapField.GetHandler();
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

		public QCandidate AddByIdentity(QCandidate candidate)
		{
			i_root = Tree.Add(i_root, candidate);
			if (candidate._size == 0)
			{
				return candidate.GetRoot();
			}
			return candidate;
		}

		internal void AddConstraint(QCon a_constraint)
		{
			i_constraints = new List4(i_constraints, a_constraint);
		}

		internal void AddOrder(QOrder a_order)
		{
			i_ordered = Tree.Add(i_ordered, a_order);
		}

		internal void ApplyOrdering(Tree orderedCandidates, int orderingID)
		{
			if (orderedCandidates == null || i_root == null)
			{
				return;
			}
			int absoluteOrderingID = Math.Abs(orderingID);
			bool major = TreatOrderingIDAsMajor(absoluteOrderingID);
			if (major && !IsUnordered())
			{
				SwapMajorOrderToMinor();
			}
			HintNewOrder(orderedCandidates, major);
			i_root = RecreateTreeFromCandidates();
			if (major)
			{
				_majorOrderingID = absoluteOrderingID;
			}
		}

		private Tree RecreateTreeFromCandidates()
		{
			Collection4 col = CollectCandidates();
			Tree newTree = null;
			IEnumerator i = col.GetEnumerator();
			while (i.MoveNext())
			{
				QCandidate candidate = (QCandidate)i.Current;
				candidate._preceding = null;
				candidate._subsequent = null;
				candidate._size = 1;
				newTree = Tree.Add(newTree, candidate);
			}
			return newTree;
		}

		private Collection4 CollectCandidates()
		{
			Collection4 col = new Collection4();
			i_root.Traverse(new _AnonymousInnerClass137(this, col));
			return col;
		}

		private sealed class _AnonymousInnerClass137 : IVisitor4
		{
			public _AnonymousInnerClass137(QCandidates _enclosing, Collection4 col)
			{
				this._enclosing = _enclosing;
				this.col = col;
			}

			public void Visit(object a_object)
			{
				QCandidate candidate = (QCandidate)a_object;
				col.Add(candidate);
			}

			private readonly QCandidates _enclosing;

			private readonly Collection4 col;
		}

		private void HintNewOrder(Tree orderedCandidates, bool major)
		{
			int[] currentOrder = new int[] { 0 };
			QOrder[] lastOrder = new QOrder[] { null };
			orderedCandidates.Traverse(new _AnonymousInnerClass150(this, lastOrder, currentOrder
				, major));
		}

		private sealed class _AnonymousInnerClass150 : IVisitor4
		{
			public _AnonymousInnerClass150(QCandidates _enclosing, QOrder[] lastOrder, int[] 
				currentOrder, bool major)
			{
				this._enclosing = _enclosing;
				this.lastOrder = lastOrder;
				this.currentOrder = currentOrder;
				this.major = major;
			}

			public void Visit(object a_object)
			{
				QOrder qo = (QOrder)a_object;
				if (!qo.IsEqual(lastOrder[0]))
				{
					currentOrder[0]++;
				}
				QCandidate candidate = qo._candidate.GetRoot();
				candidate.HintOrder(currentOrder[0], major);
				lastOrder[0] = qo;
			}

			private readonly QCandidates _enclosing;

			private readonly QOrder[] lastOrder;

			private readonly int[] currentOrder;

			private readonly bool major;
		}

		private void SwapMajorOrderToMinor()
		{
			i_root.Traverse(new _AnonymousInnerClass164(this));
		}

		private sealed class _AnonymousInnerClass164 : IVisitor4
		{
			public _AnonymousInnerClass164(QCandidates _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				QCandidate candidate = (QCandidate)obj;
				Order order = (Order)candidate._order;
				order.SwapMajorToMinor();
			}

			private readonly QCandidates _enclosing;
		}

		private bool TreatOrderingIDAsMajor(int absoluteOrderingID)
		{
			return (IsUnordered()) || (IsMoreRelevantOrderingID(absoluteOrderingID));
		}

		private bool IsUnordered()
		{
			return _majorOrderingID == 0;
		}

		private bool IsMoreRelevantOrderingID(int absoluteOrderingID)
		{
			return absoluteOrderingID < _majorOrderingID;
		}

		internal void Collect(Db4objects.Db4o.Internal.Query.Processor.QCandidates a_candidates
			)
		{
			IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				QCon qCon = (QCon)i.Current;
				SetCurrentConstraint(qCon);
				qCon.Collect(a_candidates);
			}
			SetCurrentConstraint(null);
		}

		internal void Execute()
		{
			FieldIndexProcessorResult result = ProcessFieldIndexes();
			if (result.FoundIndex())
			{
				i_root = result.ToQCandidate(this);
			}
			else
			{
				LoadFromClassIndex();
			}
			Evaluate();
		}

		public IEnumerator ExecuteSnapshot(Collection4 executionPath)
		{
			IIntIterator4 indexIterator = new IntIterator4Adaptor(IterateIndex(ProcessFieldIndexes
				()));
			Tree idRoot = TreeInt.AddAll(null, indexIterator);
			IEnumerator snapshotIterator = new TreeKeyIterator(idRoot);
			IEnumerator singleObjectQueryIterator = SingleObjectSodaProcessor(snapshotIterator
				);
			return MapIdsToExecutionPath(singleObjectQueryIterator, executionPath);
		}

		private IEnumerator SingleObjectSodaProcessor(IEnumerator indexIterator)
		{
			return new _AnonymousInnerClass217(this, indexIterator);
		}

		private sealed class _AnonymousInnerClass217 : MappingIterator
		{
			public _AnonymousInnerClass217(QCandidates _enclosing, IEnumerator baseArg1) : base
				(baseArg1)
			{
				this._enclosing = _enclosing;
			}

			protected override object Map(object current)
			{
				int id = ((int)current);
				QCandidate candidate = new QCandidate(this._enclosing, null, id, true);
				this._enclosing.i_root = candidate;
				this._enclosing.Evaluate();
				if (!candidate.Include())
				{
					return MappingIterator.SKIP;
				}
				return current;
			}

			private readonly QCandidates _enclosing;
		}

		public IEnumerator ExecuteLazy(Collection4 executionPath)
		{
			IEnumerator indexIterator = IterateIndex(ProcessFieldIndexes());
			IEnumerator singleObjectQueryIterator = SingleObjectSodaProcessor(indexIterator);
			return MapIdsToExecutionPath(singleObjectQueryIterator, executionPath);
		}

		private IEnumerator IterateIndex(FieldIndexProcessorResult result)
		{
			if (result.NoMatch())
			{
				return Iterators.EMPTY_ITERATOR;
			}
			if (result.FoundIndex())
			{
				return result.IterateIDs();
			}
			if (i_yapClass.IsPrimitive())
			{
				return Iterators.EMPTY_ITERATOR;
			}
			return BTreeClassIndexStrategy.Iterate(i_yapClass, i_trans);
		}

		private IEnumerator MapIdsToExecutionPath(IEnumerator singleObjectQueryIterator, 
			Collection4 executionPath)
		{
			if (executionPath == null)
			{
				return singleObjectQueryIterator;
			}
			IEnumerator res = singleObjectQueryIterator;
			IEnumerator executionPathIterator = executionPath.GetEnumerator();
			while (executionPathIterator.MoveNext())
			{
				string fieldName = (string)executionPathIterator.Current;
				IEnumerator mapIdToFieldIdsIterator = new _AnonymousInnerClass263(this, fieldName
					, res);
				res = new CompositeIterator4(mapIdToFieldIdsIterator);
			}
			return res;
		}

		private sealed class _AnonymousInnerClass263 : MappingIterator
		{
			public _AnonymousInnerClass263(QCandidates _enclosing, string fieldName, IEnumerator
				 baseArg1) : base(baseArg1)
			{
				this._enclosing = _enclosing;
				this.fieldName = fieldName;
			}

			protected override object Map(object current)
			{
				int id = ((int)current);
				StatefulBuffer reader = this._enclosing.Stream().ReadWriterByID(this._enclosing.i_trans
					, id);
				if (reader == null)
				{
					return MappingIterator.SKIP;
				}
				ObjectHeader oh = new ObjectHeader(this._enclosing.Stream(), reader);
				Tree idTree = oh.YapClass().CollectFieldIDs(oh._marshallerFamily, oh._headerAttributes
					, null, reader, fieldName);
				return new TreeKeyIterator(idTree);
			}

			private readonly QCandidates _enclosing;

			private readonly string fieldName;
		}

		public ObjectContainerBase Stream()
		{
			return i_trans.Stream();
		}

		public int ClassIndexEntryCount()
		{
			return i_yapClass.IndexEntryCount(i_trans);
		}

		private FieldIndexProcessorResult ProcessFieldIndexes()
		{
			if (i_constraints == null)
			{
				return FieldIndexProcessorResult.NO_INDEX_FOUND;
			}
			return new FieldIndexProcessor(this).Run();
		}

		internal void Evaluate()
		{
			if (i_constraints == null)
			{
				return;
			}
			IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				QCon qCon = (QCon)i.Current;
				qCon.SetCandidates(this);
				qCon.EvaluateSelf();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((QCon)i.Current).EvaluateSimpleChildren();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((QCon)i.Current).EvaluateEvaluations();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((QCon)i.Current).EvaluateCreateChildrenCandidates();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((QCon)i.Current).EvaluateCollectChildren();
			}
			i = IterateConstraints();
			while (i.MoveNext())
			{
				((QCon)i.Current).EvaluateChildren();
			}
		}

		internal bool IsEmpty()
		{
			bool[] ret = new bool[] { true };
			Traverse(new _AnonymousInnerClass348(this, ret));
			return ret[0];
		}

		private sealed class _AnonymousInnerClass348 : IVisitor4
		{
			public _AnonymousInnerClass348(QCandidates _enclosing, bool[] ret)
			{
				this._enclosing = _enclosing;
				this.ret = ret;
			}

			public void Visit(object obj)
			{
				if (((QCandidate)obj)._include)
				{
					ret[0] = false;
				}
			}

			private readonly QCandidates _enclosing;

			private readonly bool[] ret;
		}

		internal bool Filter(IVisitor4 a_host)
		{
			if (i_root != null)
			{
				i_root.Traverse(a_host);
				i_root = i_root.Filter(new _AnonymousInnerClass361(this));
			}
			return i_root != null;
		}

		private sealed class _AnonymousInnerClass361 : IPredicate4
		{
			public _AnonymousInnerClass361(QCandidates _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public bool Match(object a_candidate)
			{
				return ((QCandidate)a_candidate)._include;
			}

			private readonly QCandidates _enclosing;
		}

		internal int GenerateCandidateId()
		{
			if (_idGenerator == null)
			{
				_idGenerator = new IDGenerator();
			}
			return -_idGenerator.Next();
		}

		public IEnumerator IterateConstraints()
		{
			if (i_constraints == null)
			{
				return Iterators.EMPTY_ITERATOR;
			}
			return new Iterator4Impl(i_constraints);
		}

		internal sealed class TreeIntBuilder
		{
			public TreeInt tree;

			public void Add(TreeInt node)
			{
				tree = (TreeInt)Tree.Add(tree, node);
			}
		}

		internal void LoadFromClassIndex()
		{
			if (!IsEmpty())
			{
				return;
			}
			QCandidates.TreeIntBuilder result = new QCandidates.TreeIntBuilder();
			IClassIndexStrategy index = i_yapClass.Index();
			index.TraverseAll(i_trans, new _AnonymousInnerClass399(this, result));
			i_root = result.tree;
			DiagnosticProcessor dp = i_trans.Stream().i_handlers._diagnosticProcessor;
			if (dp.Enabled())
			{
				dp.LoadedFromClassIndex(i_yapClass);
			}
		}

		private sealed class _AnonymousInnerClass399 : IVisitor4
		{
			public _AnonymousInnerClass399(QCandidates _enclosing, QCandidates.TreeIntBuilder
				 result)
			{
				this._enclosing = _enclosing;
				this.result = result;
			}

			public void Visit(object obj)
			{
				result.Add(new QCandidate(this._enclosing, null, ((int)obj), true));
			}

			private readonly QCandidates _enclosing;

			private readonly QCandidates.TreeIntBuilder result;
		}

		internal void SetCurrentConstraint(QCon a_constraint)
		{
			i_currentConstraint = a_constraint;
		}

		internal void Traverse(IVisitor4 a_visitor)
		{
			if (i_root != null)
			{
				i_root.Traverse(a_visitor);
			}
		}

		internal bool TryAddConstraint(QCon a_constraint)
		{
			if (i_field != null)
			{
				QField qf = a_constraint.GetField();
				if (qf != null)
				{
					if (i_field.i_name != null && !i_field.i_name.Equals(qf.i_name))
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
			ClassMetadata yc = a_constraint.GetYapClass();
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
			QCandidate parent = (QCandidate)a_tree;
			if (parent.CreateChild(this))
			{
				return;
			}
			IEnumerator i = IterateConstraints();
			while (i.MoveNext())
			{
				((QCon)i.Current).VisitOnNull(parent.GetRoot());
			}
		}

		public override string ToString()
		{
			StringBuilder sb = new StringBuilder();
			i_root.Traverse(new _AnonymousInnerClass469(this, sb));
			return sb.ToString();
		}

		private sealed class _AnonymousInnerClass469 : IVisitor4
		{
			public _AnonymousInnerClass469(QCandidates _enclosing, StringBuilder sb)
			{
				this._enclosing = _enclosing;
				this.sb = sb;
			}

			public void Visit(object obj)
			{
				QCandidate candidate = (QCandidate)obj;
				sb.Append(" ");
				sb.Append(candidate._key);
			}

			private readonly QCandidates _enclosing;

			private readonly StringBuilder sb;
		}

		public void ClearOrdering()
		{
			i_ordered = null;
		}
	}
}

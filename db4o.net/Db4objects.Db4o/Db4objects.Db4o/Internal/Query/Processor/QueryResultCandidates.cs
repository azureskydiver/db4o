/* Copyright (C) 2004 - 2011  Versant Inc.  http://www.db4o.com */

using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal.Classindex;
using Db4objects.Db4o.Internal.Query.Processor;

namespace Db4objects.Db4o.Internal.Query.Processor
{
	/// <exclude></exclude>
	public class QueryResultCandidates
	{
		private QCandidate _candidates;

		private QCandidates _qCandidates;

		internal IIntVisitable _candidateIds;

		public QueryResultCandidates(QCandidates qCandidates)
		{
			_qCandidates = qCandidates;
		}

		public virtual void Add(QCandidate candidate)
		{
			ToQCandidates();
			_candidates = ((QCandidate)Tree.Add(_candidates, candidate));
		}

		private void ToQCandidates()
		{
			if (_candidateIds == null)
			{
				return;
			}
			_candidateIds.Traverse(new _IIntVisitor_34(this));
			_candidateIds = null;
		}

		private sealed class _IIntVisitor_34 : IIntVisitor
		{
			public _IIntVisitor_34(QueryResultCandidates _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(int id)
			{
				this._enclosing._candidates = ((QCandidate)Tree.Add(this._enclosing._candidates, 
					new QCandidate(this._enclosing._qCandidates, null, id)));
			}

			private readonly QueryResultCandidates _enclosing;
		}

		public virtual void FieldIndexProcessorResult(Db4objects.Db4o.Internal.Fieldindex.FieldIndexProcessorResult
			 result)
		{
			_candidateIds = result;
		}

		public virtual void SingleCandidate(QCandidate candidate)
		{
			_candidates = candidate;
			_candidateIds = null;
		}

		internal virtual bool Filter(IVisitor4 visitor)
		{
			ToQCandidates();
			if (_candidates != null)
			{
				_candidates.Traverse(visitor);
				_candidates = (QCandidate)_candidates.Filter(new _IPredicate4_55());
			}
			return _candidates != null;
		}

		private sealed class _IPredicate4_55 : IPredicate4
		{
			public _IPredicate4_55()
			{
			}

			public bool Match(object a_candidate)
			{
				return ((QCandidate)a_candidate)._include;
			}
		}

		public virtual void LoadFromClassIndex(IClassIndexStrategy index)
		{
			_candidateIds = index.IdVisitable(_qCandidates.Transaction());
		}

		internal virtual void Traverse(IVisitor4 visitor)
		{
			ToQCandidates();
			if (_candidates != null)
			{
				_candidates.Traverse(visitor);
			}
		}

		public virtual void TraverseIds(IIntVisitor visitor)
		{
			if (_candidateIds != null)
			{
				_candidateIds.Traverse(visitor);
				return;
			}
			Traverse(new _IVisitor4_80(visitor));
		}

		private sealed class _IVisitor4_80 : IVisitor4
		{
			public _IVisitor4_80(IIntVisitor visitor)
			{
				this.visitor = visitor;
			}

			public void Visit(object obj)
			{
				QCandidate candidate = (QCandidate)obj;
				if (candidate.Include())
				{
					visitor.Visit(candidate._key);
				}
			}

			private readonly IIntVisitor visitor;
		}
	}
}

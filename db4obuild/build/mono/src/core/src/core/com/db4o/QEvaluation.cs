/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <exclude></exclude>
	public class QEvaluation : com.db4o.QCon
	{
		[com.db4o.Transient]
		private object i_evaluation;

		internal byte[] i_marshalledEvaluation;

		internal int i_marshalledID;

		internal bool i_isDelegate;

		public QEvaluation()
		{
		}

		internal QEvaluation(com.db4o.Transaction a_trans, object a_evaluation, bool a_isDelegate
			) : base(a_trans)
		{
			i_evaluation = a_evaluation;
			i_isDelegate = a_isDelegate;
		}

		internal override void evaluateEvaluationsExec(com.db4o.QCandidates a_candidates, 
			bool rereadObject)
		{
			if (rereadObject)
			{
				a_candidates.traverse(new _AnonymousInnerClass27(this));
			}
			a_candidates.filter(this);
		}

		private sealed class _AnonymousInnerClass27 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass27(QEvaluation _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				((com.db4o.QCandidate)a_object).useField(null);
			}

			private readonly QEvaluation _enclosing;
		}

		internal override void marshall()
		{
			base.marshall();
			int[] id = { 0 };
			i_marshalledEvaluation = i_trans.i_stream.marshall(i_evaluation, id);
			i_marshalledID = id[0];
		}

		internal override void unmarshall(com.db4o.Transaction a_trans)
		{
			if (i_trans == null)
			{
				base.unmarshall(a_trans);
				i_evaluation = i_trans.i_stream.unmarshall(i_marshalledEvaluation, i_marshalledID
					);
			}
		}

		public override void visit(object obj)
		{
			com.db4o.QCandidate candidate = (com.db4o.QCandidate)obj;
			try
			{
				com.db4o.Platform.evaluationEvaluate(i_evaluation, candidate);
				if (!candidate.i_include)
				{
					doNotInclude(candidate.getRoot());
				}
			}
			catch (System.Exception e)
			{
				candidate.include(false);
				doNotInclude(candidate.getRoot());
			}
		}

		internal virtual bool supportsIndex()
		{
			return false;
		}
	}
}

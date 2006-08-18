package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.*;

public class FieldIndexProcessor {

	private QCandidates _candidates;

	public FieldIndexProcessor(QCandidates candidates) {
		_candidates = candidates;
	}

	public TreeInt run() {
		IndexedLeaf leaf = findBestIndex();
		if (null == leaf) {
			return null;
		}
		return leaf.toTreeInt();
	}

	private IndexedLeaf findBestIndex() {
		
		final Iterator4 i = findIndexedLeaves();
		while (i.hasNext()) {
			IndexedLeaf leaf = (IndexedLeaf)i.next();
			if (leaf.resultSize() > 0) {
				return leaf;
			}
		}
		return null;
	}

	private Transaction transaction() {
		return _candidates.i_trans;
	}

	private Iterator4 findIndexedLeaves() {
		final Collection4 leaves = new Collection4();
		collectIndexedLeaves(leaves, _candidates.iterateConstraints());
		return leaves.iterator();
	}
	
	static class IndexedLeaf {		
		private final QConObject _constraint;
		private BTreeRange _range;
		private final Transaction _transaction;
		
		public IndexedLeaf(Transaction transaction, QConObject qcon) {
			_transaction = transaction;
			_constraint = qcon;
			_range = search();
		}

		private BTreeRange search() {
			YapField field = getYapField();
			if (field == null) {
				return EmptyBTreeRange.INSTANCE;
			}
			
			final BTreeRange range = field.getIndex().search(_transaction, _constraint.getObject());
			final QEBitmap bitmap = QEBitmap.forQE(_constraint.i_evaluator);
			if (bitmap.takeGreater()) {
				final BTreeRange greater = range.greater();
				if (bitmap.takeEqual()) {
					return range.union(greater);
				}
				return greater;
			}
			return range;
		}

		private YapField getYapField() {
			return _constraint.getField().getYapField();
		}

		public int resultSize() {
			return _range.size();
		}

		public TreeInt toTreeInt() {
			final KeyValueIterator i = _range.iterator();
			
			TreeInt result = null;
			while (i.moveNext()) {
				result = (TreeInt) TreeInt.add(result, new TreeInt(((Integer)i.value()).intValue()));
			}
			return result;
		}

		public QConObject constraint() {
			return _constraint;
		}
	}

	private void collectIndexedLeaves(final Collection4 leaves, final Iterator4 qcons) {
		while (qcons.hasNext()) {
			QCon qcon = (QCon)qcons.next();
			if (isLeaf(qcon)) {
				if (qcon.canLoadByIndex() && qcon instanceof QConObject) {
					leaves.add(new IndexedLeaf(transaction(), (QConObject) qcon));
				}
			} else {
				collectIndexedLeaves(leaves, qcon.iterateChildren());
			}
		}
	}

	private boolean isLeaf(QCon qcon) {
		return !qcon.hasChildren();
	}
}

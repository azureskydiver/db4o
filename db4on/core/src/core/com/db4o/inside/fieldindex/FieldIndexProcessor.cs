namespace com.db4o.inside.fieldindex
{
	public class FieldIndexProcessor
	{
		private com.db4o.QCandidates _candidates;

		public FieldIndexProcessor(com.db4o.QCandidates candidates)
		{
			_candidates = candidates;
		}

		public virtual com.db4o.inside.fieldindex.FieldIndexProcessorResult Run()
		{
			com.db4o.inside.fieldindex.IndexedNode bestIndex = SelectBestIndex();
			if (null == bestIndex)
			{
				return com.db4o.inside.fieldindex.FieldIndexProcessorResult.NO_INDEX_FOUND;
			}
			if (bestIndex.ResultSize() > 0)
			{
				return new com.db4o.inside.fieldindex.FieldIndexProcessorResult(ResolveFully(bestIndex
					));
			}
			return com.db4o.inside.fieldindex.FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH;
		}

		private com.db4o.TreeInt ResolveFully(com.db4o.inside.fieldindex.IndexedNode bestIndex
			)
		{
			if (bestIndex.IsResolved())
			{
				return bestIndex.ToTreeInt();
			}
			return ResolveFully(bestIndex.Resolve());
		}

		public virtual com.db4o.inside.fieldindex.IndexedNode SelectBestIndex()
		{
			com.db4o.foundation.Iterator4 i = SelectIndexes();
			if (!i.MoveNext())
			{
				return null;
			}
			com.db4o.inside.fieldindex.IndexedNode best = (com.db4o.inside.fieldindex.IndexedNode
				)i.Current();
			while (i.MoveNext())
			{
				com.db4o.inside.fieldindex.IndexedLeaf leaf = (com.db4o.inside.fieldindex.IndexedLeaf
					)i.Current();
				if (leaf.ResultSize() < best.ResultSize())
				{
					best = leaf;
				}
			}
			return best;
		}

		private com.db4o.foundation.Iterator4 SelectIndexes()
		{
			com.db4o.foundation.Collection4 leaves = new com.db4o.foundation.Collection4();
			CollectIndexedLeaves(leaves, _candidates.IterateConstraints());
			return leaves.Iterator();
		}

		private void CollectIndexedLeaves(com.db4o.foundation.Collection4 leaves, com.db4o.foundation.Iterator4
			 qcons)
		{
			while (qcons.MoveNext())
			{
				com.db4o.QCon qcon = (com.db4o.QCon)qcons.Current();
				if (IsLeaf(qcon))
				{
					if (qcon.CanLoadByIndex() && qcon is com.db4o.QConObject)
					{
						com.db4o.QConObject conObject = (com.db4o.QConObject)qcon;
						com.db4o.inside.fieldindex.IndexedLeaf leaf = FindLeafOnSameField(leaves, conObject
							);
						if (leaf != null)
						{
							leaves.Add(Join(leaf, conObject));
						}
						else
						{
							leaves.Add(new com.db4o.inside.fieldindex.IndexedLeaf(conObject));
						}
					}
				}
				else
				{
					CollectIndexedLeaves(leaves, qcon.IterateChildren());
				}
			}
		}

		private com.db4o.inside.fieldindex.IndexedNode Join(com.db4o.inside.fieldindex.IndexedLeaf
			 existing, com.db4o.QConObject conObject)
		{
			if (existing.Constraint().HasOrJoinWith(conObject))
			{
				return new com.db4o.inside.fieldindex.OrIndexedLeaf(existing, new com.db4o.inside.fieldindex.IndexedLeaf
					(conObject));
			}
			return new com.db4o.inside.fieldindex.AndIndexedLeaf(existing, new com.db4o.inside.fieldindex.IndexedLeaf
				(conObject));
		}

		private com.db4o.inside.fieldindex.IndexedLeaf FindLeafOnSameField(com.db4o.foundation.Collection4
			 leaves, com.db4o.QConObject conObject)
		{
			com.db4o.foundation.Iterator4 i = leaves.Iterator();
			while (i.MoveNext())
			{
				com.db4o.inside.fieldindex.IndexedLeaf leaf = (com.db4o.inside.fieldindex.IndexedLeaf
					)i.Current();
				if (conObject.OnSameFieldAs(leaf.Constraint()))
				{
					return leaf;
				}
			}
			return null;
		}

		private bool IsLeaf(com.db4o.QCon qcon)
		{
			return !qcon.HasChildren();
		}
	}
}

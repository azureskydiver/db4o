namespace com.db4o.inside.fieldindex
{
	public class FieldIndexProcessor
	{
		private readonly com.db4o.QCandidates _candidates;

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
				com.db4o.inside.fieldindex.IndexedNode resolved = ResolveFully(bestIndex);
				if (null == resolved)
				{
					return com.db4o.inside.fieldindex.FieldIndexProcessorResult.NO_INDEX_FOUND;
				}
				return new com.db4o.inside.fieldindex.FieldIndexProcessorResult(resolved);
			}
			return com.db4o.inside.fieldindex.FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH;
		}

		private com.db4o.inside.fieldindex.IndexedNode ResolveFully(com.db4o.inside.fieldindex.IndexedNode
			 bestIndex)
		{
			if (null == bestIndex)
			{
				return null;
			}
			if (bestIndex.IsResolved())
			{
				return bestIndex;
			}
			return ResolveFully(bestIndex.Resolve());
		}

		public virtual com.db4o.inside.fieldindex.IndexedNode SelectBestIndex()
		{
			System.Collections.IEnumerator i = CollectIndexedNodes();
			if (!i.MoveNext())
			{
				return null;
			}
			com.db4o.inside.fieldindex.IndexedNode best = (com.db4o.inside.fieldindex.IndexedNode
				)i.Current;
			while (i.MoveNext())
			{
				com.db4o.inside.fieldindex.IndexedNode leaf = (com.db4o.inside.fieldindex.IndexedNode
					)i.Current;
				if (leaf.ResultSize() < best.ResultSize())
				{
					best = leaf;
				}
			}
			return best;
		}

		public virtual System.Collections.IEnumerator CollectIndexedNodes()
		{
			return new com.db4o.inside.fieldindex.IndexedNodeCollector(_candidates).GetNodes(
				);
		}
	}
}

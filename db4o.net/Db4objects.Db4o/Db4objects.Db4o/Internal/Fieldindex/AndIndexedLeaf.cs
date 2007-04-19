using Db4objects.Db4o.Internal.Fieldindex;
using Db4objects.Db4o.Internal.Query.Processor;

namespace Db4objects.Db4o.Internal.Fieldindex
{
	public class AndIndexedLeaf : JoinedLeaf
	{
		public AndIndexedLeaf(QCon constraint, IIndexedNodeWithRange leaf1, IIndexedNodeWithRange
			 leaf2) : base(constraint, leaf1, leaf1.GetRange().Intersect(leaf2.GetRange()))
		{
		}
	}
}
